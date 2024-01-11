package uk.iatom.prisonersdilemma.competitions;

import uk.iatom.prisonersdilemma.duels.Duel;
import uk.iatom.prisonersdilemma.duels.DuelResult;
import uk.iatom.prisonersdilemma.duels.MessyDuel;
import uk.iatom.prisonersdilemma.strategies.AbstractStrategy;

import java.util.*;
import java.util.stream.IntStream;

public class GridCompetition {

    private final int size;
    private final int roundsPerDuel;
    private final Map<AbstractStrategy, Character> strategies;
    private final float corruptionChance;
    private final Random random;

    public GridCompetition(int size, int roundsPerContest, float corruptionChance, Map<AbstractStrategy, Character> strategies) {
        this.size = size;
        this.roundsPerDuel = roundsPerContest;
        this.corruptionChance = corruptionChance;
        this.strategies = strategies;
        this.random = new Random();
    }

    public AbstractStrategy[][] getRandomGrid() {
        AbstractStrategy[][] grid = new AbstractStrategy[size][size];

        // Thanks, ChatGPT!
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                // Generate a random index to get an element from the source array
                int randomIndex = random.nextInt(strategies.keySet().size());

                // Fill the 2D array with the random element
                grid[i][j] = strategies.keySet().stream().toList().get(randomIndex);
            }
        }

        return grid;
    }

    public void runNewGridLifecycle(int rounds, int printGridEveryXRounds) {
        AbstractStrategy[][] grid = getRandomGrid();

        printGrid(0, grid);
        for (int r = 0; r < rounds; r++) {
            grid = getNextRoundsGrid(grid);

            if (
                    r % printGridEveryXRounds == 0
                            && r != 0
            ) printGrid(r, grid);
        }
    }

    public AbstractStrategy getStratAtPosOrNull(AbstractStrategy[][] grid, int x, int y) {
        if (x < 0 || y < 0) return null;
        if (x >= grid[0].length || y >= grid.length) return null;
        return grid[x][y];
    }

    public AbstractStrategy[][] getNextRoundsGrid(AbstractStrategy[][] grid) {
        AbstractStrategy[][] nextRoundGrid = new AbstractStrategy[size][size];

        // Get a random update order
        int[] _updateOrderX = IntStream.rangeClosed(0, size - 1).toArray();
        int[] _updateOrderY = IntStream.rangeClosed(0, size - 1).toArray();
        List<Integer> updateOrderX = new ArrayList<>(Arrays.asList(Arrays.stream(_updateOrderX).boxed().toArray(Integer[]::new)));
        List<Integer> updateOrderY = new ArrayList<>(Arrays.asList(Arrays.stream(_updateOrderY).boxed().toArray(Integer[]::new)));
        Collections.shuffle(updateOrderX);
        Collections.shuffle(updateOrderY);

        // Parallel mapping
        IntStream.range(0, size - 1).parallel().forEach(x -> {
                    nextRoundGrid[x] = new AbstractStrategy[size];

                    IntStream.range(0, size - 1).parallel().forEach(y -> nextRoundGrid[x][y] = findWinningStrategyForSquare(grid, x, y));
                }
        );

        // Serial mapping
        /*for (int x : updateOrderX) {
            for (int y : updateOrderY) {
                nextRoundGrid[x][y] = findWinningStrategyForSquare(grid, x, y);
            }
        }*/

        return nextRoundGrid;
    }

    private AbstractStrategy findWinningStrategyForSquare(AbstractStrategy[][] grid, int x, int y) {
        AbstractStrategy incumbent = getStratAtPosOrNull(grid, x, y);

        Map<AbstractStrategy, Integer> scores = new HashMap<>();
        int successfulDuels = 0;
        for (int xx = x - 1; xx <= x + 1; xx++) {
            for (int yy = y - 1; yy <= y + 1; yy++) {
                if (xx == x && yy == y) continue;

                AbstractStrategy competitor = getStratAtPosOrNull(grid, xx, yy);
                if (competitor == null) continue;

                int[] totals = duel(incumbent, competitor).totals();
                scores.putIfAbsent(incumbent, 0);
                scores.put(incumbent, scores.get(incumbent) + totals[0]);
                scores.putIfAbsent(competitor, 0);
                scores.put(competitor, scores.get(competitor) + totals[1]);

                successfulDuels++;
            }
        }
        // Average the incumbent's score because it got extra attempts
        if (successfulDuels > 0)
            scores.put(incumbent, scores.get(incumbent) / successfulDuels);

        if (!scores.isEmpty()) {
            List<Map.Entry<AbstractStrategy, Integer>> scoresList = new ArrayList<>(scores.entrySet().stream().toList());
            scoresList.sort(Map.Entry.comparingByValue());
            return scoresList.get(scoresList.size() - 1).getKey();
        } else {
            return incumbent;
        }
    }

    public DuelResult duel(AbstractStrategy incumbent, AbstractStrategy competitor) {
        Duel duel = new MessyDuel(corruptionChance, roundsPerDuel, incumbent, competitor);
        return duel.getDuelResults();
    }

    public void printGrid(int round, AbstractStrategy[][] grid) {
        StringBuilder builder = new StringBuilder();

        String formattedGrid = formatGrid(grid);

        builder.append("Round #").append(round).append(System.lineSeparator());
        builder.append(formattedGrid).append(System.lineSeparator());

        System.out.println(builder);
    }

    public String formatGrid(AbstractStrategy[][] grid) {
        StringBuilder builder = new StringBuilder();

        // Y changes slower to go across lines.
        for (int y = 0; y < size; y++) {
            StringBuilder line = new StringBuilder();

            for (int x = 0; x < size; x++) {
                AbstractStrategy s = getStratAtPosOrNull(grid, x, y);
                line.append(strategies.getOrDefault(s, ' '));
            }

            builder.append(line).append(System.lineSeparator());
        }

        return builder.toString();
    }
}
