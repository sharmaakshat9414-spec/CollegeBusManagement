# Database Setup (beginner friendly)

## 1. Open MySQL
- **Workbench:** open MySQL Workbench and double-click your local connection (enter the root password).
- **Command line:** run `mysql -u root -p` and enter the password.

## 2. Open `college_bus_db.sql`
- **Workbench:** *File > Open SQL Script...* and choose `database/college_bus_db.sql` from the project folder.
- **Command line:** no need to open it; you will run it directly in step 3.

## 3. Run it
- **Workbench:** click the lightning-bolt icon (Execute). Wait until all statements finish with no red errors.
- **Command line** (from the project folder):
  ```
  mysql -u root -p < database/college_bus_db.sql
  ```
The script creates the database, tables, relationships, the default admin and the sample data.
Running it again resets everything to the original sample data.

## 4. Verify the database
```sql
SHOW DATABASES;          -- college_bus_db must be listed
USE college_bus_db;
```

## 5. Verify the tables
```sql
SHOW TABLES;
```
Expected: `buses`, `bus_assignments`, `drivers`, `notices`, `routes`, `student_bus_allocations`, `students`, `users`.

Quick data check:
```sql
SELECT username, role FROM users WHERE role = 'ADMIN';   -- admin
SELECT COUNT(*) FROM buses;                               -- 6
SELECT COUNT(*) FROM students;                            -- 10
```

## 6. Configure `DBConnection.java`
Open `src/main/java/com/piet/busmanagement/config/DBConnection.java` and set `USERNAME` and `PASSWORD`
to your MySQL login (HOST `localhost`, PORT `3306`, DATABASE `college_bus_db` are already correct).

## 7. Run the project
Open `Main.java` in IntelliJ IDEA and press **Run**. Log in with role **ADMIN**, username `admin`, password `admin123`.

### Tables at a glance
| Table | Purpose |
|-------|---------|
| users | Login accounts (admin, students, drivers); passwords stored as SHA-256 hashes |
| students | Student records, linked 1-to-1 with a user account |
| drivers | Driver records, linked 1-to-1 with a user account |
| buses | Bus details and status |
| routes | Route, stops, distance, time, departure |
| bus_assignments | Bus + Driver + Route |
| student_bus_allocations | Student -> bus, route, pickup stop (one per student) |
| notices | Announcements |
