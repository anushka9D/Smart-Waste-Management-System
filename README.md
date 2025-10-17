# Smart Waste Management System

The Smart Waste Management System (SWMS) is an integrated solution designed to address the growing challenges of urban waste management. This system combines modern IoT sensors, mobile applications, and web platforms to streamline waste management operations and improve urban cleanliness.

## Key Features

- **Smart Monitoring**: Real-time bin level tracking with IoT sensors
- **Route Optimization**: Efficient collection schedules and routes
- **Citizen Interaction**: Report bins and request services easily
- **Data Analytics**: Insights for informed decision making

##  System Architecture

### Backend
- **Framework**: Spring Boot 3.5.6
- **Language**: Java 21
- **Database**: MongoDB
- **Security**: Spring Security with JWT Authentication
- **API Documentation**: Built-in RESTful APIs

### Frontend
- **Framework**: React 19 with Vite
- **Styling**: TailwindCSS
- **Routing**: React Router v7
- **Maps**: Leaflet for geolocation services
- **Charts**: Chart.js for data visualization

## Getting Started

### Prerequisites
- Java 21
- Node.js 16+
- MongoDB
- Maven 3.8+

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Install dependencies and build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
   
   The backend server will start on port `8081`.

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```
   
   The frontend server will start on port `5174` (if 5173 is occupied).

## Project Structure

```
Smart-Waste-Management-System/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/swms/
│   │   │   │   ├── controller/
│   │   │   │   ├── model/
│   │   │   │   ├── service/
│   │   │   │   ├── dto/
│   │   │   │   └── config/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
└── frontend/
    ├── src/
    │   ├── components/
    │   ├── context/
    │   ├── pages/
    │   ├── services/
    │   ├── utils/
    │   ├── App.jsx
    │   └── main.jsx
    ├── package.json
    └── vite.config.js
```

## User Roles

1. **Citizen**: Report waste bins, track requests, provide feedback
2. **City Authority**: Manage bins, view analytics, optimize routes
3. **Driver**: View assigned routes, update collection status
4. **Waste Collection Staff**: Manage waste collection operations
5. **Sensor Manager**: Monitor IoT sensor data

## Testing

### Backend Testing
The project uses JUnit for testing with a minimum code coverage requirement of 80%. Tests are organized by package and functionality.

Run tests with:
```bash
cd backend
./mvnw test
```

### Frontend Testing
Frontend component testing can be added using React Testing Library.

## API Documentation

The backend provides RESTful APIs for all system functionality. API endpoints are organized by user roles:
- Citizen APIs: `/api/citizen/**`
- City Authority APIs: `/api/city-authority/**`
- Driver APIs: `/api/driver/**`

## Development Tools

- **Backend**: Spring Boot with Maven
- **Frontend**: React with Vite
- **Code Quality**: JaCoCo for code coverage, ESLint for frontend code quality
- **Version Control**: Git

## Dependencies

### Backend Key Dependencies
- Spring Boot Web
- Spring Security
- Spring Data MongoDB
- JWT for authentication
- Lombok for boilerplate reduction
- Cloudinary for image management

### Frontend Key Dependencies
- React and React DOM
- React Router for navigation
- TailwindCSS for styling
- Leaflet for maps
- Chart.js for data visualization
- Axios for HTTP requests