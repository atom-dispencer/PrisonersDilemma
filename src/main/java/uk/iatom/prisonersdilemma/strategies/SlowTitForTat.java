package uk.iatom.prisonersdilemma.strategies;

public class SlowTitForTat extends AbstractStrategy {

    @Override
    public boolean shouldBetray(int round, boolean[] myDecisions, boolean[] opponentDecisions) {
        // Betray them if you have received two betrayals
        return round > 1 && (opponentDecisions[round - 1] && opponentDecisions[round - 2]);
    }
}
