# Waste Routing System

A waste management system with React frontend and Spring Boot backend, using Firebase as the database.

## Project Structure

```
waste-routing/
├── frontend/          # React frontend (handled separately)
└── backend/           # Spring Boot backend (this project)
    ├── src/
    │   ├── main/
    │   │   ├── java/com/wasterouting/
    │   │   │   ├── config/      # Configuration classes
    │   │   │   ├── controller/  # REST controllers
    │   │   │   ├── dto/         # Data Transfer Objects
    │   │   │   ├── exception/   # Exception handling
    │   │   │   ├── model/       # Entity classes
    │   │   │   ├── repository/  # Data access layer
    │   │   │   ├── security/    # Security configurations
    │   │   │   └── service/     # Business logic
    │   │   └── resources/
    │   │       └── application.properties
    │   └── test/                # Test files
    └── pom.xml
```

## Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- Firebase service account JSON file
- Node.js and npm (for frontend)

## Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd waste-routing
   ```

2. **Backend Setup**
   ```bash
   cd backend
   ```
   - Place your Firebase service account JSON file in `src/main/resources/`
   - Update `application.properties` with your Firebase configuration
   - Build and run the application:
     ```bash
     mvn clean install
     mvn spring-boot:run
     ```
   - The backend will be available at `http://localhost:8080`

3. **Frontend Setup**
   ```bash
   cd frontend
   npm install
   npm start
   ```
   - The frontend will be available at `http://localhost:3000`

## API Documentation

- Health Check: `GET /api/health`
- (More endpoints will be added as they are developed)

## Development Workflow

1. **Branching Strategy**
   - `main` - Production-ready code
   - `develop` - Integration branch for features
   - `feature/` - Feature branches (e.g., `feature/user-authentication`)

2. **Commit Message Convention**
   - `feat:` New feature
   - `fix:` Bug fix
   - `docs:` Documentation changes
   - `style:` Code style changes (formatting, etc.)
   - `refactor:` Code refactoring
   - `test:` Adding or modifying tests
   - `chore:` Changes to build process or auxiliary tools

3. **Pull Requests**
   - Create a PR from your feature branch to `develop`
   - Request reviews from at least one team member
   - All tests must pass before merging
   - Delete the feature branch after merging

## Firebase Setup

1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Go to Project Settings > Service Accounts
3. Generate a new private key and save it as `firebase-service-account.json` in `src/main/resources/`
4. Update the Firebase configuration in `application.properties`

## License

[Add your license information here]
