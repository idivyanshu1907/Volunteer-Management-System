<<<<<<< HEAD
# PIET Volunteer Management System — Fresh Build

A new Java 21 desktop application for college volunteer/event workflows. This project is built from scratch with Java Swing, JDBC and MySQL; it does not reuse the earlier ZIP's source code.

## Features

- **Student:** browse approved upcoming events, apply once, see registration status, and view a Student Volunteer Passport with attendance and verified hours.
- **Host / Club Captain:** create event proposals, assign an HOD, edit proposals (any edit resets approval to `PENDING`), review applications, accept/reject volunteers, cancel approved events, and record attendance for accepted volunteers only.
- **HOD:** view assigned proposals, approve/reject/request changes, and see all proposals with decision feedback.
- **Proctor:** view attendance records in read-only mode.
- Registration closes when the number of submitted applications reaches the event's volunteer requirement. Duplicate applications are prevented by a database unique constraint.
- Accepted volunteer count cannot exceed the event requirement. Only accepted volunteers can have attendance records.
- Passwords are salted PBKDF2-HMAC-SHA256 hashes. SQL operations use prepared statements. Important actions are written to `audit_logs`.
- On first launch, the app creates the database schema and demo accounts if the `users` table is empty.

## Requirements

- Windows 10/11 (or another OS that supports Java Swing)
- JDK 21
- MySQL Server 8.0+ (MySQL 8.4 recommended)
- Maven 3.9+

## Setup

1. Start MySQL Server.
2. Open a terminal in this project folder (`VolunteerManagementSystem`).
3. Configure the database credentials. In Windows PowerShell, for example:

   ```powershell
   $env:VMS_DB_USER = "root"
   $env:VMS_DB_PASSWORD = "YOUR_MYSQL_PASSWORD"
   ```

   Optional: set `$env:VMS_DB_URL` if your MySQL host/port differs. The default URL is `jdbc:mysql://localhost:3306/volunteer_management_vms?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata`.

4. Run:

   ```powershell
   mvn clean test
   mvn exec:java
   ```

   The first launch creates `volunteer_management_vms`, its tables and demo accounts. The database user needs permission to create the database on first run. If your MySQL user cannot create databases, create `volunteer_management_vms` manually, then launch again. This build uses a separate database name so older `volunteer_management` tables from the earlier prototype are left untouched and cannot cause incompatible foreign-key errors.

## Demo logins

All demo accounts use the password `Welcome123!`. Select the matching role at login.

| Role | Email | Demo name |
|---|---|---|
| Student | `student@piet.local` | Rahul Sharma |
| Student | `student2@piet.local` | Aarav Singh |
| Host / Club Captain | `host@piet.local` | Kavya Verma |
| HOD | `hod@piet.local` | Dr. Anil Sharma |
| Proctor | `proctor@piet.local` | Priya Mehta |

Demo users are created only when there are no users in the database. If you already have data in the database, the app does not overwrite it.

## Project layout

```text
VolunteerManagementSystem/
├── pom.xml
├── README.md
├── src/main/java/com/piet/vms/
│   ├── Main.java
│   ├── config/       # JDBC connection and first-run schema/demo initialization
│   ├── model/        # User and role models
│   ├── security/     # PBKDF2 password hashing/verification
│   ├── service/      # Authentication and application workflows
│   └── ui/           # Login, role dashboards, shared Swing theme
├── src/main/resources/db/schema.sql
└── src/test/java/com/piet/vms/
```

## Notes / known scope

- This is a local desktop application, not a hosted web service.
- Verified volunteer hours currently use a simple rule of **2 hours per event marked Present**. Change the policy in the service layer if the college defines a different duration.
- The app creates schema tables automatically; database backup and user account administration should be configured before production use.
- Do not commit local database passwords to source control.
=======
# Volunteer-Management-System
A Java-based Volunteer Management System designed for college campuses to streamline event creation, approval, volunteer registration, and attendance tracking. The platform provides role-based access for Students, Hosts (Club Captains), HODs, and Proctors, making volunteer coordination and event management more efficient and organized.
>>>>>>> 93f051f21a458d416d19737120525939ad355cae
