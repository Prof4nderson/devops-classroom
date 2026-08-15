import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { ChatMessage, PresenceUpdate } from '../types';

export class ChatWebSocket {
  private stompClient: Stomp.Client | null = null;
  private socket: WebSocket | null = null;
  private aulaId: number;
  private onMessage: (msg: ChatMessage) => void;
  private onPresence: (data: PresenceUpdate) => void;
  private onQuiz: (data: any) => void;
  private isConnecting: boolean = false;

  constructor(
    aulaId: number,
    onMessage: (msg: ChatMessage) => void,
    onPresence: (data: PresenceUpdate) => void,
    onQuiz: (data: any) => void
  ) {
    this.aulaId = aulaId;
    this.onMessage = onMessage;
    this.onPresence = onPresence;
    this.onQuiz = onQuiz;
  }

  connect() {
    if (this.isConnecting || this.stompClient?.connected) return;
    this.isConnecting = true;

    const token = localStorage.getItem('token') || '';
    
    const protocol = window.location.protocol === 'https:' ? 'https' : 'http';
    const socket = new SockJS(`${protocol}://${window.location.host}/ws?token=${encodeURIComponent(token)}`);
    this.socket = socket as any;
    this.stompClient = Stomp.over(socket);

    this.stompClient.debug = () => {};

    const headers = {
      Authorization: token ? `Bearer ${token}` : '',
    };

    this.stompClient.connect(
      headers,
      () => {
        this.isConnecting = false;
        console.log('STOMP Conectado com sucesso!');

        if (!this.stompClient) return;

        this.stompClient.subscribe(`/topic/chat/${this.aulaId}`, (message) => {
          if (message.body) {
            this.onMessage(JSON.parse(message.body));
          }
        });

        this.stompClient.subscribe(`/topic/presence/${this.aulaId}`, (message) => {
          if (message.body) {
            this.onPresence(JSON.parse(message.body));
          }
        });

        this.stompClient.subscribe(`/topic/quiz/${this.aulaId}`, (message) => {
          if (message.body) {
            this.onQuiz(JSON.parse(message.body));
          }
        });

        this.stompClient.send(`/app/presence/${this.aulaId}/join`);
      },
      (error) => {
        this.isConnecting = false;
        console.error('Erro de conexão no WebSocket:', error);
      }
    );
  }

  sendMessage(
    conteudo: string,
    tipo: string = 'TEXT',
    urlMidia?: string,
    nomeArquivo?: string,
    mimeType?: string
  ) {
    if (this.stompClient?.connected) {
      const mensagem = {
        aulaId: this.aulaId,
        conteudo,
        tipo,
        urlMidia,
        nomeArquivo,
        mimeType,
      };
      this.stompClient.send(`/app/chat/${this.aulaId}/send`, {}, JSON.stringify(mensagem));
    }
  }

  createQuiz(pergunta: string, opcoes: string[], quizId: number) {
    if (this.stompClient?.connected) {
      const quizData = {
        aulaId: this.aulaId,
        pergunta,
        opcoes,
        quizId,
      };
      this.stompClient.send(`/app/quiz/${this.aulaId}/create`, {}, JSON.stringify(quizData));
    }
  }

  respondQuiz(quizId: number, resposta: string) {
    if (this.stompClient?.connected) {
      const data = {
        aulaId: this.aulaId,
        quizId,
        resposta,
      };
      this.stompClient.send(`/app/quiz/${this.aulaId}/respond`, {}, JSON.stringify(data));
    }
  }

  leave() {
    this.isConnecting = false;

    if (this.stompClient?.connected) {
      try {
        this.stompClient.send(`/app/presence/${this.aulaId}/leave`);
      } catch (e) {
        // Ignora erros ao tentar sair
      }
    }

    if (this.stompClient) {
      try {
        const client = this.stompClient;
        this.stompClient = null;
        client.disconnect(() => {});
      } catch (e) {
        // Ignora erros ao desconectar
      }
    }

    if (this.socket) {
      try {
        this.socket.close();
      } catch (e) {
        // Ignora
      }
      this.socket = null;
    }
  }
}