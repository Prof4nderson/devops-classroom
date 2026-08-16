import React, { useState, useEffect, useRef, useCallback } from 'react';
import { ChatWebSocket } from '../services/websocket';
import { ChatMessage, Aula, Usuario } from '../types';
import { Send, Image, Code, Bot, Users, Clock, Trophy, LogOut } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../services/api';

interface ChatRoomProps {
  aula: Aula;
  user: Usuario;
  onLeave: () => void;
}

const ChatRoom: React.FC<ChatRoomProps> = ({ aula, user, onLeave }) => {
  const [isAiThinking, setIsAiThinking] = useState(false);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [inputText, setInputText] = useState('');
  const [presentes, setPresentes] = useState<Record<number, string>>({});
  const [showCodeEditor, setShowCodeEditor] = useState(false);
  const [codeText, setCodeText] = useState('');
  const [codeLanguage, setCodeLanguage] = useState('bash');
  const [activeQuiz, setActiveQuiz] = useState<any>(null);
  const [showQuizModal, setShowQuizModal] = useState(false);
  const [quizForm, setQuizForm] = useState({
    pergunta: '',
    opcoes: ['', '', '', ''],
    respostaCorreta: '',
    tempoLimite: 30,
  });

  const messagesEndRef = useRef<HTMLDivElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const wsRef = useRef<ChatWebSocket | null>(null);

  // Helper para normalizar dados de presença em um Map/Record
  const processarPresentes = (dados: any): Record<number, string> => {
    const mapa: Record<number, string> = {};
    const lista = Array.isArray(dados) ? dados : dados?.presentes || dados;

    if (Array.isArray(lista)) {
      lista.forEach((u: any) => {
        if (u && typeof u === 'object') {
          const id = u.id || u.usuarioId;
          const nome = u.nome || u.usuarioNome || u.nomeUsuario || u.userName;
          if (id && nome) mapa[id] = nome;
        }
      });
    } else if (typeof lista === 'object' && lista !== null) {
      Object.entries(lista).forEach(([key, val]) => {
        if (typeof val === 'string') mapa[Number(key)] = val;
        else if (val && typeof val === 'object') {
          mapa[Number(key)] =
            (val as any).nome ||
            (val as any).usuarioNome ||
            (val as any).nomeUsuario ||
            `Usuário ${key}`;
        }
      });
    }
    return mapa;
  };

  // Função para desconectar e sair da sala
  const handleLeave = async () => {
    try {
      if (aula?.id && user?.id) {
        await api
          .delete(`/api/aulas/${aula.id}/presenca`, {
            params: { usuarioId: user.id },
          })
          .catch(() => {});
      }
    } finally {
      wsRef.current?.leave();
      toast.success('Você saiu da aula');
      onLeave();
    }
  };

  // WebSocket, Presença e Carregamento do Histórico
  useEffect(() => {
    if (user?.id && user?.nome) {
      setPresentes((prev) => ({ ...prev, [user.id]: user.nome }));
    }

    const handlePresence = (data: any) => {
      const mapaAtualizado = processarPresentes(data);
      setPresentes((prev) => ({
        ...prev,
        ...mapaAtualizado,
        ...(user?.id && user?.nome ? { [user.id]: user.nome } : {}),
      }));
    };

    const handleQuiz = (data: any) => {
      if (data?.tipo === 'QUIZ_CREATED') {
        setActiveQuiz(data);
        setShowQuizModal(true);
        toast('Novo quiz do professor!', { icon: '🎯' });
      }
    };

    const ws = new ChatWebSocket(
      aula.id,
      (newMsg) =>
        setMessages((prev) => {
          if (
            prev.some(
              (m) =>
                m.id === newMsg.id ||
                (m.conteudo === newMsg.conteudo &&
                  m.usuarioId === newMsg.usuarioId &&
                  m.criadoEm === newMsg.criadoEm)
            )
          ) {
            return prev;
          }
          return [...prev, newMsg];
        }),
      handlePresence,
      handleQuiz
    );

    wsRef.current = ws;
    ws.connect();

    const inicializarSala = async () => {
      if (!aula?.id) return;

      try {
        const resMsgs = await api.get(`/api/aulas/${aula.id}/mensagens`);
        if (Array.isArray(resMsgs.data)) {
          setMessages(resMsgs.data);
        }
      } catch (err) {
        console.warn('Histórico REST indisponível.');
      }

      try {
        await api.post(`/api/aulas/${aula.id}/presenca`, { usuarioId: user.id });
      } catch (err) {
        await api.post(`/api/aulas/${aula.id}/presenca?usuarioId=${user.id}`).catch(() => {});
      }

      try {
        const resPresenca = await api.get(`/api/aulas/${aula.id}/presentes`);
        if (resPresenca?.data) {
          const mapaApi = processarPresentes(resPresenca.data);
          setPresentes((prev) => ({
            ...prev,
            ...mapaApi,
            [user.id]: user.nome,
          }));
        }
      } catch (err) {
        console.warn('Busca de presentes via REST falhou. Mantendo presença local.');
      }
    };

    inicializarSala();

    return () => {
      ws.leave();
    };
  }, [aula.id, user.id, user.nome]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const sendMessage = useCallback(async () => {
    if (!inputText.trim()) return;

    const textoEnviar = inputText;
    setInputText('');

    wsRef.current?.sendMessage(textoEnviar, 'TEXT');

    if (textoEnviar.toLowerCase().includes('@coder')) {
      setIsAiThinking(true);

      try {
        const perguntaLimpa = textoEnviar.replace(/@coder/gi, '').trim();

        await api.post('/api/rag/chat', perguntaLimpa, {
          params: { sessionId: `aula-${aula.id}` },
          headers: { 'Content-Type': 'text/plain' },
        });
      } catch (error) {
        console.error('Erro ao consultar o agente @Coder:', error);
        toast.error('O agente @Coder não conseguiu responder no momento.');
      } finally {
        setIsAiThinking(false);
      }
    }
  }, [inputText, aula.id]);

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    const isImage = file.type.startsWith('image/');
    const isPdf = file.type === 'application/pdf' || file.name.endsWith('.pdf');

    try {
      if (isPdf) {
        const toastId = toast.loading('Indexando PDF na base do @Coder...');

        await api.post('/api/rag/upload', formData, {
          headers: { 'Content-Type': 'multipart/form-data' },
        });

        toast.dismiss(toastId);
        toast.success('PDF indexado! Agora você pode fazer perguntas ao @Coder.');

        wsRef.current?.sendMessage(
          `📄 Documento adicionado à base de conhecimento: **${file.name}**`,
          'TEXT'
        );
      } else {
        const endpoint = isImage ? '/api/uploads/image' : '/api/uploads/file';
        const response = await api.post(endpoint, formData, {
          headers: { 'Content-Type': 'multipart/form-data' },
        });

        const data = response.data;
        const tipo = isImage ? (inputText.includes('```') ? 'CODE' : 'IMAGE') : 'FILE';

        wsRef.current?.sendMessage(
          inputText || file.name,
          tipo,
          data.url,
          data.nomeArquivo,
          data.mimeType
        );
      }
    } catch (error) {
      console.error('Erro no upload:', error);
      toast.error('Erro ao processar o arquivo.');
    }

    e.target.value = '';
  };

  const sendCode = () => {
    if (!codeText.trim()) return;
    const content = `\`\`\`${codeLanguage}\n${codeText}\n\`\`\``;
    wsRef.current?.sendMessage(content, 'CODE');
    setCodeText('');
    setShowCodeEditor(false);
  };

  const sendScreenshot = () => {
    if (fileInputRef.current) {
      fileInputRef.current.accept = 'image/*';
      fileInputRef.current.click();
    }
  };

  const handleOptionChange = (index: number, value: string) => {
    const novasOpcoes = [...quizForm.opcoes];
    novasOpcoes[index] = value;
    setQuizForm((prev) => ({
      ...prev,
      opcoes: novasOpcoes,
      respostaCorreta: index === 0 ? value : prev.respostaCorreta || novasOpcoes[0],
    }));
  };

  const createQuiz = async () => {
    const opcoesValidas = quizForm.opcoes.filter((o) => o.trim());
    if (!quizForm.pergunta.trim() || opcoesValidas.length < 2) {
      toast.error('Preencha a pergunta e pelo menos 2 opções.');
      return;
    }
    let quizId = Date.now();
    const payload = {
      aula: aula.id,
      pergunta: quizForm.pergunta,
      opcoes: quizForm.opcoes,
      respostaCorreta: quizForm.respostaCorreta || opcoesValidas[0],
      tempoLimiteSegundos: quizForm.tempoLimite,
    };

    try {
      const response = await api.post('/api/quizzes', payload);
      if (response.data?.id) {
        quizId = response.data.id;
      }
      toast.success('Quiz criado e salvo!');
    } catch (error) {
      console.warn('Backend REST respondeu com erro, transmitindo quiz via WebSocket...', error);
      toast('Quiz enviado via WebSocket!', { icon: '⚡' });
    }

    wsRef.current?.createQuiz(quizForm.pergunta, opcoesValidas, quizId);

    setShowQuizModal(false);
    setQuizForm({ pergunta: '', opcoes: ['', '', '', ''], respostaCorreta: '', tempoLimite: 30 });
  };

  return (
    <div className="flex h-screen">
      {/* Sidebar - Presentes */}
      <div className="w-64 glass-bar border-r flex flex-col">
        <div className="p-4 border-b divider">
          <h3 className="text-xs font-semibold txt-dim uppercase tracking-wider flex items-center gap-2">
            <Users className="w-4 h-4 neon"/>
            Presentes ({Object.keys(presentes).length})
          </h3>
        </div>
        <div className="flex-1 overflow-y-auto p-3 space-y-1">
          {Object.entries(presentes).map(([id, nome]) => (
            <div
              key={id}
              className="flex items-center gap-2 px-3 py-2 rounded-xl border border-transparent hover:border-white/10 hover:bg-white/5 transition-colors"
            >
              <div className="dot-live animate-pulse" />
              <span className="text-sm txt-dim truncate">{nome}</span>
              {parseInt(id) === user.id && <span className="text-xs neon ml-auto">(você)</span>}
            </div>
          ))}
          {Object.keys(presentes).length === 0 && (
            <p className="text-sm txt-faint text-center py-4">Ninguém presente ainda</p>
          )}
        </div>
      </div>

      {/* Chat Principal */}
      <div className="flex-1 flex flex-col">
        {/* Header */}
        <div className="p-4 border-b divider glass-bar flex items-center justify-between">
          <div>
            <h2 className="text-lg font-semibold title-glow">{aula?.titulo || 'Sala de Aula'}</h2>
            <p className="text-sm txt-dim">
              {aula?.curso?.nome} {aula?.status ? `• ${aula.status}` : ''}
            </p>
          </div>

          <div className="flex items-center gap-3">
            <div className="flex items-center gap-2 badge-neon">
              <Clock className="w-3.5 h-3.5"/>
              <span>{aula?.duracao || '0h'}</span>
            </div>

            <button
              onClick={handleLeave}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-red-500/10 text-red-400 hover:bg-red-500/20 border border-red-500/20 transition-all text-xs font-medium cursor-pointer"
              title="Sair da aula"
            >
              <LogOut className="w-3.5 h-3.5"/>
              <span>Sair</span>
            </button>
          </div>
        </div>

        {/* Quiz Modal */}
        {showQuizModal && activeQuiz && (
          <div className="mx-4 mt-4 p-4 quiz-panel">
            <div className="flex items-center gap-2 mb-2">
              <Trophy className="w-5 h-5 neon-violet"/>
              <span className="text-sm font-semibold neon-violet">
                Quiz - {activeQuiz.professorNome}
              </span>
            </div>
            <p className="mb-3">{activeQuiz.pergunta}</p>
            <div className="space-y-2">
              {(activeQuiz.opcoes || []).map((op: string, i: number) => (
                <button
                  key={i}
                  onClick={() => wsRef.current?.respondQuiz(activeQuiz.quizId, op)}
                  className="quiz-option"
                >
                  {op}
                </button>
              ))}
            </div>
          </div>
        )}

        {/* Messages */}
        <div className="flex-1 overflow-y-auto p-4 space-y-4">
          {messages.map((msg, idx) => {
            // ID de quem enviou a mensagem (busca em todas as variações possíveis)
            const autorId =
              msg.usuarioId ??
              (msg as any).usuario?.id ??
              (msg as any).usuario_id ??
              (msg as any).userId;

            // ID do usuário atualmente logado na tela (Professor ou Aluno)
            const currentUserId =
              user?.id ??
              (user as any)?.usuarioId ??
              (user as any)?.idUsuario;

            // Nome do autor
            const autorNome =
              msg.usuarioNome ||
              (msg as any).nomeUsuario ||
              (msg as any).nome ||
              (msg as any).usuario?.nome ||
              (autorId ? presentes[autorId] : null) ||
              'Usuário';

            const autorTipo =
              msg.usuarioTipo ||
              (msg as any).tipoUsuario ||
              (msg as any).usuario?.tipo;

            // Comparação blindada contra diferenças de tipo ou chave
            const ehMinha =
              autorId !== undefined &&
              currentUserId !== undefined &&
              String(autorId) === String(currentUserId);

            return (
              <div
                key={msg.id || idx}
                className={`flex ${ehMinha ? 'justify-end' : 'justify-start'}`}
              >
                <div
                  className={`max-w-[70%] rounded-2xl px-4 py-3 ${
                    autorTipo === 'AI'
                      ? 'bubble-ai'
                      : ehMinha
                      ? 'bubble-mine'
                      : 'bubble-other'
                  }`}
                >
                  {!ehMinha && (
                    <p
                      className={`text-xs font-medium mb-1 ${
                        autorTipo === 'AI' ? 'neon-violet' : 'neon'
                      }`}
                    >
                      {autorTipo === 'AI' ? <Bot className="w-3 h-3 inline mr-1"/> : null}
                      {autorNome}
                    </p>
                  )}

                  <p className="text-sm whitespace-pre-wrap">{msg.conteudo}</p>

                  <p className={`text-xs mt-1 ${ehMinha ? 'opacity-70' : 'txt-faint'}`}>
                    {msg.criadoEm
                      ? new Date(msg.criadoEm).toLocaleTimeString('pt-BR', {
                          hour: '2-digit',
                          minute: '2-digit',
                        })
                      : ''}
                  </p>
                </div>
              </div>
            );
          })}

          {/* Indicador de Digitação da IA */}
          {isAiThinking && (
            <div className="flex justify-start">
              <div className="bubble-ai max-w-[70%] rounded-2xl px-4 py-3 flex items-center gap-2">
                <Bot className="w-4 h-4 neon-violet animate-spin"/>
                <span className="text-xs text-purple-300">
                  @Coder está pensando...já já vai digitar
                </span>
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        {/* Input Area */}
        <div className="p-4 border-t divider glass-bar">
          {showCodeEditor && (
            <div className="mb-3 p-3 glass rounded-2xl">
              <div className="flex items-center justify-between mb-2">
                <span className="text-sm txt-dim">Snippet de Código</span>
                <select
                  value={codeLanguage}
                  onChange={(e) => setCodeLanguage(e.target.value)}
                  className="input-field w-auto text-xs py-1"
                >
                  <option value="bash">Bash</option>
                  <option value="docker">Dockerfile</option>
                  <option value="yaml">YAML</option>
                  <option value="python">Python</option>
                  <option value="java">Java</option>
                  <option value="javascript">JavaScript</option>
                  <option value="terraform">Terraform</option>
                  <option value="text">Texto</option>
                </select>
              </div>
              <textarea
                className="code-area"
                value={codeText}
                onChange={(e) => setCodeText(e.target.value)}
                placeholder="Cole ou digite seu código..."
              />
              <div className="flex justify-end gap-2 mt-2">
                <button
                  onClick={() => setShowCodeEditor(false)}
                  className="btn-secondary text-sm"
                >
                  Cancelar
                </button>
                <button onClick={sendCode} className="btn-primary text-sm">
                  Enviar Código
                </button>
              </div>
            </div>
          )}

          <div className="flex items-center gap-2">
            <input
              type="file"
              ref={fileInputRef}
              onChange={handleFileUpload}
              className="hidden"
              accept="image/*,.pdf,.zip,.tar.gz"
            />

            <button
              onClick={() => setShowCodeEditor(!showCodeEditor)}
              className={`icon-btn ${showCodeEditor ? 'icon-btn-on' : ''}`}
              title="Enviar código"
            >
              <Code className="w-5 h-5"/>
            </button>

            <button onClick={sendScreenshot} className="icon-btn" title="Compartilhar imagem">
              <Image className="w-5 h-5"/>
            </button>

            {user.tipo === 'PROFESSOR' && (
              <button
                onClick={() => setShowQuizModal(!showQuizModal)}
                className="icon-btn"
                title="Criar quiz"
              >
                <Trophy className="w-5 h-5 neon-violet"/>
              </button>
            )}

            <input
              type="text"
              className="input-field flex-1"
              value={inputText}
              onChange={(e) => setInputText(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && !e.shiftKey && sendMessage()}
              placeholder="Digite @Coder para perguntar à IA... (Enter para enviar)"
            />

            <button onClick={sendMessage} className="btn-primary p-2.5">
              <Send className="w-4 h-4"/>
            </button>
          </div>

          {/* Quiz Creator Modal */}
          {showQuizModal && user.tipo === 'PROFESSOR' && !activeQuiz && (
            <div className="mt-3 p-4 quiz-panel">
              <h4 className="text-sm font-semibold neon-violet mb-3">Criar Quiz</h4>
              <input
                className="input-field mb-2"
                placeholder="Pergunta..."
                value={quizForm.pergunta}
                onChange={(e) => setQuizForm({ ...quizForm, pergunta: e.target.value })}
              />
              {quizForm.opcoes.map((op, i) => (
                <input
                  type="text"
                  key={i}
                  className="input-field mb-2"
                  placeholder={`Opção ${i + 1}${i === 0 ? ' (correta)' : ''}`}
                  value={op}
                  onChange={(e) => handleOptionChange(i, e.target.value)}
                />
              ))}
              <div className="flex items-center gap-2 mb-3">
                <label className="text-sm txt-dim">Tempo (seg):</label>
                <input
                  type="number"
                  className="input-field w-20 py-1"
                  value={quizForm.tempoLimite}
                  onChange={(e) =>
                    setQuizForm({
                      ...quizForm,
                      tempoLimite: parseInt(e.target.value) || 30,
                    })
                  }
                />
              </div>
              <div className="flex justify-end gap-2">
                <button
                  onClick={() => setShowQuizModal(false)}
                  className="btn-secondary text-sm"
                >
                  Cancelar
                </button>
                <button onClick={createQuiz} className="btn-primary text-sm">
                  Criar Quiz
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ChatRoom;