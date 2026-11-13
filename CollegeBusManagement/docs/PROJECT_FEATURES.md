# Project Features

## Admin features
- Login with the default admin account; change password from Settings
- Dashboard: live counts (buses, students, drivers, routes, active assignments), recent registrations,
  recent assignments, bus status summary, important notices
- Sidebar: Dashboard, Buses, Students, Drivers, Routes, Bus Assignments, Student Allocation, Notices, Settings, Logout
- Settings: Change Password, About Project

## Student features
- Self sign-up with full validation; log in with registration number or email
- Dashboard (read only): profile, assigned bus, bus number, route and stops, driver, pickup stop, schedule, notices
- Change password and logout; cannot modify any data

## Driver features
- Account created and managed by the admin (add, edit, delete, activate, deactivate)
- Dashboard (read only): driver name and ID, assigned bus, route, stops, schedule, assigned student count, notices

## Bus management
Add / edit / delete buses (number, registration, type, capacity, status ACTIVE / INACTIVE / MAINTENANCE),
search, status filter, refresh. The driver and route columns come from the bus's active assignment.
A bus cannot be deleted while assignments or student allocations use it.

## Route management
Route name, start, destination, stops, distance, estimated time, departure time, status. Search and filter.

## Assignment management
- **Bus Assignment:** bus + driver + route + date + status. Only ACTIVE buses, drivers and routes can be
  actively assigned; a bus or a driver can have only one active assignment.
- **Student Allocation:** student -> bus, route and pickup stop (chosen from the route's stops).
  One allocation per student; bus capacity is enforced.

## Notice management
Add, edit, delete and view notices (title, description, date, status). ACTIVE notices appear on the
admin, student and driver dashboards.

## Database
MySQL database `college_bus_db` with 8 tables, primary keys, foreign keys (RESTRICT for in-use buses,
drivers and routes; CASCADE for account/allocation clean-up), UNIQUE constraints and realistic sample data.
All SQL lives in the `dao` package and uses `PreparedStatement`.

## Security / validation
- Passwords stored as SHA-256 hashes; role must match at login; deactivated accounts cannot log in
- Required-field, email, 10-digit phone, numeric, capacity (1-100), date and HH:mm time validation
- Duplicate checks (registration number, email, license number, bus number, route name)
- Password confirmation and minimum length
- Delete confirmation ("Are you sure you want to delete this record?") and friendly database error messages
