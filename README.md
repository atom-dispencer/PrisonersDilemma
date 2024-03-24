# The Prisoners' Dilemma
This is my own take on [Robert Axelrod's](https://fordschool.umich.edu/faculty/robert-axelrod)
simulation of the Prisoners' Dilemma, and was inspired by this video by the science 
YouTube channel Veritasium ([What Game Theory Reveals About Life, The Universe, and Everything - Veritasium](https://www.youtube.com/watch?v=mScpHTIi-kM)).

Knowing the [outcome](https://cs.stanford.edu/people/eroberts/courses/soco/projects/1998-99/game-theory/axelrod.html)
of Axelrod's tournament, I naturally focussed on the winning TitForTat strategy
and created a few other strategies to test it against. Though not as numerous as the
original experiment, my ecosystem of strategies is extensible in a 'plug and play'
fashion, so it's fairly easy to add new strategies.

## Configuration
The game is configured through the `scenario.config` file, which should be placed 
in the same directory as the running executable. An example configuration is provided
in this repository. Its format is like so:

```
Competition:<Name>(<option #1>, ...)  // The name and options of the competition to run
                                      // A mandatory blank line
<Name>(<option A>, ...)               // A list of strategies to put in the given competition
HyperPassive()                        // Competitions may require these to be formatted differently
F:ForgivingTitForTat(0.5)             // For example the GridCompetition requires a character ID
```

### All-Versus-All
An example configuration for the `AllVersusAll` competition. Check the `AllVersusAll`
documentation for a full description.
```
Competition:AllVersusAll()     // No extra configuration required

HyperPassive()                 // No extra configuration required - just a straight list
HyperAggressive()              // Duplicates are allowed in AllVersusAll.
HyperAggressive()
ForgivingTitForTat(0.06)       // ForgivingTitForTat needs one extra option
```

### Grid
An example configuration for the `Grid` competition. Check the `Grid`
documentation for a full explanation.
```
Competition:Grid(15, 100, 0.05, 2000, 100)  // Woah! 5 options!

1:HyperPassive()                 // Each strategy needs an display character
B:HyperAggressive()
?:ForgivingTitForTat(0.06)       // The characters don't need to be unique!
?:ForgivingTitForTat(0.14)
```

## Strategies
A strategy is a simple algorithm which makes a decision - _to betray or not to betray_.

All strategies are classes extending `AbstractStrategy`, which provides a default implementation
of `getName()`, which returns the display name of the strategy, and exposes the `shouldBetray`
abstract method.
```java
/**
 * Given the round and this and the opponent's decisions, decide whether to betray on this round.
 * The decision should be make in an information vacuum, i.e. this strategy may not probe or
 * question the other strategy, except by analysing their previous responses. An
 * {@link AbstractStrategy} must be totally stateless to allow the possibility of a single
 * strategy controlling multiple entities in some competitions.
 *
 * @return `true` if the strategy wishes to betray, or `false` if it wishes to cooperate.
 */
public abstract boolean shouldBetray(final int round, final boolean[] myDecisions, final boolean[] opponentDecisions);
```
This will be called in every round of a `Duel` to determine the outcome of said round.

## Competitions
A competition is broadly speaking a series of `Duels` between strategies, though this may take many forms.

A `Duel` takes place between exactly two strategies (though they need not be unique), and has a
fixed number of rounds. Each round, each strategy will be independently asked whether they wish to 
betray their opponent and will receive points according to the decisions made.

The standard `Duel` and `MessyDuel` allocate points like so:
- If only one betrays, the betrayer receives 5 points and the other 0
- If both betray, each receives 1 point
- If neither betray (both cooperate), both receive 3 points.

Hence, in a `Duel` of constant round count, the total points accrued will be greatest if both always
cooperate and least if both always betray, so the problem is left for what strategy a player can
have to maximise their own score.

### All-Versus-All
No extra configuration required. Each input strategy duels each other input strategy (including itself).

### Grid

The grid competition starts by randomly filling a square grid with the given strategies. Each grid
cell contains one strategy. Each grid iteration, the strategy in each grid cell duels the strategy
in each of its surrounding cells. The scores from each `Duel` is added to tallies for that cell,
referring to their strategy (i.e a `Map<AbstractStrategy, int>` for each cell). The strategy for each
given cell then becomes the strategy which scored highest in that cell. This has the effect of a sort
of 'ecological' simulation across the grid which stabilises over time.

The computation for each cell is done in parallel, but the wait time for 2000 rounds can vary from
around 2 to 12 seconds (on my hardware - times are variable and your mileage may vary)

#### Configuration
Each strategy must be assigned a display character like so:
```
C:HyperPassive()  // The display character is 'C'
```
The display character may be anything Java sees as a character, including alphanumerics and punctuation.

The competition itself is configured like so:
```
// Grid Size: 15 (15x15 square)
// Rounds per Contest: 100
// Corruption Chance: 0.05 (5%)
// Total Grid Iterations: 2000
// Print Grid Every X Rounds: 100 (every 100 rounds, starting round 0)
Competition:Grid(15, 100, 0.05, 2000, 100)
```
If this were a Java constructor, it would look like this:
```java
public GridCompetition(int size, int roundsPerContest, float corruptionChance, int gridIterations, int printGridEveryXRounds) {}
```

## Extending the Game...
Should you wish to add a new strategy, you must:
- Create a class extending `AbstractStrategy`
- Add a generator function for your strategy to the `strategySuppliers` `Map<String, Function<..>>`
at the top of `Main`. The key is the name which will be used in `scenario.config`.

Should you wish to create a new competition-type, you'll need to do a bit more work:
- There is no `Competition` class or interface.
- Add your own runner function to the `competitionSuppliers` `Map<BiConsumer<List<Float>, List<String>>>`
also at the top of `Main`. The key is name which will be used in `scenario.config`.
The function should parse the given arguments and strategies and then run the
competition. There are functions to help with this, see the existing generator
functions such as `GridCompetition::startGrid`.