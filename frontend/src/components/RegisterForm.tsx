import React, { useState } from 'react';
import toast from 'react-hot-toast';
import { UserPlus, Terminal } from 'lucide-react';

const RegisterForm: React.FC = () => {
  const [formData, setFormData] = useState({
    login: '',
    senha: '',
    nome: '',
    email: '',
    telefone: '',
    tipo: 'ALUNO',
    instituicao: '',
    bio: '',
  });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const err = await response.json();
        throw new Error(err.error || 'Erro ao criar conta');
      }

      toast.success('Conta criada com sucesso! Faça login.');
      window.location.href = '/login';
    } catch (err: any) {
      toast.error(err.message);
    } finally {
      setLoading(false);
    }
  };

  const labelClass = 'block text-xs font-semibold uppercase tracking-wider txt-dim mb-2';

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <div className="w-full max-w-lg">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl glass mb-4">
            <Terminal className="w-8 h-8 neon-violet" />
          </div>
          <h1 className="text-3xl font-bold title-glow">Criar Conta</h1>
          <p className="txt-dim mt-2 text-sm">Junte-se ao DevOps Classroom</p>
        </div>

        <form onSubmit={handleSubmit} className="card space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className={labelClass}>Nome Completo</label>
              <input
                type="text"
                className="input-field"
                value={formData.nome}
                onChange={(e) => setFormData({ ...formData, nome: e.target.value })}
                required
              />
            </div>
            <div>
              <label className={labelClass}>Login</label>
              <input
                type="text"
                className="input-field"
                value={formData.login}
                onChange={(e) => setFormData({ ...formData, login: e.target.value })}
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className={labelClass}>Email</label>
              <input
                type="email"
                className="input-field"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                required
              />
            </div>
            <div>
              <label className={labelClass}>Telefone</label>
              <input
                type="text"
                className="input-field"
                value={formData.telefone}
                onChange={(e) => setFormData({ ...formData, telefone: e.target.value })}
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className={labelClass}>Senha</label>
              <input
                type="password"
                className="input-field"
                value={formData.senha}
                onChange={(e) => setFormData({ ...formData, senha: e.target.value })}
                required
                minLength={6}
              />
            </div>
            <div>
              <label className={labelClass}>Tipo</label>
              <select
                className="input-field"
                value={formData.tipo}
                onChange={(e) => setFormData({ ...formData, tipo: e.target.value })}
              >
                <option value="ALUNO">Aluno</option>
                <option value="PROFESSOR">Professor</option>
              </select>
            </div>
          </div>

          <div>
            <label className={labelClass}>Instituição</label>
            <input
              type="text"
              className="input-field"
              value={formData.instituicao}
              onChange={(e) => setFormData({ ...formData, instituicao: e.target.value })}
            />
          </div>

          <div>
            <label className={labelClass}>Bio</label>
            <textarea
              className="input-field"
              rows={3}
              value={formData.bio}
              onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
              placeholder="Conte um pouco sobre você..."
            />
          </div>

          <button type="submit" disabled={loading} className="btn-primary w-full py-3">
            <UserPlus className="w-4 h-4" />
            {loading ? 'Criando...' : 'Criar Conta'}
          </button>
        </form>

        <div className="mt-6 text-center">
          <a href="/login" className="text-sm neon hover:opacity-80 transition-opacity">
            Já tem conta? Fazer login
          </a>
        </div>
      </div>
    </div>
  );
};

export default RegisterForm;
