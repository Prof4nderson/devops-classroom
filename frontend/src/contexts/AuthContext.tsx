import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { Usuario, AuthResponse } from '../types';

interface AuthContextType {
  user: Usuario | null;
  token: string | null;
  login: (loginInput: string, senha: string) => Promise<void>;
  logout: () => void;
  loading: boolean;
}
// O frontend e a API compartilham o mesmo domínio em produção.
const API_BASE_URL = '';

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<Usuario | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser && token) {
      try {
        setUser(JSON.parse(storedUser));
      } catch {
        localStorage.removeItem('user');
        localStorage.removeItem('token');
      }
    }
    setLoading(false);
  }, [token]);

  const login = async (loginStr: string, senha: string) => {
    // 🎯 2. MUDE DE '/api/auth/login' PARA USAR A 'API_BASE_URL'
   // 🎯 Altere de 'password: senha' para 'senha: senha'
const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ login: loginStr, senha }), 
});

    if (!response.ok) {
      const errorText = await response.text();
      let errorMessage = 'Erro ao fazer login';
      try {
        const errorJson = JSON.parse(errorText);
        errorMessage = errorJson.message || errorJson.error || errorMessage;
      } catch {
        if (errorText) errorMessage = errorText;
      }
      throw new Error(errorMessage);
    }

    const textData = await response.text();
    if (!textData) {
      throw new Error('O servidor respondeu sem conteúdo (corpo vazio).');
    }
    const data: AuthResponse = JSON.parse(textData);

    localStorage.setItem('token', data.token);
    localStorage.setItem('userId', String(data.userId));

    // 🎯 3. MUDE AQUI TAMBÉM
    const userResponse = await fetch(`${API_BASE_URL}/api/auth/users/${data.userId}`, {
      headers: { Authorization: `Bearer ${data.token}` },
    });

    if (!userResponse.ok) {
      throw new Error('Falha ao obter os dados do usuário autenticado.');
    }

    const userText = await userResponse.text();
    const userData: Usuario = userText ? JSON.parse(userText) : null;

    if (!userData) {
      throw new Error('Dados do usuário retornaram vazios.');
    }

    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
    setToken(data.token);
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('userId');
    setUser(null);
    setToken(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};