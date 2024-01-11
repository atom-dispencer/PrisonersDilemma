package uk.iatom.prisonersdilemma.duels;

import uk.iatom.prisonersdilemma.strategies.AbstractStrategy;

public class Duel {

    protected final int rounds;
    public final AbstractStrategy strategy0;
    public final AbstractStrategy strategy1;

    public Duel(int rounds, AbstractStrategy strategy0, AbstractStrategy strategy1) {
        this.rounds = rounds;
        this.strategy0 = strategy0;
        this.strategy1 = strategy1;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public DuelResult getDuelResults() {

        boolean[] decisions0 = new boolean[rounds];
        boolean[] decisions1 = new boolean[rounds];
        int[] scores0 = new int[rounds];
        int[] scores1 = new int[rounds];

        for (int r = 0; r < rounds; r++) {
            boolean[] roundDecisions = getDecisions(r, decisions0, decisions1);

            decisions0[r] = roundDecisions[0];
            decisions1[r] = roundDecisions[1];

            int[] scores = getPoints(roundDecisions[0], roundDecisions[1]);
            scores0[r] = scores[0];
            scores1[r] = scores[1];
        }

        return new DuelResult(
                this,
                new boolean[][]{decisions0, decisions1},
                new int[][]{scores0, scores1}
        );
    }

    public boolean[] getDecisions(int r, boolean[] decisions0, boolean[] decisions1) {
        return new boolean[]{
                strategy0.shouldBetray(r, decisions0, decisions1),
                strategy1.shouldBetray(r, decisions1, decisions0)
        };
    }

    public int[] getPoints(boolean betrayalZero, boolean betrayalOne) {

        // Both cooperate
        if (!betrayalZero && !betrayalOne) return new int[]{3, 3};

        // One betrays
        if (betrayalZero != betrayalOne) return new int[]{betrayalZero ? 5 : 0, betrayalOne ? 5 : 0};

        // Both betray
        return new int[]{1, 1};
    }
}
