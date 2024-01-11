package uk.iatom.prisonersdilemma.strategies;

public class HyperAggressive extends AbstractStrategy {

    @Override
    public boolean shouldBetray(int round, boolean[] myDecisions, boolean[] opponentDecisions) {
        return true;
    }
}
