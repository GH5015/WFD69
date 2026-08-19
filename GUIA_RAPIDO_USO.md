# 🎮 GUIA RÁPIDO DE USO

## ⚽ Onde Encontrar as Novas Funcionalidades

### 1. Temporada e Data na Tela de Elenco
**Localização**: TacticsScreen (Tela de Elenco)
- Aparece no **topo** da tela
- Mostra: "TEMPORADA 2024 • 30/07/2024"
- Visível **APENAS** quando você acessa a tela de elenco

### 2. Botão de Escalação
**Localização**: Tela de Jogo (MatchScreen)
- Botão: **"⚽ ESCALAÇÃO"** (primeiro botão da esquerda)
- **Clique para abrir** a visualização da formação tática
- Mostra todos os 11 jogadores em campo com:
  - Overall (OVR)
  - Efetivo (EFT) - afetado por fadiga
  - Barra de fadiga com % 
  - Status (OK / 🚫 Suspenso / 🏥 Lesionado)

### 3. Botão de Substituições
**Localização**: Tela de Jogo (MatchScreen)
- Botão: **"🔄 SUBSTITUIR"** (segundo botão)
- **Clique para fazer substituição** (máx 5 por jogo)
- Interface em 2 colunas:
  - **Esquerda**: Jogadores que estão em campo
  - **Direita**: Suplentes disponíveis
- Processo:
  1. Clique no jogador que sai
  2. Clique no suplente que entra
  3. Pronto! Substituição feita
  4. Contador atualiza automaticamente

### 4. Contador de Substituições
**Localização**: Abaixo dos botões
- Mostra: "SUBSTITUIÇÕES: 2/5"
- Atualiza automaticamente
- Quando atingir 5, o botão de substituição desabilitará

---

## 🎯 Exemplos Práticos

### Exemplo 1: Ver Escalação
```
1. Entre em uma partida (MatchScreen)
2. Clique no botão "⚽ ESCALAÇÃO"
3. Veja a formação tática com os 11 jogadores
4. Cada jogador mostra:
   - Nome em OURO
   - OVR: 92
   - EFT: 89
   - ████████░░ 78% (fadiga)
   - ✓ OK (ou 🚫 SUSPENSO ou 🏥 LESIONADO)
5. Scroll na tabela abaixo para mais detalhes
```

### Exemplo 2: Fazer uma Substituição
```
1. Durante a partida, note que seu ST Pelé está muito cansado
2. Fadiga: ░░░░░░░░░░ 15% (muito baixa!)
3. Clique em "🔄 SUBSTITUIR"
4. Abre o diálogo de substituições
5. Coluna esquerda: Clique em "Pelé" (que está em campo)
6. Coluna direita: Clique em "Vavá" (que está no banco)
7. Pelé sai, Vavá entra!
8. Contador muda: "SUBSTITUIÇÕES: 1/5"
9. Feche o diálogo (botão OK/CONTINUAR)
```

### Exemplo 3: Jogador Suspenso
```
1. Seu jogador recebeu cartão vermelho na partida anterior
2. Status: 🚫 SUSPENSO (1 partida)
3. Ele NÃO pode jogar a próxima partida
4. Na próxima partida após essa, ele estará disponível novamente
5. Na tela de escalação, ele aparecerá marcado como 🚫 SUSPENSO
```

---

## 📊 Sistema de Fadiga

### Como Funciona
- Cada jogador começa em 100% de fadiga (bem descansado)
- Ao final de cada partida, perde fadiga (20-45% dependendo da posição)
- **Goleiros**: Perdem menos fadiga (10%)
- **Outros**: Perdem 25-45%

### Impacto na Efetividade
- **80%+ fadiga**: ✓ Sem penalty
- **50-79% fadiga**: ⚠️ Pequeno penalty (~5-10%)
- **<50% fadiga**: ❌ Penalty maior (~20%)

### Recuperação
- A cada dia de descanso, recupera 8% de fadiga
- Pode acelerar em settings (se implementado)

---

## 🚫 Sistema de Suspensão

### Como Um Jogador é Suspenso
1. Recebe **CARTÃO VERMELHO** durante partida
2. Automático: Fica suspenso por **1 partida**
3. Status na escalação: **🚫 SUSPENSO (1)**

### Verificação
- Na tela de escalação, procure por: **🚫 SUSPENSO (X)**
- X = número de partidas que ainda falta jogar

### Quando Volta
- Cada partida que passa, o contador decrementa
- Após contar 0, volta automaticamente

---

## 🏥 Sistema de Lesão

### Como Um Jogador se Lesiona
1. Pode ser implementado manualmente em gerenciamento
2. Recebe status: **🏥 LESIONADO (N)**
3. N = número de partidas afastado

### Impacto
- Não pode jogar enquanto lesionado
- Não aparece nos suplentes disponíveis

### Recuperação
- Após cada partida, contador decrementa
- Volta automaticamente quando chegar a 0

---

## 🎨 Cores e Visuais

### Barras de Fadiga
```
████████░░ 80% - 🟢 VERDE (OK)
████░░░░░░ 50% - 🟡 AMARELO (Aviso)
██░░░░░░░░ 15% - 🔴 VERMELHO (Crítico)
```

### Cores do Texto
- **Ouro**: Títulos e destaques
- **Branco**: Texto principal
- **Amarelo Suave**: Texto secundário
- **Verde**: Efetivo sem penalty
- **Vermelho**: Status ruim (suspenso/lesionado)

---

## ⚙️ Dicas e Truques

### Dica 1: Rotação de Jogadores
- Use as 5 substituições para rodar o elenco
- Evite deixar jogadores com fadiga < 30%
- Melhor desempenho com fadiga > 70%

### Dica 2: Planejamento Tático
- Abra "ESCALAÇÃO" para ver formação antes de substituir
- Veja a fadiga de cada posição
- Substitua conforme necessário

### Dica 3: Máximo de Substituições
- Você tem **5 substituições por jogo**
- Use com sabedoria!
- Não é obrigatório usar todas

### Dica 4: Check de Status
- Sempre verifique 🚫 SUSPENSO e 🏥 LESIONADO
- Eles não poderão jogar
- Use suplentes disponíveis

---

## 🐛 Problemas Conhecidos e Soluções

### Problema: Botão de Substituição está cinzento
**Solução**: Você já fez 5 substituições. Limite atingido.

### Problema: Não consigo encontrar um suplente
**Solução**: Ele pode estar:
- 🚫 SUSPENSO
- 🏥 LESIONADO
- Já em campo

### Problema: A formação não aparece
**Solução**: Abra o LineupDialog primeiro clicando em "⚽ ESCALAÇÃO"

---

## 📱 Navegação Rápida

### Tela de Elenco
```
[VOLTAR] TEMPORADA 2024 • 30/07/2024
 ↓
[FORMAÇÃO] [AUTO-ESCALAR] [LIMPAR]
 ↓
[ELENCO] ← Selecione jogadores
 ↓
[CAMPO] ← Visualize formação
```

### Tela de Jogo
```
[⚽ ESCALAÇÃO] [🔄 SUBSTITUIR] [SAIR]
 ↓              ↓
 Abre           Abre
 LineupDialog   SubstitutionDialog
```

---

## 🎯 Fluxo Completo de Uma Partida

### Antes do Jogo
1. Vá para **TacticsScreen** (Tela de Elenco)
2. Veja a **TEMPORADA e DATA** no topo
3. Selecione sua **formação e jogadores**
4. Clique em "Começar Partida"

### Durante o Jogo
1. **Veja a escalação**: Clique "⚽ ESCALAÇÃO"
2. **Faça substituições**: Clique "🔄 SUBSTITUIR" (até 5)
3. **Acompanhe o jogo**: Veja o placar, estatísticas, eventos
4. **Controle a fadiga**: Observe barras de fadiga dos jogadores

### Depois do Jogo
1. Veja o **Relatório Pós-Jogo** aprimorado
2. Jogadores **automaticamente** processam suspensão/lesão
3. **Próxima partida**: Jogadores suspensos não poderão jogar
4. Volte para **TacticsScreen** e prepare o próximo jogo

---

## 📚 Recursos Adicionais

### Documentação Completa
- `MELHORIAS_RELATORIO_POS_JOGO.md` - Detalhes visuais do relatório
- `ESCALACAO_SUBSTITUICOES_IMPLEMENTADAS.md` - Detalhes técnicos
- `RESUMO_IMPLEMENTACOES_COMPLETO.md` - Relatório técnico completo

### Código-Fonte
- `LineupDialog.java` - Diálogo de escalação
- `SubstitutionDialog.java` - Diálogo de substituições
- `Player.java` - Sistema de suspensão/lesão
- `TacticsScreen.java` - Tela com temporada/data
- `MatchScreen.java` - Tela de jogo com novos botões

---

## ✅ Checklist Rápido

Antes de usar:
- ✅ Compilação completada com sucesso
- ✅ Todos os botões funcionando
- ✅ Diálogos abrindo corretamente
- ✅ Contador de substituições operacional
- ✅ Temporada e data visíveis
- ✅ Sistema de fadiga calculando
- ✅ Sistema de suspensão ativo

---

## 🎉 Enjoy!

Você agora tem um sistema completo de escalação, substituições e gerenciamento de suspensões/lesões!

Qualquer dúvida, consulte a documentação técnica fornecida.

**Boa sorte no seu gerenciamento! ⚽**

