import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from './contexts/AuthContext';
import LoginForm from './components/LoginForm';
import RegisterForm from './components/RegisterForm';
import ChatRoom from './components/ChatRoom';
import { Aula, Usuario } from './types';
import api from './services/api';
import {
  LayoutDashboard,
  Users,
  BookOpen,
  Calendar,
  MessageSquare,
  LogOut,
  Terminal,
  GraduationCap,
  Play,
  Loader2,
  Menu,
  X,
} from 'lucide-react';

type Page = 'dashboard' | 'cursos' | 'alunos' | 'aula';

const DashboardView: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [currentPage, setCurrentPage] = useState<Page>('dashboard');
  const [aulas, setAulas] = useState<Aula[]>([]);
  const [selectedAula, setSelectedAula] = useState<Aula | null>(null);
  const [cursos, setCursos] = useState<any[]>([]);
  const [alunos, setAlunos] = useState<Usuario[]>([]);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  // 1. Carregar dados do backend
  const loadData = async () => {
    try {
      const [aulasResp, cursosResp, alunosResp] = await Promise.all([
        api.get('/api/aulas/em-andamento'),
        api.get('/api/cursos'),
        api.get('/api/auth/users'),
      ]);

      setAulas(aulasResp.data);
      setCursos(cursosResp.data);
      setAlunos(alunosResp.data.filter((u: any) => u.tipo === 'ALUNO'));
    } catch (error: any) {
      console.error('Erro ao carregar dados:', error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        logout();
      }
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // 2. Ações do Dashboard
  const iniciarAula = async (aulaId: number | string) => {
    try {
      const aula = aulas.find((a) => a.id === aulaId);
      if (aula) {
        setSelectedAula(aula);
        setCurrentPage('aula');
      } else {
        const response = await api.get(`/api/aulas/${aulaId}`);
        setSelectedAula(response.data);
        setCurrentPage('aula');
      }
    } catch (error) {
      console.error('Erro ao abrir aula:', error);
    }
  };

  const handleVoltarAoDashboard = () => {
    setSelectedAula(null);
    setCurrentPage('dashboard');
  };

  // 3. Verificações de segurança e redirecionamento de tela
  if (!user) return <Navigate to="/login" replace />;

  if (currentPage === 'aula' && selectedAula) {
    return (
      <ChatRoom 
        aula={selectedAula!} 
        user={user} 
        onLeave={handleVoltarAoDashboard} 
      />
    );
  }

  // 4. Interface Principal
  return (
    <div className="min-h-screen flex flex-col md:flex-row">
      {/* 📱 BARRA SUPERIOR MOBILE */}
      <div className="md:hidden flex items-center justify-between p-4 glass-bar border-b divider z-50">
        <div className="flex items-center gap-2">
          <Terminal className="w-6 h-6 neon" />
          <span className="font-bold title-glow">DevOps Classroom</span>
        </div>
        <button
          onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
          className="p-2 rounded-xl text-white/80 hover:bg-white/10"
        >
          {isMobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
        </button>
      </div>

      {/* 💻 SIDEBAR */}
      <aside
        className={`
          fixed md:static inset-y-0 left-0 z-40 w-64 glass-bar border-r flex flex-col justify-between transition-transform duration-300 ease-in-out bg-zinc-950/95 md:bg-transparent
          ${isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0'}
        `}
      >
        <div>
          <div className="hidden md:flex p-4 border-b divider items-center gap-2">
            <Terminal className="w-6 h-6 neon" />
            <span className="font-bold title-glow">DevOps Classroom</span>
          </div>

          <nav className="p-3 space-y-1.5 mt-14 md:mt-0">
            <button
              onClick={() => { setCurrentPage('dashboard'); setIsMobileMenuOpen(false); }}
              className={`nav-item w-full ${currentPage === 'dashboard' ? 'nav-item-active' : ''}`}
            >
              <LayoutDashboard className="w-4 h-4" />
              Dashboard
            </button>

            <button
              onClick={() => { setCurrentPage('cursos'); setIsMobileMenuOpen(false); }}
              className={`nav-item w-full ${currentPage === 'cursos' ? 'nav-item-active' : ''}`}
            >
              <BookOpen className="w-4 h-4" />
              Cursos
            </button>

            <button
              onClick={() => { setCurrentPage('alunos'); setIsMobileMenuOpen(false); }}
              className={`nav-item w-full ${currentPage === 'alunos' ? 'nav-item-active' : ''}`}
            >
              <Users className="w-4 h-4" />
              Alunos
            </button>
          </nav>
        </div>

        <div className="p-4 border-t divider">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-9 h-9 rounded-xl glass flex items-center justify-center shrink-0">
              <GraduationCap className="w-4 h-4 neon" />
            </div>
            <div className="min-w-0">
              <p className="text-sm font-medium truncate">{user.nome}</p>
              <p className="text-xs txt-faint">{user.tipo}</p>
            </div>
          </div>
          <button onClick={logout} className="btn-ghost w-full">
            <LogOut className="w-4 h-4" />
            Sair
          </button>
        </div>
      </aside>

      {/* OVERLAY MOBILE */}
      {isMobileMenuOpen && (
        <div
          onClick={() => setIsMobileMenuOpen(false)}
          className="fixed inset-0 bg-black/60 backdrop-blur-sm z-30 md:hidden"
        />
      )}

      {/* 📄 CONTEÚDO PRINCIPAL */}
      <div className="flex-1 overflow-x-hidden min-w-0">
        <header className="p-4 md:p-6 border-b divider glass-bar">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
            <h1 className="text-xl md:text-2xl font-bold title-glow">
              {currentPage === 'dashboard' && 'Dashboard'}
              {currentPage === 'cursos' && 'Cursos'}
              {currentPage === 'alunos' && 'Alunos'}
            </h1>
            <span className="text-xs md:text-sm txt-dim">
              {new Date().toLocaleDateString('pt-BR', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
            </span>
          </div>
        </header>

        <main className="p-4 md:p-6">
          {currentPage === 'dashboard' && (
            <div className="space-y-6">
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                <div className="card flex items-center gap-3">
                  <BookOpen className="w-8 h-8 neon shrink-0" />
                  <div>
                    <p className="text-2xl font-bold">{cursos.length}</p>
                    <p className="text-sm txt-dim">Cursos</p>
                  </div>
                </div>
                <div className="card flex items-center gap-3">
                  <Users className="w-8 h-8 neon-lime shrink-0" />
                  <div>
                    <p className="text-2xl font-bold">{alunos.length}</p>
                    <p className="text-sm txt-dim">Alunos</p>
                  </div>
                </div>
                <div className="card flex items-center gap-3">
                  <MessageSquare className="w-8 h-8 neon-violet shrink-0" />
                  <div>
                    <p className="text-2xl font-bold">{aulas.length}</p>
                    <p className="text-sm txt-dim">Aulas Ativas</p>
                  </div>
                </div>
                <div className="card flex items-center gap-3">
                  <Calendar className="w-8 h-8 neon-amber shrink-0" />
                  <div>
                    <p className="text-2xl font-bold">0</p>
                    <p className="text-sm txt-dim">Quizzes Hoje</p>
                  </div>
                </div>
              </div>

              <div>
                <h2 className="text-lg font-semibold mb-4 title-glow">Aulas em Andamento</h2>
                <div className="space-y-3">
                  {aulas.map((aula) => (
                    <div key={aula.id} className="card flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                      <div className="flex items-center gap-4">
                        <div
                          className={
                            aula.status === 'EM_ANDAMENTO'
                              ? 'dot-live animate-pulse shrink-0'
                              : 'w-2 h-2 rounded-full bg-amber-300 shrink-0'
                          }
                        />
                        <div className="min-w-0">
                          <p className="font-medium truncate">{aula.titulo}</p>
                          <p className="text-sm txt-dim truncate">{aula.curso?.nome} • {aula.duracao}</p>
                        </div>
                      </div>
                      <button onClick={() => iniciarAula(aula.id)} className="btn-primary w-full sm:w-auto justify-center">
                        <Play className="w-4 h-4" />
                        Entrar
                      </button>
                    </div>
                  ))}
                  {aulas.length === 0 && (
                    <p className="txt-faint text-center py-8">Nenhuma aula em andamento</p>
                  )}
                </div>
              </div>
            </div>
          )}

          {currentPage === 'cursos' && (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {cursos.map((curso) => (
                <div key={curso.id} className="card">
                  <div className="flex items-start justify-between mb-3">
                    <h3 className="font-semibold">{curso.nome}</h3>
                    <span className="badge-neon">{curso.codigo}</span>
                  </div>
                  <p className="text-sm txt-dim mb-3">{curso.descricao || 'Sem descrição'}</p>
                  <p className="text-xs txt-faint">Professor: {curso.professor?.nome}</p>
                </div>
              ))}
            </div>
          )}

          {currentPage === 'alunos' && (
            <div className="card overflow-x-auto">
              <h3 className="text-lg font-semibold title-glow mb-4">Alunos Cadastrados</h3>
              <table className="w-full text-sm min-w-[400px]">
                <thead>
                  <tr className="border-b divider">
                    <th className="text-left py-3 px-4 txt-dim font-medium">Nome</th>
                    <th className="text-left py-3 px-4 txt-dim font-medium">Email</th>
                  </tr>
                </thead>
                <tbody>
                  {alunos.map((aluno) => (
                    <tr key={aluno.id} className="border-b divider">
                      <td className="py-3 px-4">{aluno.nome}</td>
                      <td className="py-3 px-4 txt-dim">{aluno.email}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

// Gerenciador de Rotas Principal
function App() {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Loader2 className="w-8 h-8 neon animate-spin" />
      </div>
    );
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={!user ? <LoginForm /> : <Navigate to="/dashboard" replace />} />
        <Route path="/register" element={!user ? <RegisterForm /> : <Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<DashboardView />} />
        <Route path="*" element={<Navigate to={user ? "/dashboard" : "/login"} replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;