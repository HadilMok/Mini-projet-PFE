import random
import pandas as pd
import psycopg2
from collections import defaultdict
from ortools.sat.python import cp_model
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()
app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_methods=["*"], allow_headers=["*"])

DB_CONFIG = {'host':'localhost','port':5432,'database':'gymapp','user':'postgres','password':'hadil123'}

ALL_PROFESSORS = [
    "Ahmed Ben Ali","Fatima Mansouri","Hassan Trabelsi","Layla Chahed","Omar Belhaj",
    "Sara Ksouri","Youssef Mbarki","Mona Jebali","Khalid Oueslati","Amina Boughanmi",
    "Nadia Hamdi","Sami Khalil","Mohamed Trabelsi","Ali Ben Salem","Rim Sfaxi",
    "Bilel Ayari","Asma Rekik","Tarek Jouini","Sonia Zouari","Mehdi Bouzid",
    "Imen Haddad","Riadh Ferchichi","Dorra Sellami","Wassim Gharbi","Olfa Turki",
    "Hatem Belhadj","Cyrine Mrad","Fathi Mnasri","Leila Boujemaa","Adel Chaabouni",
    "Wafa Bargaoui","Slim Abid","Najeh Hajlaoui","Rania Cherni","Ezzeddine Hadj",
    "Lamia Karray","Moez Ltaief","Hela Trabelsi","Chokri Jemaa","Nabil Ammar",
    "Emna Ouali","Samir Bouaziz"
]

EXTRA = ['Big Data Platform', 'Quantum Computing App', '5G Network Optimizer', 'Blockchain Voting', 'Smart City Dashboard', 'Predictive Maintenance', 'Digital Twin System', 'Edge Computing App', 'Federated Learning', 'Zero Trust Security', 'DevOps Automation', 'Microservices Platform', 'Kubernetes Manager', 'CI/CD Pipeline', 'API Gateway', 'Data Lake Architecture', 'Real-time Analytics', 'Computer Vision App', 'Speech Recognition', 'Gesture Control', 'Emotion Detection', 'Biometric Auth', 'Smart Grid System', 'Solar Energy Monitor', 'Water Quality Sensor', 'Air Pollution Tracker', 'Waste Management AI', 'Food Safety App', 'Drug Discovery AI', 'Telemedicine Platform', 'Mental Health App', 'Elderly Care Robot', 'Smart Classroom', 'E-learning Platform', 'Plagiarism Detector', 'Auto Essay Grader', 'Campus Navigation', 'Student Counseling AI', 'Research Paper Summarizer', 'Grant Management', 'HR Analytics', 'Payroll Automation', 'ERP System', 'CRM Platform', 'Inventory Manager', 'Logistics Optimizer', 'Fleet Management', 'Last Mile Delivery', 'Customer Churn Predictor', 'Price Optimization', 'Demand Forecasting', 'Market Basket Analysis', 'Social Media Analytics', 'Content Moderation', 'Fake News Detector', 'Clickbait Detector', 'Privacy Preserving ML', 'Explainable AI', 'AutoML Platform', 'Model Monitoring']

FICTIONAL = [
    "AI Chatbot","Blockchain App","Cybersecurity Tool","Cloud Migration",
    "Data Mining System","E-commerce Platform","IoT Network","Machine Learning Model",
    "Smart Home System","Facial Recognition","NLP Chatbot","AR Application",
    "Drone Navigation","Medical Diagnosis AI","Finance Predictor","Traffic Optimizer",
    "Voice Assistant","Recommendation Engine","Fraud Detection","Energy Manager",
    "Social Network Analyzer","Image Classifier","Robot Controller","Game AI",
    "Supply Chain Optimizer","Weather Predictor","Document Scanner","Code Generator",
    "Health Monitor","Virtual Reality App","Autonomous Vehicle","Smart Agriculture",
    "Cybersecurity Scanner","Sentiment Analyzer","Music Generator","Video Summarizer",
    "Language Translator","News Aggregator","Job Matcher","Student Performance Predictor"
]

def get_projects_from_db():
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        df = pd.read_sql_query("SELECT p.name || ' (' || COALESCE(p.code,'') || ')' as n FROM projects p", conn)
        conn.close()
        return df['n'].tolist()
    except:
        return []

@app.get("/generate-schedule")
def generate():
    db_projects = get_projects_from_db()
    all_fictional = FICTIONAL + [p for p in EXTRA if p not in FICTIONAL]
    projects = db_projects + [p for p in all_fictional if p not in db_projects]
    projects = projects[:100]

    NUM_DAYS = 7
    SESSIONS_PER_DAY = 6
    rooms = ["Salle 1","Salle 2","Salle 3","Salle 4","Salle 5","Salle 6"]

    # Only 10 profs as encadrants -> each gets ~4 projects -> 12 sessions total
    profs_shuffled = ALL_PROFESSORS[:]
    random.shuffle(profs_shuffled)
    encadrant_profs = profs_shuffled[:10]
    other_profs = profs_shuffled[10:]

    # Assign encadrants with VARIED loads (between 6 and 14 per prof)
    encadrants = {}
    enc_load = defaultdict(int)
    # Give each encadrant prof a random target load between 6 and 14
    target_loads = {}
    remaining = len(projects)
    for i, prof in enumerate(encadrant_profs):
        if i == len(encadrant_profs) - 1:
            target_loads[prof] = remaining
        else:
            load = random.randint(6, min(14, remaining - (len(encadrant_profs)-i-1)*6))
            target_loads[prof] = load
            remaining -= load

    # Assign projects based on target loads
    proj_list = projects[:]
    random.shuffle(proj_list)
    idx = 0
    for prof, load in target_loads.items():
        for _ in range(load):
            if idx < len(proj_list):
                encadrants[proj_list[idx]] = prof
                enc_load[prof] = load
                idx += 1
    # Fill any remaining
    while idx < len(proj_list):
        chosen = min(encadrant_profs, key=lambda p: enc_load[p])
        encadrants[proj_list[idx]] = chosen
        enc_load[chosen] += 1
        idx += 1

    # STRICT RULE: Encadrant N -> President EXACTLY N -> Rapporteur EXACTLY N
    # Build a pool: for each encadrant prof with N projects,
    # they must appear exactly N times as president and N times as rapporteur
    
    # Build president pool: each prof appears enc_load[prof] times
    pres_pool = []
    for prof in encadrant_profs:
        pres_pool.extend([prof] * enc_load[prof])
    random.shuffle(pres_pool)

    # Build rapporteur pool: each prof appears enc_load[prof] times  
    rap_pool = []
    for prof in encadrant_profs:
        rap_pool.extend([prof] * enc_load[prof])
    random.shuffle(rap_pool)

    # Assign presidents ensuring no conflict with encadrant
    presidents = {}
    pres_remaining = pres_pool[:]
    for proj in projects:
        enc = encadrants[proj]
        # Find first valid president (not the encadrant)
        for i, p in enumerate(pres_remaining):
            if p != enc:
                presidents[proj] = p
                pres_remaining.pop(i)
                break
        else:
            # Fallback
            candidates = [p for p in encadrant_profs if p != enc]
            presidents[proj] = random.choice(candidates)

    # Assign rapporteurs ensuring no conflict with encadrant and president
    rapporteurs = {}
    rap_remaining = rap_pool[:]
    for proj in projects:
        enc = encadrants[proj]
        pres = presidents[proj]
        # Find first valid rapporteur
        for i, p in enumerate(rap_remaining):
            if p != enc and p != pres:
                rapporteurs[proj] = p
                rap_remaining.pop(i)
                break
        else:
            # Fallback
            candidates = [p for p in encadrant_profs if p != enc and p != pres]
            rapporteurs[proj] = random.choice(candidates) if candidates else random.choice(encadrant_profs)

    # Schedule with OR-Tools
    model = cp_model.CpModel()
    n_proj = len(projects)
    n_rooms = len(rooms)
    S = {}
    for p in range(n_proj):
        for d in range(1, NUM_DAYS+1):
            for s in range(SESSIONS_PER_DAY):
                for r in range(n_rooms):
                    S[(p,d,s,r)] = model.NewBoolVar(f"S_{p}_{d}_{s}_{r}")

    for p in range(n_proj):
        model.AddExactlyOne(S[(p,d,s,r)] for d in range(1,NUM_DAYS+1) for s in range(SESSIONS_PER_DAY) for r in range(n_rooms))

    for d in range(1, NUM_DAYS+1):
        for s in range(SESSIONS_PER_DAY):
            for r in range(n_rooms):
                model.Add(sum(S[(p,d,s,r)] for p in range(n_proj)) <= 1)

    # No prof conflict per slot
    prof_projects = defaultdict(list)
    for i, proj in enumerate(projects):
        for prof in [encadrants[proj], presidents[proj], rapporteurs[proj]]:
            prof_projects[prof].append(i)

    for prof, proj_list in prof_projects.items():
        if len(proj_list) > 1:
            for d in range(1, NUM_DAYS+1):
                for s in range(SESSIONS_PER_DAY):
                    model.Add(sum(S[(p,d,s,r)] for p in proj_list for r in range(n_rooms)) <= 1)

    solver = cp_model.CpSolver()
    solver.parameters.max_time_in_seconds = 15.0
    status = solver.Solve(model)

    result = []
    for p in range(n_proj):
        proj = projects[p]
        assigned = False
        if status in (cp_model.OPTIMAL, cp_model.FEASIBLE):
            for d in range(1, NUM_DAYS+1):
                for s in range(SESSIONS_PER_DAY):
                    for r in range(n_rooms):
                        if solver.Value(S[(p,d,s,r)]):
                            result.append({
                                "project": proj,
                                "day": f"Jour {d}",
                                "session": f"S{s+1}",
                                "room": rooms[r],
                                "encadrant": encadrants[proj],
                                "president": presidents[proj],
                                "rapporteur": rapporteurs[proj]
                            })
                            assigned = True
                            break
                if assigned: break
        if not assigned:
            result.append({
                "project": proj,
                "day": f"Jour {random.randint(1,7)}",
                "session": f"S{random.randint(1,6)}",
                "room": random.choice(rooms),
                "encadrant": encadrants[proj],
                "president": presidents[proj],
                "rapporteur": rapporteurs[proj]
            })

    result.sort(key=lambda x: (x["day"], x["session"]))
    return result
