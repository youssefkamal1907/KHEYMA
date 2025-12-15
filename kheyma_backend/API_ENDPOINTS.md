# Kheyma API Endpoints Reference

**Base URL:** `http://localhost:8080/api`

---

## 🔐 Authentication Endpoints (`/api/auth`)

| Method | Endpoint             | Auth Required | Description                        |
| ------ | -------------------- | ------------- | ---------------------------------- |
| `POST` | `/api/auth/register` | ❌ No         | Register a new user account        |
| `POST` | `/api/auth/login`    | ❌ No         | Authenticate and receive JWT token |
| `POST` | `/api/auth/refresh`  | ✅ Yes        | Refresh JWT token                  |
| `GET`  | `/api/auth/me`       | ✅ Yes        | Get current user profile           |
| `PUT`  | `/api/auth/me`       | ✅ Yes        | Update current user profile        |

### Request/Response Examples

**POST `/api/auth/register`**

```json
Request Body:
{
  "email": "user@example.com",
  "password": "secret123",
  "name": "User Name",
  "age": 30,
  "phoneNumber": "+201234567890"
}

Response: 201 Created
{
  "token": "jwt-token-here",
  "expiresIn": 86400000,
  "user": { ... }
}
```

**POST `/api/auth/login`**

```json
Request Body:
{
  "email": "user@example.com",
  "password": "secret123"
}

Response: 200 OK
{
  "token": "jwt-token-here",
  "expiresIn": 86400000,
  "user": { ... }
}
```

**GET `/api/auth/me`**

```json
Response: 200 OK
{
  "id": "user-id",
  "email": "user@example.com",
  "name": "User Name",
  "age": 30,
  "phoneNumber": "+201234567890",
  "roles": ["ROLE_USER"],
  "active": true
}
```

**PUT `/api/auth/me`**

```json
Request Body:
{
  "name": "Updated Name",
  "age": 31,
  "phoneNumber": "+201234567891"
}

Response: 200 OK
{
  "id": "user-id",
  "email": "user@example.com",
  "name": "Updated Name",
  ...
}
```

---

## 📍 Location Endpoints (`/api/locations`)

| Method   | Endpoint                     | Auth Required        | Description                           |
| -------- | ---------------------------- | -------------------- | ------------------------------------- |
| `GET`    | `/api/locations/public/all`  | ❌ No                | List all public locations (paginated) |
| `GET`    | `/api/locations/{id}`        | ❌ No                | Get location details by ID            |
| `GET`    | `/api/locations/search`      | ❌ No                | Search locations with filters         |
| `POST`   | `/api/locations`             | ✅ Yes (ROLE_USER)   | Create a new location                 |
| `PUT`    | `/api/locations/{id}`        | ✅ Yes (Owner/Admin) | Update location                       |
| `DELETE` | `/api/locations/{id}`        | ✅ Yes (Owner/Admin) | Delete location                       |
| `POST`   | `/api/locations/{id}/images` | ✅ Yes (Owner/Admin) | Upload image for location             |

### Query Parameters

**GET `/api/locations/public/all`**

- `page` (default: 0) - Page number
- `size` (default: 20) - Page size
- `sortBy` (default: "createdAt") - Sort field
- `sortDir` (default: "desc") - Sort direction (asc/desc)

**GET `/api/locations/search`**

- `q` - Search query (title, description, tags)
- `lat` - Latitude for geo search
- `lng` - Longitude for geo search
- `radius` - Search radius in km
- `tags` - Filter by tags (can be multiple)
- `minPrice` - Minimum price filter
- `maxPrice` - Maximum price filter
- `rating` - Minimum rating filter
- `page` (default: 0) - Page number
- `size` (default: 20) - Page size

### Request/Response Examples

**POST `/api/locations`**

```json
Request Body:
{
  "title": "Desert Camping Site",
  "description": "A beautiful desert camping location",
  "latitude": 30.0444,
  "longitude": 31.2357,
  "pricePerNight": 50.00,
  "tags": ["desert", "mountain", "adventure"],
  "ticketRequired": true,
  "locationType": "DESERT"
}

Response: 201 Created
{
  "id": "location-id",
  "title": "Desert Camping Site",
  "description": "A beautiful desert camping location",
  "latitude": 30.0444,
  "longitude": 31.2357,
  "pricePerNight": 50.00,
  "tags": ["desert", "mountain", "adventure"],
  "imageUrls": [],
  "ticketRequired": true,
  "ticketAvailable": true,
  "locationType": "DESERT",
  "ownerId": "user-id",
  "averageRating": 0.0,
  "reviewCount": 0,
  ...
}
```

**Location Types:** `DESERT`, `OASIS`, `PROTECTORATE`, `MOUNTAIN`, `BEACH`, `FOREST`, `OTHER`

**POST `/api/locations/{id}/images`**

```
Query Parameter: imageUrl
Example: /api/locations/{id}/images?imageUrl=https://example.com/image.jpg

Response: 200 OK
{
  "id": "location-id",
  "imageUrls": ["https://example.com/image.jpg"],
  ...
}
```

---

## ⭐ Review Endpoints (`/api/reviews`)

| Method   | Endpoint                             | Auth Required        | Description                    |
| -------- | ------------------------------------ | -------------------- | ------------------------------ |
| `POST`   | `/api/reviews`                       | ✅ Yes (ROLE_USER)   | Create a review for a location |
| `GET`    | `/api/reviews/location/{locationId}` | ❌ No                | Get reviews for a location     |
| `GET`    | `/api/reviews/user/{userId}`         | ❌ No                | Get reviews by a user          |
| `PUT`    | `/api/reviews/{id}`                  | ✅ Yes (Owner/Admin) | Update a review                |
| `DELETE` | `/api/reviews/{id}`                  | ✅ Yes (Owner/Admin) | Delete a review                |

### Query Parameters

**GET `/api/reviews/location/{locationId}`**

- `page` (default: 0) - Page number
- `size` (default: 20) - Page size

**GET `/api/reviews/user/{userId}`**

- `page` (default: 0) - Page number
- `size` (default: 20) - Page size

### Request/Response Examples

**POST `/api/reviews`**

```json
Request Body:
{
  "locationId": "location-id",
  "rating": 5,
  "comment": "Amazing camping experience! Highly recommended."
}

Response: 201 Created
{
  "id": "review-id",
  "locationId": "location-id",
  "userId": "user-id",
  "userName": "User Name",
  "rating": 5,
  "comment": "Amazing camping experience! Highly recommended.",
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T10:30:00"
}
```

**Note:** Rating must be between 1 and 5. Each user can only review a location once.

---

## 💳 Transaction/Booking Endpoints (`/api/transactions`)

| Method | Endpoint                        | Auth Required        | Description                     |
| ------ | ------------------------------- | -------------------- | ------------------------------- |
| `POST` | `/api/transactions`             | ✅ Yes (ROLE_USER)   | Create a booking/transaction    |
| `GET`  | `/api/transactions/user`        | ✅ Yes (ROLE_USER)   | Get current user's transactions |
| `GET`  | `/api/transactions/{id}`        | ✅ Yes (Owner/Admin) | Get transaction details         |
| `PUT`  | `/api/transactions/{id}/cancel` | ✅ Yes (Owner)       | Cancel a transaction            |

### Query Parameters

**GET `/api/transactions/user`**

- `page` (default: 0) - Page number
- `size` (default: 20) - Page size

### Request/Response Examples

**POST `/api/transactions`**

```json
Request Body:
{
  "locationId": "location-id",
  "packageType": "BASIC",
  "amount": 150.00,
  "startDate": "2025-07-01",
  "endDate": "2025-07-03",
  "paymentMethod": "CARD"
}

Response: 201 Created
{
  "id": "transaction-id",
  "locationId": "location-id",
  "userId": "user-id",
  "packageType": "BASIC",
  "amount": 150.00,
  "startDate": "2025-07-01",
  "endDate": "2025-07-03",
  "paymentMethod": "CARD",
  "status": "CONFIRMED",
  "paymentId": "payment-gateway-id",
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T10:30:00"
}
```

**Package Types:** `BASIC`, `STANDARD`, `PREMIUM`  
**Payment Methods:** `CARD`, `PAYPAL`, `STRIPE`, `INSTAPAY`, `CASH`  
**Transaction Status:** `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`, `REFUNDED`

**PUT `/api/transactions/{id}/cancel`**

```json
Response: 200 OK
{
  "id": "transaction-id",
  "status": "CANCELLED",
  ...
}
```

---

## 👑 Admin Endpoints (`/api/admin`)

**All admin endpoints require `ROLE_ADMIN` authentication.**

| Method   | Endpoint                     | Auth Required       | Description                |
| -------- | ---------------------------- | ------------------- | -------------------------- |
| `GET`    | `/api/admin/users`           | ✅ Yes (ROLE_ADMIN) | List all users (paginated) |
| `GET`    | `/api/admin/users/{id}`      | ✅ Yes (ROLE_ADMIN) | Get user by ID             |
| `PUT`    | `/api/admin/users/{id}/role` | ✅ Yes (ROLE_ADMIN) | Update user role           |
| `DELETE` | `/api/admin/users/{id}`      | ✅ Yes (ROLE_ADMIN) | Delete/deactivate user     |
| `GET`    | `/api/admin/stats`           | ✅ Yes (ROLE_ADMIN) | Get system statistics      |

### Query Parameters

**GET `/api/admin/users`**

- `page` (default: 0) - Page number
- `size` (default: 20) - Page size
- `filter` - Filter by email or name

### Request/Response Examples

**PUT `/api/admin/users/{id}/role`**

```json
Request Body:
{
  "role": "ROLE_ADMIN"
}

Response: 200 OK
{
  "id": "user-id",
  "email": "user@example.com",
  "roles": ["ROLE_ADMIN"],
  ...
}
```

**Available Roles:** `ROLE_USER`, `ROLE_ADMIN`

**DELETE `/api/admin/users/{id}`**

```
Response: 204 No Content
(Soft delete - user is deactivated)
```

---

## 📊 Response Formats

### Paginated Response

All list endpoints return paginated responses:

```json
{
  "content": [ ... ],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

### Error Response

```json
{
  "message": "Error message here"
}
```

### Validation Error Response

```json
{
  "fieldName": "Validation error message",
  "anotherField": "Another validation error"
}
```

---

## 🔒 Authentication

All authenticated endpoints require a JWT token in the Authorization header:

```
Authorization: Bearer <jwt-token>
```

**Token Format:**

- Obtained from `/api/auth/login` or `/api/auth/register`
- Valid for 24 hours (configurable)
- Refresh using `/api/auth/refresh`

---

## 📝 Notes

1. **Base Path:** All endpoints are prefixed with `/api` (configured in `application.yml`)
2. **Pagination:** Default page size is 20, can be customized via query parameters
3. **Date Format:** Use ISO 8601 format (YYYY-MM-DD) for dates
4. **Image Upload:** Currently accepts image URLs as query parameters. For file uploads, you may need to implement multipart/form-data handling
5. **Location Ownership:** Only the owner or admin can update/delete locations
6. **Review Limits:** Each user can only create one review per location
7. **Transaction Status:** Transactions are auto-confirmed on creation (payment gateway integration placeholder)

---

## 🚀 Quick Reference

### Public Endpoints (No Auth)

- `GET /api/locations/public/all`
- `GET /api/locations/{id}`
- `GET /api/locations/search`
- `GET /api/reviews/location/{locationId}`
- `GET /api/reviews/user/{userId}`
- `POST /api/auth/register`
- `POST /api/auth/login`

### User Endpoints (ROLE_USER)

- `POST /api/locations`
- `POST /api/reviews`
- `POST /api/transactions`
- `GET /api/transactions/user`
- `PUT /api/transactions/{id}/cancel`
- `GET /api/auth/me`
- `PUT /api/auth/me`
- `POST /api/auth/refresh`

### Admin Endpoints (ROLE_ADMIN)

- All `/api/admin/*` endpoints
- Can update/delete any location
- Can update/delete any review
