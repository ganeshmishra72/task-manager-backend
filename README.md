
🚀 Task Manager Backend API

A scalable and secure Task Management Backend API built using Spring Boot, implementing JWT Authentication, Role-Based Access Control, and RESTful API design principles.

This backend powers a full-stack application where users can securely manage tasks with authentication and authorization.

---

🌐 Live API

🔗 Backend Base URL(Render): https://backend-tms-z5n6.onrender.com

🔗 Live  URL: https://rdtms.vercel.app



🌐 Frontend GetHub Repo

🔗  URL: https://github.com/ganeshmishra72/task-manager-frontend.git

---

🛠 Tech Stack

- ☕ Java 17
- 🌱 Spring Boot
- 🔐 Spring Security + JWT
- 🗄 MySQL / PostgreSQL
- 📄 Swagger (API Documentation)
- 🐳 Docker (Containerization)

---

✨ Features

🔐 Authentication & Authorization

- User Registration
- User Login with JWT
- Password hashing using BCrypt
- Role-Based Access:
  - USER
  - ADMIN

---

📦 Task Management (CRUD APIs)

- Create Task
- Get Tasks (User-specific / Admin-all)
- Update Task
- Delete Task

---

⚙️ API Capabilities

- RESTful API design
- API versioning ("/api/v1")
- Pagination support
- Filtering (status-based)
- Global Exception Handling
- Request validation

---

📁 Project Structure

src/main/java/com/project

 ├── config        # Security & JWT config
 
 ├── controller    # REST Controllers
 
 ├── service       # Business logic
 
 ├── repository    # JPA repositories
 
 ├── entity        # Database entities
 
 ├── dto           # Request/Response DTOs
 
 ├── exception     # Global exception handling
 
 └── util          # Utility classes

---

🔗 API Endpoints

🔐 Auth APIs

POST   /api/v1/auth/register
POST   /api/v1/auth/login

---

📦 Task APIs

GET    /api/v1/tasks
GET    /api/v1/tasks/{id}
POST   /api/v1/tasks
PUT    /api/v1/tasks/{id}
DELETE /api/v1/tasks/{id}

---

🔐 Authentication Header

Authorization: Bearer <JWT_TOKEN>

---

🗄 Database Schema

👤 User

- id
- name
- email
- password
- role

📝 Task

- id
- title
- description
- status
- user_id (FK)

---

⚙️ Setup Instructions

1️⃣ Clone Repository

git clone https://github.com/ganeshmishra72/task-manager-backend.git
cd task-manager-backend

---

2️⃣ Configure Environment

Create "application.properties":

spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
spring.datasource.username=your_username
spring.datasource.password=your_password

jwt.secret=your_secret

---

3️⃣ Build Project

mvn clean install

---

4️⃣ Run Application

mvn spring-boot:run

---

🐳 Docker Setup

Build Image

docker build -t task-backend .

Run Container

docker run -p 8080:8080 task-backend

---

📄 API Documentation

Swagger UI available at:

http://localhost:8080/swagger-ui/index.html

---

🔐 Security Practices

- JWT-based authentication
- Password hashing (BCrypt)
- Role-based authorization
- Input validation
- CORS configuration

---

🚀 Deployment

- Backend deployed using Docker + Render
- Database hosted on Neon (PostgreSQL)

---

🧠 Scalability Note

This project is designed with scalability in mind:

- Can be split into microservices (Auth + Task Service)
- Supports caching using Redis (future scope)
- Load balancing via reverse proxy (Nginx)
- Containerized deployment using Docker

---

📸 Screenshots

Add Swagger / API screenshots here

---

👨‍💻 Author

Ganesh Mishra

- GitHub: https://github.com/ganeshmishra72

---

💥 Final Note

This project demonstrates:

- Clean backend architecture
- Secure authentication & authorization
- Scalable REST API design
- Production-ready deployment using Docker

---

⭐ If you like this project, give it a star!
