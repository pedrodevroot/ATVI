# AutoBots — Microsserviço de Cadastro de Clientes

API REST para o CRUD de clientes do sistema de gestão **AutoBots**, voltado a
lojas de manutenção veicular e venda de autopeças.

Primeiro microsserviço do projeto — os próximos (veículos, serviços, vendas)
serão containers independentes na mesma rede Docker.

**Atividade ATVI** — Prof. Dr. Eng. Gerson Penha

---

## Tecnologias

| | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 2.6.3 |
| Spring Data JPA / Hibernate | 5.6 |
| PostgreSQL | 16 |
| Docker Compose | — |

## O que precisa estar instalado na máquina

**Apenas o Docker Desktop** (ou Docker + Docker Compose no Linux).

Java, Maven e IDE **não** são necessários para executar. A compilação e a
execução acontecem dentro dos containers: o `Dockerfile` baixa o Maven, compila o
projeto e gera a imagem final contendo apenas o JRE e o `.jar`.

As portas `18080` (API) e `5433` (PostgreSQL) precisam estar livres.

---

## Como executar

```bash
git clone https://github.com/pedrodevroot/ATVI.git
cd ATVI
docker compose up -d --build
```

Nada para configurar: usuário, senha e nome do banco já estão no
`docker-compose.yml`.

| | Endereço |
|---|---|
| API | http://localhost:18080 |
| PostgreSQL | localhost:5433 — base/usuário/senha: `autobots` |

O comando sobe dois containers: o PostgreSQL e o microsserviço. Na primeira
subida, um cliente de demonstração é cadastrado automaticamente.

### Conferir se subiu

```bash
docker compose ps
curl http://localhost:18080/cliente/clientes
```

### Parar

```bash
docker compose down        # para os containers e mantém os dados
docker compose down -v     # para e apaga o banco
```

---

## Como executar os testes

```bash
./mvnw test          # Windows: .\mvnw.cmd test
```

São **175 testes automatizados**, em cerca de 20 segundos.

Os testes **não precisam de Docker nem de PostgreSQL** — os de integração usam
banco H2 em memória. Precisam apenas de **Java 17** instalado; o Maven vem junto
com o projeto, no `mvnw`.

| Camada | Testes | Ferramenta |
|---|---|---|
| Regras de domínio | 29 | JUnit 5 |
| Conversores DTO | 24 | JUnit 5 |
| Serviços | 46 | JUnit 5 + Mockito |
| Controllers | 54 | MockMvc |
| Mapeamento JPA | 12 | H2 |
| Integração ponta a ponta | 10 | H2 |

Para rodar os testes sem instalar Java, dentro de um container:

```bash
docker run --rm -v "${PWD}:/app" -w /app maven:3.8.4-eclipse-temurin-17 mvn test
```

---

## Endpoints

Base: `http://localhost:18080`

Em todo `POST` e `PUT` o corpo é JSON (`Content-Type: application/json`).

Depois do `docker compose up`, o banco contém: cliente `1`, documentos `1` e `2`,
endereço `1` e telefone `1`.

### Cliente

| Método | URL | Corpo | Esperado |
|---|---|---|---|
| `GET` | `/cliente/clientes` | — | 200 |
| `GET` | `/cliente/1` | — | 200 |
| `POST` | `/cliente/cadastro` | **A** | 201 |
| `POST` | `/cliente/cadastro` | **B** | 201 |
| `PUT` | `/cliente/atualizar` | **C** | 200 |
| `DELETE` | `/cliente/excluir/2` | — | 204 |

### Telefone

| Método | URL | Corpo | Esperado |
|---|---|---|---|
| `GET` | `/cliente/1/telefones` | — | 200 |
| `POST` | `/cliente/1/telefones` | **D** | 201 |
| `GET` | `/telefone/1` | — | 200 |
| `PUT` | `/telefone/1` | `{ "ddd": "99" }` | 200 |
| `DELETE` | `/telefone/1` | — | 204 |

### Documento

| Método | URL | Corpo | Esperado |
|---|---|---|---|
| `GET` | `/cliente/1/documentos` | — | 200 |
| `POST` | `/cliente/1/documentos` | **E** | 201 |
| `GET` | `/documento/1` | — | 200 |
| `PUT` | `/documento/1` | `{ "tipo": "RG-novo" }` | 200 |
| `DELETE` | `/documento/1` | — | 204 |

### Endereço

Um cliente pode ter **vários** endereços.

| Método | URL | Corpo | Esperado |
|---|---|---|---|
| `GET` | `/cliente/1/enderecos` | — | 200 |
| `POST` | `/cliente/1/enderecos` | **F** | 201 |
| `GET` | `/endereco/1` | — | 200 |
| `PUT` | `/endereco/1` | `{ "cidade": "Niteroi" }` | 200 |
| `DELETE` | `/endereco/1` | — | 204 |

### Corpos

**A** — cadastro simples
```json
{ "nome": "Maria Silva" }
```

**B** — cadastro completo
```json
{
  "nome": "Joana Ferreira de Souza",
  "nomeSocial": "Joana",
  "dataNascimento": "1995-08-20T00:00:00.000+00:00",
  "documentos": [
    { "tipo": "RG", "numero": "998877" },
    { "tipo": "CPF", "numero": "12345678901" }
  ],
  "enderecos": [
    {
      "estado": "Sao Paulo", "cidade": "Santos", "bairro": "Gonzaga",
      "rua": "Avenida Ana Costa", "numero": "250",
      "codigoPostal": "11060002", "informacoesAdicionais": "Apartamento 32"
    }
  ],
  "telefones": [
    { "ddd": "13", "numero": "981234567" },
    { "ddd": "13", "numero": "32221100" }
  ]
}
```

**C** — atualização parcial: só os campos enviados são alterados. Para alterar um
filho, o `id` dele é obrigatório.
```json
{
  "id": 1,
  "nomeSocial": "Dom Pedro I",
  "enderecos": [ { "id": 1, "codigoPostal": "11015200" } ],
  "telefones": [ { "id": 1, "ddd": "11" } ]
}
```

**D** — telefone
```json
{ "ddd": "13", "numero": "988887777" }
```

**E** — documento
```json
{ "tipo": "CNH", "numero": "55443322" }
```

**F** — endereço
```json
{
  "estado": "Sao Paulo", "cidade": "Santos", "bairro": "Gonzaga",
  "rua": "Avenida Ana Costa", "numero": "250", "codigoPostal": "11060002"
}
```

> Os `id` nas URLs valem para o banco recém-criado. Cada `POST` devolve o `id`
> gerado no corpo da resposta e no header `Location` — é esse valor que deve ser
> usado nas requisições seguintes.

### Casos de erro

| Método | URL | Corpo | Esperado |
|---|---|---|---|
| `GET` | `/cliente/999` | — | 404 |
| `GET` | `/telefone/999` | — | 404 |
| `GET` | `/documento/999` | — | 404 |
| `GET` | `/endereco/999` | — | 404 |
| `DELETE` | `/cliente/excluir/999` | — | 404 |
| `POST` | `/cliente/999/telefones` | `{ "ddd": "13", "numero": "981234567" }` | 404 |
| `POST` | `/cliente/cadastro` | `{ "nomeSocial": "So apelido" }` | 400 |
| `POST` | `/cliente/cadastro` | `{ "nome": "X", "dataNascimento": "2099-01-01T00:00:00.000+00:00" }` | 400 |
| `POST` | `/cliente/cadastro` | **G** | 400 |
| `POST` | `/cliente/1/telefones` | `{ "ddd": "1", "numero": "abc" }` | 400 |
| `POST` | `/cliente/1/enderecos` | `{ "bairro": "Centro" }` | 400 |
| `PUT` | `/endereco/1` | `{ "codigoPostal": "123" }` | 400 |
| `PUT` | `/cliente/atualizar` | `{ "nome": "Sem Id" }` | 400 |
| `PUT` | `/cliente/atualizar` | `{ "id": 1, "telefones": [ { "ddd": "11" } ] }` | 400 |
| `POST` | `/cliente/cadastro` | **H** | 409 |
| `POST` | `/cliente/cadastro` | `{ "id": 1, "nome": "Tentativa" }` | 201, cria um novo |

**G** — vários campos inválidos ao mesmo tempo
```json
{
  "nome": "Invalido",
  "telefones": [ { "ddd": "1", "numero": "abc" } ],
  "enderecos": [ { "bairro": "Centro" } ],
  "documentos": [ { "tipo": "" } ]
}
```

**H** — número de documento duplicado (o RG `1500` já pertence ao cliente 1)
```json
{ "nome": "Documento Repetido", "documentos": [ { "tipo": "RG", "numero": "1500" } ] }
```

### Respostas de erro

```json
{
  "momento": "2026-09-27T18:01:30.026",
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Cliente de id 999 nao foi encontrado",
  "caminho": "/cliente/999"
}
```

Erros de validação acrescentam a lista de campos, todos de uma vez:

```json
{
  "status": 400,
  "mensagem": "Os dados enviados nao passaram na validacao.",
  "erros": [
    { "campo": "nome", "mensagem": "O nome e obrigatorio" },
    { "campo": "telefones[0].ddd", "mensagem": "O DDD deve conter exatamente 2 digitos" },
    { "campo": "enderecos[0].cidade", "mensagem": "A cidade e obrigatoria" }
  ]
}
```

### Validações

| Campo | Regra |
|---|---|
| `cliente.nome` | obrigatório no cadastro |
| `cliente.dataNascimento` | se informada, deve estar no passado |
| `cliente.dataCadastro` | definido pelo servidor, não aceito na entrada |
| `documento.tipo` · `numero` | obrigatórios; `numero` é único no sistema |
| `telefone.ddd` | 2 dígitos |
| `telefone.numero` | 8 ou 9 dígitos |
| `endereco.cidade` · `rua` · `numero` | obrigatórios |
| `endereco.codigoPostal` | se informado, 8 dígitos |
| `id` dos filhos na atualização | obrigatório |

---

## Alterações em relação ao código inicial

| Problema | Correção |
|---|---|
| `Long` comparado com `==` nos atualizadores — compara referência, não valor. Funcionava para ids de 1 a 127 (cache de `Long` da JVM) e falhava silenciosamente a partir de 128 | `.equals()` |
| Prefixo repetido gerava a rota `/cliente/cliente/{id}` | `@GetMapping("/{id}")` |
| `codigoPostal` não era atualizado — o CEP era imutável pela API | bloco acrescentado ao `EnderecoAtualizador` |
| `NullPointerException` no `EnderecoAtualizador` quando o endereço existente era nulo | guarda contra nulo |
| `POST /cadastro` aceitava `id` e o `save()` fazia *upsert* — era possível sobrescrever um cliente existente | `id` removido do DTO de cadastro |
| `dataCadastro` ficava nula quando não enviada | definida pelo `ClienteServico` |
| Listagens sem `ORDER BY` — a mesma requisição podia devolver ordens diferentes | `findAll(Sort.by("id"))` e `@OrderBy("id")` nas coleções |
