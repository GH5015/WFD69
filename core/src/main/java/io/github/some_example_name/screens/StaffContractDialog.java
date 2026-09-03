package io.github.some_example_name.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.*;
import io.github.some_example_name.utils.ScreenUI;
import io.github.some_example_name.utils.StyleFactory;

/** Editable staff offer, with explicit counteroffer acceptance and live expense preview. */
final class StaffContractDialog {
    private StaffContractDialog() { }

    static void show(Main game, Club club, StaffMember candidate, Texture star, Stage stage, Runnable onSigned) {
        final int year = game.league.getCurrentSeason();
        final Dialog dialog = new Dialog("", game.skin);
        dialog.setName("staff-contract-dialog");
        dialog.setModal(true);
        dialog.setMovable(false);
        Table content = dialog.getContentTable();
        content.pad(20);
        content.setBackground(StyleFactory.createMetallicBoard(1000, 650, Color.valueOf("091813")));
        content.add(ScreenUI.createSectionTitle(game.skin, "PROPOSTA DE CONTRATO")).growX().left().padBottom(8).row();
        Table identity = ScreenUI.createSubtlePanel();
        Table copy = new Table();
        copy.add(ScreenUI.createBoldValue(game.skin, candidate.getName().toUpperCase(), Color.WHITE, Align.left)).left().row();
        copy.add(ScreenUI.createSubtitle(game.skin, candidate.getRole().getLabel() + " • " + candidate.getNationality())).left().padTop(5).row();
        copy.add(ScreenUI.createSubtitle(game.skin, candidate.getSpecialty())).left().padTop(5);
        identity.add(copy).growX();
        identity.add(ScreenUI.createStarRating(star, candidate.getDisplayRating(), 21f)).width(135);
        content.add(identity).growX().height(100).padBottom(12).row();

        SelectBox<SalaryOption> salary = new SelectBox<>(game.skin);
        salary.setName("staff-offer-salary");
        Array<SalaryOption> salaries = new Array<>();
        long first = Math.max(10_000L, candidate.getAnnualSalary() / 20_000L * 10_000L);
        long last = Math.max(first, candidate.getAnnualSalary() * 16 / 10 + 10_000L);
        for (long value = first; value <= last; value += 10_000L) salaries.add(new SalaryOption(value));
        salary.setItems(salaries);
        SelectBox<Integer> years = new SelectBox<>(game.skin);
        years.setName("staff-offer-years");
        years.setItems(1, 2, 3, 4, 5);
        years.setSelected(StaffNegotiationService.preferredYears(candidate, year));

        Table terms = ScreenUI.createPanel();
        terms.add(ScreenUI.createSectionTitle(game.skin, "TERMOS DA OFERTA")).colspan(2).left().padBottom(14).row();
        row(game, terms, "Pedido inicial", ScreenUI.createSubtitle(game.skin, money(candidate.getAnnualSalary()) + " / ano"));
        row(game, terms, "Salário anual", salary);
        row(game, terms, "Duração (anos)", years);
        Label willingness = ScreenUI.createSubtitle(game.skin, "");
        willingness.setWrap(true);
        terms.add(willingness).colspan(2).growX().height(64).padTop(8).row();
        TextButton suggest = ScreenUI.createInteractiveButton("USAR OFERTA SUGERIDA", game.skin);
        suggest.setName("staff-suggest-offer");
        terms.add(suggest).colspan(2).growX().height(40);

        Table impact = ScreenUI.createPanel();
        impact.top();
        impact.add(ScreenUI.createSectionTitle(game.skin, "IMPACTO FINANCEIRO")).colspan(2).left().padBottom(14).row();
        Label payroll = ScreenUI.createSubtitle(game.skin, "");
        Label monthly = ScreenUI.createSubtitle(game.skin, "");
        Label total = ScreenUI.createSubtitle(game.skin, "");
        Label end = ScreenUI.createSubtitle(game.skin, "");
        row(game, impact, "Comissão / ano", payroll);
        row(game, impact, "Comissão / mês", monthly);
        row(game, impact, "Total do contrato", total);
        row(game, impact, "Contrato até", end);
        Label note = ScreenUI.createSubtitle(game.skin, "Substitui o profissional atual neste cargo.\nSalário lançado nas despesas mensais, fora do Salary Cap dos jogadores.");
        note.setWrap(true);
        impact.add(note).colspan(2).growX().height(78).padTop(8);

        Table body = new Table();
        body.add(terms).width(455).growY().padRight(12);
        body.add(impact).width(455).growY();
        content.add(body).growX().padBottom(10).row();
        Label feedback = ScreenUI.createSubtitle(game.skin, "Edite os termos e envie sua proposta. Nenhuma contratação ocorre antes do envio.");
        feedback.setName("staff-offer-feedback");
        feedback.setWrap(true);
        content.add(feedback).growX().height(54).row();

        TextButton counter = ScreenUI.createInteractiveButton("USAR CONTRAPROPOSTA", game.skin);
        counter.setName("staff-use-counteroffer");
        counter.setVisible(false);
        final long[] counterSalary = {0};
        Runnable preview = () -> {
            long amount = salary.getSelected().value;
            int duration = years.getSelected();
            long requested = StaffNegotiationService.requestedSalary(candidate, year, duration);
            willingness.setText(amount >= requested ? "Termos compatíveis com o pedido do profissional."
                : "Oferta abaixo do pedido: o profissional poderá fazer uma contraproposta.");
            willingness.setColor(amount >= requested ? ScreenUI.SUCCESS : ScreenUI.WARNING);
            StaffMember current = club.getStaffMember(candidate.getRole());
            long annual = club.getFinance().getAnnualStaffPayroll() - (current == null ? 0 : current.getAnnualSalary()) + amount;
            payroll.setText(money(annual));
            monthly.setText(money(annual / 12));
            total.setText(money(amount * duration));
            end.setText(String.valueOf(year + duration));
            counterSalary[0] = 0;
            counter.setVisible(false);
            feedback.setText("Edite os termos e envie sua proposta. Nenhuma contratação ocorre antes do envio.");
        };
        ChangeListener update = new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { preview.run(); }
        };
        salary.addListener(update);
        years.addListener(update);
        selectSalary(salary, StaffNegotiationService.requestedSalary(candidate, year, years.getSelected()));
        preview.run();
        suggest.addListener(action(() -> selectSalary(salary, StaffNegotiationService.requestedSalary(candidate, year, years.getSelected()))));
        counter.addListener(action(() -> { if (counterSalary[0] > 0) selectSalary(salary, counterSalary[0]); }));
        TextButton send = ScreenUI.createPrimaryButton(game.skin, "ENVIAR OFERTA");
        send.setName("staff-send-offer");
        send.addListener(action(() -> {
            StaffNegotiationService.Result result = StaffNegotiationService.submit(game.league, club, candidate,
                salary.getSelected().value, years.getSelected());
            if (result.accepted) {
                dialog.hide();
                onSigned.run();
                Dialog confirmation = new Dialog("CONTRATO ASSINADO", game.skin);
                confirmation.text(candidate.getName() + "\n" + money(salary.getSelected().value) + " / ano • " + years.getSelected() + " anos\n" + result.message);
                confirmation.button("CONTINUAR");
                confirmation.show(stage);
            } else {
                feedback.setText(result.message + (result.counterSalary > 0 ? "\n" + money(result.counterSalary) + " / ano por " + years.getSelected() + " anos." : ""));
                counterSalary[0] = result.counterSalary;
                counter.setVisible(result.counterSalary > 0);
            }
        }));
        TextButton cancel = ScreenUI.createInteractiveButton("CANCELAR", game.skin);
        cancel.addListener(action(dialog::hide));
        dialog.getButtonTable().add(cancel).width(190).height(45).pad(8);
        dialog.getButtonTable().add(counter).width(270).height(45).pad(8);
        dialog.getButtonTable().add(send).width(250).height(45).pad(8);
        dialog.show(stage);
        dialog.setSize(1000, 660);
        dialog.setPosition((stage.getWidth() - dialog.getWidth()) / 2, (stage.getHeight() - dialog.getHeight()) / 2);
    }

    private static void row(Main game, Table table, String label, Actor value) {
        table.add(ScreenUI.createSubtitle(game.skin, label)).expandX().left().height(40);
        table.add(value).width(215).height(36).right().padBottom(5).row();
    }
    private static ChangeListener action(Runnable run) {
        return new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { run.run(); } };
    }
    private static void selectSalary(SelectBox<SalaryOption> box, long amount) {
        for (SalaryOption option : box.getItems()) if (option.value == amount) { box.setSelected(option); return; }
        Array<SalaryOption> options = new Array<>(box.getItems());
        SalaryOption option = new SalaryOption(amount);
        options.add(option);
        options.sort((a, b) -> Long.compare(a.value, b.value));
        box.setItems(options);
        box.setSelected(option);
    }
    private static String money(long amount) {
        return "WFL$ " + String.format(java.util.Locale.forLanguageTag("pt-BR"), "%,d", amount);
    }
    private static final class SalaryOption {
        final long value;
        SalaryOption(long value) { this.value = value; }
        @Override public String toString() { return money(value); }
    }
}
