package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.*;
import io.github.some_example_name.utils.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Evento de seleção do Draft: ordem, prospects e relatório de scouting na mesma tela. */
public class DraftScreen implements Screen {
    private final Main game; private final Club club; private final DraftScoutManager scout; private final Stage stage;
    private final Texture clubLogo;
    private List<DraftPick> picks; private Player selected; private String filter="TODOS"; private boolean pauseAfterUserPick;
    public DraftScreen(Main game,Club club,DraftScoutManager scout){
        this.game=game;this.club=club;this.scout=scout;stage=new Stage(new ResponsiveViewport());
        clubLogo=club.getLogoPath()!=null&&Gdx.files.internal(club.getLogoPath()).exists()?ClubLogoAssets.load(club.getLogoPath()):null;
    }
    @Override public void show(){if(LeagueExpansionService.isPending(game.league)){game.setScreen(new OffSeasonScreen(game,club));return;}Gdx.input.setInputProcessor(stage);refresh();}
    private void refresh() {
        if (game.league.isDraftFinalized()) { game.setScreen(new DraftSummaryScreen(game, club)); return; }
        if (!pauseAfterUserPick) advanceAiPicks(Integer.MAX_VALUE);
        picks = DraftOrderService.getCurrentDraftOrder(game.league, game.league.getCurrentSeason() + 1);
        List<Player> available = available();
        if (selected == null || !available.contains(selected)) selected = available.isEmpty() ? null : available.get(0);
        DraftPick current = currentPick();
        if (current == null) {
            for (Player player : available) player.transferTo(null);
            game.freeAgencyService.addUndraftedFreeAgents(available);
            game.league.finalizeDraft();
            game.setScreen(new DraftSummaryScreen(game, club));
            return;
        }
        stage.clear();
        Table page = new Table(); page.setFillParent(true); page.top(); page.pad(12f);
        page.background(StyleFactory.createRoundedPanel(com.badlogic.gdx.graphics.Color.valueOf("06140F"), StyleFactory.DARK_GOLD));
        stage.addActor(page);
        Table heading = new Table();
        Table brand = new Table(); brand.left();
        Label title = ScreenUI.createSectionTitle(game.skin, "WFL DRAFT"); title.setFontScale(1.45f);
        brand.add(title).left().row();
        Label sub = ScreenUI.createSubtitle(game.skin, "OFF SEASON " + (game.league.getCurrentSeason()+1) + "  •  "
            + current.getRound() + "ª RODADA  •  PICK #" + overall(current));
        sub.setFontScale(.52f); brand.add(sub).left().padTop(2f);
        heading.add(brand).width(355f).left();
        heading.add(phaseRail()).growX().padLeft(20f).padRight(20f);
        heading.add(ScreenUI.createBoldValue(game.skin, club.getName().toUpperCase(), StyleFactory.SOFT_YELLOW, Align.right)).right();
        if(clubLogo!=null)heading.add(new Image(clubLogo)).size(52f).padLeft(10f);
        page.add(heading).growX().height(70f).padBottom(8f).row();
        Table columns = new Table();
        columns.add(orderPanel()).width(365f).growY().padRight(10f);
        columns.add(centerColumn(available, current)).grow().minWidth(0).padRight(10f);
        columns.add(reportPanel(current)).width(470f).growY();
        page.add(columns).grow().row();
        page.add(createDraftFooter(current)).growX().height(125f).padTop(10f);
        if (current.getCurrentOwner() == club) IncomingTradeOfferService.processDraftPickOffer(game.league, club, current);
    }

    /** Coluna central da referência: lista, propostas da pick e comandos do Draft. */
    private Table centerColumn(List<Player> available, DraftPick current) {
        Table center = new Table(); center.top();
        center.add(prospectPanel(available)).grow().minHeight(0).row();
        center.add(pickOfferPanel(current)).growX().height(126f).padTop(8f).row();
        center.add(centerActions(current)).growX().height(48f).padTop(7f);
        return center;
    }
    private Table orderPanel() {
        Table panel = styledPanel("ORDEM DO DRAFT");
        Table rows = new Table(); rows.top();
        for (DraftPick pick : picks) {
            int number = overall(pick);
            DraftSelection choice = selection(pick);
            boolean current = pick == currentPick();
            Table row = new Table(); row.pad(7f);
            row.background(StyleFactory.createRoundedPanel(current ? com.badlogic.gdx.graphics.Color.valueOf("3A3014")
                : com.badlogic.gdx.graphics.Color.valueOf("0D1B16"), current ? StyleFactory.GOLD : com.badlogic.gdx.graphics.Color.valueOf("31443A")));
            Label rank = ScreenUI.createBoldValue(game.skin, "#" + number, StyleFactory.SOFT_YELLOW, Align.center);
            row.add(rank).width(48f);
            Table copy = new Table(); copy.left();
            String owner = pick.getCurrentOwner().getName();
            Label ownerLabel = ScreenUI.createBoldValue(game.skin, owner, current ? StyleFactory.SOFT_YELLOW : com.badlogic.gdx.graphics.Color.WHITE, Align.left);
            ownerLabel.setFontScale(.48f); ownerLabel.setEllipsis(true);
            copy.add(ownerLabel).width(255f).left().row();
            String detail = choice != null ? choice.getPlayer().getName() + " (" + choice.getPlayer().getPosition() + ")"
                : current && pick.getCurrentOwner() == club ? "SUA VEZ"
                : pick.getOriginalOwner() != pick.getCurrentOwner() ? "via " + pick.getOriginalOwner().getName() : "—";
            Label detailLabel = ScreenUI.createSubtitle(game.skin, detail);
            detailLabel.setColor(choice != null ? ScreenUI.SUCCESS : ScreenUI.MUTED_TEXT); detailLabel.setEllipsis(true);
            copy.add(detailLabel).width(255f).left();
            row.add(copy).growX();
            rows.add(row).growX().height(58f).padBottom(3f).row();
        }
        ScrollPane scroll = new ScrollPane(rows, game.skin); scroll.setScrollingDisabled(true, false); scroll.setFadeScrollBars(false);
        panel.add(scroll).grow();
        return panel;
    }
    private Table prospectPanel(List<Player> available) {
        Table panel = styledPanel("PROSPECTOS DISPONÍVEIS");
        Table filters = new Table();
        for (String option : new String[]{"TODOS", "GK", "DEF", "MEI", "ATA", "ALVOS"}) {
            TextButton button = ScreenUI.createInteractiveButton(option, game.skin, "toggle");
            button.setChecked(option.equals(filter)); button.getLabel().setFontScale(.45f);
            button.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { filter = option; refresh(); } });
            filters.add(button).width(78f).height(34f).padRight(4f);
        }
        filters.add().expandX();
        filters.add(ScreenUI.createSubtitle(game.skin, "ORDENAR: RANKING")).right();
        panel.add(filters).colspan(8).growX().padBottom(8f).row();
        Table headers = new Table();
        String[] labels = {"#", "JOGADOR", "POS", "IDADE", "OVR", "POT", "SCOUT", "PROJEÇÃO"};
        float[] widths = {38, 210, 52, 48, 52, 52, 62, 84};
        for (int i = 0; i < labels.length; i++) headers.add(ScreenUI.createTableHeaderLabel(game.skin, labels[i], Align.center)).width(widths[i]);
        panel.add(headers).colspan(8).growX().height(32f).row();
        Table list = new Table(); list.top();
        int ranking = 0;
        for (Player player : available) {
            ranking++;
            if (!matches(player)) continue;
            ScoutTarget target = target(player); boolean active = player == selected;
            Table row = ScreenUI.createRow(ranking);
            if (active) row.background(StyleFactory.createRoundedPanel(com.badlogic.gdx.graphics.Color.valueOf("352E15"), StyleFactory.GOLD));
            row.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
            row.add(cell(String.valueOf(ranking), active)).width(widths[0]);
            Label name = ScreenUI.createBoldValue(game.skin, player.getName(), active ? StyleFactory.SOFT_YELLOW : com.badlogic.gdx.graphics.Color.WHITE, Align.left);
            name.setFontScale(.48f); name.setEllipsis(true); row.add(name).width(widths[1]).left();
            row.add(cell(player.getPrimaryPosition().name(), active)).width(widths[2]);
            row.add(cell(String.valueOf(player.getAge()), active)).width(widths[3]);
            row.add(cell(displayOvr(player, target), active)).width(widths[4]);
            row.add(cell(displayPot(player, target), active)).width(widths[5]);
            row.add(cell(target == null ? "0%" : Math.round(target.getKnowledgePercentage()) + "%", active)).width(widths[6]);
            row.add(cell(projection(player), active)).width(widths[7]);
            row.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { selected = player; refresh(); } });
            list.add(row).growX().height(48f).padBottom(2f).row();
        }
        ScrollPane scroll = new ScrollPane(list, game.skin); scroll.setName("draft-prospects"); scroll.setScrollingDisabled(true, false); scroll.setFadeScrollBars(false);
        panel.add(scroll).colspan(8).grow();
        return panel;
    }

    private Table pickOfferPanel(final DraftPick current) {
        Table panel = styledPanel("OFERTAS PELA PICK #" + (current == null ? "—" : overall(current)));
        TradeOffer offer = game.league.getPendingIncomingTradeOffer();
        boolean canTrade = current != null && current.getCurrentOwner() == club;
        if (offer != null && canTrade) {
            offerRow(panel, "OFERTA RECEBIDA", offer.getTargetClub().getName(),
                assets(offer.getTargetPlayers(), offer.getTargetPicks()),
                "EM TROCA DA PICK #" + overall(current), new Runnable() {
                    @Override public void run() {
                        IncomingTradeOfferDialog.showPending(stage, game, club, new Runnable() {
                            @Override public void run() { refresh(); }
                        });
                    }
                });
        } else if (canTrade) {
            offerRow(panel, "MERCADO DA WFL", "NEGOCIE ESTA PICK", "Abra a central de trocas para comparar ativos", "PICK #" + overall(current), new Runnable() {
                @Override public void run() { game.setScreen(new TradeHubScreen(game, club)); }
            });
        } else {
            panel.add(ScreenUI.createSubtitle(game.skin, "As propostas pela sua próxima escolha aparecerão quando sua franquia entrar no relógio."))
                .growX().left().expandY();
        }
        return panel;
    }

    private void offerRow(Table panel, String label, String partner, String receives, String sends, final Runnable action) {
        Table row = ScreenUI.createSubtlePanel(); row.pad(7f);
        Label badge = ScreenUI.createBoldValue(game.skin, label, StyleFactory.SOFT_YELLOW, Align.left); badge.setFontScale(.42f);
        row.add(badge).width(112f).left();
        Table party = new Table(); party.left();
        Label clubName = ScreenUI.createBoldValue(game.skin, partner, Color.WHITE, Align.left); clubName.setFontScale(.48f); clubName.setEllipsis(true);
        party.add(clubName).width(125f).left().row();
        party.add(ScreenUI.createSubtitle(game.skin, receives)).width(125f).left();
        row.add(party).width(130f).left();
        Label arrow = ScreenUI.createBoldValue(game.skin, "⇄", StyleFactory.SOFT_YELLOW, Align.center); arrow.setFontScale(.68f);
        row.add(arrow).width(38f);
        Label returnText = ScreenUI.createSubtitle(game.skin, sends); returnText.setWrap(true);
        row.add(returnText).growX().left();
        TextButton evaluate = ScreenUI.createInteractiveButton("AVALIAR", game.skin); evaluate.getLabel().setFontScale(.40f);
        evaluate.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { action.run(); } });
        row.add(evaluate).width(86f).height(32f).padLeft(6f);
        panel.add(row).growX().height(66f).expandY();
    }

    private String assets(List<Player> players, List<DraftPick> picks) {
        if (players != null && !players.isEmpty()) return players.get(0).getName() + (players.size() > 1 ? " + ativos" : "");
        if (picks != null && !picks.isEmpty()) return picks.get(0).getRound() + "ª rodada " + picks.get(0).getYear();
        return "Ativos de troca";
    }

    private Table centerActions(final DraftPick current) {
        Table actions = new Table();
        TextButton board = ScreenUI.createInteractiveButton("☷  VER CLASSE COMPLETA", game.skin); board.getLabel().setFontScale(.42f);
        board.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { game.setScreen(new DraftBoardScreen(game, club, scout)); } });
        actions.add(board).width(210f).height(44f).padRight(7f);
        boolean yours = current != null && current.getCurrentOwner() == club;
        TextButton simulate = ScreenUI.createInteractiveButton(yours ? "≫  SIMULAR ATÉ MINHA PICK" : "≫  AVANÇAR PICK", game.skin); simulate.getLabel().setFontScale(.40f);
        simulate.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) {
            if (yours) return;
            pauseAfterUserPick = false; refresh();
        } });
        actions.add(simulate).width(220f).height(44f).padRight(7f);
        TextButton draft = ScreenUI.createPrimaryButton(game.skin, yours ? "✦  DRAFTAR JOGADOR" : "AGUARDAR SUA VEZ");
        draft.getLabel().setFontScale(.43f); draft.setDisabled(!yours || selected == null);
        draft.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { if (current != null && current.getCurrentOwner() == club) confirmDraft(current); } });
        actions.add(draft).growX().height(44f);
        return actions;
    }
    private Table reportPanel(DraftPick current) {
        Table panel = styledPanel("PROSPECTO SELECIONADO");
        if (selected == null) { panel.add(ScreenUI.createSubtitle(game.skin, "Nenhum prospecto disponível.")).center(); return panel; }
        ScoutTarget target = target(selected);
        Label name = ScreenUI.createBoldValue(game.skin, selected.getName().toUpperCase(), StyleFactory.SOFT_YELLOW, Align.center);
        name.setFontScale(.78f); name.setWrap(true);
        panel.add(name).growX().center().padTop(6f).row();
        panel.add(ScreenUI.createSubtitle(game.skin, selected.getNationality() + "  •  " + selected.getPrimaryPosition() + "  •  " + selected.getAge() + " anos"))
            .center().padTop(5f).row();
        Table ratings = new Table();
        ratings.add(ScreenUI.createStatusBox(game.skin, "OVR", displayOvr(selected, target), StyleFactory.SOFT_YELLOW)).growX().height(76f).padRight(8f);
        ratings.add(ScreenUI.createStatusBox(game.skin, "POTENCIAL", displayPot(selected, target), ScreenUI.SUCCESS)).growX().height(76f);
        panel.add(ratings).growX().padTop(14f).row();
        Table attributes = ScreenUI.createSubtlePanel();
        statColumn(attributes, "ATA", attribute(selected, "ataque", target));
        statColumn(attributes, "PAS", attribute(selected, "passe", target));
        statColumn(attributes, "DEF", attribute(selected, "defesa", target));
        statColumn(attributes, "FIS", attribute(selected, "fisico", target));
        statColumn(attributes, "DRI", attribute(selected, "drible", target));
        panel.add(attributes).growX().height(68f).padTop(10f).row();
        double knowledge = target == null ? 0 : target.getKnowledgePercentage();
        Table knowledgePanel = ScreenUI.createSubtlePanel();
        knowledgePanel.add(ScreenUI.createSubtitle(game.skin, "CONHECIMENTO")).left().expandX();
        knowledgePanel.add(ScreenUI.createBoldValue(game.skin, Math.round(knowledge) + "%", ScreenUI.SUCCESS, Align.right)).right().row();
        knowledgePanel.add(ScreenUI.createBlockProgress(game.skin, knowledge, 16, ScreenUI.SUCCESS)).colspan(2).growX().height(12f).padTop(7f);
        panel.add(knowledgePanel).growX().height(66f).padTop(10f).row();
        Table analysis = ScreenUI.createSubtlePanel(); analysis.top().left(); analysis.defaults().padLeft(7f).padRight(7f);
        analysis.add(ScreenUI.createSubtitle(game.skin, "PROJEÇÃO")).width(145f).left();
        analysis.add(ScreenUI.createBoldValue(game.skin, projection(selected), StyleFactory.SOFT_YELLOW, Align.left)).growX().left().row();
        analysis.add(ScreenUI.createSubtitle(game.skin, "BIG BOARD")).left().padTop(8f);
        analysis.add(ScreenUI.createBoldValue(game.skin, "#" + bigBoardRank(selected), Color.WHITE, Align.left)).left().padTop(8f).row();
        analysis.add(ScreenUI.createSubtitle(game.skin, "NECESSIDADE")).left().padTop(8f);
        analysis.add(ScreenUI.createBoldValue(game.skin, stars(positionNeed(club, selected.getPrimaryPosition())), StyleFactory.SOFT_YELLOW, Align.left)).left().padTop(8f).row();
        analysis.add(ScreenUI.createSubtitle(game.skin, "CONTRATO ROOKIE")).left().padTop(8f);
        analysis.add(ScreenUI.createBoldValue(game.skin, "4 anos  •  " + String.format(java.util.Locale.US, "WFL$ %.1fM/ano", selected.getAnnualSalary() / 1_000_000d), ScreenUI.SUCCESS, Align.left)).left().padTop(8f).row();
        panel.add(analysis).growX().height(132f).padTop(10f).row();
        Player tip = recommended();
        Label rationale = ScreenUI.createSubtitle(game.skin, "SCOUT: " + tip.getName() + "  •  " + recommendationText(tip));
        rationale.setWrap(true); rationale.setAlignment(Align.center);
        panel.add(rationale).width(420f).center().expandY().padTop(12f).row();
        TextButton favorite = ScreenUI.createInteractiveButton(scout.isFavorite(selected) ? "★ FAVORITO" : "☆ FAVORITAR", game.skin);
        favorite.getLabel().setFontScale(.48f);
        favorite.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { scout.toggleFavorite(selected); refresh(); } });
        panel.add(favorite).width(190f).height(38f).center().padTop(8f);
        return panel;
    }

    private void statColumn(Table panel, String title, String value) {
        Table column = new Table();
        column.add(ScreenUI.createSubtitle(game.skin, title)).row();
        column.add(ScreenUI.createBoldValue(game.skin, value, StyleFactory.SOFT_YELLOW, Align.center)).padTop(3f);
        panel.add(column).growX().uniformX();
    }

    private Table styledPanel(String title) {
        Table panel = ScreenUI.createPanel(); panel.top();
        panel.background(StyleFactory.createRoundedPanel(Color.valueOf("091813"), StyleFactory.DARK_GOLD));
        Label heading = ScreenUI.createSectionTitle(game.skin, title); heading.setFontScale(.68f);
        panel.add(heading).growX().left().padBottom(9f).row();
        return panel;
    }
    private Table phaseRail(){
        Table rail=new Table();
        String[] phases={"FREE AGENCY","STAFF","LOTERIA DO DRAFT","DRAFT"};
        for(int i=0;i<phases.length;i++){
            boolean active=i==phases.length-1;
            Label label=ScreenUI.createBoldValue(game.skin,phases[i],active?StyleFactory.SOFT_YELLOW:ScreenUI.MUTED_TEXT,Align.center);
            label.setFontScale(.39f);
            rail.add(label).growX();
            if(i<phases.length-1)rail.add(ScreenUI.createSubtitle(game.skin,"—")).width(22f);
        }
        return rail;
    }
    private Table bottomPanel(DraftPick current){
        Table panel=ScreenUI.createPanel(); panel.pad(7f);
        TextButton back=ScreenUI.createInteractiveButton("VOLTAR À OFF SEASON",game.skin);
        back.getLabel().setFontScale(.4f);
        back.addListener(new ClickListener(){@Override public void clicked(InputEvent e,float x,float y){game.setScreen(new OffSeasonScreen(game,club));}});
        panel.add(back).width(250f).height(42f).padRight(14f);
        Label note=ScreenUI.createSubtitle(game.skin,"Jogadores parcialmente observados exibem grades até o scouting completo.");note.setFontScale(.43f);
        panel.add(note).left().expandX();
        Label date=ScreenUI.createBoldValue(game.skin,"▣  " + game.league.getCurrentSeason(),StyleFactory.CREME_AGED,Align.center);date.setFontScale(.43f);
        panel.add(date).width(110f);
        Label cash=ScreenUI.createBoldValue(game.skin,String.format(java.util.Locale.US,"WFL$ %.1fM",club.getFinance().getBalance()/1_000_000d),ScreenUI.SUCCESS,Align.center);cash.setFontScale(.43f);
        panel.add(cash).width(125f);
        Label picks=ScreenUI.createBoldValue(game.skin,"PICKS: "+club.getDraftPicks().size(),StyleFactory.CREME_AGED,Align.center);picks.setFontScale(.43f);
        panel.add(picks).width(120f);
        return panel;
    }
    private void advanceAiPicks(int max){List<DraftPick> order=DraftOrderService.getCurrentDraftOrder(game.league,game.league.getCurrentSeason()+1);int made=0;for(DraftPick p:order){if(game.league.isDraftPickUsed(p))continue;if(p.getCurrentOwner()==club||made>=max)break;List<Player> pool=available();if(pool.isEmpty())break;pool.sort(Comparator.comparingInt((Player x)->draftScore(p.getCurrentOwner(),x)).reversed());game.league.recordDraftSelection(p,pool.get(0));made++;}}
    private void simulateRound(){DraftPick current=currentPick();if(current==null)return;int round=current.getRound();List<DraftPick> order=DraftOrderService.getCurrentDraftOrder(game.league,game.league.getCurrentSeason()+1);for(DraftPick p:order){if(p.getRound()!=round||game.league.isDraftPickUsed(p))continue;if(p.getCurrentOwner()==club)break;List<Player> pool=available();if(pool.isEmpty())return;pool.sort(Comparator.comparingInt((Player x)->draftScore(p.getCurrentOwner(),x)).reversed());game.league.recordDraftSelection(p,pool.get(0));}}
    private List<Player> available(){List<Player> list=new ArrayList<>();for(Player p:game.draftClass){boolean taken=false;for(DraftSelection s:game.league.getDraftSelections())if(s.getPlayer()==p){taken=true;break;}if(!taken)list.add(p);}list.sort(Comparator.comparingInt((Player p)->p.getOverall()+p.getPotential()/3).reversed());return list;}
    private DraftPick currentPick(){if(picks==null)picks=DraftOrderService.getCurrentDraftOrder(game.league,game.league.getCurrentSeason()+1);for(DraftPick p:picks)if(!game.league.isDraftPickUsed(p))return p;return null;}
    private DraftSelection selection(DraftPick p){for(DraftSelection s:game.league.getDraftSelections())if(s.getPick()==p)return s;return null;}
    private int overall(DraftPick p){return p.getProjectedOverallPosition();}
    private ScoutTarget target(Player p){if(scout==null)return null;for(ScoutTarget t:scout.getActiveTargets())if(t.getPlayer()==p)return t;for(ScoutTarget t:scout.getCompletedTargets())if(t.getPlayer()==p)return t;return null;}
    private String displayOvr(Player p,ScoutTarget t){return t==null?"?":t.getDisplayOverall();} private String displayPot(Player p,ScoutTarget t){return t==null?"?":t.getDisplayPotential();} private String attribute(Player p,String key,ScoutTarget t){return t==null?"?":t.getAttributeDisplay(p.getTechnicalAttributes().getAttributeByName(key));}
    private boolean matches(Player p){if("TODOS".equals(filter))return true;if("ALVOS".equals(filter))return scout.isFavorite(p);String pos=p.getPrimaryPosition().name();if("GK".equals(filter))return "GK".equals(pos);if("DEF".equals(filter))return pos.endsWith("B");if("MEI".equals(filter))return pos.contains("M");return pos.equals("ST")||pos.equals("CF")||pos.equals("LW")||pos.equals("RW");}
    private Label cell(String text,boolean selectedRow){return ScreenUI.createBoldValue(game.skin,text,selectedRow?StyleFactory.SOFT_YELLOW:StyleFactory.CREME_AGED,Align.center);} private void header(Table p,String... a){for(String s:a)p.add(ScreenUI.createTableHeaderLabel(game.skin,s,Align.center));p.row();}private void stat(Table p,String n,String v){p.add(ScreenUI.createSubtitle(game.skin,n)).left().expandX().padTop(7);p.add(ScreenUI.createBoldValue(game.skin,v,StyleFactory.SOFT_YELLOW,Align.right)).right().padTop(7).row();}
    private void confirmDraft(final DraftPick pick){if(pick==null||selected==null)return;final Player choice=selected;Dialog dialog=new Dialog("CONFIRMAR ESCOLHA",game.skin);dialog.text("#"+overall(pick)+" PICK\n\n"+club.getName().toUpperCase()+" seleciona\n\n"+choice.getName().toUpperCase()+"\n"+choice.getPrimaryPosition()+" • "+choice.getNationality()+" • "+choice.getAge()+" anos");TextButton confirm=ScreenUI.createPrimaryButton(game.skin,"CONFIRMAR");confirm.addListener(new ClickListener(){@Override public void clicked(InputEvent e,float x,float y){game.league.recordDraftSelection(pick,choice);pauseAfterUserPick=true;dialog.hide();announcement(choice,pick);}});dialog.button(confirm);dialog.button("VOLTAR");dialog.show(stage);}
    private void announcement(Player choice,DraftPick pick){Dialog dialog=new Dialog("WFL DRAFT",game.skin);dialog.text("WITH THE "+overall(pick)+"TH PICK IN THE\n"+(game.league.getCurrentSeason()+1)+" WFL DRAFT\n\n"+club.getName().toUpperCase()+"\nSELECTS\n\n"+choice.getName().toUpperCase()+"\n"+choice.getPrimaryPosition()+" • "+choice.getNationality()+" • "+choice.getAge()+"\n\nBig Board: #"+bigBoardRank(choice)+" • "+selectionValue(pick,choice));TextButton continueButton=ScreenUI.createPrimaryButton(game.skin,"CONTINUAR");continueButton.addListener(new ClickListener(){@Override public void clicked(InputEvent e,float x,float y){dialog.hide();refresh();}});dialog.button(continueButton);dialog.show(stage);}
    private int draftScore(Club drafting,Player p){return p.getOverall()*3+p.getPotential()+positionNeed(drafting,p.getPrimaryPosition())*5+(int)(Math.random()*8);}
    private int bigBoardRank(Player player){List<Player> board=new ArrayList<>(game.draftClass);board.sort(Comparator.comparingInt((Player p)->p.getOverall()+p.getPotential()/3).reversed());return board.indexOf(player)+1;}
    private String selectionValue(DraftPick pick,Player player){int delta=bigBoardRank(player)-overall(pick);return delta<=-4?"STEAL":delta>=5?"REACH":"VALOR JUSTO";}
    private Player recommended(){List<Player> pool=available();if(pool.isEmpty())return selected;pool.sort(Comparator.comparingInt((Player p)->draftScore(club,p)).reversed());return pool.get(0);}
    private int recommendationStars(Player p){return Math.max(1,Math.min(5,(p.getPotential()+positionNeed(club,p.getPrimaryPosition())*4-70)/6));}
    private String recommendationText(Player p){return positionNeed(club,p.getPrimaryPosition())>=4?"Melhor opção disponível e atende uma das maiores necessidades do elenco.":"Alto talento disponível, mas não é uma necessidade imediata.";}
    private int positionNeed(Club team,Position position){int count=0;for(Player p:team.getSquad())if(sameGroup(position,p.getPrimaryPosition()))count++;return Math.max(1,Math.min(5,5-count));}
    private boolean sameGroup(Position a,Position b){String x=a.name(),y=b.name();if("GK".equals(x))return "GK".equals(y);if(x.endsWith("B"))return y.endsWith("B");if(x.contains("M"))return y.contains("M");return y.equals("ST")||y.equals("CF")||y.equals("LW")||y.equals("RW");}
    private String projection(Player p){return p.getPotential()>=88?"Top 5":"Titular futuro";}private String needs(){return "GK "+stars(positionNeed(club,Position.GK))+"  CB "+stars(positionNeed(club,Position.CB))+"  LB "+stars(positionNeed(club,Position.LB))+"  CM "+stars(positionNeed(club,Position.CM))+"  RW "+stars(positionNeed(club,Position.RW))+"  ST "+stars(positionNeed(club,Position.ST));}private String stars(int n){return ScreenUI.formatStars(n);}
    @Override public void render(float d){Gdx.gl.glClearColor(0,0,0,1);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);stage.act(d);stage.draw();}@Override public void resize(int w,int h){stage.getViewport().update(w,h,true);}@Override public void pause(){}@Override public void resume(){}@Override public void hide(){}@Override public void dispose(){stage.dispose();if(clubLogo!=null)clubLogo.dispose();}
    private Table createDraftFooter(DraftPick current) {
        Table footer = new Table();
        Table board = ScreenUI.createPanel(); board.top().left();
        board.add(ScreenUI.createSectionTitle(game.skin, "BIG BOARD • TOP 5")).colspan(5).growX().left().padBottom(4f).row();
        List<Player> ranked = new ArrayList<>(available());
        ranked.sort(Comparator.comparingInt((Player player) -> player.getOverall() + player.getPotential() / 3).reversed());
        for (int i = 0; i < Math.min(5, ranked.size()); i++) {
            Player prospect = ranked.get(i); ScoutTarget target = target(prospect);
            Table card = new Table();
            Label boardName = ScreenUI.createBoldValue(game.skin, (i + 1) + "  " + ScreenUI.shorten(prospect.getName(), 15), Color.WHITE, Align.left);
            boardName.setFontScale(.43f);
            card.add(boardName).left().row();
            card.add(ScreenUI.createSubtitle(game.skin, prospect.getPosition() + "  •  " + displayOvr(prospect, target)
                + " / " + displayPot(prospect, target))).left();
            board.add(card).growX().uniformX().padRight(5f);
        }
        Table recent = ScreenUI.createPanel(); recent.top().left();
        recent.add(ScreenUI.createSectionTitle(game.skin, "ÚLTIMAS PICKS")).colspan(3).growX().left().padBottom(4f).row();
        List<DraftSelection> selections = game.league.getDraftSelections();
        for (int i = selections.size() - 1; i >= Math.max(0, selections.size() - 3); i--) {
            DraftSelection choice = selections.get(i);
            Label pick=ScreenUI.createSubtitle(game.skin, "#" + overall(choice.getPick()) + "  "
                + ScreenUI.shorten(choice.getPick().getCurrentOwner().getName(),12) + "\n" + ScreenUI.shorten(choice.getPlayer().getName(),13));
            pick.setWrap(true); pick.setAlignment(Align.left);
            recent.add(pick).growX().uniformX().left().padRight(5f);
        }
        if (selections.isEmpty()) recent.add(ScreenUI.createSubtitle(game.skin, "O Draft ainda não começou.")).left();
        Table upper = new Table();
        upper.add(board).grow().padRight(8f); upper.add(recent).width(450f).growY();
        footer.add(upper).growX().height(69f).row();
        footer.add(bottomPanel(current)).growX().height(51f).padTop(5f);
        return footer;
    }

}
