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
import io.github.some_example_name.model.DraftSelection;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.utils.ResponsiveViewport;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Fechamento do evento anual: registra os rookies e conecta o Draft ao elenco. */
public class DraftSummaryScreen implements Screen {
    private final Main game; private final Club club; private final Stage stage;
    public DraftSummaryScreen(Main game, Club club) { this.game=game; this.club=club; stage=new Stage(new ResponsiveViewport()); }
    @Override public void show(){Gdx.input.setInputProcessor(stage);refresh();}
    private void refresh(){stage.clear();Stack root=new Stack();root.setFillParent(true);stage.addActor(root);root.add(new Image(game.background));Table page=ScreenUI.createPage(true);page.add(ScreenUI.createHeader(game.skin,"DRAFT COMPLETO",String.valueOf(game.league.getCurrentSeason()+1))).growX().height(ScreenUI.HEADER_HEIGHT).padBottom(12).row();Table panel=ScreenUI.createPanel();panel.top();panel.add(ScreenUI.createSectionTitle(game.skin,club.getName().toUpperCase())).center().padBottom(12).row();List<DraftSelection> rookies=new ArrayList<>();for(DraftSelection selection:game.league.getDraftSelections())if(selection.getPick().getCurrentOwner()==club)rookies.add(selection);rookies.sort(Comparator.comparingInt(s->s.getPick().getProjectedOverallPosition()));if(rookies.isEmpty())panel.add(ScreenUI.createSubtitle(game.skin,"Sua franquia não realizou escolhas neste Draft.")).center().padTop(18).row();for(DraftSelection selection:rookies){Player p=selection.getPlayer();int pick=selection.getPick().getProjectedOverallPosition();panel.add(ScreenUI.createBoldValue(game.skin,"PICK #"+pick,StyleFactory.SOFT_YELLOW,Align.center)).center().padTop(8).row();panel.add(ScreenUI.createBoldValue(game.skin,p.getName(),StyleFactory.CREME_AGED,Align.center)).center().padTop(3).row();panel.add(ScreenUI.createSubtitle(game.skin,p.getPrimaryPosition()+" • "+p.getAge()+" anos • OVR "+p.getOverall()+" • POT "+p.getPotential())).center().padBottom(9).row();}panel.add(ScreenUI.createSectionTitle(game.skin,"NOTA DA CLASSE")).center().padTop(12).row();panel.add(ScreenUI.createBoldValue(game.skin,grade(rookies),StyleFactory.SOFT_YELLOW,Align.center)).center().padTop(4);page.add(panel).grow().padBottom(12).row();Table actions=new Table();button(actions,"VER DRAFT COMPLETO",()->game.setScreen(new DraftBoardScreen(game,club,game.draftScoutManager)));button(actions,"IR PARA O ELENCO",()->game.setScreen(new ClubManagementScreen(game,club)));button(actions,"VOLTAR À OFF SEASON",()->game.setScreen(new OffSeasonScreen(game,club)));page.add(actions).center().height(50);root.add(page);}
    private String grade(List<DraftSelection> rookies){if(rookies.isEmpty())return "C";double score=0;for(DraftSelection s:rookies)score+=s.getPlayer().getOverall()+s.getPlayer().getPotential();score/=rookies.size()*2d;return score>=85?"A":score>=78?"B":score>=70?"C":"D";}
    private void button(Table table,String label,Runnable run){TextButton b=ScreenUI.createPrimaryButton(game.skin,label);b.getLabel().setFontScale(.45f);b.addListener(new ClickListener(){@Override public void clicked(InputEvent e,float x,float y){run.run();}});table.add(b).width(210).height(48).pad(0,5,0,5);}
    @Override public void render(float d){Gdx.gl.glClearColor(0,0,0,1);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);stage.act(d);stage.draw();}@Override public void resize(int w,int h){stage.getViewport().update(w,h,true);}@Override public void pause(){}@Override public void resume(){}@Override public void hide(){}@Override public void dispose(){stage.dispose();}
}
