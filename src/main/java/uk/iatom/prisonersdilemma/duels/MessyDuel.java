package uk.iatom.prisonersdilemma.duels;

import java.util.Random;
import uk.iatom.prisonersdilemma.strategies.AbstractStrategy;

public class MessyDuel extends Duel {

  private final float corruptionChance;
  private final Random random;

  public MessyDuel(float corruptionChance,
      int rounds, AbstractStrategy strategy0, AbstractStrategy strategy1) {
    super(rounds, strategy0, strategy1);
    this.corruptionChance = corruptionChance;
    this.random = new Random();
  }

  @Override
  public boolean[] getDecisions(int r, boolean[] decisions0, boolean[] decisions1) {
    boolean[] decisions = super.getDecisions(r, decisions0, decisions1);

    // Random chance to flip each decision
    for (int i = 0; i < decisions.length; i++) {
      decisions[i] = (random.nextFloat() < corruptionChance) != decisions[i];
    }

    return decisions;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName() + ", " + (corruptionChance * 100) + "%";
  }
}
