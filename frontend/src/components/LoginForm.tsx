import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import toast from 'react-hot-toast';
import { LogIn, Terminal } from 'lucide-react';
import { Link } from 'react-router-dom';

const LoginForm: React.FC = () => {
  const { login } = useAuth();
  const [formData, setFormData] = useState({ login: '', senha: '' });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await login(formData.login, formData.senha);
      toast.success('Login realizado com sucesso!');
    } catch (err: any) {
      toast.error(err.message || 'Erro ao fazer login');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl glass mb-4">
            <Terminal className="w-8 h-8 neon" />
          </div>
          <h1 className="text-3xl font-bold title-glow">DevOps Classroom</h1>
          <p className="txt-dim mt-2 text-sm">Gerenciamento inteligente de aulas</p>
        </div>

        <form onSubmit={handleSubmit} className="card space-y-5">
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider txt-dim mb-2">
              Login
            </label>
            <input
              type="text"
              className="input-field"
              value={formData.login}
              onChange={(e) => setFormData({ ...formData, login: e.target.value })}
              placeholder="Seu login"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider txt-dim mb-2">
              Senha
            </label>
            <input
              type="password"
              className="input-field"
              value={formData.senha}
              onChange={(e) => setFormData({ ...formData, senha: e.target.value })}
              placeholder="Sua senha"
              required
            />
          </div>

          <button type="submit" disabled={loading} className="btn-primary w-full py-3">
            <LogIn className="w-4 h-4" />
            {loading ? 'Entrando...' : 'Entrar'}
          </button>
        </form>

        {/* 2. Substitua a tag <a> na parte inferior pelo <Link> */}
<div className="mt-6 text-center">
  <Link to="/register" className="text-sm neon hover:opacity-80 transition-opacity">
    Criar nova conta
  </Link>
</div>
      </div>
    </div>
  );
};

export default LoginForm;
