package uk.iatom.prisonersdilemma.strategies;

public class TitForTat extends AbstractStrategy {

  @Override
  public boolean shouldBetray(int round, boolean[] myDecisions, boolean[] opponentDecisions) {
    // Whatever the opponent did last round
    return round > 0 && opponentDecisions[round - 1];
  }
}
