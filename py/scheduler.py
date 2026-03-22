from ortools.sat.python import cp_model
import random
from collections import defaultdict
import pandas as pd
import matplotlib.pyplot as plt

# -------------------------
# Données d'exemple
# -------------------------
projects = [f"Projet {i+1}" for i in range(60)]
professors = [
    "Dr. Ahmed Ben Salah", "Dr. Youssef Chourabi", "Dr. Fatma Boussaid", 
    "Dr. Hichem Zouari", "Dr. Leila Khaldi", "Dr. Khaled Mansouri", 
    "Dr. Amine Sfar", "Dr. Raja Mzali", "Dr. Nourhene Hachemi", 
    "Dr. Sami Ben Ahmed", "Dr. Mohamed Khemiri", "Dr. Imene Slama", 
    "Dr. Nabil Mahjoub", "Dr. Salah Nasraoui", "Dr. Amel Karray"
]

# Sélection aléatoire des encadrants
random.seed(0)
selected_encadrants = random.sample(professors, 10)
encadrants = {projects[i]: selected_encadrants[i % 10] for i in range(len(projects))}

# Paramètres de planning
days = list(range(1, 9))          # Jours 1 à 8
sessions_per_day = 7               # 7 créneaux par jour
rooms = [f"Salle {i+1}" for i in range(8)]  # 8 salles
min_per_room = len(projects) // len(rooms)           # 60 // 8 = 7
max_per_room = min_per_room + (1 if len(projects) % len(rooms) else 0)  # = 8

# -------------------------
# Fonction: assign_roles
# -------------------------
def assign_roles(projects, professors, encadrants):
    model = cp_model.CpModel()
    X = {}
    for proj in projects:
        for prof in professors:
            for role in ("encadrant", "president", "rapporteur"):
                X[(proj, prof, role)] = model.NewBoolVar(f"X_{proj}_{prof}_{role}")
    for proj in projects:
        enc = encadrants[proj]
        model.Add(X[(proj, enc, "encadrant")] == 1)
        model.AddExactlyOne(X[(proj, prof, "president")] for prof in professors if prof != enc)
        model.AddExactlyOne(X[(proj, prof, "rapporteur")] for prof in professors if prof != enc)
    enc_count = {prof: sum(1 for p in projects if encadrants[p] == prof) for prof in professors}
    for prof in professors:
        model.Add(sum(X[(p, prof, "president")] for p in projects) == enc_count[prof])
        model.Add(sum(X[(p, prof, "rapporteur")] for p in projects) == enc_count[prof])
    solver = cp_model.CpSolver()
    status = solver.Solve(model)
    if status not in (cp_model.OPTIMAL, cp_model.FEASIBLE):
        raise RuntimeError("Aucune solution de répartition des rôles trouvée.")
    return {p: {role: prof for prof in professors for role in ("encadrant","president","rapporteur") if solver.Value(X[(p,prof,role)])} for p in projects}

# -------------------------
# Fonction: generate_schedule
# -------------------------
def generate_schedule(projects, professors, days, sessions, rooms, roles):
    model = cp_model.CpModel()
    S = {}
    for p in projects:
        for d in days:
            for s in range(sessions):
                for r in rooms:
                    S[(p,d,s,r)] = model.NewBoolVar(f"S_{p}_{d}_{s}_{r}")
    for p in projects:
        model.AddExactlyOne(S[(p,d,s,r)] for d in days for s in range(sessions) for r in rooms)
    for prof in professors:
        for d in days:
            for s in range(sessions):
                model.Add(sum(S[(p,d,s,r)] for p in projects for r in rooms if roles[p]['encadrant']==prof or roles[p]['president']==prof or roles[p]['rapporteur']==prof) <= 1)
    for d in days:
        for s in range(sessions):
            for r in rooms:
                model.Add(sum(S[(p,d,s,r)] for p in projects) <= 1)
    for r in rooms:
        total_r = sum(S[(p,d,s,r)] for p in projects for d in days for s in range(sessions))
        model.Add(total_r >= min_per_room)
        model.Add(total_r <= max_per_room)
    solver = cp_model.CpSolver()
    status = solver.Solve(model)
    if status not in (cp_model.OPTIMAL, cp_model.FEASIBLE):
        raise RuntimeError("Aucune solution de planning trouvée.")
    return {p:(d,s,r) for p in projects for d in days for s in range(sessions) for r in rooms if solver.Value(S[(p,d,s,r)])}

# -------------------------
# Exécution principale
# -------------------------
if __name__ == "__main__":
    roles = assign_roles(projects, professors, encadrants)
    schedule = generate_schedule(projects, professors, days, sessions_per_day, rooms, roles)

    # Tableaux par jour
    all_days = {}
    for day in days:
        rows = []
        for proj, (d,s,r) in schedule.items():
            if d == day:
                ro = roles[proj]
                rows.append({
                    'Session': s+1, 'Salle': r, 'Projet': proj,
                    'Encadrant': ro['encadrant'], 'President': ro['president'], 'Rapporteur': ro['rapporteur']
                })
        df = pd.DataFrame(rows).sort_values(['Session','Salle']).reset_index(drop=True)
        all_days[day] = df
        print(f"\n===== Jour {day} =====")
        print(df.to_string(index=False))

    # Analytique globale
    # Participation professeurs
    participation = defaultdict(lambda: {'encadrant':0,'president':0,'rapporteur':0,'total':0})
    for proj, ro in roles.items():
        for role, prof in ro.items():
            participation[prof][role] += 1
            participation[prof]['total'] += 1
    df_part = pd.DataFrame([{'Professor':prof,**vals} for prof,vals in participation.items()]).sort_values('total',ascending=False)
    print("\n===== Synthèse Participation Professeurs =====")
    print(df_part.to_string(index=False))

    # Soutenances par salle
    salle_count = defaultdict(int)
    for proj,(_,_,r) in schedule.items(): salle_count[r]+=1
    df_salle = pd.DataFrame([{'Salle':s,'Count':c} for s,c in salle_count.items()]).sort_values('Salle')
    print("\n===== Synthèse Soutenances par Salle =====")
    print(df_salle.to_string(index=False))

    # Statistiques globales
    total_projects = len(projects)
    print(f"\nTotal projets programmés: {total_projects}")
    print(f"Total créneaux disponibles: {len(days)*sessions_per_day*len(rooms)}")
    print(f"Taux d'occupation salles: {total_projects/(len(days)*sessions_per_day*len(rooms))*100:.2f}%")