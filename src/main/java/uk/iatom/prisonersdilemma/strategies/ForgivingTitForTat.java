package uk.iatom.prisonersdilemma.strategies;

import java.util.Random;

public class ForgivingTitForTat extends TitForTat {

  private float forgivingChance;
  private Random random;

  public ForgivingTitForTat(float forgivingChance) {
    this.forgivingChance = forgivingChance;
    this.random = new Random();
  }

  @Override
  public boolean shouldBetray(int round, boolean[] myDecisions, boolean[] opponentDecisions) {
    boolean betray = super.shouldBetray(round, myDecisions, opponentDecisions);
    boolean forgive = random.nextFloat() < forgivingChance;
    return betray & !forgive;
  }

  @Override
  public String getName() {
    return super.getName() + " (" + (forgivingChance * 100) + "%)";
  }
}
