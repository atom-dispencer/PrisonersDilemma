package uk.iatom.prisonersdilemma.competitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.iatom.prisonersdilemma.duels.Duel;
import uk.iatom.prisonersdilemma.duels.DuelResult;
import uk.iatom.prisonersdilemma.duels.MessyDuel;
import uk.iatom.prisonersdilemma.strategies.AbstractStrategy;

public class AllVersusAllCompetition {

  private final AbstractStrategy[] strategies;
  private final Map<AbstractStrategy, Integer> superTotals = new HashMap<>();
  private final Map<AbstractStrategy, Integer> superScores = new HashMap<>();

  public AllVersusAllCompetition(AbstractStrategy[] strategies) {
    this.strategies = strategies;
  }

  public void runDuelSet() {
    List<Duel> duels = new ArrayList<>();
    for (AbstractStrategy s1 : strategies) {
      for (AbstractStrategy s2 : strategies) {
        duels.add(new MessyDuel(0.05f, 100, s1, s2));
      }
    }

    String duelNameFormat = "%s  vs.  %s     [%s]";

    for (Duel d : duels) {
      DuelResult results = d.getDuelResults();
      increaseSuperTotal(d.strategy0, results.totals()[0]);
      increaseSuperTotal(d.strategy1, results.totals()[1]);
      incrementSuperScore(results.winner() == 0 ? d.strategy0 : d.strategy1);

      String duelTitle = String.format(duelNameFormat, d.strategy0.getName(), d.strategy1.getName(),
          d.getName());
      System.out.println(duelTitle);
      System.out.println(results);
    }

    System.out.println("Super-Totals:");
    String superTotalFormat = "%s    Score: %s    Wins: %s";
    superTotals.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .forEach(
            e -> {
              String name = e.getKey().getName();
              int superTotal = e.getValue();
              int superScore = superScores.get(e.getKey());

              String formatted = String.format(superTotalFormat, name, superTotal, superScore);
              System.out.printf("%100s\n", formatted);
            }
        );
  }

  private void increaseSuperTotal(AbstractStrategy strategy, int increase) {
    superTotals.putIfAbsent(strategy, 0);
    superTotals.put(strategy, superTotals.get(strategy) + increase);
  }

  private void incrementSuperScore(AbstractStrategy strategy) {
    superScores.putIfAbsent(strategy, 0);
    superScores.put(strategy, superScores.get(strategy) + 1);
  }
}
