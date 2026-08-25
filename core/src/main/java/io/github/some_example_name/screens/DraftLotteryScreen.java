package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.DraftPick;
import io.github.some_example_name.utils.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Cerimônia de lottery: probabilidades, revelação regressiva e resultado oficial. */
public class DraftLotteryScreen implements Screen {
    private enum View { INTRO, ODDS, CEREMONY, RESULTS }
    private final Main game; private final Club club; private final Stage stage;
    private final Map<Club,Integer> odds=new LinkedHashMap<>(); private final List<Club> projected=new ArrayList<>();
    private List<Club> result=new ArrayList<>(); private View view=View.INTRO; private int revealed;
    public DraftLotteryScreen(Main game,Club club){this.game=game;this.club=club;stage=new Stage(new ResponsiveViewport());odds.putAll(game.league.getDraftLotteryOdds());projected.addAll(odds.keySet());}
    @Override public void show(){Gdx.input.setInputProcessor(stage);refresh();}
    private void refresh(){stage.clear();Stack root=new Stack();root.setFillParent(true);stage.addActor(root);root.add(new Image(game.background));Table page=ScreenUI.createPage(true);page.add(ScreenUI.createHeader(game.skin,"WFL DRAFT LOTTERY",String.valueOf(game.league.getCurrentSeason()+1))).growX().height(ScreenUI.HEADER_HEIGHT).padBottom(12).row();Table panel=ScreenUI.createPanel();panel.top();if(view==View.INTRO)intro(panel);else if(view==View.ODDS)odds(panel);else if(view==View.CEREMONY)ceremony(panel);else results(panel);page.add(panel).grow().padBottom(12).row();Table actions=new Table();actions(actions);page.add(actions).height(50).center();root.add(page);}
    private void intro(Table p){Club origin=ownLotteryOrigin();int pick=origin==null?0:projected.indexOf(origin)+1,chance=origin==null?0:odds.get(origin);p.add(ScreenUI.createSectionTitle(game.skin,"DRAFT LOTTERY")).center().padTop(24).row();p.add(ScreenUI.createSubtitle(game.skin,"A próxima geração começa aqui.")).center().padTop(8).row();p.add(ScreenUI.createSectionTitle(game.skin,"SUA FRANQUIA")).center().padTop(25).row();p.add(value(club.getName().toUpperCase(),true)).center().padTop(7).row();p.add(ScreenUI.createSubtitle(game.skin,origin==null?"Sua franquia não possui escolha na lottery.":"Campanha da pick: "+game.league.getClubRecord(origin)+" • Origem: "+origin.getName())).center().padTop(6).row();p.add(value(pick>0?"PICK PROJETADA: #"+pick:"FORA DA LOTTERY",false)).center().padTop(15).row();p.add(ScreenUI.createSubtitle(game.skin,"Chance da Pick #1: "+chance+"%   •   Chance Top 3: "+topThree(chance)+"%")).center().padTop(8);}
    private void odds(Table p){p.add(ScreenUI.createSectionTitle(game.skin,"LOTTERY ODDS")).colspan(4).center().padBottom(12).row();header(p,"#","FRANQUIA","CAMPANHA","CHANCE #1");int rank=1;for(Club c:projected){boolean mine=c==club;p.add(value("#"+rank++,mine)).width(70);p.add(value(c.getName(),mine)).growX();p.add(value(game.league.getClubRecord(c),mine)).width(130);p.add(value(odds.get(c)+"%",mine)).width(130).row();}}
    private void ceremony(Table p){int total=result.size(),current=total-revealed;p.add(ScreenUI.createSectionTitle(game.skin,revealed==total?"LOTTERY CONCLUÍDA":"PICK #"+current)).center().padTop(10).row();if(revealed==0)p.add(value("PRONTO PARA REVELAR",true)).center().padTop(18).row();else{Club newest=result.get(current);int prior=projected.indexOf(newest)+1;p.add(value(pickLabel(newest).toUpperCase(),ownsPick(newest))).center().padTop(14).row();p.add(ScreenUI.createSubtitle(game.skin,"Pick projetada: #"+prior+"     Pick obtida: #"+current+"     "+movement(current,prior))).center().padTop(7).row();}if(topThreeGuaranteed())p.add(value("TOP 3 GARANTIDO • "+club.getName().toUpperCase(),true)).center().padTop(10).row();p.add(ScreenUI.createSectionTitle(game.skin,"ORDEM REVELADA")).center().padTop(16).row();for(int i=total-1;i>=0;i--){String text=i>=total-revealed?"#"+(i+1)+"   "+pickLabel(result.get(i)):"#"+(i+1)+"   ?";p.add(ScreenUI.createSubtitle(game.skin,text)).center().height(21).row();}}
    private void results(Table p){p.add(ScreenUI.createSectionTitle(game.skin,"WFL DRAFT ORDER • "+(game.league.getCurrentSeason()+1))).colspan(3).center().padBottom(10).row();header(p,"#","FRANQUIA","MOVIMENTO");for(int i=0;i<result.size();i++){Club c=result.get(i);int prior=projected.indexOf(c)+1;boolean mine=ownsPick(c);p.add(value("#"+(i+1),mine)).width(70);p.add(value(pickLabel(c),mine)).growX();p.add(value(movement(i+1,prior),mine)).width(145).row();}Club ownOrigin=ownLotteryOrigin();int own=ownOrigin==null?0:result.indexOf(ownOrigin)+1;if(own>0)p.add(value("SUA ESCOLHA: #"+own,true)).colspan(3).center().padTop(14);}
    private Label value(String s,boolean mine){return ScreenUI.createBoldValue(game.skin,s,mine?StyleFactory.SOFT_YELLOW:StyleFactory.CREME_AGED,Align.center);}
    private void header(Table p,String... labels){for(String label:labels)p.add(ScreenUI.createTableHeaderLabel(game.skin,label,Align.center)).padBottom(5);p.row();}
    private void actions(Table p){if(view==View.INTRO){button(p,"VER PROBABILIDADES",()->{view=View.ODDS;refresh();});button(p,"INICIAR LOTERIA",this::start);}else if(view==View.ODDS)button(p,"VOLTAR",()->{view=View.INTRO;refresh();});else if(view==View.CEREMONY){button(p,revealed==result.size()?"VER RESULTADO FINAL":"REVELAR",this::reveal);if(revealed<result.size())button(p,"PULAR LOTERIA",()->{revealed=result.size();view=View.RESULTS;refresh();});}else{button(p,"IR PARA O DRAFT",()->game.setScreen(new DraftScreen(game,club,game.draftScoutManager)));button(p,"VOLTAR À OFF SEASON",()->game.setScreen(new OffSeasonScreen(game,club)));}}
    private void button(Table p,String text,Runnable run){TextButton b=ScreenUI.createPrimaryButton(game.skin,text);b.addListener(new ClickListener(){@Override public void clicked(InputEvent e,float x,float y){run.run();}});p.add(b).width(240).height(48).pad(0,6,0,6);}
    private void start(){result=new ArrayList<>(game.league.runDraftLottery().subList(0,projected.size()));revealed=0;view=View.CEREMONY;refresh();}
    private void reveal(){if(revealed<result.size())revealed++;else view=View.RESULTS;refresh();}
    private boolean topThreeGuaranteed(){for(Club origin:result.subList(Math.max(0,result.size()-revealed),result.size()))if(ownsPick(origin))return false;return ownLotteryOrigin()!=null&&revealed>=result.size()-3;}
    private Club ownLotteryOrigin(){for(Club origin:projected)if(ownsPick(origin))return origin;return null;}
    private boolean ownsPick(Club origin){return pickOwner(origin)==club;}
    private Club pickOwner(Club origin){for(Club holder:game.league.getClubs())for(DraftPick pick:holder.getDraftPicks())if(pick.getYear()==game.league.getCurrentSeason()+1&&pick.getRound()==1&&pick.getOriginalOwner()==origin)return pick.getCurrentOwner();return origin;}
    private String pickLabel(Club origin){Club owner=pickOwner(origin);return owner==origin?owner.getName():owner.getName()+" (via "+origin.getName()+")";}
    private int topThree(int chance){return Math.min(95,(int)Math.round(chance*2.7));}
    private String movement(int actual,int projectedPick){int change=projectedPick-actual;return change>0?"▲ +"+change:change<0?"▼ "+change:"—";}
    @Override public void render(float d){Gdx.gl.glClearColor(0,0,0,1);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);stage.act(d);stage.draw();}@Override public void resize(int w,int h){stage.getViewport().update(w,h,true);}@Override public void pause(){}@Override public void resume(){}@Override public void hide(){}@Override public void dispose(){stage.dispose();}
}
