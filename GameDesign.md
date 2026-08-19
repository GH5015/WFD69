\# 🌍 WORLD FOOTBALL LEAGUE (WFL)

\## Game Design Document (GDD)



Versão: 1.0

Início do Projeto: 2026



\---



\# Visão Geral



\## Nome



World Football League (WFL)



\## Gênero



Football Club Management Simulator



\## Inspirações



\- Brasfoot

\- Football Manager

\- NBA 2K (Franchise Mode)

\- Out of the Park Baseball

\- Motorsport Manager



\## Engine



Java 21



\## Interface



JavaFX



\## Banco de Dados



SQLite



\## Objetivo



Criar um simulador profundo de gerenciamento de clubes de futebol, ambientado em uma liga mundial fictícia criada em 1969, misturando elementos do futebol europeu com sistemas utilizados nas ligas americanas, principalmente a NBA.



O jogador assume uma franquia e é responsável por:



\- montar o elenco;

\- contratar jogadores;

\- realizar trocas;

\- administrar finanças;

\- participar do Draft;

\- desenvolver atletas;

\- definir táticas;

\- conquistar campeonatos.



\---



\# Filosofia do jogo



O objetivo NÃO é copiar o Football Manager.



A WFL deve possuir identidade própria.



Os pilares do jogo são:



✔ Draft



✔ Salary Cap



✔ Trocas



✔ Desenvolvimento de jogadores



✔ Simulação rápida



✔ Fácil de aprender



✔ Difícil de dominar



\---



\# História da Liga



Ano inicial:



1969



Após décadas de discussões entre FIFA e grandes federações, nasce uma liga mundial independente.



Características:



\- apenas 20 clubes

\- sem rebaixamento

\- sem promoção

\- modelo de franquias

\- Draft anual

\- Salary Cap

\- Free Agency

\- Trocas obrigatórias para negociações



A liga pretende reunir os melhores jogadores do mundo.



\---



\# Estrutura da Liga



\## Conferência Ocidental



15 clubes



Europa



América do Sul



Brasil



Argentina



Uruguai



Portugal



Espanha



Alemanha



Holanda



Hungria



Inglaterra



Irlanda do Norte



França



Itália



\---



\## Conferência Oriental



5 clubes



Japão



Coreia do Sul



Irã



Iraque



Israel



\---



\# Clubes Fundadores



🇧🇷 Santos Atlântico FC



🇧🇷 Rio Imperial FC



🇮🇹 Milano Calcio FC



🇩🇪 Bavaria München FC



🇳🇱 Amsterdam Total FC



🏴 Manchester Albion FC



🏴 London Royals FC



🇪🇸 Madrid Real FC



🇪🇸 Barcelona Mediterráneo FC



🇵🇹 Lisboa Atlântica FC



🇫🇷 Paris Étoile FC



🇦🇷 Buenos Aires Plata FC



🇺🇾 Montevideo Celeste FC



🇭🇺 Danubia Budapest FC



🇬🇧 Belfast Northern Stars FC



🇯🇵 Tokyo Rising Sun FC



🇰🇷 Seoul Phoenix FC



🇮🇷 Tehran Lions FC



🇮🇶 Baghdad Mesopotamia FC



🇮🇱 Tel Aviv Stars FC



\---



\# Elencos



Cada clube possui:



mínimo:

23 jogadores



máximo:

26 jogadores



Categorias



Goleiros



Laterais



Zagueiros



Volantes



Meias



Pontas



Atacantes



\---



\# Sistema de Overall



Cada jogador possui atributos.



Exemplo:



Ataque



Passe



Defesa



Finalização



Controle



Drible



Velocidade



Força



Resistência



Posicionamento



Marcação



Cabeceio



Potencial



Moral



Fadiga



Overall calculado automaticamente.



\---



\# Potencial



Escala:



40 até 99



Jogadores evoluem:



idade



treinos



minutos



técnico



identidade do clube



potencial



moral



\---



\# Desenvolvimento



Cada temporada calcula:



evolução



declínio



lesões



ganho técnico



ganho físico



ganho mental



\---



\# Identidade dos Clubes



Cada franquia possui:



cores



uniformes



estádio



torcida



história



filosofia



academia



bônus próprios



Exemplo:



Barcelona



\+ desenvolvimento de passe



Tokyo



\+ evolução de jovens



Bavaria



\+ disciplina



Amsterdam



\+ inteligência tática



\---



\# Salary Cap



Cada temporada possui um teto salarial.



Base:



Média da receita da liga



\+



bônus pela colocação



1°

+10%



2°

+8%



3°

+6%



4°

+4%



5°

+2%



Últimos colocados



sem bônus



Existe também um HARD CAP.



Nenhum clube pode ultrapassar.



\---



\# Receitas



Bilheteria



Patrocínio



Premiações



Venda de camisas



Direitos da liga



Academia



Transferências internacionais



\---



\# Despesas



Salários



Equipe técnica



Estádio



Academia



Infraestrutura



Funcionários



\---



\# Mercado



Existem apenas:



Trocas



Draft



Free Agency



Não existe compra direta entre clubes.



\---



\# Draft



Realizado todos os anos.



Ordem:



piores campanhas primeiro.



Duas rodadas.



Cada clube possui:



2 escolhas



Escolhas futuras podem ser trocadas.



\---



\# Free Agency



Jogadores sem contrato.



Podem negociar com qualquer clube.



\---



\# Calendário



Janeiro



Pré-temporada



Fevereiro



Temporada Regular



Março



Temporada Regular



Abril



Temporada Regular



Maio



Temporada Regular



Junho



Temporada Regular



Julho



Temporada Regular



Agosto



Temporada Regular



Setembro



Temporada Regular



Outubro



Playoffs



Novembro



Finais



Premiações



Draft Lottery



Draft



Dezembro



Free Agency



Renovações



Pré-temporada



\---



\# Playoffs



16 clubes



Melhor de 3



Quartas



Melhor de 3



Semifinais



Melhor de 3



Final



Jogo único



\---



\# Táticas



O jogador escolhe:



Formação



Mentalidade



Defensiva



Equilibrada



Ofensiva



Ritmo



Lento



Normal



Rápido



Muito rápido



Pressão



Baixa



Média



Alta



Largura



Fechado



Equilibrado



Aberto



Estilo de Passe



Curto



Misto



Longo



\---



\# Simulação



O motor considera:



Overall



Entrosamento



Tática



Moral



Fadiga



Clima



Casa/Fora



Forma



Lesões



Cartões



Identidade do clube



Especialidades



\---



\# Interface



Tela Principal



Dashboard



Tabela



Agenda



Financeiro



Elenco



Base



Draft



Mercado



Treinos



Estatísticas



Hall da Fama



\---



\# IA



Cada clube possui personalidade.



Exemplo:



Barcelona



investe na base



Rio Imperial



contrata estrelas



Milano



prioriza defesa



Amsterdam



busca jovens técnicos



Tokyo



desenvolve jogadores



Bavaria



prefere estabilidade



\---



\# Objetivo do Jogador



Criar uma dinastia.



Conquistar títulos.



Desenvolver craques.



Dominar financeiramente a liga.



Construir uma academia histórica.



Ser lembrado como a maior franquia da história da WFL.



\---



\# Futuro



Sistema de olheiros



Rede mundial de base



Expansão da liga



Hall da Fama



Museu do clube



Seleções nacionais



Competições internacionais



Árbitros



Clima dinâmico



Personalidades de jogadores



Relacionamento treinador/jogador



Imprensa



Modo online



Editor de ligas



Steam Workshop



Mods



\---



\# Frase que resume o projeto



> "A história do futebol não será reescrita. Ela será reinventada."

