package uk.iatom.prisonersdilemma.duels;

import java.util.Arrays;

public record DuelResult(Duel duel, boolean[][] decisions, int[][] scores) {

  public int[] totals() {
    return new int[]{
        Arrays.stream(scores()[0]).sum(),
        Arrays.stream(scores()[1]).sum()
    };
  }

  public int winner() {
    int[] ts = totals();

    if (ts[0] == ts[1]) {
      return -1;
    }
    if (ts[0] > ts[1]) {
      return 0;
    }
    return 1;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i : new int[]{0, 1}) {
      builder.append(toStringLine(i));
    }
    return builder.toString();
  }

  private String toStringLine(int i) {
    // Pad right to 5 chars
    String scoreFormat = "%-5s";
    // Win (*/ ), score, decisions
    String lineFormat = "%s %s %s" + System.lineSeparator();

    String win = winner() == i || winner() == -1 ? "*" : " ";
    String score = String.format(scoreFormat, totals()[i]);

    String[] decisionStrs = new String[decisions()[i].length];
    for (int j = 0; j < decisionStrs.length; j++) {
      decisionStrs[j] = decisions()[i][j] ? "x" : ".";
    }
    String decisions = String.join("", decisionStrs);

    return String.format(lineFormat, win, score, decisions);
  }
}
