# Expansão da WFL

As franquias estreiam nas temporadas abaixo. Sua entrada administrativa acontece em 1 de novembro da temporada anterior, antes da Free Agency e do Draft daquela safra.

| Temporada de estreia | Novas franquias | Ocidente / Oriente | Picks | Playoffs |
| --- | --- | --- | --- | --- |
| 1969 | Fundadoras | 15 / 5 | 40 | 8 |
| 1974 | Mexico City Aztecs, Cairo Pharaohs | 16 / 6 | 44 | 8 |
| 1978 | Shanghai Dragons, Sydney Southern Cross | 16 / 8 | 48 | 10 |
| 1982 | New York Empire, Riyadh Falcons | 17 / 9 | 52 | 10 |
| 1986 | Bangkok Elephants, Bombay Tigers | 17 / 11 | 56 | 12 |
| 1990 | Marseille Méditerranée, Jakarta Garudas | 18 / 12 | 60 | 12 |

## Expansion Draft

- A WFL News anuncia cada expansão uma temporada antes.
- Na fase WFL Expansion da off-season, o usuário pode alterar os 15 protegidos sugeridos para seu clube. A IA protege os 15 melhores por uma combinação de overall e potencial.
- Cada clube existente perde no máximo dois jogadores. Atletas sem contrato vigente para a nova temporada não participam.
- Os novos clubes alternam escolhas em ordem serpentina, até 15 atletas cada. As escolhas consideram posição, qualidade e hard cap; contratos existentes são mantidos.
- Se não houver atletas elegíveis suficientes, a seleção termina com o elenco disponível, sem criar veteranos artificiais. O mercado existente completa as vagas restantes.
- As novas franquias recebem uma escolha em cada uma das duas rodadas do Draft normal. As classes históricas são preservadas e complementadas proceduralmente até haver candidatos para todas as escolhas.
- O calendário e a loteria não avançam enquanto o usuário não concluir a proteção. Para um treinador desempregado, a proteção e a seleção são automáticas.
- Reabrir a fase não repete seleções, reinicia clubes nem devolve picks já negociadas.

## Playoffs

O formato anterior de 6 classificados ocidentais e 2 orientais é mantido com 20–22 clubes.

Com 24–26 clubes, classificam-se cinco por conferência: 4º x 5º disputam o Play-In; 1º enfrenta o vencedor e 2º enfrenta 3º nas quartas.

Com 28–30 clubes, classificam-se seis por conferência: 3º x 6º e 4º x 5º disputam o Play-In; 1º e 2º têm bye. Cada conferência determina seu representante na final.

Play-In e final são jogos únicos; quartas e semifinais são melhor de três. A fase regular se distribui entre 2 de janeiro e 15 de setembro, e os playoffs terminam até 31 de outubro.

## Validação

Execute `gradlew.bat :core:expansionRegression :lwjgl3:compileJava` para verificar todas as etapas de expansão, composição das conferências, proteção manual, limites de perdas, idempotência, classes e picks, loteria, contratação de complementos e progressão completa dos playoffs. Os testes são sem interface gráfica.

O catálogo de identidades, escudos, reputações, estádios e caixa inicial fica em `LeagueExpansionService`. Valores financeiros e capacidades não especificados na proposta são parâmetros iniciais de balanceamento.
