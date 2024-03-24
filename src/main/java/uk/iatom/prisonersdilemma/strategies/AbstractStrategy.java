package uk.iatom.prisonersdilemma.strategies;

public abstract class AbstractStrategy {

  public String getName() {
    return getClass().getSimpleName();
  }

  /**
   * Given the round and this and the opponent's decisions, decide whether to betray on this round.
   * The decision should be make in an information vacuum, i.e. this strategy may not probe or
   * question the other strategy, except by analysing their previous responses. An
   * {@link AbstractStrategy} must be totally stateless to allow the possibility of a single
   * strategy controlling multiple entities in some competitions.
   *
   * @return `true` if the strategy wishes to betray, or `false` if it wishes to cooperate.
   */
  public abstract boolean shouldBetray(final int round, final boolean[] myDecisions,
      final boolean[] opponentDecisions);
}
