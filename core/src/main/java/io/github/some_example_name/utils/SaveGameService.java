package io.github.some_example_name.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import io.github.some_example_name.Main;
import io.github.some_example_name.database.GameDatabase;
import io.github.some_example_name.engine.DevelopmentEngine;
import io.github.some_example_name.engine.MatchEngine;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.model.FreeAgencyService;
import io.github.some_example_name.model.League;
import io.github.some_example_name.model.ManagerCareer;
import io.github.some_example_name.model.MatchEvent;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.simulation.PlayoffSimulator;
import io.github.some_example_name.simulation.SeasonSimulator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/** Slot único de carreira, gravado fora da pasta do executável. */
public final class SaveGameService {
    private static final int FORMAT_VERSION = 1;
    private static final String SAVE_PATH = "WorldFD69/saves/career.wfl";

    private SaveGameService() { }

    public static boolean hasSave() {
        return saveFile().exists();
    }

    public static String getSaveLocation() {
        return saveFile().file().getAbsolutePath();
    }

    /** Grava primeiro em um arquivo temporário para não corromper o save anterior. */
    public static void save(Main game) throws IOException {
        validateGame(game);
        FileHandle target = saveFile();
        target.parent().mkdirs();
        FileHandle temporary = Gdx.files.absolute(target.file().getAbsolutePath() + ".tmp");
        SaveData data = SaveData.capture(game);

        try (FileOutputStream file = new FileOutputStream(temporary.file());
             ObjectOutputStream output = new ObjectOutputStream(
                 new BufferedOutputStream(file))) {
            output.writeObject(data);
            output.flush();
        } catch (IOException failure) {
            temporary.delete();
            throw failure;
        }

        try {
            Files.move(
                temporary.file().toPath(),
                target.file().toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            );
        } catch (java.nio.file.AtomicMoveNotSupportedException ignored) {
            Files.move(
                temporary.file().toPath(),
                target.file().toPath(),
                StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    public static void load(Main game) throws IOException {
        if (!hasSave()) throw new IOException("Nenhuma partida salva foi encontrada.");
        SaveData data;
        try (ObjectInputStream input = new ObjectInputStream(
            new BufferedInputStream(new FileInputStream(saveFile().file())))) {
            Object stored = input.readObject();
            if (!(stored instanceof SaveData)) throw new IOException("O arquivo não é um save válido da WFL.");
            data = (SaveData) stored;
        } catch (ClassNotFoundException failure) {
            throw new IOException("O save usa dados incompatíveis com esta versão do jogo.", failure);
        }

        if (data.version != FORMAT_VERSION) {
            throw new IOException("O save pertence a uma versão incompatível do jogo.");
        }
        data.restore(game);
    }

    private static void validateGame(Main game) throws IOException {
        if (game == null || game.league == null || game.playerClub == null) {
            throw new IOException("Inicie ou carregue uma carreira antes de salvar.");
        }
    }

    private static FileHandle saveFile() {
        return Gdx.files.external(SAVE_PATH);
    }

    /** Contêiner preserva referências compartilhadas entre clubes, atletas e partidas. */
    private static final class SaveData implements Serializable {
        private static final long serialVersionUID = 1L;

        int version = FORMAT_VERSION;
        long savedAt = System.currentTimeMillis();
        GameDatabase database;
        League league;
        FreeAgencyService freeAgencyService;
        Club playerClub;
        ManagerCareer managerCareer;
        List<Player> draftClass;
        int draftClassYear;
        DraftScoutManager draftScoutManager;
        List<MatchEvent> pendingSquadAlerts;

        static SaveData capture(Main game) {
            SaveData data = new SaveData();
            data.database = game.database;
            data.league = game.league;
            data.freeAgencyService = game.freeAgencyService;
            data.playerClub = game.playerClub;
            data.managerCareer = game.managerCareer;
            data.draftClass = game.draftClass == null ? null : new ArrayList<>(game.draftClass);
            data.draftClassYear = game.draftClassYear;
            data.draftScoutManager = game.draftScoutManager;
            data.pendingSquadAlerts = game.snapshotPendingSquadAlerts();
            return data;
        }

        void restore(Main game) throws IOException {
            if (database == null || league == null || playerClub == null || managerCareer == null) {
                throw new IOException("O save está incompleto ou corrompido.");
            }
            game.database = database;
            game.league = league;
            game.playerClub = playerClub;
            game.managerCareer = managerCareer;
            game.freeAgencyService = freeAgencyService != null
                ? freeAgencyService
                : new FreeAgencyService(league);
            game.draftClass = draftClass;
            game.draftClassYear = draftClassYear;
            game.draftScoutManager = draftScoutManager != null
                ? draftScoutManager
                : new DraftScoutManager(3);
            game.restorePendingSquadAlerts(pendingSquadAlerts);

            game.matchEngine = new MatchEngine(league);
            game.developmentEngine = new DevelopmentEngine();
            game.seasonSimulator = new SeasonSimulator();
            game.playoffSimulator = new PlayoffSimulator(game.matchEngine, league);
        }
    }
}
