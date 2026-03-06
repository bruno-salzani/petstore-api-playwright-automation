## 🚀 Test Automation – Petstore API (Rest Assured)

![Status](https://img.shields.io/badge/Status-Ativo-brightgreen)
![Framework](https://img.shields.io/badge/Framework-Rest%20Assured%20%7C%20JUnit%205-green)
![Stack](https://img.shields.io/badge/Stack-Java%2017%20%7C%20Maven-blue)
![Contratos](https://img.shields.io/badge/Contracts-WireMock-orange)
![Performance](https://img.shields.io/badge/Performance-Gatling-purple)
[![CI/CD Pipeline](https://github.com/bruno-salzani/petstore-api-restassured-java/actions/workflows/main.yml/badge.svg)](https://github.com/bruno-salzani/petstore-api-restassured-java/actions/workflows/main.yml)
[![Allure Report](https://img.shields.io/badge/Allure-Report-ff69b4?logo=allure&logoColor=white)](https://bruno-salzani.github.io/petstore-api-restassured-java/)
[![CodeQL](https://github.com/bruno-salzani/petstore-api-restassured-java/actions/workflows/codeql.yml/badge.svg)](https://github.com/bruno-salzani/petstore-api-restassured-java/actions/workflows/codeql.yml)
[![Dependabot](https://img.shields.io/badge/Dependabot-enabled-blue?logo=dependabot)](.github/dependabot.yml)
[![License](https://img.shields.io/badge/License-MIT-lightgrey)](LICENSE)
[![Criado por Bruno Salzani](https://img.shields.io/badge/Criado%20por-Bruno%20Salzani-blue)](https://github.com/bruno-salzani)

Automação de testes de API para a Swagger Petstore, validando fluxos críticos e contratos, com foco em confiabilidade, leitura do código e execução reprodutível em diferentes ambientes.

## Destaques de Engenharia

| Prática | Benefício |
| :--- | :--- |
| **Arquitetura em Camadas** | **Manutenibilidade e Reutilização.** Separação clara de responsabilidades entre Testes, Flows, Clients e Data, facilitando a manutenção e a evolução do projeto. |
| **Observabilidade Profunda** | **Análise de Causa Raiz.** Cabeçalho `traceparent` injetado por filtro, **SLF4J/Logback** para logs estruturados e listener customizado para métricas de execução, permitindo uma análise profunda de falhas. |
| **Data Factories Robustas** | **Testes Confiáveis.** Geração de massa de dados de teste realista e idempotente, crucial para a confiabilidade e a manutenção dos testes. |
| **CI/CD Avançado** | **Qualidade Contínua.** Pipeline com matriz de build, quality gates (Checkstyle, PMD, SpotBugs), análise de segurança (CodeQL) e publicação automática de relatórios. |
| **Resiliência e Confiabilidade** | **Testes Estáveis.** Estratégia de retries com backoff exponencial para lidar com a instabilidade de ambientes. |

## Arquitetura do Framework

A arquitetura foi projetada para ser modular, escalável e de fácil manutenção, seguindo padrões de mercado para testes de API.

```mermaid
graph TD
    subgraph "Camada de Testes (JUnit 5)"
        direction LR
        T1[E2E Tests] --> F
        T2[Consistency Tests] --> C
        T3[Contract Tests] --> C
    end

    subgraph "Camada de Orquestração"
        direction LR
        F(PetFlow) --> C
    end

    subgraph "Camada de Cliente HTTP"
        direction LR
        C(PetClient) --> I
    end

    subgraph "Camada de Dados"
        direction LR
        DF(DataFactory) --> T1
        M[POJOs] --> DF
    end

    subgraph "Infraestrutura & API"
        I[RestAssured] --> A[API Real]
    end

    subgraph "Observabilidade"
        O1[Allure Reports] --> T1
        O2[Logging (SLF4J)] --> I
        O3[Metrics Listener] --> T1
    end

    style T1 fill:#cce5ff,stroke:#333,stroke-width:2px
    style F fill:#d4edda,stroke:#333,stroke-width:2px
```

- **Testes (JUnit 5):** Contêm a lógica de asserção e são divididos por tipo (E2E, Consistência, Contrato).
- **Flows:** Orquestram chamadas de múltiplos `clients` para simular fluxos de negócio complexos.
- **Clients:** Abstraem a complexidade do HTTP, expondo métodos de negócio (ex: `createPet`, `getPet`).
- **Data Layer:** `Data Factories` geram massa de dados de teste, utilizando `POJOs` para representar as entidades da API.
- **Observabilidade:** Ferramentas como `Allure`, `SLF4J` e listeners customizados fornecem visibilidade sobre a execução dos testes.

## Como Executar

O projeto utiliza o Maven Wrapper (`mvnw`), eliminando a necessidade de uma instalação local do Maven.

### Pré-requisitos

- Java 17+
- Docker Desktop (opcional). Se o Docker não estiver disponível, os testes de integração caem automaticamente para a API pública da Petstore.

### Perfis Principais

| Comando | Propósito |
| :--- | :--- |
| `./mvnw -Pfast-checks test` | **Smoke Test.** Valida os fluxos críticos contra a API real. |
| `./mvnw -Pregression test` | **Regressão Completa.** Executa a suíte de integração completa. |
| `./mvnw -Pe2e test` | **E2E.** Executa somente os cenários de ponta a ponta. |
| `./mvnw -Pparallel -Dparallel.threads=4 test` | **Paralelo.** Executa em paralelo com o número de threads informado. |
| `./mvnw -Pperformance gatling:test` | **Performance (Gatling).** Executa a simulação com SLA p95 < 500ms. |

### Modos de Execução

- Com Docker (padrão): Sobe um container da Swagger Petstore v3 localmente via Testcontainers e roda os testes contra ele.
- Sem Docker: Se o Docker não estiver disponível, o framework usa a API pública em `https://petstore.swagger.io/v2` automaticamente.

Você pode forçar o uso de uma URL específica passando a propriedade:

- Linux/macOS: `./mvnw -Dpetstore.baseUrl=https://petstore.swagger.io/v2 test`
- Windows: `.\mvnw.cmd -Dpetstore.baseUrl=https://petstore.swagger.io/v2 test`

### Performance (Gatling)

- Simulação: `src/gatling/java/com/petstore/perf/PetstoreSimulation.java` (FindByStatus)
- Execução: 
  - Linux/macOS: `./mvnw -Pperformance -Dgatling.simulationClass=com.petstore.perf.PetstoreSimulation gatling:test`
  - Windows: `.\mvnw.cmd -Pperformance -Dgatling.simulationClass=com.petstore.perf.PetstoreSimulation gatling:test`
- Base URL: usa `PETSTORE_BASE_URL` (env) ou `petstore.baseUrl` (system prop). Padrão: `https://petstore.swagger.io/v2`.
- Parâmetros: `-Dperf.rps=<n>` e `-Dperf.durationSec=<segundos>` (padrões: 5 RPS por 30s).
- SLA codificado: `p95 < 500ms` e taxa de sucesso > 99%. Violações quebram o build da simulação.
- Relatórios: `target/gatling/` (HTML e métricas detalhadas).

### Atalhos de Execução (Convenientes)

- Smoke: `./mvnw -Pfast-checks test`
- Regressão: `./mvnw -Pregression test`
- E2E: `./mvnw -Pe2e test`
- Paralelo: `./mvnw -Pparallel -Dparallel.threads=4 test`
- Performance padrão: `./mvnw -Pperformance -Dgatling.simulationClass=com.petstore.perf.PetstoreSimulation gatling:test`
- Performance customizada (ex.: 10 RPS por 60s):  
  - Linux/macOS: `./mvnw -Pperformance -Dgatling.simulationClass=com.petstore.perf.PetstoreSimulation -Dperf.rps=10 -Dperf.durationSec=60 gatling:test`  
  - Windows: `.\mvnw.cmd -Pperformance -Dgatling.simulationClass=com.petstore.perf.PetstoreSimulation -Dperf.rps=10 -Dperf.durationSec=60 gatling:test`

### Análise de Qualidade

| Comando | Propósito |
| :--- | :--- |
| `./mvnw verify` | **Quality Gate.** Executa os testes e roda Checkstyle, PMD e formatação (SpotBugs opcional). |
| `./mvnw -Pstatic-analysis verify` | **Quality Gate Completo.** Inclui SpotBugs (exige JDK compatível). |
| `./mvnw -DskipTests verify` | Executa apenas as análises estáticas (sem testes). |
| `./mvnw -Pmutation org.pitest:pitest-maven:mutationCoverage` | **Mutation Testing (PITest).** Mede a eficácia da suíte. Relatório em `target/pit-reports`. |

### Relatórios

- Allure: `./mvnw test && ./mvnw allure:serve` para visualizar localmente.
- Resultados brutos: `target/surefire-reports/`.

### Configurações Importantes

É possível ajustar comportamento e resiliência via propriedades:

- `petstore.slaMs` (padrão: 2000) – SLA máximo por requisição em ms.
- `petstore.retryCount` (padrão: 2) – Número de tentativas em erros transitórios.
- `petstore.retryDelayMs` (padrão: 250) – Atraso base para backoff exponencial.
- `petstore.connectTimeoutMs` (padrão: 2000) – Timeout de conexão HTTP.
- `petstore.socketTimeoutMs` (padrão: 4000) – Timeout de leitura HTTP.
- `petstore.baseUrl` – URL base quando executando sem Docker.
- `enable.chaos=true` ou `ENABLE_CHAOS=true` – Ativa a indução de falha única (429/503) para testar resiliência.
- `JAEGER_BASE_URL` ou `-Djaeger.baseUrl` – Base do Jaeger para link dinâmico no Allure.

### Exemplo de Teste E2E (com Flow e DataFactory)

```java
@Test
@Tag("smoke")
@DisplayName("Deve executar CRUD completo do Pet com sucesso")
void deveExecutarCrudCompletoDoPet() {
    // 1. Massa de dados: Usando a Data Factory
    Pet newPet = PetDataFactory.newPet();
    long petId = newPet.getId();

    // 2. Fluxo de Negócio: Orquestração de chamadas
    step("Cadastrar um novo pet e verificar", () -> {
        petFlow.addPetAndVerify(newPet);
    });

    // 3. Lógica de Teste: Atualização e validação
    step("Atualizar o status do pet para sold", () -> {
        Pet updatedPet = PetDataFactory.updatedPet(newPet);
        petClient.updatePet(updatedPet).then().statusCode(200).body("status", is("sold"));
    });

    // 4. Limpeza: O @AfterEach do BaseApiTest cuida da limpeza
}
```

## Pipeline de CI/CD e Artefatos

Nosso pipeline no GitHub Actions é a espinha dorsal da nossa estratégia de qualidade. Para cada execução, os seguintes artefatos são gerados e podem ser baixados para análise:

- **Allure Report:** Relatório completo dos testes (publicado no GitHub Pages).
- **Surefire Reports:** Relatórios XML brutos dos testes.
- **OWASP Dependency-Check Report:** Relatório de vulnerabilidades das dependências (opcional/local sob demanda).

## Decisões e Trade-offs

| Decisão | Justificativa e Trade-offs |
| :--- | :--- |
| **Java & Maven** | **Justificativa:** Ecossistema maduro, forte tipagem, vasto suporte da comunidade e ferramentas de build robustas. **Trade-off:** Mais verboso que linguagens dinâmicas como Python ou JavaScript. |
| **RestAssured** | **Justificativa:** DSL fluente e expressiva para testes de API em Java, facilitando a escrita e leitura dos testes. **Trade-off:** Focado em Java, menos flexível que clientes HTTP puros para cenários muito complexos. |
| **JUnit 5** | **Justificativa:** Framework de testes moderno, com arquitetura extensível (listeners, extensions), suporte a tags e testes parametrizados. **Trade-off:** Requer uma curva de aprendizado maior que o JUnit 4. |
| **Allure Reports** | **Justificativa:** Gera relatórios ricos e interativos, com screenshots, steps e logs, facilitando a análise de falhas. **Trade-off:** Adiciona uma dependência extra ao build e requer um passo adicional para gerar e servir o relatório. |
| **Arquitetura em Camadas** | **Justificativa:** Separação de responsabilidades (Testes, Flows, Clients, Data), aumentando a manutenibilidade e a reutilização de código. **Trade-off:** Adiciona um boilerplate inicial maior, que pode ser excessivo para projetos muito pequenos. |

## Roadmap do Projeto

- [x] Arquitetura com Service Object Model, Data Factory e Enum de Endpoints.
- [x] Suítes de testes de integração (`smoke`, `regression`, `e2e`).
- [x] Retries com backoff exponencial e timeouts configuráveis.
- [x] Relatórios com Allure e publicação no GitHub Pages (`nightly`).
- [x] CI com GitHub Actions, matriz de build e quality gates.
- [x] Análise de qualidade (Checkstyle, PMD, SpotBugs, Spotless).
- [x] Análise de segurança (CodeQL, OWASP Dependency-Check).
- [x] Governança (Dependabot, CODEOWNERS, SECURITY.md, templates).
- [x] Testes de Contrato com WireMock (básicos e negativos).
- [x] Testes de Performance com Gatling.
- [ ] Integração com SonarCloud para um dashboard de qualidade centralizado.

## Notas de Arquitetura Avançadas

- Validação de Contrato: Os testes utilizam `matchesJsonSchemaInClasspath` contra schemas estáticos em `src/test/resources/schemas` (ex.: `pet.json`), garantindo compatibilidade com o contrato.
- Resiliência com Caos: Um `ChaosFilter` pode ser habilitado com `-Denable.chaos=true` ou `ENABLE_CHAOS=true` para induzir uma falha transitória (503/429) na primeira tentativa por thread e validar o mecanismo de retry/backoff.
- Segurança de Concorrência: `DataFactory` usa `ThreadLocal<Faker>` e o `TracingFilter` injeta `traceparent` com `ThreadLocal`, assegurando paralelismo seguro.
- Observabilidade – Deep Link: Em falhas, o listener adiciona link “View Trace in Jaeger” no Allure, apontando para o `traceId` exato (`traceparent`). Configure `JAEGER_BASE_URL` para apontar ao seu Jaeger.
- Hermeticidade – Janitor: Executado no setup quando usando a API pública; remove pets com tag `automation-<epoch>` criados há >24h, mantendo o ambiente limpo.
- Contratos Estritos: Schemas `schemas/pet-strict.json` e `schemas/error.json` cobrem respostas de sucesso estritas e erros estruturados (ex.: 415).

## Como Contribuir

Consulte o [**CONTRIBUTING.md**](CONTRIBUTING.md) para padrões de código, tags e fluxo de Pull Requests.

## Licença

Este projeto está sob a licença [MIT](LICENSE).
