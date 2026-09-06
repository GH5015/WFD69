package io.github.some_example_name;

import io.github.some_example_name.database.DraftClassRepository;
import io.github.some_example_name.database.GameDatabase;
import io.github.some_example_name.model.Club;
import io.github.some_example_name.model.DraftOrderService;
import io.github.some_example_name.model.DraftScoutManager;
import io.github.some_example_name.model.FreeAgencyService;
import io.github.some_example_name.model.League;
import io.github.some_example_name.model.ManagerCareer;
import io.github.some_example_name.model.Player;
import io.github.some_example_name.simulation.SeasonSimulator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/** Garante que o grafo completo usado pelo slot de carreira pode ir e voltar do disco. */
public final class SaveGameSerializationRegressionTest {
    public static void main(String[] args) throws Exception {
        GameDatabase database = new GameDatabase();
        League league = new League("Liga Mundial", 1969);
        database.getClubs().forEach(league::addClub);
        database.applyInitialContractsAndBindClubs(1969);
        DraftOrderService.initializeDraftPicks(league, 1970);
        new SeasonSimulator().createSchedule(league);

        Club userClub = league.getClubs().get(0);
        userClub.setUserControlled(true);
        ManagerCareer career = new ManagerCareer();
        career.startJob(userClub);
        FreeAgencyService freeAgency = new FreeAgencyService(league);
        List<Player> draftClass = DraftClassRepository.getClassForYear(1970);
        DraftScoutManager scouting = new DraftScoutManager(3);
        scouting.addTarget(draftClass.get(0));

        Object[] roots = {database, league, freeAgency, userClub, career, draftClass, scouting};
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(roots);
        }

        Object[] restored;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (Object[]) input.readObject();
        }

        League restoredLeague = (League) restored[1];
        Club restoredUserClub = (Club) restored[3];
        if (restoredLeague.getClubs().get(0) != restoredUserClub) {
            throw new AssertionError("As referências entre liga e clube não foram preservadas.");
        }
        if (restoredUserClub.getSquad().isEmpty()
            || restoredUserClub.getSquad().get(0).getCurrentClub() != restoredUserClub) {
            throw new AssertionError("O vínculo entre atleta e clube não foi restaurado.");
        }
        if (((DraftScoutManager) restored[6]).getActiveTargets().size() != 1) {
            throw new AssertionError("O progresso de scouting não foi preservado.");
        }
        System.out.println("SaveGameSerializationRegressionTest: OK (" + bytes.size() + " bytes)");
    }
}
