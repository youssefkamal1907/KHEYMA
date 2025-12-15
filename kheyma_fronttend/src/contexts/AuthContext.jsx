import { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  // Normalize user object from backend to frontend format
  const normalizeUser = (userData, responseData) => {
    // Backend User model: email, type (ADMIN/USER), dob, address, etc.
    // Frontend expects: email, role (ROLE_ADMIN/ROLE_USER), name (optional)
    const user = userData || {
      email: responseData?.email,
      type: responseData?.userType,
    };

    // Map key roles for frontend compatibility
    if (user.roles && Array.isArray(user.roles)) {
      if (user.roles.includes('ROLE_ADMIN')) {
        user.role = 'ROLE_ADMIN';
      } else if (user.roles.includes('ROLE_USER')) {
        user.role = 'ROLE_USER';
      }
    }

    // Fallback: Map type to role for frontend compatibility (legacy)
    if (user.type && !user.role) {
      user.role = user.type === 'ADMIN' ? 'ROLE_ADMIN' : 'ROLE_USER';
    }

    return user;
  };

  useEffect(() => {
    const initAuth = async () => {
      const storedToken = localStorage.getItem('token');
      const storedUser = localStorage.getItem('user');

      if (storedToken && storedUser) {
        setToken(storedToken);
        setUser(JSON.parse(storedUser));
        // Verify token is still valid
        try {
          const response = await authAPI.getMe();
          const user = normalizeUser(response.data, null);
          setUser(user);
          localStorage.setItem('user', JSON.stringify(user));
        } catch (error) {
          // Token invalid, clear storage
          console.error('Token validation error:', error);
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          setToken(null);
          setUser(null);
        }
      }
      setLoading(false);
    };

    initAuth();
  }, []);

  const login = async (email, password) => {
    try {
      const response = await authAPI.login({ email, password });
      const { token: newToken, user: userData } = response.data;

      const user = normalizeUser(userData, response.data);

      setToken(newToken);
      setUser(user);
      localStorage.setItem('token', newToken);
      localStorage.setItem('user', JSON.stringify(user));


      return { success: true, role: user.role };
    } catch (error) {
      console.error('Login error:', error);
      return {
        success: false,
        error: error.response?.data?.message || error.message || 'Login failed',
      };
    }
  };

  const register = async (email, password, name = null) => {
    try {
      // Backend RegisterRequest expects: email, password, name (required), age (optional), phoneNumber (optional)
      const requestData = {
        email,
        password,
        name: name || email.split('@')[0] // Use email prefix if name not provided
      };

      console.log('Attempting registration with data:', { ...requestData, password: '***' });
      console.log('API Base URL:', import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api');

      const response = await authAPI.register(requestData);
      const { token: newToken, user: userData } = response.data;

      const user = normalizeUser(userData, response.data);

      // Store name if provided
      if (name) {
        user.name = name;
      }

      setToken(newToken);
      setUser(user);
      localStorage.setItem('token', newToken);
      localStorage.setItem('user', JSON.stringify(user));

      return { success: true };
    } catch (error) {
      console.error('Registration error:', error);

      // Provide more specific error messages
      let errorMessage = 'Registration failed';

      if (!error.response) {
        // Network error - backend not reachable
        errorMessage = 'Cannot connect to server. Please ensure the backend is running on http://localhost:8080';
      } else if (error.response.status === 400) {
        // Validation error
        const data = error.response.data;
        if (typeof data === 'object' && data !== null) {
          // Handle validation errors (could be object with field names)
          const messages = Object.values(data).filter(msg => typeof msg === 'string');
          errorMessage = messages.length > 0 ? messages.join(', ') : data.message || 'Invalid registration data';
        } else if (typeof data === 'string') {
          errorMessage = data;
        } else {
          errorMessage = data?.message || 'Invalid registration data';
        }
      } else if (error.response.status === 409) {
        errorMessage = 'Email already exists';
      } else if (error.response.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.message) {
        errorMessage = error.message;
      }

      return {
        success: false,
        error: errorMessage,
      };
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  const updateUser = async (data) => {
    try {
      const response = await authAPI.updateMe(data);
      const user = normalizeUser(response.data, null);
      setUser(user);
      localStorage.setItem('user', JSON.stringify(user));
      return { success: true };
    } catch (error) {
      console.error('Update user error:', error);
      return {
        success: false,
        error: error.response?.data?.message || error.message || 'Update failed',
      };
    }
  };

  const value = {
    user,
    token,
    loading,
    login,
    register,
    logout,
    updateUser,
    isAuthenticated: !!token,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

