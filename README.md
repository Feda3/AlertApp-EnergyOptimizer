# AlertApp - Dynamic Energy Tariff Optimizer

> **Disclaimer regarding commit history:** This repository was initialized and the codebase was uploaded as a single consolidated commit. However, this software is the result of my Bachelor's thesis project, which was actively researched, designed, and developed over a span of several months and finalized earlier this year. It is published here to serve as a portfolio piece for software engineering roles.

## Project Overview
AlertApp is a full-stack web platform designed to help residential consumers automatically adapt to the volatility of dynamic energy tariffs. The system acts as an intelligent middleware between the ENTSO-E transparency platform (Day-Ahead Market) and the end-user.

Instead of manually analyzing daily XML market documents, users can set their comfort rules and technical constraints (e.g., minimum duration for running appliances, desired timeframes, and price thresholds). The underlying algorithmic engine fetches the stock data, processes it against user logic, and generates actionable usage signals.

## Tech Stack & Architecture
This project was built utilizing a robust Client-Server architecture, bootstrapped via **JHipster 8.11.0**, ensuring a clean separation of concerns:

*   **Backend:** Java, Spring Boot, Spring Data JPA, Hibernate.
*   **Frontend:** Angular, TypeScript, Bootstrap 5.
*   **Database:** MySQL (Production) / H2 (Development) managed via Liquibase.
*   **Security:** JSON Web Tokens (JWT) authentication, Role-Based Access Control (RBAC), and strict row-level multi-tenancy.

## ⚙️ Key Features
*   **Automated Data Ingestion:** Scheduled JVM Cron Jobs fetch Day-Ahead Market prices via the ENTSO-E API.
*   **XML Sanitization & Parsing:** Extracts relevant price/time nodes and persists them to the local database to reduce redundant network calls.
*   **Algorithmic Processing Engine:** Evaluates daily market data against specific user settings (Price thresholds, Temporal filters, Consecutive block validation).
*   **Inbox & Notifications:** Generates `UserSignals` delivering actionable intelligence (e.g., "CONSUME", "INFO") straight to the user's dashboard.
*   **Automated Email Verification:** Integrated SMTP service with Thymeleaf templating for secure user registration and account activation.

---

## Local Development & Setup

This application was generated using JHipster. Node is required for frontend generation, and Maven is used for the Java backend.

### Running the Application
To create a blissful development experience where your browser auto-refreshes when files change on your hard drive, run the following commands in two separate terminals:

    ./mvnw

    ./npmw start

### Building for Production
To build the final jar and optimize the alertApp application for production, run:

    ./mvnw -Pprod clean verify

To ensure everything worked, run the generated executable:

    java -jar target/*.jar

Then navigate to `http://localhost:8080` in your browser.

### Docker Support
You can start the required third-party services (like the database) in Docker containers by running:

    docker compose -f src/main/docker/services.yml up -d

---

> **Financial Disclaimer:** This application was developed as a Proof of Concept (PoC) for a university thesis. While the ENTSO-E data fetched is accurate, the generated signals serve an educational and orientational purpose. The platform does not constitute authorized financial advice. Real-world trading decisions or the adoption of dynamic tariff contracts carry financial risks and should be approached with a thorough understanding of energy markets.
