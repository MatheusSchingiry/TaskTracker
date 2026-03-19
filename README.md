# TaskTracker

> Aplicação de gerenciamento de projetos e tarefas com interface web e cliente de terminal de alta performance.

**Java 21** • **Spring Boot 3** • **PostgreSQL 16** • **Redis 7** • **TypeScript/Angular**

---

## ⚠️ Aviso — Estado Atual do Projeto

> **Esta é a primeira versão do TaskTracker e o projeto ainda está em desenvolvimento ativo, sem previsão de conclusão.**

Antes de utilizar ou contribuir, esteja ciente das seguintes limitações atuais:

- 🔴 **Sem autenticação** — não há nenhum módulo de autenticação implementado. Os endpoints `/admin` estão acessíveis sem qualquer validação de credencial
- 🔴 **Sem validação de credenciais** — qualquer requisição às rotas administrativas é aceita sem verificação de identidade
- 🟡 **Execução apenas local** — o sistema ainda não possui ambiente de produção configurado. É necessário rodar todos os serviços (servidor, banco de dados e cache) localmente na própria máquina
- 🟡 **Em constante atualização** — funcionalidades, endpoints e estrutura de dados podem sofrer alterações sem aviso prévio

Estas limitações são conhecidas e fazem parte do roadmap de evolução do projeto.

---

## Visão Geral

O TaskTracker é um sistema completo para gerenciamento de projetos e tarefas, construído sobre dois pilares principais:

**Terminal Linux** — Interface CLI inicializada automaticamente junto ao sistema via systemd, com layout visual em ASCII Art, cores ANSI e auto-refresh a cada 30 segundos.

**Interface Web** — Frontend em TypeScript/Angular com formulários de criação, listagem com filtros, busca por nome e endpoints separados para Admin e Public.

---

## Arquitetura do Projeto

```
TaskTracker/
├── tasktracker-server/                         # Backend REST API (Java 21 + Spring Boot)
│   └── src/main/java/com/tasktracker/
│       ├── config/                             # RedisConfig, SecurityConfig
│       ├── controller/
│       │   ├── admin/                          # AdminProjectController, AdminTaskController
│       │   └── pub/                            # PublicProjectController
│       ├── service/                            # ProjectService, TaskService (@Cacheable/@CacheEvict)
│       ├── repository/                         # ProjectRepository, TaskRepository
│       ├── model/                              # Entidades JPA: Project, Task
│       └── dto/                               # ProjectDTO, TaskDTO
│
├── tasktracker-cli/                            # Interface de linha de comando (Java)
│   └── src/main/java/com/tasktracker/cli/
│       ├── dto/                               # DTOs do cliente CLI
│       ├── ApiClient.java                     # Cliente HTTP para consumir a API REST
│       ├── Main.java                          # Ponto de entrada da CLI
│       └── TaskTrackerCLI.java               # Interface visual (ASCII Art + ANSI colors)
│
├── tasktracker-webclient/                      # Frontend (TypeScript + Angular + SCSS)
│   └── src/app/
│       ├── components/
│       │   ├── layout/                        # Componente de layout base
│       │   ├── projects/                      # Listagem e formulários de projetos
│       │   └── tasks/                         # Listagem e formulários de tasks
│       ├── models/
│       │   ├── project.model.ts               # Interface/tipo do Projeto
│       │   └── task.model.ts                  # Interface/tipo da Task
│       ├── services/
│       │   ├── project.service.ts             # Chamadas HTTP para endpoints de projetos
│       │   ├── task.service.ts                # Chamadas HTTP para endpoints de tasks
│       │   └── translation.service.ts         # Serviço de internacionalização (i18n)
│       ├── app.component.ts                   # Componente raiz
│       ├── app.config.ts                      # Configuração da aplicação
│       └── app.routes.ts                      # Definição de rotas
│
└── pom.xml                                    # Maven multi-módulo
```

---

## Stack Tecnológico

| Tecnologia | Versão | Função |
|---|---|---|
| Java | 21 LTS (OpenJDK) | Linguagem principal do backend |
| Spring Boot | 3.x (Spring Framework 6) | Framework REST + Auto-config |
| Spring Data JPA | Incluso no Spring Boot 3.x | ORM / persistência de dados |
| Spring Cache | Incluso no Spring Boot 3.x | Abstração de cache com Redis |
| PostgreSQL | 16.x | Banco de dados relacional principal |
| Redis | 7.x | Cache de leitura (projetos/tasks) |
| Maven | 3.9+ | Build e gerenciamento de dependências |
| TypeScript / Angular | — | Interface web (webclient) |
| SCSS | — | Estilização do cliente web |

---

## Pré-requisitos

- [Java 21+](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)
- [PostgreSQL 16+](https://www.postgresql.org/)
- [Redis 7+](https://redis.io/)
- [Node.js 18+](https://nodejs.org/) + [Angular CLI](https://angular.io/cli) *(para o webclient)*

---

## Como Executar

### 1. Clonar o repositório

```bash
git clone https://github.com/MatheusSchingiry/TaskTracker.git
cd TaskTracker
```

### 2. Configurar o banco de dados

Execute o script DDL no PostgreSQL:

```sql
-- Tabela Projetos
CREATE TABLE projetos (
  id          BIGSERIAL PRIMARY KEY,
  nome        VARCHAR(150) NOT NULL,
  descricao   TEXT,
  tecnologias VARCHAR(500),
  status      VARCHAR(30)  NOT NULL DEFAULT 'ATIVO',
  criado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Tabela Tasks
CREATE TABLE tasks (
  id             BIGSERIAL PRIMARY KEY,
  fk_projeto_id  BIGINT NOT NULL REFERENCES projetos(id) ON DELETE CASCADE,
  nome           VARCHAR(200) NOT NULL,
  descricao      TEXT,
  status         VARCHAR(30)  NOT NULL DEFAULT 'PENDENTE',
  criado_em      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tasks_projeto ON tasks(fk_projeto_id);
```

### 3. Configurar variáveis de ambiente

```bash
export DB_PASSWORD=sua_senha_postgres
```

### 4. Configurar o `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tasktracker
    username: postgres
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  data:
    redis:
      host: localhost
      port: 6379
  cache:
    type: redis
    redis:
      time-to-live: 300000   # 5 minutos em ms
      cache-null-values: false
```

### 5. Build e execução do servidor

```bash
mvn clean install
cd tasktracker-server
mvn spring-boot:run
```

O servidor estará disponível em: `http://localhost:8080`

### 6. Executar o cliente web

```bash
cd tasktracker-webclient
npm install
ng serve
```

O cliente web estará disponível em: `http://localhost:4200`

### 7. Executar a CLI

```bash
cd tasktracker-cli
mvn exec:java
```

---

## API — Endpoints

A API é dividida em dois grupos: **Admin** (leitura e escrita) e **Public** (somente leitura, sem autenticação).

> ⚠️ **Atenção:** Nenhum dos endpoints possui autenticação ou proteção de acesso no momento.

### Admin — Projetos

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/admin/Project/create` | Criar novo projeto |
| `GET` | `/admin/Project/read` | Listar todos os projetos |
| `GET` | `/admin/Project/readByName?nome={nome}` | Buscar projeto por nome |
| `PUT` | `/admin/Project/edit/{id}` | Atualizar dados do projeto |
| `DELETE` | `/admin/Project/delete/{id}` | Remover projeto |

### Admin — Tasks (por Projeto)

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/admin/{projectId}/task/create` | Criar nova task no projeto |
| `GET` | `/admin/{projectId}/task/read` | Listar todas as tasks do projeto |
| `GET` | `/admin/{projectId}/task/readByName?nome={nome}` | Buscar task por nome |
| `PUT` | `/admin/{projectId}/task/edit/{taskId}` | Atualizar dados da task |
| `DELETE` | `/admin/{projectId}/task/delete/{taskId}` | Remover task do projeto |

### Public — Leitura (sem autenticação)

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/public/Project/read` | Listar todos os projetos |
| `GET` | `/public/Project/readByName?nome={nome}` | Buscar projeto por nome |
| `GET` | `/public/{projectId}/task/read` | Listar tasks de um projeto |
| `GET` | `/public/{projectId}/task/readByName?nome={nome}` | Buscar task por nome |

---

## Modelo de Dados

### Tabela `projetos`

| Coluna | Tipo | Nulo | Descrição |
|---|---|---|---|
| `id` | BIGSERIAL | NOT NULL (PK) | Identificador autoincremental |
| `nome` | VARCHAR(150) | NOT NULL | Nome do projeto |
| `descricao` | TEXT | NULL | Descrição detalhada |
| `tecnologias` | VARCHAR(500) | NULL | Stack separada por vírgula |
| `status` | VARCHAR(30) | NOT NULL | `ATIVO` \| `PAUSADO` \| `CONCLUIDO` \| `CANCELADO` |
| `criado_em` | TIMESTAMP | NOT NULL | Data/hora de criação (UTC) |

### Tabela `tasks`

| Coluna | Tipo | Nulo | Descrição |
|---|---|---|---|
| `id` | BIGSERIAL | NOT NULL (PK) | Identificador autoincremental |
| `fk_projeto_id` | BIGINT | NOT NULL (FK) | Referência ao projeto (CASCADE DELETE) |
| `nome` | VARCHAR(200) | NOT NULL | Nome da task |
| `descricao` | TEXT | NULL | Descrição detalhada |
| `status` | VARCHAR(30) | NOT NULL | `PENDENTE` \| `EM_PROGRESSO` \| `CONCLUIDA` \| `CANCELADA` |
| `criado_em` | TIMESTAMP | NOT NULL | Data/hora de criação (UTC) |

---

## Cache com Redis

| Cache Key | Operação | TTL | Invalidação |
|---|---|---|---|
| `projetos:all` | GET `/public/Project/read` | 5 min | POST / PUT / DELETE Projeto |
| `projetos:name:{nome}` | GET `readByName` (projetos) | 5 min | PUT / DELETE Projeto |
| `tasks:projeto:{id}:all` | GET `/public/{id}/task/read` | 5 min | POST / PUT / DELETE Task |
| `tasks:projeto:{id}:name:{nome}` | GET `readByName` (tasks) | 5 min | PUT / DELETE Task |

Utiliza as anotações `@Cacheable`, `@CacheEvict` e `@CachePut` do Spring Cache, com `RedisTemplate` configurado com serialização JSON (Jackson).

---

## Interface CLI — Terminal

A CLI é exibida com banner ASCII Art, tabela formatada e cores ANSI por status:

```
╔══════════════════════════════════════════════════════════╗
║  ████████╗ █████╗ ███████╗██╗  ██╗                       ║
║     ██╔══╝██╔══██╗██╔════╝██║ ██╔╝                       ║
║     ██║   ███████║███████╗█████╔╝                        ║
║     ██║   ██╔══██║╚════██║██╔═██╗                        ║
║     ██║   ██║  ██║███████║██║  ██╗                       ║
║     ╚═╝   ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝  TaskTracker v1.0    ║
╚══════════════════════════════════════════════════════════╝

[PROJETOS] Total: 4   Ativos: 3   Concluídos: 1
──────────────────────────────────────────────────────────
 #   PROJETO          STATUS        TASKS   CRIADO EM
──────────────────────────────────────────────────────────
 1   Backend API      [ATIVO]         12    2026-01-10
 2   Dashboard Web    [ATIVO]          8    2026-02-05
 3   Mobile App       [PAUSADO]        3    2026-02-20
 4   Deploy Infra     [CONCLUIDO]      5    2025-12-01
──────────────────────────────────────────────────────────
[q] Sair   [r] Atualizar   [p] Ver tasks de projeto
```

**Cores ANSI:** 🟢 Verde = `ATIVO` · 🟡 Amarelo = `PAUSADO` · 🔵 Azul = `CONCLUIDO` · 🔴 Vermelho = `CANCELADO`

---

## Interface Web — Componentes

| Componente | Descrição |
|---|---|
| **Listagem de Projetos** | Tabela com Nome, Status, Tecnologias, Criado Em e botão de expansão de tasks |
| **Formulário — Criar Projeto** | Campos: Nome*, Descrição, Tecnologias, Status → `POST /admin/Project/create` |
| **Listagem de Tasks** | Tabela aninhada por projeto com Nome, Status, Criado Em e ações (editar/deletar) |
| **Formulário — Criar Task** | Campos: Nome*, Descrição, Status → vinculado ao `projectId` selecionado |
| **Busca por Nome** | Input de pesquisa no topo da listagem, consome `readByName` via query param |

---

## Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/minha-feature`)
3. Commit suas alterações (`git commit -m 'feat: adiciona minha feature'`)
4. Faça o push (`git push origin feature/minha-feature`)
5. Abra um Pull Request

---

## Autor

**Matheus Schingiry** — [GitHub](https://github.com/MatheusSchingiry)

---

*TaskTracker v1.0 · Primeira versão · Em desenvolvimento · Março 2026*
