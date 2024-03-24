package uk.iatom.prisonersdilemma.strategies;

import java.util.Random;

public class RandomChoice extends AbstractStrategy {

  private final Random random = new Random();

  @Override
  public boolean shouldBetray(int round, boolean[] myDecisions, boolean[] opponentDecisions) {
    return random.nextBoolean();
  }
}
