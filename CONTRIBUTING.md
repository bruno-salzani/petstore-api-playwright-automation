## Como contribuir

- Use tags de teste: smoke, regression, contract, consistency, headers, flaky.
- Garanta qualidade antes de PR:
  - mvn -B -DskipTests=true enforcer:enforce
  - mvn spotless:apply
  - mvn -B verify
  - mvn -Pfast-checks test
- Commits no padrão convencional (feat:, fix:, docs:, chore:, test:, refactor:).
- Evite strings de rotas diretas; use o catálogo em http/Endpoint.
- Dados de teste via factory; inclua variações: válido, mínimo, inválido, limites.
- Anexe links/artefatos úteis no Allure em cenários-chave.
