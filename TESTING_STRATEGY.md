# Estratégia de Testes

Este documento descreve a filosofia e a estratégia de testes para o projeto `petstore-api-test-automation`, garantindo a qualidade, confiabilidade e manutenibilidade do software.

## 1. Filosofia de Qualidade

Nossa abordagem é baseada no conceito de **Qualidade Contínua**, onde a qualidade é responsabilidade de todos e é construída em cada etapa do ciclo de vida de desenvolvimento, não como uma fase final. Priorizamos a automação para fornecer feedback rápido e confiável.

## 2. Pirâmide de Testes

Adotamos uma abordagem de pirâmide de testes para otimizar a eficácia e o custo da automação.

```
      /\      <-- Testes de Integração (E2E)
     /  \         (Lentos, frágeis, caros)
    /____\    <-- Testes de Contrato (rápidos, estáveis)
   /______\   <-- Testes de Unidade (não aplicável aqui, mas parte da teoria)
```

### 2.1. Testes de Contrato (a base da nossa pirâmide)

- **Propósito:** Validar a lógica de negócio e a conformidade com a especificação da API de forma rápida e isolada.
- **Ferramentas:** JUnit 5, RestAssured, **WireMock**, **Testcontainers**, **OpenAPI Validator**.
- **Escopo:**
  - Validação de todos os endpoints da camada de `service`.
  - Cenários positivos (caminho feliz).
  - Cenários negativos (e.g., 404 Not Found).
  - Validação de contrato contra a especificação OpenAPI (`swagger.json`).
- **Execução:** Rodam a cada commit no CI (`mock-tests` profile). Fornecem feedback em **segundos**.

### 2.2. Testes de Integração

- **Propósito:** Validar o comportamento do sistema de ponta a ponta, interagindo com o ambiente real da API Petstore.
- **Ferramentas:** JUnit 5, RestAssured.
- **Escopo:**
  - **Smoke Tests (`fast-checks`):** Um subconjunto pequeno e rápido que valida os fluxos mais críticos (e.g., criar e obter um pet). Roda a cada commit.
  - **Regression Tests (`regression`):** A suíte completa que cobre todos os cenários de negócio, incluindo consistência, validação de headers e casos de borda. Roda em PRs e `nightly`.
  - **E2E Tests (`e2e`):** Focado em um fluxo de usuário completo (CRUD).
- **Estratégia de Resiliência:** Devido à instabilidade do ambiente público, estes testes utilizam uma estratégia de **retry com backoff exponencial e jitter** para mitigar falsos negativos.

## 3. Testes Não-Funcionais

### 3.1. Qualidade de Código

- **Ferramentas:** Checkstyle, PMD, SpotBugs, Spotless.
- **Estratégia:** Integrado ao build do Maven (`verify` phase). O CI falhará se houver violações, garantindo um padrão de código consistente e de alta qualidade.

### 3.2. Segurança

- **Ferramentas:** OWASP Dependency-Check, CodeQL.
- **Estratégia:**
  - **Dependency-Check:** Roda no CI para escanear as dependências em busca de vulnerabilidades conhecidas (CVEs).
  - **CodeQL:** Análise estática de segurança contínua para identificar padrões de código vulneráveis.

### 3.3. Testes de Mutação

- **Ferramentas:** **Pitest**.
- **Propósito:** Medir a **eficácia** da nossa suíte de testes de contrato. Ele introduz falhas (mutações) no código-fonte para garantir que nossos testes são capazes de detectá-las.
- **Execução:** Pode ser rodado localmente para validar a qualidade dos testes antes de submeter um PR.

## 4. Execução no CI/CD

Nossa pipeline no GitHub Actions é projetada para feedback rápido e abrangente:

1.  **Pull Request:** Roda as checagens de qualidade, os testes de contrato (`mock-tests`) e os testes de integração (`regression`).
2.  **Merge na `main`:** Roda o mesmo que o PR e, em caso de sucesso, publica o relatório do Allure no GitHub Pages.
3.  **Nightly:** Executa a suíte de regressão completa e atualiza o Allure Pages, fornecendo um snapshot diário da saúde do projeto.

## 5. Governança

- **Documentação:** O `README.md` serve como a porta de entrada, enquanto este documento detalha a estratégia.
- **Propriedade do Código:** `CODEOWNERS` garante que as pessoas certas revisem as mudanças críticas.
- **Segurança:** `SECURITY.md` define a política para reportar vulnerabilidades.
- **Dependências:** `Dependabot` mantém as dependências do projeto sempre atualizadas.
