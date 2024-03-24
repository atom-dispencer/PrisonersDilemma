package uk.iatom.prisonersdilemma.strategies;

import java.util.List;
import java.util.Random;

public class ForgivingTitForTat extends TitForTat {

  private final float forgivingChance;
  private final Random random;

  public ForgivingTitForTat(float forgivingChance) {
    this.forgivingChance = forgivingChance;
    this.random = new Random();
  }

  public static ForgivingTitForTat createForgivingTitForTat(List<Float> arguments) {

    if (arguments.size() != 1) {
      System.err.printf("ForgivingTitForTat must have exactly one argument, got %d",
          arguments.size());
      System.exit(-18);
    }

    float forgivenessChance = arguments.get(0);
    if (forgivenessChance < 0 || forgivenessChance > 1) {
      System.err.printf("Forgiveness chance must be in interval [0,1] but got: %f",
          forgivenessChance);
      System.exit(-15);
    }
    return new ForgivingTitForTat(forgivenessChance);
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
