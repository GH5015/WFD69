package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.StaffMember;
import io.github.some_example_name.model.StaffRole;
import io.github.some_example_name.database.StaffDatabase;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

import java.util.List;

/** Gestão de comissão por cargo, com profissionais disponíveis na Off Season. */
public class StaffScreen implements Screen {
    private final Main game; private final Club club; private final Stage stage; private final Texture starTexture; private StaffRole selectedRole=StaffRole.COACH;
    public StaffScreen(Main game,Club club){this.game=game;this.club=club;stage=new Stage(new ResponsiveViewport());starTexture=ScreenUI.loadTintableIcon("Icons8/icons8-estrela-48.png");}
    @Override public void show(){Gdx.input.setInputProcessor(stage);refresh();}
    private void refresh(){stage.clear();Stack root=new Stack();root.setFillParent(true);stage.addActor(root);root.add(new Image(game.background));boolean offseason="OFFSEASON".equals(game.league.getCurrentStage());Table page=ScreenUI.createPage(true);page.add(ScreenUI.createHeader(game.skin,"STAFF",club.getName().toUpperCase()+" • "+(offseason?"OFF SEASON":"CONSULTA • ALTERAÇÕES SÓ NA OFF SEASON"))).growX().height(ScreenUI.HEADER_HEIGHT).padBottom(10).row();Table summary=ScreenUI.createPanel();summary.add(ScreenUI.createStatusBox(game.skin,"CUSTO ANUAL",money(annualCost()),StyleFactory.SOFT_YELLOW)).growX().uniformX().padRight(8);summary.add(ScreenUI.createStatusBox(game.skin,"ORÇAMENTO",money(club.getFinance().getBalance()),ScreenUI.SUCCESS)).growX().uniformX();page.add(summary).growX().height(62).padBottom(9).row();page.add(tabs()).growX().height(47).padBottom(9).row();page.add(currentPanel(offseason)).growX().height(255).padBottom(9).row();page.add(candidatesPanel(offseason)).grow().minHeight(150).padBottom(10).row();TextButton back=ScreenUI.createPrimaryButton(game.skin,offseason?"← VOLTAR À OFF SEASON":"VOLTAR");back.addListener(new ClickListener(){@Override public void clicked(InputEvent e,float x,float y){game.setScreen(offseason?new OffSeasonScreen(game,club):new ClubManagementScreen(game,club));}});page.add(back).width(280).height(48).center();root.add(page);}
    private Table tabs(){Table panel=ScreenUI.createPanel();for(final StaffRole role:StaffRole.values()){TextButton tab=ScreenUI.createInteractiveButton(role.getLabel().toUpperCase(),game.skin);tab.getLabel().setFontScale(.43f);tab.setColor(role==selectedRole?StyleFactory.GOLD:com.badlogic.gdx.graphics.Color.WHITE);tab.addListener(new ClickListener(){@Override public void clicked(InputEvent e,float x,float y){selectedRole=role;refresh();}});panel.add(tab).growX().height(35).pad(0,3,0,3);}return panel;}
    private Table currentPanel(boolean offseason){StaffMember member=club.getStaffMember(selectedRole);boolean expired=member.getContractEndYear()<=game.league.getCurrentSeason();Table panel=ScreenUI.createPanel();panel.add(ScreenUI.createSectionTitle(game.skin,selectedRole.getLabel().toUpperCase())).left().expandX();panel.add(stars(member.getDisplayRating())).right().row();panel.add(ScreenUI.createBoldValue(game.skin,member.getName()+"  •  "+member.getNationality(),StyleFactory.CREME_AGED,Align.left)).colspan(2).left().padTop(8).row();panel.add(ScreenUI.createSubtitle(game.skin,"Salário")).left().padTop(8);panel.add(ScreenUI.createBoldValue(game.skin,money(member.getAnnualSalary()),StyleFactory.CREME_AGED,Align.right)).right().padTop(8).row();panel.add(ScreenUI.createSubtitle(game.skin,"Contrato")).left().padTop(4);panel.add(ScreenUI.createBoldValue(game.skin,expired?"EXPIRADO":"até "+member.getContractEndYear(),expired?ScreenUI.WARNING:ScreenUI.SUCCESS,Align.right)).right().padTop(4).row();panel.add(ScreenUI.createSubtitle(game.skin,"ESPECIALIDADE")).left().padTop(5);panel.add(ScreenUI.createBoldValue(game.skin,member.getSpecialty(),StyleFactory.CREME_AGED,Align.right)).right().padTop(5).row();panel.add(ScreenUI.createSubtitle(game.skin,"IMPACTO NO SISTEMA")).left().padTop(5);panel.add(ScreenUI.createBoldValue(game.skin,systemImpact(selectedRole,member.getEffectLevel()),ScreenUI.SUCCESS,Align.right)).right().padTop(5).row();Table actions=new Table();TextButton renew=ScreenUI.createPrimaryButton(game.skin,"RENOVAR");renew.setDisabled(!offseason);renew.addListener(new ClickListener(){@Override public void clicked(InputEvent e,float x,float y){if(offseason){offer(member);}}});actions.add(renew).width(145).height(38).padRight(6);TextButton replace=ScreenUI.createInteractiveButton("SUBSTITUIR",game.skin);replace.setDisabled(!offseason);replace.addListener(new ClickListener(){@Override public void clicked(InputEvent e,float x,float y){if(offseason)showReplacementInfo();}});actions.add(replace).width(145).height(38);panel.add(actions).colspan(2).center().padTop(7);return panel;}
    private Table candidatesPanel(boolean offseason){List<StaffMember> available=candidates(selectedRole);Table panel=ScreenUI.createPanel();panel.top();Table title=new Table();title.add(ScreenUI.createSectionTitle(game.skin,"MERCADO • "+selectedRole.getLabel().toUpperCase())).left().expandX();title.add(ScreenUI.createSubtitle(game.skin,available.size()+" DISPONÍVEIS NESTE CARGO • ROLE PARA VER TODOS")).right();panel.add(title).growX().padBottom(7).row();Table content=new Table();content.top();for(final StaffMember candidate:available){Table row=ScreenUI.createRow(candidate.getName().hashCode());row.add(ScreenUI.createBoldValue(game.skin,candidate.getName(),StyleFactory.CREME_AGED,Align.left)).growX().padLeft(8);row.add(ScreenUI.createSubtitle(game.skin,candidate.getNationality())).width(125).left();row.add(stars(candidate.getDisplayRating())).width(120).center();row.add(ScreenUI.createSubtitle(game.skin,candidate.getSpecialty()+"\n"+money(candidate.getAnnualSalary())+" • "+years(candidate)+" anos")).width(270).left();TextButton hire=ScreenUI.createInteractiveButton(offseason?"NEGOCIAR":"BLOQUEADO",game.skin);hire.setName("staff-negotiate-"+candidate.getName());hire.getLabel().setFontScale(.43f);hire.setDisabled(!offseason);hire.addListener(new ClickListener(){@Override public void clicked(InputEvent e,float x,float y){if(offseason)offer(candidate);}});row.add(hire).width(155).height(34).center().padRight(7);content.add(row).growX().height(52).padBottom(4).row();}ScrollPane scroll=new ScrollPane(content,game.skin);scroll.setName("staff-market-scroll");scroll.setScrollingDisabled(true,false);scroll.setFadeScrollBars(false);panel.add(scroll).grow().minHeight(0);stage.setScrollFocus(scroll);return panel;}
    private List<StaffMember> candidates(StaffRole role){return StaffDatabase.getOffseasonCandidates(role,game.league.getCurrentSeason(),game.league.getClubs(),club,15);}
    private void offer(final StaffMember candidate) {
        StaffContractDialog.show(game, club, candidate, starTexture, stage, this::refresh);
    }
    private void showReplacementInfo(){Dialog dialog=new Dialog("SUBSTITUIR STAFF",game.skin);dialog.text("Escolha um dos profissionais disponíveis abaixo.\nO membro atual deixará o cargo imediatamente.");dialog.button("ENTENDI");dialog.show(stage);}
    private long annualCost(){long total=0;for(StaffRole role:StaffRole.values())total+=club.getStaffMember(role).getAnnualSalary();return total;}
    private int years(StaffMember member){return Math.max(1,member.getContractEndYear()-game.league.getCurrentSeason());}
    private String specialty(StaffRole role){switch(role){case COACH:return "Desenvolvimento";case SCOUT:return "Jovens";case DEVELOPMENT_DIRECTOR:return "Potencial";case FITNESS_COACH:return "Recuperação";default:return "Lesões";}}
    private String systemImpact(StaffRole role,int stars){switch(role){case COACH:return String.format(java.util.Locale.US,"Tática e desempenho: %+.1f%%",(io.github.some_example_name.model.StaffImpact.coachPerformance(stars)-1d)*100d);case SCOUT:return String.format(java.util.Locale.US,"Scouting: %.1f%% por dia",io.github.some_example_name.model.StaffImpact.scoutingDailyProgress(stars));case DEVELOPMENT_DIRECTOR:return "Evolução e aproveitamento do potencial";case FITNESS_COACH:return String.format(java.util.Locale.US,"Desgaste em jogo: %.0f%%",io.github.some_example_name.model.StaffImpact.matchFatigueMultiplier(stars)*100d);default:return String.format(java.util.Locale.US,"Risco de lesão: %.0f%%",io.github.some_example_name.model.StaffImpact.injuryRiskMultiplier(stars)*100d);}}
    private Table stars(float rating){return ScreenUI.createStarRating(starTexture,rating,19f);}
    private String money(long value){return value>=1_000_000?String.format(java.util.Locale.US,"WFL$ %.1fM",value/1_000_000d):String.format(java.util.Locale.US,"WFL$ %.0fK",value/1_000d);}
    @Override public void render(float d){Gdx.gl.glClearColor(0,0,0,1);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);stage.act(d);stage.draw();}@Override public void resize(int w,int h){stage.getViewport().update(w,h,true);}@Override public void pause(){}@Override public void resume(){}@Override public void hide(){}@Override public void dispose(){stage.dispose();starTexture.dispose();}
}
