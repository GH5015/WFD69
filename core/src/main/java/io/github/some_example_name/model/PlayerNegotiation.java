package io.github.some_example_name.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/** Barganha limitada por jogador, clube e temporada; consultas não sorteiam respostas. */
public final class PlayerNegotiation {
    private PlayerNegotiation() { }
    public static class Session {
        public double askingRatio = 1d;
        public int rounds;
        public Map<String, Response> responses = new HashMap<>();
    }
    public static class Response {
        public boolean accepted;
        public boolean rejected;
        public long salary;
        public int years;
        public String message;
        public Response() { }
    }
    public static long askingSalary(Session session, long base) {
        return Math.max(12_000L, Math.round(base * session.askingRatio / 1000d) * 1000L);
    }
    public static Response respond(Session session, long base, long offer, int years,
                                   int preferred, int interest, String identity) {
        String key = base + ":" + offer + ":" + years;
        Response cached = session.responses.get(key);
        if (cached != null) return cached;
        long ask = askingSalary(session, base);
        Response result = new Response();
        result.salary = ask; result.years = preferred;
        int durationGap = Math.abs(years - preferred);
        if (offer <= 0 || years < 1 || years > 5 || offer < base * .75d) {
            result.rejected = true;
            result.message = "A oferta está distante demais das expectativas do jogador.";
        } else if (offer >= ask && durationGap <= 1) {
            result.accepted = true; result.salary = offer; result.years = years;
            result.message = "O jogador concordou com os termos financeiros e a duração.";
        } else {
            double closeness = offer / (double) Math.max(1L, ask);
            double chance = Math.max(.05d, Math.min(.80d,
                .20d + (interest - 1) * .10d + (closeness - .85d) * 1.2d - durationGap * .12d - session.rounds * .06d));
            Random random = new Random(((long) identity.hashCode() << 32) ^ key.hashCode() ^ session.rounds);
            boolean concedes = session.rounds < 4 && offer < ask && durationGap <= 1 && random.nextDouble() < chance;
            if (concedes) {
                session.askingRatio = Math.max(.88d, session.askingRatio - (.02d + random.nextDouble() * .03d));
                result.salary = askingSalary(session, base);
                if (offer >= result.salary) {
                    result.accepted = true; result.salary = offer; result.years = years;
                    result.message = "O jogador cedeu na pedida e aceitou sua proposta.";
                } else {
                    result.message = "O jogador reduziu a pedida e apresentou novos termos.";
                }
            } else {
                result.message = durationGap > 1
                    ? "O jogador quer outra duração de contrato e mantém a pedida."
                    : session.rounds >= 4 ? "O jogador chegou ao limite da negociação e mantém sua última pedida."
                    : "O jogador ouviu a proposta, mas decidiu manter a pedida nesta rodada.";
            }
            session.rounds++;
        }
        session.responses.put(key, result);
        return result;
    }
}
