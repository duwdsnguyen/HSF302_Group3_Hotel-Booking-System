# HSF302_Group3_Hotel-Booking-System
<h2>Project Overview</h2>
Hotel Booking System is a web application that allows guests to search available rooms, make reservations online, and enables receptionists to manage check-in and check-out operations.


<h3>Technology Stack / Languages and Tools:</h3><br>
- Java 21 <a href="https://www.java.com" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" alt="java" width="40" height="40"/></a><br><br>
- Spring Boot, Spring MVC, Thymeleaf <a href="https://spring.io/" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/springio/springio-icon.svg" alt="spring" width="40" height="40"/></a><br><br>
- Spring Data JPA, Microsoft SQL Server <a href="https://www.microsoft.com/en-us/sql-server" target="_blank" rel="noreferrer"> <img src="https://www.svgrepo.com/show/303229/microsoft-sql-server-logo.svg" alt="mssql" width="40" height="40"/></a><br><br>
- HTML, CSS, JAVASCRIPT(optional) <a href="https://www.w3.org/html/" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/html5/html5-original-wordmark.svg" alt="html5" width="40" height="40"/> </a> <a href="https://www.w3schools.com/css/" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/css3/css3-original-wordmark.svg" alt="css3" width="40" height="40"/> </a> <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/javascript/javascript-original.svg" alt="javascript" width="40" height="40"/> </a> <br><br>
- Git & GitHub  <a href="https://git-scm.com/" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/git-scm/git-scm-icon.svg" alt="git" width="40" height="40"/> </a>

<h3>Build Tool</h3>
Maven


<h3>Project Structure</h3>

```
src/main/java/hsf/g3/hotel_booking_system
│
├── config
├── controller
├── service
├── repository
├── entity
├── dto
├── exception
└── util
src/main/resources
│
├── templates
├── static
└── application.properties
docs
├── database
├── diagrams
└── requirements
```
<h3>For Contributors, database initialize:</h3><br>
<p>open CMD or PowerShell:</p><br>

```
setx DB_URL "jdbc:sqlserver://localhost:1433;databaseName=hotel_booking_system;encrypt=true;trustServerCertificate=true"
setx DB_USERNAME "your_username"
setx DB_PASSWORD "your_password"
```
reopen and check:

```
echo %DB_URL%
echo %DB_USERNAME%
echo %DB_PASSWORD%
```


<p>open Microsoft SQL Server Managment Studio:</p><br>

```
CREATE DATABASE hotel_booking_system;
```

<p>open Itellij:</p>
<p>run file: DatabaseInitializerApplication.java first</p>

| Member          | Module                           | Branch                                                                |
| ----------------| -------------------------------- | --------------------------------------------------------------------- |
| duwdsnguyen     | User/Auth Management             | `feature/auth`                                                        |
| Member 2        | Room Management/Search           | `feature/admin-room-management` + `feature/guest-room-search`         |
| Member 3        | Booking Management               | `feature/guest-booking` + `feature/receptionist-booking`              |
| Member 4        | Check-in/Check-out + Room Change | `feature/receptionist-checkin-checkout` + `feature/guest-room-change` |
| Member 5        | Service + Report                 | `feature/admin-service-management` + `feature/admin-report`           |
