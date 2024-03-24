package uk.iatom.prisonersdilemma.strategies;

public abstract class AbstractStrategy {

  public String getName() {
    return getClass().getSimpleName();
  }

  public abstract boolean shouldBetray(int round, boolean[] myDecisions,
      boolean[] opponentDecisions);
}
