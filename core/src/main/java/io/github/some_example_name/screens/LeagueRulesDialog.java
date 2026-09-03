package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

/** Regulamento único da WFL, acessível antes e durante a carreira. */
public final class LeagueRulesDialog {
    private static final String[] CATEGORIES = {
        "VISÃO GERAL",
        "TEMPORADA",
        "ELENCOS",
        "FINANÇAS",
        "MERCADO",
        "CONTRATOS",
        "DRAFT E SCOUTING",
        "DIRETORIA E CARREIRA"
    };

    private LeagueRulesDialog() {
    }

    public static void show(Stage stage, Main game) {
        if (stage == null || game == null) return;

        final Dialog dialog = new Dialog("", game.skin);
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setResizable(false);
        dialog.getTitleTable().clear();
        dialog.setBackground(
            StyleFactory.createRoundedPanel(
                Color.valueOf("07140E"),
                StyleFactory.GOLD
            )
        );

        Table root = dialog.getContentTable();
        root.clear();
        root.pad(16f);

        Table header = new Table();
        header.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf("0B1D14"),
                Color.valueOf("6E591C")
            )
        );
        header.pad(12f, 18f, 12f, 18f);

        Table titleArea = new Table();
        titleArea.left();
        Label title = new Label("REGRAS DA WFL", game.skin, "font-title");
        title.setFontScale(0.82f);
        title.setColor(StyleFactory.SOFT_YELLOW);
        titleArea.add(title).left().row();
        titleArea.add(
            ScreenUI.createSubtitle(
                game.skin,
                "REGULAMENTO DA WORLD FOOTBALL LEAGUE"
            )
        ).left().padTop(2f);
        header.add(titleArea).left().expandX();

        Label season = ScreenUI.createBoldValue(
            game.skin,
            "WFL  •  TEMPORADA " + game.league.getCurrentSeason(),
            StyleFactory.GOLD,
            Align.right
        );
        season.setFontScale(0.58f);
        header.add(season).right();
        root.add(header).colspan(2).growX().height(86f).padBottom(10f).row();

        final Table content = new Table();
        content.top().left().pad(4f, 10f, 12f, 10f);
        final ScrollPane contentScroll = new ScrollPane(content, game.skin);
        contentScroll.setScrollingDisabled(true, false);
        contentScroll.setFadeScrollBars(false);
        contentScroll.setOverscroll(false, false);

        Table navigation = new Table();
        navigation.top();
        navigation.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf("0B1711"),
                Color.valueOf("35443B")
            )
        );
        navigation.pad(10f);
        Label navTitle = ScreenUI.createBoldValue(
            game.skin,
            "CATEGORIAS",
            StyleFactory.GOLD,
            Align.left
        );
        navTitle.setFontScale(0.54f);
        navigation.add(navTitle).growX().left().pad(5f, 7f, 10f, 7f).row();

        ButtonGroup<TextButton> group = new ButtonGroup<>();
        group.setMinCheckCount(1);
        group.setMaxCheckCount(1);
        group.setUncheckLast(true);

        for (int index = 0; index < CATEGORIES.length; index++) {
            final int selectedIndex = index;
            TextButton button = ScreenUI.createSecondaryButton(game.skin, CATEGORIES[index]);
            button.getLabel().setAlignment(Align.left);
            button.getLabel().setFontScale(0.49f);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    renderCategory(content, game, selectedIndex);
                    contentScroll.setScrollY(0f);
                }
            });
            group.add(button);
            navigation.add(button).growX().height(51f).padBottom(7f).row();
            if (index == 0) button.setChecked(true);
        }

        root.add(navigation).width(275f).growY().padRight(10f);

        Table readingArea = new Table();
        readingArea.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf("0D1913"),
                Color.valueOf("35443B")
            )
        );
        readingArea.add(contentScroll).grow();
        root.add(readingArea).grow();
        root.row();

        Table footer = new Table();
        footer.padTop(10f);
        footer.add(
            ScreenUI.createSubtitle(
                game.skin,
                "As regras também podem ser consultadas na tela de elenco."
            )
        ).left().expandX();
        TextButton close = ScreenUI.createPrimaryButton(game.skin, "FECHAR REGULAMENTO");
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });
        footer.add(close).width(250f).height(48f).right();
        root.add(footer).colspan(2).growX().height(62f);

        renderCategory(content, game, 0);
        dialog.show(stage);
        float width = Math.min(1540f, stage.getViewport().getWorldWidth() - 80f);
        float height = Math.min(930f, stage.getViewport().getWorldHeight() - 70f);
        dialog.setSize(width, height);
        dialog.setPosition(
            (stage.getViewport().getWorldWidth() - width) / 2f,
            (stage.getViewport().getWorldHeight() - height) / 2f
        );
    }

    private static void renderCategory(Table content, Main game, int category) {
        content.clearChildren();

        Label categoryTitle = ScreenUI.createSectionTitle(game.skin, CATEGORIES[category]);
        categoryTitle.setFontScale(0.82f);
        content.add(categoryTitle).width(1060f).left().padBottom(10f).row();

        switch (category) {
            case 0:
                addGoldenRules(content, game);
                addRule(content, game, "ESTRUTURA DA WFL",
                    "• Liga mundial fechada com " + game.league.getClubs().size() + " franquias.\n" +
                    "• Expansões em 1974, 1978, 1982, 1986 e 1990: de 20 a 30 clubes.\n" +
                    "• Não existe acesso nem rebaixamento.\n" +
                    "• Temporada regular: janeiro a setembro.\n" +
                    "• Playoffs: outubro. Off Season: novembro e dezembro.");
                break;
            case 1:
                addRule(content, game, "TEMPORADA REGULAR",
                    "• Cada clube disputa " + ((game.league.getClubs().size() - 1) * 2) + " partidas, em turno e returno.\n" +
                    "• Vitória vale 3 pontos; empate, 1 ponto; derrota, 0.\n\n" +
                    "Critérios de desempate:\n" +
                    "1. Pontos  2. Saldo de gols  3. Gols marcados\n" +
                    "4. Confronto direto  5. Sorteio da liga");
                addRule(content, game, "PLAYOFFS",
                    "• 20–22 clubes: 8 vagas (6 Ocidente + 2 Oriente).\n" +
                    "• 24–26 clubes: 10 vagas (5 por conferência); 4º x 5º no Play-In.\n" +
                    "• 28–30 clubes: 12 vagas (6 por conferência); 3º x 6º e 4º x 5º no Play-In.\n" +
                    "• Play-In em jogo único; os melhores recebem bye. Quartas e semifinais em melhor de 3; final em jogo único.");
                break;
            case 2:
                addRule(content, game, "COMPOSIÇÃO DO ELENCO",
                    "• Mínimo de 23 e máximo de 26 jogadores por franquia.\n" +
                    "• Antes de uma partida, os 11 titulares devem estar preenchidos.\n" +
                    "• O banco de reservas respeita o limite da competição.\n" +
                    "• Lesionados e suspensos não podem ser selecionados.\n" +
                    "• Clubes abaixo do mínimo devem completar o elenco.");
                addRule(content, game, "DISPONIBILIDADE",
                    "Expulsões, suspensões, lesões e condição física afetam a escalação. " +
                    "A comissão técnica e a profundidade do elenco reduzem o impacto dessas ausências.");
                break;
            case 3:
                addRule(content, game, "SALARY CAP",
                    "• Todos os salários do elenco contam para o teto anual.\n" +
                    "• Salary Cap: WFL$ 14,5M — pode ser ultrapassado.\n" +
                    "• Luxury Tax: WFL$ 16,0M — imposto financeiro pesado.\n" +
                    "• Hard Cap: WFL$ 17,5M — nenhuma operação pode ultrapassá-lo.\n" +
                    "• A tela de finanças mostra folha, teto e espaço disponível em tempo real.");
                addRule(content, game, "LUXURY TAX",
                    "É permitido ultrapassar o Salary Cap. Acima de WFL$ 16,0M, a franquia paga " +
                    "250% sobre tudo que exceder a linha da Luxury Tax. O valor é debitado mensalmente.");
                addRule(content, game, "IMPACTO DO STAFF",
                    "• Treinador: tática, desempenho e desenvolvimento.\n" +
                    "• Scout: velocidade e precisão dos relatórios.\n" +
                    "• Diretor de desenvolvimento: evolução e potencial.\n" +
                    "• Preparador físico: desgaste e recuperação.\n" +
                    "• Médico: recuperação, lesões e risco de recaída.");
                addRule(content, game, "REVENUE SHARING",
                    "Parte das receitas comerciais da liga é distribuída entre as franquias. " +
                    "A divisão combina uma parcela igualitária com reputação e desempenho, evitando domínio financeiro permanente.");
                addRule(content, game, "INFRAESTRUTURA",
                    "Cada clube administra estádio, centro de treinamento, academia, departamento médico e scouting. " +
                    "Os níveis vão de uma a cinco estrelas e afetam somente seus sistemas correspondentes.");
                break;
            case 4:
                addRule(content, game, "MERCADO DA WFL",
                    "Não existem transferências por pagamento entre franquias. Jogadores mudam de clube por:\n" +
                    "• Trades  • Free Agency  • Draft");
                addRule(content, game, "TROCAS",
                    "• Podem envolver jogadores e escolhas de Draft.\n" +
                    "• Dinheiro não pode ser negociado diretamente.\n" +
                    "• Trade deadline: 15 de setembro.\n" +
                    "• Trocas ficam fechadas dos playoffs até a reabertura da Off Season.\n" +
                    "• Contratados na Free Agency: 60 dias sem troca.\n" +
                    "• Jogadores recém-renovados: 30 dias sem troca.");
                break;
            case 5:
                addRule(content, game, "RENOVAÇÕES",
                    "• Disponíveis durante a temporada regular.\n" +
                    "• Encerram ao fim da temporada regular.\n" +
                    "• De 1 a 5 de novembro, cada clube possui exclusividade para negociar com seus próprios atletas em fim de contrato.");
                addRule(content, game, "FREE AGENCY",
                    "• Mercado principal aberto às franquias durante a janela da Off Season.\n" +
                    "• As propostas são avaliadas diariamente.\n" +
                    "• Jogadores que permanecerem sem contrato podem ser contratados durante a temporada até o início dos playoffs.");
                break;
            case 6:
                addRule(content, game, "WFL DRAFT",
                    "• Realizado uma vez por temporada.\n" +
                    "• Duas rodadas, com uma escolha por clube em cada uma (até 60 escolhas em 1990).\n" +
                    "• Times fora dos playoffs participam da Draft Lottery.\n" +
                    "• Times dos playoffs escolhem depois.\n" +
                    "• A pior campanha recebe chances melhores, mas nunca garantia da Pick #1.");
                addRule(content, game, "WFL EXPANSION",
                    "Anúncio uma temporada antes. Na off-season anterior à estreia, cada clube protege 15 jogadores " +
                    "(ou menos para deixar três desprotegidos) e perde no máximo 3. As novas franquias escolhem 20 veteranos cada, mantendo seus contratos. " +
                    "Depois completam o elenco na Free Agency e no Draft normal. Nenhum rookie é consumido pelo Expansion Draft.");
                addRule(content, game, "SCOUTING DO DRAFT",
                    "Cada franquia observa um número limitado de prospectos. O conhecimento avança de 0% a 100%. " +
                    "Antes de 100%, atributos aparecem em conceitos; ao completar a observação, os valores exatos são revelados.\n\n" +
                    "Quanto melhor o scouting, menor a incerteza no Draft.");
                addRule(content, game, "CONTRATOS DE ROOKIE",
                    "• Contratos são automáticos após o Draft.\n" +
                    "• 1ª rodada: 4 anos. 2ª rodada: 3 anos.\n" +
                    "• O salário acompanha a posição da escolha: picks mais altas recebem valores maiores.");
                break;
            default:
                addRule(content, game, "OBJETIVOS DA DIRETORIA",
                    "Cada franquia possui expectativas esportivas, financeiras, de elenco, Draft e desenvolvimento. " +
                    "Os objetivos têm prioridades diferentes e são avaliados ao longo e ao fim da temporada.");
                addRule(content, game, "CONFIANÇA E DEMISSÃO",
                    "A diretoria considera objetivos, desempenho atual e histórico do treinador. " +
                    "Uma avaliação insuficiente pode colocar o cargo em risco ou encerrar o contrato.");
                addRule(content, game, "CARREIRA DO TREINADOR",
                    "Ser demitido não encerra o save. O treinador pode ficar desempregado, avançar o tempo, receber ofertas e assumir outra franquia. " +
                    "Títulos, playoffs, desenvolvimento e objetivos cumpridos aumentam sua reputação.");
                break;
        }
    }

    private static void addGoldenRules(Table content, Main game) {
        Table card = new Table();
        card.top().left();
        card.background(
            StyleFactory.createRoundedPanel(
                Color.valueOf("30270B"),
                StyleFactory.GOLD
            )
        );
        card.pad(14f, 18f, 14f, 18f);
        Label title = ScreenUI.createSectionTitle(game.skin, "WFL EM 5 REGRAS");
        title.setFontScale(0.67f);
        card.add(title).growX().left().padBottom(8f).row();
        String[] rules = {
            "1. Liga fechada, de 20 a 30 franquias, sem rebaixamento.",
            "2. Jogadores chegam por Draft, Trades e Free Agency.",
            "3. O Salary Cap controla os salários.",
            "4. O Draft ajuda franquias em reconstrução.",
            "5. A diretoria pode demitir por desempenho insuficiente."
        };
        for (String rule : rules) {
            Label line = ScreenUI.createBoldValue(
                game.skin,
                rule,
                StyleFactory.CREME_AGED,
                Align.left
            );
            line.setFontScale(0.53f);
            card.add(line).growX().left().height(30f).row();
        }
        content.add(card).width(1060f).growX().padBottom(10f).row();
    }

    private static void addRule(Table content, Main game, String titleText, String bodyText) {
        Table card = ScreenUI.createSubtlePanel();
        card.top().left();
        Label title = ScreenUI.createBoldValue(
            game.skin,
            titleText,
            StyleFactory.SOFT_YELLOW,
            Align.left
        );
        title.setFontScale(0.61f);
        card.add(title).growX().left().padBottom(7f).row();
        card.add(ScreenUI.createDivider()).growX().height(1f).padBottom(8f).row();

        Label body = ScreenUI.createSubtitle(game.skin, bodyText);
        body.setColor(StyleFactory.TEXT_SECONDARY);
        body.setFontScale(0.54f);
        body.setWrap(true);
        body.setAlignment(Align.topLeft);
        card.add(body).width(1015f).growX().left().padBottom(2f);
        content.add(card).width(1060f).growX().padBottom(10f).row();
    }
}
