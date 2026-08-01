const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export interface UserProfile {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  clientId?: string; 
  customerId?: string;
  clientType: string;
  emailVerified: boolean;
  mfaEnabled: boolean;
  authorities: string[];
  createdAt: string;
  lastLoginAt?: string;
}

export interface LoginRequest {
  phone: string;
  password: string;
}

export interface RegisterRequest {
  phone: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;  
  birthDate?: string;
  address?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
}

export interface LoginResponseData {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  tokenType: string;
  user: UserProfile;
}

class AuthService {
  private static instance: AuthService;

  public static getInstance(): AuthService {
    if (!AuthService.instance) {
      AuthService.instance = new AuthService();
    }
    return AuthService.instance;
  }

  async login(credentials: LoginRequest): Promise<ApiResponse<LoginResponseData>> {
    const apiRequest = {
      username: credentials.phone,
      password: credentials.password
    };

    try {
      const response = await fetch(`/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(apiRequest),
      });

      const data = await response.json();

      if (response.ok && data.accessToken) {
        this.setTokens(data.accessToken, data.refreshToken, data.user);
        return {
          success: true,
          message: 'Вход выполнен успешно',
          data
        };
      }

      return {
        success: false,
        message: data.message || data.error || 'Неверный телефон или пароль'
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.message || 'Ошибка при входе'
      };
    }
  }

  async register(userData: RegisterRequest): Promise<ApiResponse<LoginResponseData>> {
    const apiRequest = {
      username: userData.phone,
      password: userData.password,
      email: userData.email,
      phone: userData.phone,
      firstName: userData.firstName,
      lastName: userData.lastName
    };

    try {
      const response = await fetch(`/api/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(apiRequest),
      });

      const data = await response.json();

      if (response.ok && data.id) {
        // После регистрации — автоматический вход
        const loginResult = await this.login({
          phone: userData.phone,
          password: userData.password
        });

        if (loginResult.success) {
          return {
            success: true,
            message: 'Регистрация успешна',
            data: loginResult.data
          };
        }

        return {
          success: true,
          message: 'Регистрация успешна. Войдите в систему.'
        };
      }

      return {
        success: false,
        message: data.message || data.error || 'Ошибка регистрации'
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.message || 'Ошибка при регистрации'
      };
    }
  }

  async logout(): Promise<void> {
    try {
      const token = this.getAccessToken();
      const refreshToken = this.getRefreshToken();

      if (token) {
        await fetch(`/api/auth/logout`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
            'X-Refresh-Token': refreshToken || '',
          },
        });
      }
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      this.clearTokens();
    }
  }

  getAccessToken(): string | null {
    if (typeof window !== 'undefined') {
      return localStorage.getItem('access_token');
    }
    return null;
  }

  getRefreshToken(): string | null {
    if (typeof window !== 'undefined') {
      return localStorage.getItem('refresh_token');
    }
    return null;
  }

  getCurrentUserFromStorage(): UserProfile | null {
    if (typeof window !== 'undefined') {
      const userStr = localStorage.getItem('user');
      if (userStr) {
        try {
          return JSON.parse(userStr);
        } catch {
          return null;
        }
      }
    }
    return null;
  }

  isAuthenticated(): boolean {
    return !!this.getAccessToken();
  }

  private setTokens(accessToken: string, refreshToken: string, user: UserProfile): void {
    if (typeof window !== 'undefined') {
      localStorage.setItem('access_token', accessToken);
      localStorage.setItem('refresh_token', refreshToken);
      localStorage.setItem('user', JSON.stringify(user));
      document.cookie = `access_token=${accessToken}; path=/; max-age=3600; SameSite=Lax`;
    }
  }

  private clearTokens(): void {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
      localStorage.removeItem('user');
      document.cookie = 'access_token=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    }
  }
}

export const authService = AuthService.getInstance();