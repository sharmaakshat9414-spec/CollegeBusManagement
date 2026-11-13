# College Bus Tracking & Route Management System

**Poornima Institute of Engineering and Technology (PIET), Jaipur**
Java Swing + MySQL + JDBC desktop application (Maven project, opens directly in IntelliJ IDEA).

---

## 1. Project Overview
A local desktop application that manages a college's transport: buses, routes, drivers, students,
bus assignments, student bus allocations and notices. There are three kinds of users - **Admin**,
**Student** and **Driver** - each with their own dashboard. "Tracking" means *management* of buses,
routes, drivers, students and assignments. There is **no GPS, no Google Maps and no external API**.

## 2. Features
- Modern split-screen login with role selection (ADMIN / STUDENT / DRIVER)
- Student self sign-up (stored permanently in MySQL, duplicate registration number / email blocked)
- Forgot password (verify username + registered email)
- Admin dashboard with live statistics, recent registrations, recent assignments, bus status summary and notices
- Full CRUD with search, filter, refresh: Buses, Students, Drivers, Routes, Bus Assignments, Student Allocation, Notices
- Driver activate / deactivate (deactivated accounts cannot log in)
- Student dashboard (profile, bus, route, driver, pickup stop, notices) - read only
- Driver dashboard (bus, route, stops, schedule, student count, notices) - read only
- Change password, About dialog, Logout on every dashboard
- Validation, friendly error messages, delete confirmation, safe-delete when records are in use

## 3. Technology Stack
| Item | Version |
|------|---------|
| Java | 17 or newer (JDK) |
| UI | Java Swing + FlatLaf 3.5.1 |
| Database | MySQL 5.7 / 8.x |
| JDBC driver | MySQL Connector/J 8.4.0 |
| Build | Maven (bundled with IntelliJ IDEA) |
| Icons | Drawn in code (`IconFactory`) - no external files |

## 4. Folder Structure
```
CollegeBusManagement/
├── pom.xml
├── README.md
├── .gitignore
├── database/college_bus_db.sql
├── docs/ (DATABASE_SETUP.md, PROJECT_FEATURES.md)
└── src/main/
    ├── java/com/piet/busmanagement/
    │   ├── Main.java
    │   ├── config/   DBConnection, UITheme, Session, PasswordUtil, AppException
    │   ├── model/    User, Student, Driver, Bus, Route, BusAssignment, StudentBusAllocation, Notice
    │   ├── dao/      BaseDAO + one DAO per table + DashboardDAO
    │   └── ui/
    │       ├── LoginFrame, StudentSignupFrame, MainDashboardFrame, StudentDashboardFrame, DriverDashboardFrame
    │       ├── components/  ModernButton, SidebarButton, StatCard, ModernTable, FormHelper, IconFactory ...
    │       ├── panels/      CrudPanel + the management panels, DashboardPanel, SettingsPanel
    │       └── dialogs/     BaseFormDialog + all form dialogs, ChangePassword, ForgotPassword, About
    └── resources/icons/README.txt
```

## 5. Requirements
- JDK 17+ (IntelliJ can download one: *File > Project Structure > SDK > Download JDK*)
- MySQL Server 5.7+ or 8.x (and optionally MySQL Workbench)
- IntelliJ IDEA (Community is enough)
- Internet access once, so Maven can download FlatLaf and the MySQL driver

## 6. MySQL Installation
1. Download **MySQL Community Server** from https://dev.mysql.com/downloads/ and install it.
2. During setup, set a password for the `root` user and remember it.
3. Make sure the MySQL service is running (default port **3306**).

## 7. Database Setup  /  8. SQL Import Instructions
Using **MySQL Workbench**: *File > Open SQL Script* > choose `database/college_bus_db.sql` > click the lightning bolt.

Using the **command line**:
```
mysql -u root -p < database/college_bus_db.sql
```
The script creates the database `college_bus_db`, all tables, keys, the default admin and sample data.
Running it again **resets** the database to the sample data. See `docs/DATABASE_SETUP.md` for details.

## 9. DBConnection Configuration
Open `src/main/java/com/piet/busmanagement/config/DBConnection.java` and edit:
```java
public static final String HOST     = "localhost";
public static final String PORT     = "3306";
public static final String DATABASE = "college_bus_db";
public static final String USERNAME = "root";
public static final String PASSWORD = "root";   // <- put YOUR MySQL password here
```
URL used: `jdbc:mysql://localhost:3306/college_bus_db`

## 10. IntelliJ IDEA Setup  /  11. Maven Setup
1. *File > Open* and select the `CollegeBusManagement` folder (the one containing `pom.xml`). Choose **Open as Project**.
2. If asked, click **Trust Project** and **Load Maven Project**. Wait until dependencies download.
3. *File > Project Structure > Project*: set SDK to JDK 17+ and language level 17.
4. If Maven did not load automatically: right-click `pom.xml` > *Maven > Reload project*.

## 12. How to Run
- Open `Main.java` (`com.piet.busmanagement.Main`) and click the green **Run** arrow, **or**
- Terminal: `mvn compile exec:java`, **or**
- Build a runnable jar: `mvn clean package` then `java -jar target/college-bus-management-1.0.0.jar`

If MySQL is not reachable, the app shows a clear message with a **Retry / Exit** choice.

## 13. Default Admin Credentials
| Role | Username | Password |
|------|----------|----------|
| ADMIN | `admin` | `admin123` |

Change it later from **Settings > Change Password**.

Sample accounts (from the SQL file):
- Drivers: `rajesh.sharma@piet.edu`, `mohan.gurjar@piet.edu`, `sunil.yadav@piet.edu`, `bhanwar.shekhawat@piet.edu` - password `driver123`
- Students: `PIET23CS001` ... (or the email) - password `student123`

## 14. Student Registration
On the login screen click **Student Sign Up**, fill the form and press **Create Account**.
Then log in with role **STUDENT**, using your registration number (or email) and password.
The admin can then allocate a bus under *Student Allocation*.

## 15. Driver Login
Admin creates drivers under **Drivers** (email = login username, plus password).
The driver logs in with role **DRIVER** and sees the assigned bus, route, stops, schedule and student count.
A bus's driver and route come from its **active assignment** (*Bus Assignments*).

## 16. Troubleshooting
| Problem | Fix |
|---------|-----|
| "Cannot connect to MySQL" | Start the MySQL service; check username/password in `DBConnection.java` |
| `Unknown database 'college_bus_db'` | Import `database/college_bus_db.sql` |
| `Access denied for user` | Wrong MySQL password in `DBConnection.java` |
| Maven dependencies red in IntelliJ | *Maven > Reload project*, check internet connection |
| `release version 17 not supported` | Set the project SDK to JDK 17 or newer |
| `Public Key Retrieval is not allowed` | Already handled in the JDBC URL (`allowPublicKeyRetrieval=true`) |
| Invalid login credentials | Choose the correct **role** on the login screen |
| "Unable to delete this record because it is currently in use" | Remove the assignments/allocations that use the record first |

## 17. Future Scope
Attendance on buses, fee/transport-charge management, SMS/email alerts, PDF/Excel reports,
role-based sub-admins, and (optionally) GPS tracking in a later version.
