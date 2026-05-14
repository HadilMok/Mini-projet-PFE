import psycopg2
from ortools.sat.python import cp_model
from collections import defaultdict
import pandas as pd

# -----------------------------
# CONFIG DB gymapp
# -----------------------------
DB_CONFIG = {
    'host': 'localhost',
    'port': 5432,
    'database': 'gymapp',
    'user': 'postgres',
    'password': 'hadil123'
}

# -----------------------------
# Récupération des données
# -----------------------------
def get_data_from_db():
    conn = psycopg2.connect(**DB_CONFIG)
    projects_query = """
        SELECT p.id, p.name || ' (' || COALESCE(p.code, '') || ')' as project_name, p.encadrant_id as enc_id
        FROM projects p 
        WHERE p.encadrant_id IS NOT NULL
    """
    projects_df = pd.read_sql_query(projects_query, conn)
    projects = projects_df['id'].tolist()
    project_names = projects_df['project_name'].tolist()

    profs_query = """
        SELECT DISTINCT p.id, u.first_name || ' ' || u.last_name as name
        FROM professor p
        JOIN user_account u ON p.id = u.id
        WHERE u.role = 'PROFESSOR'
    """
    profs_df = pd.read_sql_query(profs_query, conn)
    professors = profs_df['id'].tolist()
    prof_names = dict(zip(profs_df['id'], profs_df['name']))

    encadrants = dict(zip(projects_df['id'], projects_df['enc_id']))
    conn.close()
    return projects, professors, encadrants, project_names, prof_names

# -----------------------------
# Assignation des rôles
# -----------------------------
def assign_roles(projects, professors, encadrants):
    model = cp_model.CpModel()
    X = {}
    roles_list = ["encadrant", "president", "rapporteur"]

    # Variables
    for proj in projects:
        for prof in professors:
            for role in roles_list:
                X[(proj, prof, role)] = model.NewBoolVar(f"X_{proj}_{prof}_{role}")

    # Contraintes
    for proj in projects:
        enc = encadrants[proj]
        model.Add(X[(proj, enc, "encadrant")] == 1)
        for prof in professors:
            if prof != enc:
                model.Add(X[(proj, prof, "encadrant")] == 0)
        model.AddExactlyOne(X[(proj, prof, "president")] for prof in professors if prof != enc)
        model.AddExactlyOne(X[(proj, prof, "rapporteur")] for prof in professors if prof != enc)
        for prof in professors:
            if prof != enc:
                model.Add(X[(proj, prof, "president")] + X[(proj, prof, "rapporteur")] <= 1)

    # Resolution
    solver = cp_model.CpSolver()
    status = solver.Solve(model)
    if status not in (cp_model.OPTIMAL, cp_model.FEASIBLE):
        print("Pas de solution roles!")
        return None

    # Extraction
    roles = {}
    for p in projects:
        roles[p] = {}
        for prof in professors:
            for role in roles_list:
                if solver.Value(X[(p, prof, role)]):
                    roles[p][role] = prof
    return roles

def generate_schedule(projects, professors, days, sessions_per_day, rooms, roles):
    model = cp_model.CpModel()
    S = {}
    for p in projects:
        for d in days:
            for s in range(sessions_per_day):
                for r in rooms:
                    S[(p,d,s,r)] = model.NewBoolVar(f"S_{p}_{d}_{s}_{r}")

    # Chaque projet a exactement 1 creneau
    for p in projects:
        model.AddExactlyOne(S[(p,d,s,r)] for d in days for s in range(sessions_per_day) for r in rooms)

    # Un prof max par session
    for prof in professors:
        for d in days:
            for s in range(sessions_per_day):
                model.Add(sum(S[(p,d,s,r)] for p in projects for r in rooms
                    if roles[p].get('encadrant')==prof or roles[p].get('president')==prof or roles[p].get('rapporteur')==prof) <= 1)

    # Un projet max par creneau par salle
    for d in days:
        for s in range(sessions_per_day):
            for r in rooms:
                model.Add(sum(S[(p,d,s,r)] for p in projects) <= 1)

    # Equilibrage des salles
    min_per_room = len(projects) // len(rooms)
    max_per_room = min_per_room + (1 if len(projects) % len(rooms) else 0)
    for r in rooms:
        total_r = sum(S[(p,d,s,r)] for p in projects for d in days for s in range(sessions_per_day))
        model.Add(total_r >= min_per_room)
        model.Add(total_r <= max_per_room)

    solver = cp_model.CpSolver()
    status = solver.Solve(model)
    if status not in (cp_model.OPTIMAL, cp_model.FEASIBLE):
        return None

    schedule = {}
    for p in projects:
        for d in days:
            for s in range(sessions_per_day):
                for r in rooms:
                    if solver.Value(S[(p,d,s,r)]):
                        schedule[p] = (d, s+1, r)
    return schedule


if __name__ == "__main__":
    print("Chargement donnees gymapp...")
    projects, professors, encadrants, project_names, prof_names = get_data_from_db()

    print(f"Projets trouves: {len(projects)}")
    print(f"Profs trouves: {len(professors)}")

    days = list(range(1, 9))
    sessions_per_day = 7
    rooms = [f"Salle {i+1}" for i in range(8)]

    print("Assignation roles...")
    roles = assign_roles(projects, professors, encadrants)
    if not roles:
        print("Pas de solution!")
        exit(1)

    print("Generation planning...")
    schedule = generate_schedule(projects, professors, days, sessions_per_day, rooms, roles)
    if not schedule:
        print("Pas de planning!")
        exit(1)

    for day in days:
        rows = []
        for proj_id, (d, s, r) in schedule.items():
            if d == day:
                ro = roles[proj_id]
                rows.append({
                    'Session': s, 'Salle': r,
                    'Projet': project_names[projects.index(proj_id)],
                    'Encadrant': prof_names[ro['encadrant']],
                    'President': prof_names[ro['president']],
                    'Rapporteur': prof_names[ro['rapporteur']]
                })
        if rows:
            import pandas as pd
            df = pd.DataFrame(rows).sort_values(['Session','Salle'])
            print(f"\n===== Jour {day} =====")
            print(df.to_string(index=False))
