# APU Assessment Feedback System (AFS)

> A multi-role Java desktop application for managing users, classes, grading systems, and assessment feedback at Asia Pacific University.

![Java](https://img.shields.io/badge/Language-Java-ED8B00?logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/UI-Swing-007396)
![License](https://img.shields.io/badge/license-Academic-blue)

---

## Overview

The **Assessment Feedback System (AFS)** is a Java Swing desktop application that simulates an academic administration platform. It supports multiple user roles (admin, lecturer, student), provides comprehensive user management, class scheduling, a configurable grading system, and feedback collection. The system uses **file-based persistence** (no database) and is built around clean OOP principles, full UML modelling, and form-level validation.

---

## Module / Course

- **Module Code:** OODJ (05-2 2511)
- **Course:** Object-Oriented Development with Java
- **Institution:** Asia Pacific University
- **Project Type:** Group Assignment

---

## Key Features

### User Management
- CRUD operations on users (admins, lecturers, students)
- Real-time search and multi-filter (Role, Gender)
- Custom rounded-button table renderers
- 11-point validation: email format, Malaysian phone number (`01x-xxxxxxxx`), age (13–120), and more
- Password reset with checkbox toggle

### Class Management
- Class scheduling with predefined time slots (08:30–21:00)
- Multi-filter (Module, Mode, Weekday)
- Auto-populated weekday from date selection
- Time-order validation (start < end)

### Grading System
- Tiered grade table (Grade, GPA, Classification, Min/Max marks)
- Add / edit / delete grade tiers
- Overlap detection between mark ranges
- Increment / decrement controls for mark boundaries

### Dashboard
- Custom background image panel
- Sidebar navigation across modules
- Role/username top bar

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| UI | Swing (custom renderers, panels) |
| Persistence | File-based (text files) |
| Modelling | UML — Use Case + Class Diagrams |
| IDE | NetBeans / IntelliJ IDEA |

---

## Architecture

```
AFS/
├── Models/             # Domain classes (User, Class, Grade, …)
├── UI/                 # Swing dialogs and panels
│   ├── DefaultDashboardUI.java
│   ├── userListUI.java / userCreateUI.java / userDetailsEditUI.java
│   ├── classListUI.java / classCreateUI.java / classEditUI.java
│   └── gradingsystemUI.java
├── Controllers/        # Business logic & validation
├── Utils/              # File I/O, helpers
└── data/               # Persistent text files
```

UML deliverables:
- `UCD.drawio` — full use-case diagram
- `Admin-UCD.drawio` — admin-specific use-case diagram
- `CD.svg` — full class diagram

---

## Getting Started

```bash
# 1. Open in IntelliJ IDEA / NetBeans / Eclipse

# 2. Build
javac -d out src/**/*.java

# 3. Run
java -cp out Main
```

---

## Screenshots

> _Add screenshots of the dashboard, user list, class scheduling, and grading system._

---

## Documentation

- `Group_X_OODJ_Documentation.pdf` — full system documentation
- `SYSTEM_DOCUMENTATION.md` — module-by-module breakdown with line references
- `UCD.drawio` / `Admin-UCD.drawio` — use case diagrams
- `CD.svg` — class diagram

---

## License

Academic project. Source provided for portfolio reference; not for commercial reuse.
