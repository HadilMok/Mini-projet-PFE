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
        # L'encadrant est fixe
        model.Add(X[(proj, enc, "encadrant")] == 1)

        # Président et Rapporteur ≠ Encadrant
        model.AddExactlyOne(X[(proj, prof, "president")] for prof in professors if prof != enc)
        model.AddExactlyOne(X[(proj, prof, "rapporteur")] for prof in professors if prof != enc and prof not in [encadrants[proj] for proj in projects if True])  # dummy to emphasize != enc
        model.AddAllDifferent([X[(proj, prof, "president")] for prof in professors if prof != enc] + [X[(proj, prof, "rapporteur")] for prof in professors if prof != enc])

    # Équilibre rôles selon nombre de projets encadrés
    enc_count = defaultdict(int)
    for proj, enc in encadrants.items():
        enc_count[enc] += 1

    for prof in professors:
        model.Add(sum(X[(p, prof, "president")] for p in projects) == enc_count[prof])
        model.Add(sum(X[(p, prof, "rapporteur")] for p in projects) == enc_count[prof])

    # Résolution
    solver = cp_model.CpSolver()
    status = solver.Solve(model)
    if status not in (cp_model.OPTIMAL, cp_model.FEASIBLE):
        print("Pas de solution rôles!")
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

# -----------------------------
# Génération du planning
# -----------------------------
def generate_schedule(projects, professors, days, sessions_per_day, rooms, roles, week_start=True):
    model = cp_model.CpModel()
    S = {}

    # Variables
    for p in projects:
        for d in days:
            for s in range(sessions_per_day):
                for r in rooms:
                    S[(p,d,s,r)] = model.NewBoolVar(f"S_{p}_{d}_{s}_{r}")

    # Chaque projet a exactement 1 créneau
    for p in projects:
        model.AddExactlyOne(S[(p,d,s,r)] for d in days for s in range(sessions_per_day) for r in rooms)

    # Un prof max par session
    for prof in professors:
        for d in days:
            for s in range(sessions_per_day):
                model.Add(sum(S[(p,d,s,r)] for p in projects for r in rooms if prof in roles[p].values()) <= 1)

    # Un projet max par créneau
    for d in days:
        for s in range(sessions_per_day):
            for r in rooms:
                model.Add(sum(S[(p,d,s,r)] for p in projects) <= 1)

    # Équilibrage des salles
    min_per_room = len(projects) // len(rooms)
    max_per_room = min_per_room + (1 if len(projects) % len(rooms) else 0)
    for r in rooms:
        total_r = sum(S[(p,d,s,r)] for p in projects for d in days for s in range(sessions_per_day))
        model.Add(total_r >= min_per_room)
        model.Add(total_r <= max_per_room)

    # Force profs to early or late week
    early_days = [1,2,3,4]
    late_days = [5,6,7,8]
    import random
    random.seed(42)  # reproducible
    prof_avail = {prof: early_days if random.random() < 0.5 else late_days for prof in professors}
    for prof in professors:
        unavailable_days = set(days) - set(prof_avail[prof])
        for d in unavailable_days:
            for s in range(sessions_per_day):
                model.Add(sum(S[(p,d,s,r)] for p in projects for r in rooms if prof in roles[p].values()) == 0)

    # Résolution
    solver = cp_model.CpSolver()
    status = solver.Solve(model)
    if status not in (cp_model.OPTIMAL, cp_model.FEASIBLE):
        print("Pas de solution planning!")
        return None

    # Extraction
    schedule = {}
    for p in projects:
        for d in days:
            for s in range(sessions_per_day):
                for r in rooms:
                    if solver.Value(S[(p,d,s,r)]):
                        schedule[p] = (d, s+1, r)
                        break
    return schedule

# -----------------------------
# MAIN
# -----------------------------
if __name__ == "__main__":
    print("Chargement données gymapp...")
    projects, professors, encadrants, project_names, prof_names = get_data_from_db()

    print(f"Projets trouvés: {len(projects)}")
    print(f"Profs trouvés: {len(professors)}")

    days = list(range(1, 9))
    sessions_per_day = 7
    rooms = [f"Salle {i+1}" for i in range(8)]

    print("Assignation rôles...")
    roles = assign_roles(projects, professors, encadrants)
    if not roles:
        exit(1)

    print("Génération planning...")
    schedule = generate_schedule(projects, professors, days, sessions_per_day, rooms, roles, week_start=True)
    if not schedule:
        exit(1)

    # Affichage
    for day in days:
        rows = []
        for proj_id, (d, s, r) in schedule.items():
            if d == day:
                ro = roles[proj_id]
                # Vérification Président ≠ Rapporteur
                if ro['president'] == ro['rapporteur']:
                    print(f"Erreur: même prof pour Président et Rapporteur dans {project_names[projects.index(proj_id)]}")
                rows.append({
                    'Session': s, 'Salle': r,
                    'Projet': project_names[projects.index(proj_id)],
                    'Encadrant': prof_names[ro['encadrant']],
                    'President': prof_names[ro['president']],
                    'Rapporteur': prof_names[ro['rapporteur']]
                })
        if rows:
            df = pd.DataFrame(rows).sort_values(['Session','Salle'])
            print(f"\n===== Jour {day} ===")
            print(df.to_string(index=False))