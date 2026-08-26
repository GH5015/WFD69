# AGENT.md — WFD69 / WorldFD69

## Objetivo do projeto

WFD69 é um jogo de gerenciamento de futebol ambientado em uma liga mundial alternativa iniciada em 1969. O projeto é uma aplicação desktop em **Java + LibGDX**, com simulação de partidas, calendário, playoffs, elenco, táticas, finanças, contratos, trocas, draft e scouting.

A prioridade é preservar uma carreira consistente: mudanças na interface devem refletir o mesmo estado persistente da liga, dos clubes e dos jogadores.

## Estrutura

- `core/`: regras de negócio, modelos, simulação e telas compartilhadas.
  - `model/`: estado da carreira e entidades como `League`, `Club`, `Player`, `Match`, contratos e draft.
  - `engine/`: cálculos da partida e mecânicas, como `MatchEngine`, ataque, defesa, posse, tática e desenvolvimento.
  - `simulation/`: simulação de temporada e playoffs.
  - `database/`: dados iniciais da liga e classe de draft. `GameDatabase` monta clubes e elencos.
  - `screens/`: telas e diálogos LibGDX/Scene2D.
  - `utils/`: componentes reutilizáveis, estilos e responsividade.
- `lwjgl3/`: launcher desktop LWJGL3.
- `assets/`: fontes, ícones, logos e demais recursos carregados em runtime.

O ponto de entrada da aplicação é `io.github.some_example_name.lwjgl3.Lwjgl3Launcher`; `Main` cria e mantém os objetos compartilhados da carreira.

## Estado compartilhado: regra principal

`Main` é a composição da partida/carreira. Ele inicializa e mantém, entre outros:

- `database`, `league` e `playerClub`;
- alertas pós-partida do elenco, mantidos em `Main` até serem exibidos na `ClubManagementScreen`;
- `matchEngine`, `seasonSimulator`, `playoffSimulator`, `developmentEngine` e `freeAgencyService`;
- `draftScoutManager` e `draftClass`.

Não recrie `League`, `GameDatabase`, `DraftScoutManager` ou a classe do draft ao abrir uma tela. Telas e diálogos recebem `Main` e devem usar as instâncias já existentes. Isso evita reinicializar elenco, calendário, contratos, progresso de scouting ou UUIDs dos prospectos.

Ao adicionar estado de longa duração, inicialize-o uma vez em `Main.create()` e o reutilize nas telas. Para dados específicos de uma entidade, prefira o modelo adequado (`League`, `Club`, `Player` etc.).

## Convenções de implementação

- Use Java 8 como alvo: não introduza APIs ou sintaxe posteriores ao Java 8.
- Mantenha regras e cálculos fora das telas sempre que possível:
  - modelos guardam estado e invariantes;
  - engines/services calculam simulação e operações;
  - telas renderizam, coletam a ação do usuário e chamam essas APIs.
- Evite duplicar fórmulas de overall, salário, valor de troca, fadiga, tática ou classificação em uma tela. Reutilize/crie um serviço ou método no domínio.
- Preserve o encapsulamento: use getters/setters e métodos de domínio em vez de alterar campos diretamente.
- Valide entradas e estados nulos nas fronteiras (cliques, listas, seleção de jogador, próxima partida), sem mascarar erros de inicialização.
- Ao alterar uma mecânica, considere as consequências em temporada regular, playoffs, calendário, finanças, contratos, draft e offseason.
- Dados históricos e iniciais devem ser adicionados em `GameDatabase`/classes de draft, usando os construtores e posições existentes.
- Não altere nomes, caminhos ou extensões de assets sem atualizar todos os carregamentos correspondentes.

## UI LibGDX / Scene2D

- As telas vivem em `screens/` e normalmente recebem `Main game` e o `Club` selecionado.
- Use `ResponsiveViewport`, `ScreenUI`, `StyleFactory` e componentes de `utils/` para manter proporções e identidade visual.
- Preserve o padrão visual verde escuro, dourado e creme e reutilize estilos/ícones existentes antes de criar novos.
- Use `Stage`, `Table` e listeners Scene2D de forma consistente; descarte recursos de tela (`Stage`, texturas próprias etc.) quando aplicável.
- Ao criar uma nova tela de carreira, inclua integração coerente com `NavigationDrawer` e `CareerOverlay` quando fizer sentido.
- Não carregue/crie `Texture` repetidamente a cada frame. Cacheie recursos estáticos e descarte-os no ciclo de vida adequado.

## Simulação e calendário

- `League` é a fonte de verdade para temporada, rodada, agenda, estágio e chave de playoffs.
- `SeasonSimulator` cria/processa calendário; `PlayoffSimulator` e `League` coordenam a fase eliminatória.
- `MatchEngine` prepara escalações e simula a partida. Mudanças nele precisam respeitar jogadores lesionados/suspensos, fadiga, moral, táticas, estatísticas e finanças.
- Não marque uma partida como concluída, avance o índice/calendário ou atualize estatísticas duas vezes.
- Quando a última partida de uma rodada regular terminar, a `League` registra o resumo como pendente. `CareerOverlay` o apresenta apenas no próximo clique em **Avançar dia**, por meio de `RoundSummaryDialog`; não mostre o painel novamente ao trocar de tela.
- Lesões são medidas em dias restantes: CareerOverlay reduz um dia por avanço de data e médicos podem conceder recuperação adicional. O fim de uma partida não reduz a lesão. MatchEngine sorteia diagnósticos ponderados, de contusões de 1–4 dias a fraturas de 60–120 dias; fadiga torna os casos graves um pouco mais prováveis.
- No início da Off Season, FreeAgencyService remove dos clubes os atletas cujo contrato terminou na temporada corrente, limpa escalação/tática e os adiciona uma única vez à lista de agentes livres.
- League processa aposentadorias após o envelhecimento na transição para a Off Season: atletas com 33+ anos usam chance crescente por idade e, se aposentados, ficam em RetirementRecord. SeasonSummaryScreen encaminha para RetirementSummaryScreen antes do dashboard da Off Season.
- Preserve a separação entre ações do usuário e automação da IA: clubes não controlados pelo usuário podem escolher escalação automaticamente; o clube do usuário deve preservar suas escolhas válidas.

## Build, execução e validação

Use o Gradle Wrapper do projeto.

No Windows:

```bat
gradlew.bat :core:classes :core:testClasses :lwjgl3:classes :lwjgl3:testClasses
gradlew.bat lwjgl3:run
```

No macOS/Linux:

```bash
./gradlew :core:classes :core:testClasses :lwjgl3:classes :lwjgl3:testClasses
./gradlew lwjgl3:run
```

Antes de concluir uma alteração:

1. Compile os módulos afetados; para mudanças gerais, compile ambos os módulos.
2. Execute os testes existentes quando houver (`./gradlew test`).
3. Abra o jogo e percorra o fluxo alterado, confirmando a tela, a navegação e a evolução do estado.
4. Para alterações de simulação, teste ao menos uma partida e o avanço de data/rodada; para offseason, teste a transição de fase.

Os avisos de compatibilidade de source/target Java 8 podem aparecer com JDKs recentes; trate erros de compilação reais como bloqueadores.

## Escopo de mudanças

- Faça mudanças pequenas e focadas; não refatore áreas não relacionadas em uma correção funcional.
- Não substitua a base de dados de elencos, nem reequilibre toda a economia, sem uma solicitação explícita.
- Ao modificar fórmulas, documente a intenção e preserve compatibilidade com carreiras já inicializadas quando possível.
- Atualize este arquivo se mudarem a arquitetura, os comandos de execução ou as regras centrais de estado.