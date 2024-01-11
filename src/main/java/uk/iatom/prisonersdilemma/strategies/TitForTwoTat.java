package uk.iatom.prisonersdilemma.strategies;

public class TitForTwoTat extends AbstractStrategy {

    @Override
    public boolean shouldBetray(int round, boolean[] myDecisions, boolean[] opponentDecisions) {
        // Whatever the opponent did last round, or betray from two rounds ago
        return round > 1 && (opponentDecisions[round - 1] || opponentDecisions[round - 2]);
    }
}
