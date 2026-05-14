import sys
import random
import pandas as pd
import psycopg2
from ortools.sat.python import cp_model
from PyQt6.QtWidgets import (
    QApplication, QWidget, QVBoxLayout, QHBoxLayout,
    QPushButton, QTableWidget, QTableWidgetItem, QLabel,
    QTabWidget, QComboBox, QScrollArea, QFormLayout
)
from matplotlib.backends.backend_qt5agg import FigureCanvasQTAgg as FigureCanvas
from matplotlib.figure import Figure
from reportlab.platypus import SimpleDocTemplate, Table as PDFTable, TableStyle
from reportlab.lib import colors

# -----------------------------
# CONFIGURATION
# -----------------------------
NUM_DAYS = 7
SESSIONS_PER_DAY = 6
ROLES = ["Encadrant","Rapporteur","President"]
rooms = ["Salle 1","Salle 2","Salle 3","Salle 4","Salle 5","Salle 6"]

professors = [
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

DB_CONFIG = {
    'host': 'localhost',
    'port': 5432,
    'database': 'gymapp',
    'user': 'postgres',
    'password': 'hadil123'
}

# -----------------------------
# GET PROJECTS FROM DB
# -----------------------------
def get_projects_from_db():
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        df = pd.read_sql_query(
            "SELECT p.name || ' (' || COALESCE(p.code, '') || ')' as project_name FROM projects p",
            conn
        )
        conn.close()
        return df['project_name'].tolist()
    except:
        return ["AI Chatbot","Blockchain App","Cybersecurity Tool","Cloud Migration",
                "Data Mining System","E-commerce Platform","IoT Network","ML Model"]

# -----------------------------
# OR-TOOLS SCHEDULE
# -----------------------------
def generate_schedule(prof_availability=None):
    db_projects = get_projects_from_db()
    # Add fictional projects to reach similar count as original script
    fictional = [
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
    projects = db_projects + [p for p in fictional if p not in db_projects]
    projects = projects[:42]
    sessions = [(day+1, sess+1) for day in range(NUM_DAYS) for sess in range(SESSIONS_PER_DAY)]

    # Assign encadrant randomly
    encadrants = {}
    for proj in projects:
        encadrants[proj] = random.choice(professors)

    model = cp_model.CpModel()
    proj_indices = list(range(len(projects)))
    prof_indices = list(range(len(professors)))
    room_indices = list(range(len(rooms)))
    day_list = list(range(1, NUM_DAYS+1))
    sess_list = list(range(SESSIONS_PER_DAY))

    # Variables: S[p, d, s, r]
    S = {}
    for p in proj_indices:
        for d in day_list:
            for s in sess_list:
                for r in room_indices:
                    S[(p,d,s,r)] = model.NewBoolVar(f"S_{p}_{d}_{s}_{r}")

    # Each project exactly one slot
    for p in proj_indices:
        model.AddExactlyOne(S[(p,d,s,r)] for d in day_list for s in sess_list for r in room_indices)

    # One project per slot per room
    for d in day_list:
        for s in sess_list:
            for r in room_indices:
                model.Add(sum(S[(p,d,s,r)] for p in proj_indices) <= 1)

    # Professor availability constraints
    if prof_availability:
        for prof, unavail_days in prof_availability.items():
            if prof in professors:
                pi = professors.index(prof)
                enc_projects = [i for i, pname in enumerate(projects) if encadrants[pname] == prof]
                for p in enc_projects:
                    for d in unavail_days:
                        for s in sess_list:
                            for r in room_indices:
                                model.Add(S[(p,d,s,r)] == 0)

    solver = cp_model.CpSolver()
    solver.parameters.max_time_in_seconds = 10.0
    status = solver.Solve(model)

    if status not in (cp_model.OPTIMAL, cp_model.FEASIBLE):
        # Fallback to random if no solution
        table = []
        for i, proj in enumerate(projects):
            d, s = sessions[i % len(sessions)]
            r = rooms[i % len(rooms)]
            enc = encadrants[proj]
            others = [p for p in professors if p != enc]
            pres = random.choice(others)
            rap = random.choice([p for p in others if p != pres])
            table.append([d, s, proj, r, enc, rap, pres])
        df = pd.DataFrame(table, columns=["Day","Session","Project","Room","Encadrant","Rapporteur","President"])
        prof_roles_count = {prof: {"Encadrant":0,"Rapporteur":0,"President":0} for prof in professors}
        return df, prof_roles_count

    # Extract solution
    table = []
    prof_roles_count = {prof: {"Encadrant":0,"Rapporteur":0,"President":0} for prof in professors}
    for p in proj_indices:
        for d in day_list:
            for s in sess_list:
                for r in room_indices:
                    if solver.Value(S[(p,d,s,r)]):
                        proj = projects[p]
                        enc = encadrants[proj]
                        others = [pr for pr in professors if pr != enc]
                        pres = random.choice(others)
                        rap = random.choice([pr for pr in others if pr != pres])
                        table.append([d, s+1, proj, rooms[r], enc, rap, pres])
                        prof_roles_count[enc]["Encadrant"] += 1
                        prof_roles_count[pres]["President"] += 1
                        prof_roles_count[rap]["Rapporteur"] += 1

    df = pd.DataFrame(table, columns=["Day","Session","Project","Room","Encadrant","Rapporteur","President"])
    df = df.sort_values(["Day","Session"])
    return df, prof_roles_count

# -----------------------------
# UI CLASS
# -----------------------------
class SchedulerUI(QWidget):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Advanced Thesis Scheduler")
        self.resize(1200, 800)
        self.layout = QVBoxLayout()
        self.setLayout(self.layout)
        self.tabs = QTabWidget()
        self.layout.addWidget(self.tabs)
        self.tab_schedule = QWidget()
        self.tab_data = QWidget()
        self.tab_constraints = QWidget()
        self.tabs.addTab(self.tab_schedule, "Schedule")
        self.tabs.addTab(self.tab_data, "Charts & Metrics")
        self.tabs.addTab(self.tab_constraints, "Professor Availability")
        self.create_schedule_tab()
        self.create_data_tab()
        self.create_constraints_tab()

    def create_schedule_tab(self):
        layout = QVBoxLayout()
        self.tab_schedule.setLayout(layout)
        self.generate_btn = QPushButton("Generate Schedule")
        self.generate_btn.clicked.connect(self.generate_schedule)
        layout.addWidget(self.generate_btn)
        self.export_pdf_btn = QPushButton("Export Schedule to PDF")
        self.export_pdf_btn.clicked.connect(self.export_pdf)
        layout.addWidget(self.export_pdf_btn)
        self.table_widget = QTableWidget()
        layout.addWidget(self.table_widget)

    def create_data_tab(self):
        layout = QVBoxLayout()
        self.tab_data.setLayout(layout)
        self.chart_canvas = FigureCanvas(Figure(figsize=(8, 4)))
        layout.addWidget(self.chart_canvas)
        self.ax = self.chart_canvas.figure.add_subplot(121)
        self.ax_pie = self.chart_canvas.figure.add_subplot(122)

    def create_constraints_tab(self):
        layout = QVBoxLayout()
        self.tab_constraints.setLayout(layout)
        scroll_area = QScrollArea()
        scroll_area.setWidgetResizable(True)
        layout.addWidget(scroll_area)
        widget = QWidget()
        scroll_area.setWidget(widget)
        form_layout = QFormLayout()
        widget.setLayout(form_layout)
        self.availability_inputs = {}
        for prof in professors:
            combo = QComboBox()
            combo.addItem("None")
            for day in range(1, NUM_DAYS+1):
                combo.addItem(f"Day {day}")
            self.availability_inputs[prof] = combo
            form_layout.addRow(QLabel(prof), combo)

    def generate_schedule(self):
        prof_avail = {}
        for prof, combo in self.availability_inputs.items():
            val = combo.currentText()
            if val.startswith("Day"):
                day = int(val.split()[1])
                prof_avail[prof] = [day]
        self.df, self.prof_summary = generate_schedule(prof_avail)
        self.load_table()
        self.load_charts()

    def load_table(self):
        df = self.df
        self.table_widget.setRowCount(df.shape[0])
        self.table_widget.setColumnCount(df.shape[1])
        self.table_widget.setHorizontalHeaderLabels(df.columns.tolist())
        for i, row in df.iterrows():
            for j, val in enumerate(row):
                item = QTableWidgetItem(str(val))
                self.table_widget.setItem(i, j, item)
        self.table_widget.resizeColumnsToContents()
        self.table_widget.cellClicked.connect(self.on_cell_clicked)

    def on_cell_clicked(self, row, col):
        if col in [4, 5, 6]:  # Encadrant, Rapporteur, President columns
            prof_name = self.table_widget.item(row, col).text()
            self.show_prof_details(prof_name)

    def show_prof_details(self, prof_name):
        from PyQt6.QtWidgets import QDialog, QVBoxLayout, QTableWidget, QTableWidgetItem, QLabel
        dialog = QDialog(self)
        dialog.setWindowTitle(f"Charge de {prof_name}")
        dialog.resize(700, 400)
        layout = QVBoxLayout()
        dialog.setLayout(layout)

        # Summary
        enc_count = (self.df["Encadrant"] == prof_name).sum()
        pres_count = (self.df["President"] == prof_name).sum()
        rap_count = (self.df["Rapporteur"] == prof_name).sum()
        total = enc_count + pres_count + rap_count

        summary = QLabel(f"<b>{prof_name}</b> � Total: {total} sessions | Encadrant: {enc_count} | Pr�sident: {pres_count} | Rapporteur: {rap_count}")
        layout.addWidget(summary)

        # Detail table
        prof_rows = self.df[
            (self.df["Encadrant"] == prof_name) |
            (self.df["President"] == prof_name) |
            (self.df["Rapporteur"] == prof_name)
        ].copy()

        def get_role(row):
            roles = []
            if row["Encadrant"] == prof_name: roles.append("Encadrant")
            if row["President"] == prof_name: roles.append("Pr�sident")
            if row["Rapporteur"] == prof_name: roles.append("Rapporteur")
            return ", ".join(roles)

        prof_rows["Role"] = prof_rows.apply(get_role, axis=1)

        table = QTableWidget()
        cols = ["Day", "Session", "Project", "Room", "Role"]
        table.setColumnCount(len(cols))
        table.setHorizontalHeaderLabels(cols)
        table.setRowCount(len(prof_rows))
        for i, (_, row) in enumerate(prof_rows.iterrows()):
            for j, col in enumerate(cols):
                table.setItem(i, j, QTableWidgetItem(str(row[col])))
        table.resizeColumnsToContents()
        layout.addWidget(table)
        dialog.exec()

    def load_charts(self):
        self.ax.clear()
        self.ax_pie.clear()
        prof_counts = {}
        for prof in professors:
            prof_counts[prof] = (
                (self.df["Encadrant"] == prof).sum() +
                (self.df["Rapporteur"] == prof).sum() +
                (self.df["President"] == prof).sum()
            )
        prof_counts = {k: v for k, v in prof_counts.items() if v > 0}
        self.ax.bar(prof_counts.keys(), prof_counts.values(), color='skyblue')
        self.ax.set_title("Sessions per Professor")
        self.ax.set_ylabel("Number of Sessions")
        self.ax.tick_params(axis='x', rotation=90)
        room_counts = self.df["Room"].value_counts()
        self.ax_pie.pie(room_counts, labels=room_counts.index, autopct='%1.1f%%', startangle=90)
        self.ax_pie.set_title("Room Usage Distribution")
        self.chart_canvas.figure.tight_layout()
        self.chart_canvas.draw()

    def export_pdf(self):
        from PyQt6.QtWidgets import QFileDialog
        path, _ = QFileDialog.getSaveFileName(self, "Save PDF", "", "PDF Files (*.pdf)")
        if not path:
            return
        doc = SimpleDocTemplate(path)
        data = [self.df.columns.to_list()] + self.df.values.tolist()
        table = PDFTable(data)
        table.setStyle(TableStyle([
            ('BACKGROUND', (0,0), (-1,0), colors.gray),
            ('TEXTCOLOR', (0,0), (-1,0), colors.whitesmoke),
            ('GRID', (0,0), (-1,-1), 1, colors.black),
            ('FONTSIZE', (0,0), (-1,-1), 8),
        ]))
        doc.build([table])
        print("PDF exported to", path)

if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = SchedulerUI()
    window.show()
    sys.exit(app.exec())
