import axios from "axios";

// Backend has context-path: /api configured in application.yml
// So base URL should include /api prefix
const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Add token to requests if available
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    // Log request for debugging
    console.log("API Request:", {
      method: config.method?.toUpperCase(),
      url: config.baseURL + config.url,
      data: config.data,
    });
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Handle token expiration and log errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Log error details for debugging
    const errorDetails = {
      url: error.config?.url,
      method: error.config?.method,
      baseURL: error.config?.baseURL,
      fullURL: error.config?.baseURL + error.config?.url,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      message: error.message,
      code: error.code,
    };
    
    console.error("API Error Details:", errorDetails);

    // Handle network errors (backend not reachable)
    if (!error.response) {
      console.error("Network Error - Backend may not be running or CORS issue");
      console.error("Attempted URL:", errorDetails.fullURL);
      console.error("Error Code:", error.code);
      console.error("Error Message:", error.message);
      
      // Common network error codes
      if (error.code === 'ERR_NETWORK' || error.code === 'ECONNREFUSED') {
        console.error("Connection refused - Is the backend running on http://localhost:8080?");
      } else if (error.code === 'ERR_CONNECTION_REFUSED') {
        console.error("Connection refused - Backend server is not accepting connections");
      } else if (error.message.includes('CORS')) {
        console.error("CORS error - Check backend CORS configuration");
      }
    }

    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  register: (data) => api.post("/auth/register", data),
  login: (data) => api.post("/auth/login", data),
  refresh: () => api.post("/auth/refresh"),
  getMe: () => api.get("/auth/me"),
  updateMe: (data) => api.put("/auth/me", data),
  // Note: forgot/reset password endpoints not implemented in backend yet
};

// Locations API
export const locationsAPI = {
  getAll: (params) => api.get("/locations/public/all", { params }),
  getById: (id) => api.get(`/locations/${id}`),
  search: (params) => api.get("/locations/search", { params }),
  create: (data) => api.post("/locations", data),
  update: (id, data) => api.put(`/locations/${id}`, data),
  delete: (id) => api.delete(`/locations/${id}`),
  uploadImages: (id, imageUrl) =>
    api.post(`/locations/${id}/images`, null, {
      params: { imageUrl },
    }),
};

// Reviews API
export const reviewsAPI = {
  create: (data) => api.post("/reviews", data),
  getByLocation: (locationId, params) =>
    api.get(`/reviews/location/${locationId}`, { params }),
  getByUser: (userId, params) =>
    api.get(`/reviews/user/${userId}`, { params }),
  update: (id, data) => api.put(`/reviews/${id}`, data),
  delete: (id) => api.delete(`/reviews/${id}`),
};

// Transactions/Bookings API
export const transactionsAPI = {
  create: (data) => api.post("/transactions", data),
  getUserTransactions: (params) =>
    api.get("/transactions/user", { params }),
  getById: (id) => api.get(`/transactions/${id}`),
  cancel: (id) => api.put(`/transactions/${id}/cancel`),
};

// Admin API
export const adminAPI = {
  getUsers: (params) => api.get("/admin/users", { params }),
  getUserById: (id) => api.get(`/admin/users/${id}`),
  updateUserRole: (id, role) =>
    api.put(`/admin/users/${id}/role`, { role }),
  deleteUser: (id) => api.delete(`/admin/users/${id}`),
  getStats: () => api.get("/admin/stats"),
};

export default api;
