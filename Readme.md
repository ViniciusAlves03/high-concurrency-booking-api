# High Concurrency Booking API

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/Rabbitmq-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![K6](https://img.shields.io/badge/k6-%237D64FF.svg?style=for-the-badge&logo=k6&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)

Este é o repositório da **High Concurrency Booking API**, uma API de venda de ingressos projetado para suportar picos extremos de tráfego.

O objetivo principal deste projeto é demonstrar como garantir a consistência de dados e alta disponibilidade quando milhares de usuários tentam comprar o mesmo assento simultaneamente.

Construído com Java 25 e Spring Boot 4.0.0, o projeto utiliza **Virtual Threads**, **processamento assíncrono** com RabbitMQ e **Distributed Locking** com Redis.

*Nota: Os testes de carga/desempenho desenvolvidos estão em um [repositório](https://github.com/ViniciusAlves03/high-concurrency-booking-k6) separado.

## ✨ Principais Características e Funcionalidades

* **Venda de Ingressos Assíncrona:**
    * A API recebe a requisição e responde imediatamente (202 Accepted), delegando o processamento pesado para background workers.
* **Controle de Concorrência (Distributed Lock):**
    * Utiliza Redis para garantir que apenas uma transação por vez tente reservar um assento específico, aliviando a carga no banco de dados.
* **Java Virtual Threads:**
    * Uso de Java Virtual Threads para lidar com milhares de conexões simultâneas com baixo overhead.
* **Resiliência e Retries:**
    * Configuração de RabbitMQ com políticas de tentativas (retry) e Dead Letter Queues (DLQ) para falhas persistentes.
* **Validação de Dados:**
    * Validação robusta de IDs (Mongo ObjectIds), datas (formato YYYY-MM-DD) e campos obrigatórios.
* **Testcontainers:**
    * Suporte a Testcontainers para testes de integração reais e ambiente de desenvolvimento isolado..

## 🚀 Tecnologias Utilizadas

* **Linguagem:** Java 25
* **Framework:** Spring Boot 4.0.0 (Snapshot)
* **Banco de Dados:** PostgreSQL
* **Cache & Lock:** Redis
* **Mensageria:** RabbitMQ
* **Testes de Carga:** Grafana k6
* **Containerização:** Docker & Docker Compose

## 📋 Pré-requisitos

Para executar este projeto, você precisará ter os seguintes serviços instalados e em execução:

* JDK 25, preferencialmente o Oracle OpenJDK 25.0.1.
* Docker e Docker Compose.

## ⚙️ Instalação e Execução

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/ViniciusAlves03/high-concurrency-booking-api.git
    cd high-concurrency-booking-api
    ```

2. **Configure as variáveis de ambiente:**
   Crie um arquivo `.env` na raiz do projeto, baseado no `.env.example`. Você pode usar o seguinte comando:
    ```bash
    cp .env.example .env
    ```

3. **Inicie os serviços (PostgreSQL, Redis e RabbitMQ):**
    Use o arquivo `docker-compose.yaml` para iniciar os containers das dependências em background.
    ```bash
    docker compose up -d --build
    ```

4. **Inicie os serviços (PostgreSQL, Redis e RabbitMQ):**
   Use o comando `./mvnw spring-boot:run` no terminal ou configure sua IDE da seguinte maneira:
   * Importe o projeto como um projeto Maven.
   * Certifique-se de que o SDK do projeto está definido como Java 25.
   * Execute a classe principal: HighConcurrencyBookingApiApplication.java.

A API estará disponível em: `http://localhost:8080`.

*Nota: O `DataSetup` irá inserir automaticamente 100 assentos no banco de dados na primeira execução.*

## 🏗️ Estrutura do Projeto

```sh
src/main/java/com/hcb/highconcurrencybookingapi/
├── background/      # Workers: consome filas e processa vendas (TicketWorker)
├── config/          # Configs: RabbitMQ, Setup de Dados
├── controller/      # API: Recebe requisições HTTP
├── dto/             # Records: Contratos de dados (Request/Response)
├── exception/       # Tratamento global de erros
├── model/           # Entidades JPA (Seat)
├── repository/      # Acesso ao banco de dados
├── service/         # Regras de Negócio (Locks, Validações)
└── utils/           # Mensagens e Constantes

k6/
├── scripts/         # Scripts de teste de carga (JS)
└── docker-compose.yaml # Configuração para rodar o K6 via Docker
```

## 📖 Documentação da API (Swagger)

Com a aplicação rodando, acesse a documentação interativa:

👉 Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### 🎟️ Tickets Endpoints

Rotas para comprar um ticket/assento e verificar o status da compra.

| Método | Rota (Path)                 | Descrição                                                                         |
|:-------|:----------------------------|:----------------------------------------------------------------------------------|
| `POST` | `/v1/tickets/buy`           | Enfileira um pedido de compra. Retorna `202 Accepted` e um `requestId`.           |
| `GET`  | `/v1/tickets/status/{requestId}` | Consulta o status do pedido (`CONFIRMED`, `FAILED_SEAT_TAKEN`, `ERROR_RETRYING`). |

---

## 🧑‍💻 Autor <a id="autor"></a>

<p align="center">Desenvolvido por Vinícius Alves <strong><a href="https://github.com/ViniciusAlves03">(eu)</a></strong>.</p>

---
