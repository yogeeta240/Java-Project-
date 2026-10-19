# PlacementAI: AI-Powered Placement Management System

An enterprise-grade campus recruitment and placement automation platform engineered with **Java 21**, **Spring Boot 3**, **MongoDB Atlas**, and **Thymeleaf**. PlacementAI bridges candidates, corporate recruiters, and university placement cells with intelligent skill-match algorithms, interactive candidate portfolios, real-time application pipelines, and automated institutional analytics.

---

## Key Features

### 1. Student Portal
- **AI Recommendation Engine**: Computes personalized match scores (%) between candidate skill vectors and corporate job requirements.
- **Drive Discovery & Search**: Filter active drives by department eligibility, minimum CGPA, compensation package, and role.
- **One-Click Application Flow**: Submit verified credentials directly to corporate hiring pipelines.
- **Academic Portfolio Management**: Add and manage verified coursework projects, tech stacks, and GitHub repositories with real-time modal interactivity.
- **Interview Feedback Repository**: Submit confidential interview experiences and ratings for peer and institutional review.

### 2. Recruiter Portal
- **Drive Publishing**: Create and broadcast recruitment drives specifying allowed academic branches, eligibility cutoffs, and package CTCs.
- **Interactive Screening Board**: Review applicants, filter candidates by department/status, and execute real-time candidate actions (*Shortlist*, *Schedule Interview*, *Reject*).
- **Interview Slot Scheduler**: Dispatch structured interview invitations with integrated meeting URLs (Google Meet / Microsoft Teams).
- **Candidate Evaluation**: Log technical and behavioral assessment feedback directly into candidate profiles.

### 3. Institutional Administrator Portal
- **Campus-Wide Monitoring**: Centralized visibility into all university candidate submissions across academic departments.
- **Dual-Mode Filtering**: Instant real-time client-side DOM filtering combined with server-side query parameter persistence for fast, uninterrupted monitoring.
- **Corporate Partner Verification**: Approve or suspend corporate recruiting access with verified partner clearance badges.
- **Accreditation Analytics**: Track university placement percentages, median compensation, and department-wise placement metrics compliant with NIRF and NAAC accreditation standards.

### 4. Developer Experience & Universal Portal Switcher
- **Floating Persona Switcher**: Seamlessly switch between **Student**, **Recruiter**, and **Admin** portals during live demonstrations without re-authenticating.
- **Cache-Busted Assets**: Automatic versioning on static assets to prevent browser caching discrepancies during development.
- **Zero Heavy Framework Dependencies**: Pure Vanilla ES6+ JavaScript and custom Vanilla CSS design tokens with zero bloated external UI frameworks.

---

## Architecture & Technology Stack

```
+-------------------------------------------------------------+
|                      Client Layer                           |
|       Thymeleaf 3 + Vanilla HTML5 / CSS3 / ES6+ JS          |
+-------------------------------------------------------------+
                              | HTTP / REST
+-------------------------------------------------------------+
|                   Application Controllers                   |
|     AdminController | RecruiterController | StudentController|
+-------------------------------------------------------------+
                              |
+-------------------------------------------------------------+
|                      Service Layer                          |
|         AiRecommendationService (Cosine Skill Matching)     |
+-------------------------------------------------------------+
                              |
+-------------------------------------------------------------+
|                   Spring Data Repositories                  |
|  User | StudentProfile | JobPosting | Application | Feedback|
+-------------------------------------------------------------+
                              | Mongo Driver
+-------------------------------------------------------------+
|                      Persistence Layer                      |
|                 MongoDB / MongoDB Atlas Cluster             |
+-------------------------------------------------------------+
```

| Layer | Technologies |
|---|---|
| **Language** | Java 21 (LTS) |
| **Framework** | Spring Boot 3.4.2 (Spring MVC, Spring Data MongoDB, Validation, Security Crypto) |
| **Database** | MongoDB Atlas (Cloud NoSQL Document Database) |
| **Frontend** | Thymeleaf Template Engine, Custom CSS3 Design System, Vanilla ES6+ JavaScript |
| **Build & Tooling** | Apache Maven 3.9+, Spring Boot DevTools |

---

## Getting Started

### Prerequisites
- **Java Development Kit (JDK)**: Version 21 or higher
- **Maven**: Version 3.8+ (or use the included `mvnw` wrapper)
- **MongoDB**: Local MongoDB instance (port `27017`) or a free [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) cluster connection string

---

### Installation & Setup

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/placement-ai.git
   cd placement-ai
   ```

2. **Configure Database Connection**:
   Edit `src/main/resources/application.properties` or set the `MONGODB_URI` environment variable:
   ```properties
   spring.data.mongodb.uri=mongodb+srv://<username>:<password>@cluster0.mongodb.net/placement_ai?retryWrites=true&w=majority
   spring.data.mongodb.auto-index-creation=true
   server.port=8080
   spring.thymeleaf.cache=false
   ```

3. **Build the Application**:
   ```bash
   # Windows
   .\mvnw.cmd clean compile

   # Linux / macOS
   ./mvnw clean compile
   ```

4. **Run the Application**:
   ```bash
   # Windows
   .\mvnw.cmd spring-boot:run

   # Linux / macOS
   ./mvnw spring-boot:run
   ```

5. **Access the Portal**:
   Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

---

## Seeded Demo Credentials

The platform automatically seeds demo accounts on the first application startup:

| Portal | Email | Password | Role / Description |
|---|---|---|---|
| **Student** | `aarav@univ.edu` | `password123` | Aarav Sharma (CSE &bull; 8.92 CGPA) |
| **Student** | `priya@univ.edu` | `password123` | Priya Patel (IT &bull; 9.15 CGPA) |
| **Recruiter** | `recruiter@microsoft.com` | `password123` | Sarah Jenkins (Microsoft Talent Team) |
| **Recruiter** | `recruiter@google.com` | `password123` | Amit Verma (Google Campus Lead) |
| **Admin** | `dean@univ.edu` | `password123` | Dr. Rajesh Varma (Dean, Training & Placement) |

> **Tip**: You can also use the floating **Portals** button on the bottom right of any page to switch personas instantly with one click.

---

## Project Structure

```
placement-ai/
├── src/
│   ├── main/
│   │   ├── java/com/placement/
│   │   │   ├── config/
│   │   │   │   └── DataInitializer.java        # Seeds initial demo users, jobs, & applications
│   │   │   ├── controller/
│   │   │   │   ├── AdminController.java        # Institutional console, reports & monitoring
│   │   │   │   ├── AuthController.java         # Authentication & registration endpoints
│   │   │   │   ├── HomeController.java         # Public landing page gateway
│   │   │   │   ├── RecruiterController.java    # Drive posting, candidate review & scheduling
│   │   │   │   └── StudentController.java      # Student dashboard, jobs, profile & projects
│   │   │   ├── model/
│   │   │   │   ├── AcademicProject.java        # Portfolio project entity
│   │   │   │   ├── Application.java            # Placement application lifecycle entity
│   │   │   │   ├── Company.java                # Corporate partner profile
│   │   │   │   ├── Feedback.java               # Interview evaluations & ratings
│   │   │   │   ├── InterviewSlot.java          # Scheduled interview session details
│   │   │   │   ├── JobPosting.java             # Campus recruitment drive posting
│   │   │   │   ├── Role.java                   # Security role enum (STUDENT, RECRUITER, ADMIN)
│   │   │   │   ├── StudentProfile.java         # Academic credentials & verified skills
│   │   │   │   └── User.java                   # Core authentication model
│   │   │   ├── repository/                     # Spring Data MongoDB repository interfaces
│   │   │   ├── service/
│   │   │   │   └── AiRecommendationService.java# Skill matching & recommendation algorithms
│   │   │   └── PlacementAiApplication.java     # Spring Boot application entry point
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/                        # Responsive CSS3 enterprise design tokens
│   │       │   └── js/main.js                  # Client-side filtering, modals & notifications
│   │       ├── templates/                      # Thymeleaf server-rendered view templates
│   │       │   ├── admin/                      # Admin monitoring & analytics templates
│   │       │   ├── auth/                       # Login & registration templates
│   │       │   ├── recruiter/                  # Recruiter candidate board & scheduling
│   │       │   └── student/                    # Student dashboard, profile & jobs
│   │       └── application.properties          # Server port & MongoDB configuration
└── pom.xml                                     # Maven build definition & dependencies
```

---

## AI Skill-Match Algorithm

The `AiRecommendationService` evaluates candidate eligibility and match scores through a multi-factor calculation:
1. **Keyword Normalization**: Tokenizes and cleans both candidate skills and required job technologies.
2. **Jaccard / Overlap Metric**: Calculates direct intersection and weighted coverage of required skills against candidate profile tags.
3. **Academic Threshold Check**: Validates minimum CGPA and active backlog limits.
4. **Composite Score**: Outputs an actionable match score (`0% - 100%`) displayed to students and recruiters for informed shortlisting.

---

