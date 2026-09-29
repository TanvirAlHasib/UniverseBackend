# ☕ UniVerse Backend (Spring Boot 4)

The backend service for the **UniVerse** campus platform, built with **Spring Boot 4.1.1**, **Java 25**, **Spring Data JPA**, **Spring Security (JWT)**, and **Spring WebSocket (STOMP)**.

---

## 🛠 Tech Stack & Dependencies

* **Language**: Java 25
* **Framework**: Spring Boot 4.1.1 (`spring-boot-starter-webmvc`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`, `spring-boot-starter-websocket`, `spring-boot-starter-validation`)
* **Database**: MySQL 8.0+
* **JWT**: `io.jsonwebtoken:jjwt-api:0.12.5`
* **Boilerplate**: Project Lombok
* **Build Tool**: Apache Maven (`mvnw` wrapper included)

---

## ⚙️ Configuration (`application.properties`)

```properties
spring.application.name=uniVerse
server.port=8080

# MySQL Database
spring.datasource.url=jdbc:mysql://localhost:3306/universe_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=am134937r
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000 # 24 hours in milliseconds
```

---

## 📡 REST API Reference

All protected endpoints require the HTTP header:
```http
Authorization: Bearer <your-jwt-token>
```

### 1. Authentication (`/api/v1/auth`)
* `POST /api/v1/auth/register` — Public registration
  ```json
  {
    "firstName": "John",
    "lastName": "Doe",
    "email": "johndoe@university.edu",
    "password": "password123",
    "departmentCode": "CSE",
    "role": "STUDENT"
  }
  ```
* `POST /api/v1/auth/login` — Public login (returns Bearer JWT)
  ```json
  {
    "email": "johndoe@university.edu",
    "password": "password123"
  }
  ```

### 2. Campus Feed (`/api/v1/feed`)
* `GET /api/v1/feed?page=0&size=10` — Paginated post feed
* `POST /api/v1/feed` — Create a post
  ```json
  {
    "content": "Does anyone have past papers for CSE301?",
    "imageUrl": "https://example.com/notes.jpg"
  }
  ```
* `GET /api/v1/feed/{id}` — Get single post by ID
* `DELETE /api/v1/feed/{id}` — Delete a post (author only)
* `POST /api/v1/feed/{postId}/like` — Toggle like/unlike
* `GET /api/v1/feed/{postId}/comments` — List comments for a post
* `POST /api/v1/feed/{postId}/comments` — Add comment to post
  ```json
  {
    "content": "Check the department drive, I uploaded them yesterday!"
  }
  ```

### 3. Study Rooms & Real-time Chat (`/api/v1/study-rooms`)
* `GET /api/v1/study-rooms` — List all study rooms
* `POST /api/v1/study-rooms` — Create study room
  ```json
  {
    "title": "Machine Learning Midterm Study",
    "description": "Focusing on supervised learning and gradient descent.",
    "category": "EXAM_PREP",
    "maxMembers": 25
  }
  ```
* `POST /api/v1/study-rooms/{id}/join` — Request to join room
* `GET /api/v1/study-rooms/my-rooms` — Get rooms user has joined
* `GET /api/v1/study-rooms/{id}/messages` — Retrieve historical chat messages

### 4. Housing & Roommate Finder (`/api/v1/housing`)
* `GET /api/v1/housing?type=OFFERING` — List housing (filter: `OFFERING` or `WANTED`)
* `POST /api/v1/housing` — Post housing listing
  ```json
  {
    "type": "OFFERING",
    "title": "Private room with balcony near university gate",
    "location": "Green Road, Dhaka",
    "roomType": "SINGLE",
    "genderPreference": "MALE_ONLY",
    "price": 8500.0,
    "amenities": "WiFi, Generator, AC, Attached Bath"
  }
  ```
* `PATCH /api/v1/housing/{id}/filled` — Mark listing as filled (owner only)

### 5. Campus Marketplace (`/api/v1/marketplace/listings`)
* `GET /api/v1/marketplace/listings?category=BOOKS` — List marketplace items
* `POST /api/v1/marketplace/listings` — List item for sale
  ```json
  {
    "title": "Casio FX-991EX Scientific Calculator",
    "description": "Barely used, in pristine condition.",
    "price": 25.0,
    "category": "ELECTRONICS",
    "condition": "LIKE_NEW",
    "location": "Engineering Building"
  }
  ```
* `PATCH /api/v1/marketplace/listings/{id}/sold` — Mark listing as sold

### 6. Lost & Found (`/api/v1/lost-found`)
* `GET /api/v1/lost-found?type=LOST` — List open items (filter: `LOST` or `FOUND`)
* `POST /api/v1/lost-found` — Report lost or found item
  ```json
  {
    "title": "Blue Dell Laptop Charger",
    "description": "Left behind in Room 402 after 3 PM lecture.",
    "type": "FOUND",
    "location": "Academic Building 1, Room 402",
    "incidentDate": "2026-09-28"
  }
  ```
* `PATCH /api/v1/lost-found/{id}/resolve` — Mark report resolved

---

## ⚡ WebSocket & STOMP Protocol

* **Connection Endpoint**: `ws://localhost:8080/ws`
* **App Prefix**: `/app`
* **Broker Prefix**: `/topic`

### STOMP Handshake & Security:
1. **CONNECT Frame**:
   Include standard authorization header:
   ```stomp
   CONNECT
   Authorization: Bearer <JWT_TOKEN>
   accept-version:1.1,1.0
   heart-beat:10000,10000
   ```
2. **SUBSCRIBE Frame**:
   Subscribe to room messages:
   ```stomp
   SUBSCRIBE
   id:sub-0
   destination:/topic/room/1
   ```
   > **Note**: [`WebSocketAuthInterceptor`](file:///D:/UniVerse/uniVerse/uniVerse/src/main/java/com/amr/uniVerse/config/WebSocketAuthInterceptor.java) enforces that only users with an **`APPROVED`** membership status can subscribe to a room's topic.

3. **SEND Frame**:
   Send message to room:
   ```stomp
   SEND
   destination:/app/chat.send
   content-type:application/json

   {"roomId": 1, "content": "Hello everyone!"}
   ```

---

## 🔨 How to Build and Run

```powershell
# In the uniVerse backend directory
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```
