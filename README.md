# JavaFX Database Management System

## Overview

This project is a **JavaFX-based Database Management System** designed to handle user records with features like authentication, CRUD operations (Create, Read, Update, Delete), and file uploads to Azure Blob Storage. The application integrates a responsive GUI with database interaction using MySQL and Azure Storage.

---

## Features

1. **Authentication and User Management**
   - Login and signup system with session management.
   - Real-time session tracking using the `UserSession` class.

2. **Database Integration**
   - Automatic database creation (if it doesn't exist).
   - Supports CRUD operations on user records.

3. **File Upload**
   - Upload and manage files using Azure Blob Storage.

4. **Data Export and Import**
   - Import user data from CSV files.
   - Export data to CSV files for sharing or backup.

5. **Report Generation**
   - Generate PDF reports summarizing user data by department or major.

6. **Graphical User Interface**
   - Responsive and interactive UI with features like editable tables and data validation.
   - Splash screen and transition effects.

7. **Theme Management**
   - Switch between light and dark themes.

8. **Error Logging**
   - Integrated error and event logging via `MyLogger`.

---

## Project Structure

- **Main Application**
  - `MainApplication.java`: Entry point for the application.
- **Controllers**
  - `DB_GUI_Controller.java`: Manages user data operations.
  - `LoginController.java`: Handles user login.
  - `SignUpController.java`: Manages new user registration.
  - `SpalshScreenController.java`: Controls the splash screen.
- **Database Connectivity**
  - `DbConnectivityClass.java`: Handles database interaction using JDBC.
- **Models**
  - `Person.java`: Represents a user entity in the system.
- **Services**
  - `UserSession.java`: Manages the active user session.
  - `StorageUploader.java`: Manages file uploads to Azure Blob Storage.
  - `MyLogger.java`: Logs important application events and errors.

---

## Setup Instructions

### Prerequisites

1. **Java Development Kit (JDK)**: Version 11 or higher.
2. **MySQL Server**: Ensure MySQL is installed and running.
3. **Azure Account**: Access to an Azure Blob Storage account.
4. **IDE**: IntelliJ IDEA or any IDE that supports JavaFX.

### Database Configuration

Update `DbConnectivityClass.java` with your database credentials:
```java
final static String SQL_SERVER_URL = "jdbc:mysql://<your-server-name>";
final static String DB_URL = SQL_SERVER_URL + "/<database-name>";
final static String USERNAME = "<your-database-username>";
final static String PASSWORD = "<your-database-password>";
```

### Azure Blob Storage Configuration

Update `StorageUploader.java` with your Azure connection string:
```java
.connectionString("<your-azure-connection-string>")
.containerName("<your-container-name>");
```

### Running the Application

1. Clone the repository and open it in your IDE.
2. Configure the project to use JavaFX SDK.
3. Build and run the `MainApplication.java` file.
4. Follow the splash screen, and proceed to login or signup.

---

## How to Use

### Login
1. Enter your username and password on the login screen.
2. If you don’t have an account, click "Sign Up" to create one.

### Managing Records
1. Use the GUI to view, add, edit, or delete user records.
2. Click on the "Upload" button to upload images to Azure Blob Storage.

### Import/Export CSV
1. Use the menu options to import or export user data in CSV format.

### Generate Reports
1. Generate a PDF report summarizing user statistics using the "Generate Report" menu option.

### Changing Themes
1. Toggle between light and dark themes from the menu bar.

---

## Known Issues

- Ensure correct database credentials are provided in the configuration.
- Azure Blob Storage operations require valid connection credentials.

---

## Future Improvements

- Enhance security by hashing passwords.
- Improve error handling for file uploads and database operations.
- Implement user roles with different access levels.

---

## License

This project is open-source and available under the MIT License.
