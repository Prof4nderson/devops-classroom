# Implantação na VPS Hostinger

O projeto executa frontend, backend e PostgreSQL em containers Docker. O Nginx do host termina o HTTPS e encaminha a aplicação para `127.0.0.1:8081`.

## Arquitetura

O serviço `postgres` fica somente na rede Docker e usa o volume persistente `postgres_data`. O backend Spring Boot acessa o banco pelo hostname Docker `postgres` na porta 5432. O frontend Nginx é publicado localmente em `127.0.0.1:8081` e encaminha `/api` e `/ws` para o backend.

## Preparação

Instale Docker, o plugin Docker Compose e Nginx na VPS. Copie `.env.example` para `.env`, gere uma senha forte para `DB_PASSWORD` e uma chave forte para `JWT_SECRET`, e ajuste `CORS_ALLOWED_ORIGINS` para a URL HTTPS real.

```bash
cp .env.example .env
chmod 600 .env
nano .env
docker compose up -d --build

docker compose ps
docker compose logs -f backend
```

Não publique as portas 5432 ou 8080 na internet. O Compose mantém PostgreSQL e backend apenas na rede interna e publica o frontend localmente em `127.0.0.1:8081`.

## Nginx e TLS

Copie `deploy/nginx-devops-classroom.conf` para `/etc/nginx/sites-available/devops-classroom.conf`, substitua `seu-dominio.com`, crie o link simbólico e valide a configuração.

```bash
sudo ln -s /etc/nginx/sites-available/devops-classroom.conf /etc/nginx/sites-enabled/devops-classroom.conf
sudo nginx -t
sudo systemctl reload nginx
```

Depois, emita o certificado TLS com Certbot para o domínio e recarregue o Nginx. O proxy `/api` preserva o prefixo usado pelos controllers Spring. O proxy `/ws` mantém os headers `Upgrade` e `Connection` necessários ao SockJS/STOMP.

## Diagnóstico

```bash
docker compose ps
docker compose logs --tail=200 postgres
docker compose logs --tail=200 backend
docker compose logs --tail=100 frontend
curl -I http://127.0.0.1:8081/
curl -i http://127.0.0.1:8081/api/auth/login
sudo nginx -t
sudo ss -lntp | grep -E ':80|:443|:8081'
```

Se o backend não iniciar, verifique primeiro se o serviço `postgres` está `healthy` e se `DB_NAME`, `DB_USER` e `DB_PASSWORD` no `.env` estão corretos. Em uma instalação nova, o banco é criado automaticamente. Não remova o volume `postgres_data` em uma instalação com dados sem possuir backup.
