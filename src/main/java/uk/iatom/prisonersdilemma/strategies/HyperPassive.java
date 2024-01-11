package uk.iatom.prisonersdilemma.strategies;

public class HyperPassive extends AbstractStrategy {

    @Override
    public boolean shouldBetray(int round, boolean[] myDecisions, boolean[] opponentDecisions) {
        return false;
    }
}
