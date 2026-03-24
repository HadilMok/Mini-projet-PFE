import sys
import random
import pandas as pd
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
professors = ["Ahmed","Fatima","Hassan","Layla","Omar","Sara","Youssef","Mona","Khalid","Amina"]
projects = ["AI Chatbot","Blockchain App","Cybersecurity Tool","Cloud Migration",
            "Data Mining System","E-commerce Platform","IoT Network","Machine Learning Model"]
rooms = ["Salle 1","Salle 2","Salle 3","Salle 4","Salle 5","Salle 6"]

# -----------------------------
# HELPER FUNCTIONS
# -----------------------------
def generate_schedule(prof_availability=None):
    sessions = [(day+1,sess+1) for day in range(NUM_DAYS) for sess in range(SESSIONS_PER_DAY)]
    schedule = {s:{role:None for role in ROLES} for s in sessions}
    assigned_projects = random.sample(projects * ((len(sessions)//len(projects))+1), len(sessions))
    assigned_rooms = random.choices(rooms, k=len(sessions))
    prof_roles_count = {prof:{role:0 for role in ROLES} for prof in professors}

    for s,p,r in zip(sessions,assigned_projects,assigned_rooms):
        schedule[s]["Project"]=p
        schedule[s]["Room"]=r

    # Assign Encadrant
    prof_enc_sessions={}
    for prof in professors:
        num_enc = random.randint(1,3)
        prof_enc_sessions[prof]=[]
        avail = [s for s in sessions if schedule[s]["Encadrant"] is None]
        random.shuffle(avail)
        for _ in range(num_enc):
            for s in avail:
                if prof_availability and s in prof_availability.get(prof,[]):
                    continue
                if prof not in schedule[s].values():
                    schedule[s]["Encadrant"]=prof
                    prof_roles_count[prof]["Encadrant"]+=1
                    prof_enc_sessions[prof].append(s)
                    avail.remove(s)
                    break

    # Assign President & Rapporteur near Encadrant
    for prof, enc_sess in prof_enc_sessions.items():
        for role in ["Rapporteur","President"]:
            for es in enc_sess:
                assign_nearest(schedule, prof, role, es, prof_availability)

    # Fill remaining empty roles
    for s,data in schedule.items():
        for role in ROLES:
            if data[role] is None:
                choices=[p for p in professors if p not in data.values()]
                # filter availability
                if prof_availability:
                    choices = [p for p in choices if s not in prof_availability.get(p,[])]
                data[role]=random.choice(choices)
                prof_roles_count[data[role]][role]+=1

    table=[]
    for (day,sess),data in schedule.items():
        table.append([day,sess,data["Project"],data["Room"],data["Encadrant"],data["Rapporteur"],data["President"]])
    df=pd.DataFrame(table,columns=["Day","Session","Project","Room","Encadrant","Rapporteur","President"])
    return df, prof_roles_count

def assign_nearest(schedule,prof,role,target_session,prof_availability):
    sessions_list=[(d,s) for d in range(1,NUM_DAYS+1) for s in range(1,SESSIONS_PER_DAY+1)]
    def dist(s): return abs((s[0]-1)*SESSIONS_PER_DAY + (s[1]-1) - ((target_session[0]-1)*SESSIONS_PER_DAY + (target_session[1]-1)))
    candidates=[s for s in sessions_list if schedule[s][role] is None and prof not in schedule[s].values()]
    if prof_availability:
        candidates = [s for s in candidates if s not in prof_availability.get(prof,[])]
    if not candidates: return
    candidates.sort(key=dist)
    nearest = candidates[0]
    schedule[nearest][role]=prof

# -----------------------------
# UI CLASS
# -----------------------------
class SchedulerUI(QWidget):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Advanced Thesis Scheduler")
        self.resize(1200,800)
        self.layout=QVBoxLayout()
        self.setLayout(self.layout)

        self.tabs=QTabWidget()
        self.layout.addWidget(self.tabs)

        self.tab_schedule=QWidget()
        self.tab_data=QWidget()
        self.tab_constraints=QWidget()
        self.tabs.addTab(self.tab_schedule,"Schedule")
        self.tabs.addTab(self.tab_data,"Charts & Metrics")
        self.tabs.addTab(self.tab_constraints,"Professor Availability")

        self.create_schedule_tab()
        self.create_data_tab()
        self.create_constraints_tab()

    # -----------------------------
    def create_schedule_tab(self):
        layout=QVBoxLayout()
        self.tab_schedule.setLayout(layout)
        self.generate_btn=QPushButton("Generate Schedule")
        self.generate_btn.clicked.connect(self.generate_schedule)
        layout.addWidget(self.generate_btn)
        self.export_pdf_btn=QPushButton("Export Schedule to PDF")
        self.export_pdf_btn.clicked.connect(self.export_pdf)
        layout.addWidget(self.export_pdf_btn)
        self.table_widget=QTableWidget()
        layout.addWidget(self.table_widget)

    # -----------------------------
    def create_data_tab(self):
        layout=QVBoxLayout()
        self.tab_data.setLayout(layout)
        self.chart_canvas=FigureCanvas(Figure(figsize=(8,4)))
        layout.addWidget(self.chart_canvas)
        self.ax=self.chart_canvas.figure.add_subplot(121)
        self.ax_pie=self.chart_canvas.figure.add_subplot(122)

    # -----------------------------
    def create_constraints_tab(self):
        layout=QVBoxLayout()
        self.tab_constraints.setLayout(layout)
        scroll_area=QScrollArea()
        scroll_area.setWidgetResizable(True)
        layout.addWidget(scroll_area)
        widget=QWidget()
        scroll_area.setWidget(widget)
        form_layout=QFormLayout()
        widget.setLayout(form_layout)
        self.availability_inputs={}
        for prof in professors:
            combo=QComboBox()
            combo.addItem("None")
            for day in range(1,NUM_DAYS+1):
                combo.addItem(f"Day {day}")
            self.availability_inputs[prof]=combo
            form_layout.addRow(QLabel(prof),combo)

    # -----------------------------
    def generate_schedule(self):
        prof_avail={}
        for prof,combo in self.availability_inputs.items():
            val=combo.currentText()
            if val.startswith("Day"):
                day=int(val.split()[1])
                prof_avail[prof]=[(day,sess) for sess in range(1,SESSIONS_PER_DAY+1)]
        self.df, self.prof_summary = generate_schedule(prof_avail)
        self.load_table()
        self.load_charts()

    # -----------------------------
    def load_table(self):
        df=self.df
        self.table_widget.setRowCount(df.shape[0])
        self.table_widget.setColumnCount(df.shape[1])
        self.table_widget.setHorizontalHeaderLabels(df.columns)
        for i,row in df.iterrows():
            for j,val in enumerate(row):
                self.table_widget.setItem(i,j,QTableWidgetItem(str(val)))
        self.table_widget.resizeColumnsToContents()

    # -----------------------------
    def load_charts(self):
        self.ax.clear()
        self.ax_pie.clear()
        # Bar chart per professor
        prof_counts={}
        for prof in professors:
            prof_counts[prof]=((self.df["Encadrant"]==prof).sum()+
                               (self.df["Rapporteur"]==prof).sum()+
                               (self.df["President"]==prof).sum())
        self.ax.bar(prof_counts.keys(),prof_counts.values(),color='skyblue')
        self.ax.set_title("Sessions per Professor")
        self.ax.set_ylabel("Number of Sessions")
        # Pie chart for rooms
        room_counts=self.df["Room"].value_counts()
        self.ax_pie.pie(room_counts, labels=room_counts.index, autopct='%1.1f%%', startangle=90)
        self.ax_pie.set_title("Room Usage Distribution")
        self.chart_canvas.draw()

    # -----------------------------
    def export_pdf(self):
        from PyQt6.QtWidgets import QFileDialog
        path,_ = QFileDialog.getSaveFileName(self,"Save PDF","","PDF Files (*.pdf)")
        if not path: return
        from reportlab.platypus import SimpleDocTemplate, Table as PDFTable, TableStyle
        from reportlab.lib import colors
        doc=SimpleDocTemplate(path)
        data=[self.df.columns.to_list()]+self.df.values.tolist()
        table=PDFTable(data)
        table.setStyle(TableStyle([
            ('BACKGROUND',(0,0),(-1,0),colors.gray),
            ('TEXTCOLOR',(0,0),(-1,0),colors.whitesmoke),
            ('GRID',(0,0),(-1,-1),1,colors.black)
        ]))
        doc.build([table])
        print("PDF exported to",path)

# -----------------------------
# RUN APP
# -----------------------------
if __name__=="__main__":
    app=QApplication(sys.argv)
    window=SchedulerUI()
    window.show()
    sys.exit(app.exec())
