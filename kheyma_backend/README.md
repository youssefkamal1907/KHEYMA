# Kheyma Backend

A Spring Boot backend application for a camping reservation system in Egypt.

## Features

- User authentication and authorization (JWT-based)
- Location (campsite) management
- Review and rating system
- Transaction/booking management
- Admin panel for user and content management
- MongoDB database integration

## Technology Stack

- Java 17
- Spring Boot 3.2.0
- Spring Security with JWT
- MongoDB
- Maven

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MongoDB (running on localhost:27017)

### Configuration

1. Update `src/main/resources/application.yml` with your MongoDB connection string if needed
2. Set JWT secret in environment variable or update `application.yml`:
   ```yaml
   jwt:
     secret: your-secret-key-here
   ```

### Running the Application

```bash
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

## API Endpoints

### Authentication (`/api/auth`)
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token
- `POST /api/auth/refresh` - Refresh JWT token
- `GET /api/auth/me` - Get current user profile
- `PUT /api/auth/me` - Update current user profile

### Locations (`/api/locations`)
- `GET /api/locations/public/all` - List all public locations
- `GET /api/locations/{id}` - Get location details
- `GET /api/locations/search` - Search locations
- `POST /api/locations` - Create new location (requires authentication)
- `PUT /api/locations/{id}` - Update location (owner or admin)
- `DELETE /api/locations/{id}` - Delete location (owner or admin)
- `POST /api/locations/{id}/images` - Upload image for location

### Reviews (`/api/reviews`)
- `POST /api/reviews` - Create review (requires authentication)
- `GET /api/reviews/location/{locationId}` - Get reviews for location
- `GET /api/reviews/user/{userId}` - Get reviews by user
- `PUT /api/reviews/{id}` - Update review (owner)
- `DELETE /api/reviews/{id}` - Delete review (owner or admin)

### Transactions (`/api/transactions`)
- `POST /api/transactions` - Create booking/transaction (requires authentication)
- `GET /api/transactions/user` - Get user's transactions
- `GET /api/transactions/{id}` - Get transaction details
- `PUT /api/transactions/{id}/cancel` - Cancel transaction

### Admin (`/api/admin`)
- `GET /api/admin/users` - List all users (admin only)
- `GET /api/admin/users/{id}` - Get user by ID (admin only)
- `PUT /api/admin/users/{id}/role` - Update user role (admin only)
- `DELETE /api/admin/users/{id}` - Delete user (admin only)
- `GET /api/admin/stats` - Get statistics (admin only)

## Security

- JWT-based authentication
- Password encryption using BCrypt
- Role-based access control (ROLE_USER, ROLE_ADMIN)
- CORS configuration for frontend integration

## Database

MongoDB collections:
- `users` - User accounts
- `locations` - Camping locations
- `reviews` - User reviews
- `transactions` - Bookings and transactions

## Notes

- File upload functionality for images is simplified (accepts imageUrl as parameter)
- Payment gateway integration is placeholder (transactions are auto-confirmed)
- Date conflict checking for bookings is simplified
- User ID retrieval from JWT uses email; consider storing user ID in JWT claims for production

