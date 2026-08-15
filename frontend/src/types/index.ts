export interface Usuario {
  id: number;
  login: string;
  nome: string;
  email: string;
  telefone: string;
  tipo: 'ALUNO' | 'PROFESSOR' | 'ADMIN';
  instituicao: string;
  avatar: string;
  bio: string;
  criadoEm: string;
}

export interface AuthResponse {
  token: string;
  tipo: string;
  userId: number;
  nome: string;
  login: string;
  tipoUsuario: string;
}

export interface Curso {
  id: number;
  nome: string;
  descricao: string;
  codigo: string;
  professor: Usuario;
  criadoEm: string;
}

export interface Aula {
  id: number;
  curso: Curso;
  titulo: string;
  descricao: string;
  dataAula: string;
  duracao: string;
  status: 'AGENDADA' | 'EM_ANDAMENTO' | 'FINALIZADA' | 'CANCELADA';
  criadoEm: string;
}

export interface Mensagem {
  id: number;
  aulaId: number;
  usuario: Usuario;
  conteudo: string;
  tipo: 'TEXT' | 'IMAGE' | 'CODE' | 'SCREENSHOT' | 'FILE' | 'QUIZ';
  urlMidia: string;
  nomeArquivo: string;
  mimeType: string;
  criadoEm: string;
}

export interface Quiz {
  id: number;
  aulaId: number;
  professor: Usuario;
  pergunta: string;
  opcoes: string;
  respostaCorreta: string;
  tempoLimiteSegundos: number;
  status: 'ATIVO' | 'FINALIZADO' | 'EXPIRADO';
  criadoEm: string;
  respostas: RespostaQuiz[];
}

export interface RespostaQuiz {
  id: number;
  quizId: number;
  usuario: Usuario;
  respostaSelecionada: string;
  estaCorreta: boolean;
  dataResposta: string;
}

export interface PresencaPresente {
  id: number;
  nome: string;
  tipo: string;
  dataRegistro: string;
}

export interface ChatMessage {
  id: number;
  conteudo: string;
  tipo: string;
  urlMidia?: string;
  nomeArquivo?: string;
  mimeType?: string;
  usuarioNome: string;
  usuarioId: number;
  usuarioTipo: string;
  criadoEm: string;
}

export interface PresenceUpdate {
  tipo: string;
  presentes: Record<number, string>;
  totalPresentes: number;
}
