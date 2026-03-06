# ADR 001: Escolha do PITest em vez de JaCoCo

## Contexto
Cobertura de código por linha (ex.: JaCoCo) mede apenas quais linhas foram executadas pelos testes. Isso pode gerar uma falsa sensação de segurança: é possível ter 90% de cobertura e ainda assim perder bugs importantes porque faltam asserts relevantes ou cenários críticos.

## Decisão
Adotar testes de mutação com **PITest** como métrica de eficácia da suíte de testes. O PITest introduz mutações controladas no bytecode e verifica se os testes falham; se não falham, indica fragilidade nos asserts.

## Consequências
- Positivas:
  - Mede qualidade real dos testes (capacidade de detectar mudanças indesejadas).
  - Orienta escrita de asserts mais robustos e cenários significativos.
- Negativas:
  - Execução mais lenta que cobertura por linha.
  - Requer ajustes (ex.: exclusões pontuais) para reduzir falsos positivos.

## Alternativas Consideradas
- Apenas JaCoCo (cobertura por linha): simples e rápido, porém insuficiente como qualidade de testes por si só.
- Ferramentas customizadas: alto custo de manutenção e pouco benefício comparado ao PITest.

## Status
Aceita.

## Como Executar
```
./mvnw -Pmutation org.pitest:pitest-maven:mutationCoverage
```

