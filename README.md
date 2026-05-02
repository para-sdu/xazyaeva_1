# Charity Platform

A full-featured charity platform connecting donors and people in need. The project is built using **Spring Boot (backend)** and **Vue.js (frontend)** with **Supabase PostgreSQL** and JWT authentication.

---

## 📋 Description

Charity Platform is a web-based system that allows:

* **Donors** to donate to various projects
* **People in need** to create fundraising projects after verification
* **Administrators** to manage users, projects, and categories
* **Volunteers** to participate in events
* **Users** to explore projects via an interactive **city map**

---

## 🚀 Technologies

### Backend

* Spring Boot 3.x
* PostgreSQL (Supabase)
* Spring Security with JWT
* Spring Data JPA / Hibernate
* Maven
* Java 17+
* MapStruct

### Frontend

* Vue.js
* Vite
* JavaScript

---

## 📦 Requirements

* Java 17 or higher
* Maven 3.6+
* Node.js 18+
* PostgreSQL (Supabase)
* Git

---

## 🔧 Installation and Running

### 1) Frontend

```bash
npm install
npm run dev
```

The app will be available at:

```
http://127.0.0.1:5173/
```

---

### 2) Backend

Navigate to backend folder:

```bash
cd "src 2"
```

Run the backend:

```bash
mvn spring-boot:run
```

Backend will run on:

```
http://localhost:8080
```

---


## 📚 API Documentation

### Base URL

```
http://localhost:8080/api/v1
```

---

## 🗺️ Additional Features

* City map integration for displaying projects
* Supabase database support
* Clear architecture separation:

  * `src/` → frontend
  * `src 2/` → backend

---

## 📁 Project Structure

```
project/
├── src/                 # Frontend (Vue)
│   ├── api/
│   ├── assets/
│   ├── components/
│   ├── router/
│   ├── stores/
│   ├── utils/
│   ├── views/
│   ├── App.vue
│   └── main.js
│
├── src 2/              # Backend (Spring Boot)
│   ├── data/
│   ├── main/
│   ├── target/
│   ├── test/
│   ├── mvnw
│   ├── mvnw.cmd
│   └── pom.xml
```

---

## 👥 Team

* Sultangazyyeva Altynay — 230103346
* Kystaubay Ayanat — 230103075
* Kraman Zhaniya — 230103077

---

## 🔒 Security

* JWT authentication
* Spring Security authorization
* Input validation
* File size and type restrictions

---

## 📝 Features

* Automatic donor verification
* Admin verification system
* Project moderation
* Donation statistics
* Volunteer event management
* Map-based project visualization

---

## 📄 License

This project was created for educational purposes.

