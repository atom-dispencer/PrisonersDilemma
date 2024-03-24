package uk.iatom.prisonersdilemma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.iatom.prisonersdilemma.competitions.AllVersusAllCompetition;
import uk.iatom.prisonersdilemma.competitions.GridCompetition;
import uk.iatom.prisonersdilemma.strategies.AbstractStrategy;
import uk.iatom.prisonersdilemma.strategies.ForgivingTitForTat;
import uk.iatom.prisonersdilemma.strategies.HyperAggressive;
import uk.iatom.prisonersdilemma.strategies.HyperPassive;
import uk.iatom.prisonersdilemma.strategies.RandomChoice;
import uk.iatom.prisonersdilemma.strategies.SlowTitForTat;
import uk.iatom.prisonersdilemma.strategies.TitForTat;
import uk.iatom.prisonersdilemma.strategies.TitForTwoTat;

public class Main {

  private static final String ARGUMENTS_REGEX = "(\\(.*?\\))";
  private static final Pattern ARGUMENTS_PATTERN = Pattern.compile(ARGUMENTS_REGEX);

  public static void main(String[] args) {

    List<String> lines = null;
    try {
      lines = Files.readAllLines(Paths.get("scenario.config"));
    } catch (IOException iox) {
      System.err.printf("Could not load scenario.config: %s%n", iox.getMessage());
      System.exit(-1);
    }

    if (lines.size() < 4) {
      System.err.printf("Config is too short: Only %s of minimum 4 lines.%n", lines.size());
      System.exit(-1);
    }

    String typeString = lines.get(0);
    String separator = lines.get(1);
    List<String> strategies = lines.subList(2, lines.size());

    if (typeString.isBlank()) {
      System.err.println("No competition type found on first line.");
      System.exit(-9);
    }

    if (!separator.isBlank()) {
      System.err.printf(
          "Improperly formatted configuration. Separator line must be blank, found: %s%n",
          separator);
      System.exit(-8);
    }

    Matcher matcher = ARGUMENTS_PATTERN.matcher(typeString.trim());
    if (!matcher.find()) {
      System.err.println("No competition arguments found. Must be at least ().");
      System.exit(-8);
    }
    String group = matcher.group(1);
    float[] competitionArguments = parseArgsList(group);

    if (typeString.trim().startsWith("Competition:AllVersusAll")) {
      startAllVersusAll(strategies);
    } else if (typeString.trim().startsWith("Competition:Grid")) {
      startGrid(competitionArguments, strategies);
    } else {
      System.err.printf("No valid competition type specified, found: %s%n", typeString);
      System.exit(-10);
    }

//    HashMap<AbstractStrategy, Character> strategiesMap = new HashMap<>();
//    strategiesMap.put(new HyperAggressive(), 'x');
//    strategiesMap.put(new HyperAggressive(), 'x');
//    strategiesMap.put(new HyperAggressive(), 'x');
//    strategiesMap.put(new HyperAggressive(), 'x');
//    strategiesMap.put(new HyperAggressive(), 'x');
////        strategiesMap.put(new HyperAggressive(), 'x');
////        strategiesMap.put(new HyperAggressive(), 'x');
////        strategiesMap.put(new HyperAggressive(), 'x');
////        strategiesMap.put(new HyperAggressive(), 'x');
////        strategiesMap.put(new HyperPassive(), '.');
////        strategiesMap.put(new RandomChoice(), '?');
//    strategiesMap.put(new TitForTat(), '~');
//    strategiesMap.put(new TitForTat(), '~');
//    strategiesMap.put(new TitForTat(), '~');
//    strategiesMap.put(new TitForTat(), '~');
//    strategiesMap.put(new TitForTat(), '~');
////        strategiesMap.put(new TitForTwoTat(), '2');
////        strategiesMap.put(new SlowTitForTat(), 'S');
////        strategiesMap.put(new ForgivingTitForTat(0.2f), ',');
////        strategiesMap.put(new ForgivingTitForTat(0.1f), 'f');
//
////        AbstractStrategy[] strategiesArray = strategiesMap.keySet().toArray(new AbstractStrategy[0]);
////        AllVersusAllCompetition allVersusAllCompetition = new AllVersusAllCompetition(strategiesArray);
////        allVersusAllCompetition.runDuelSets(100);
//
//    GridCompetition gridCompetition = new GridCompetition(15, 100, 0.05f, strategiesMap);
//    gridCompetition.runNewGridLifecycle(200, 1);
//
//    AllVersusAllCompetition ava = new AllVersusAllCompetition(new AbstractStrategy[]{});
//    ava.runDuelSet();
  }

  /**
   * Parses '(4, 5, 6, 7)' into { 4, 5, 6, 7 }
   *
   * @param args
   * @return
   * @throws NumberFormatException
   */
  private static float[] parseArgsList(String args) throws NumberFormatException {

    if (args.trim().startsWith("(") && args.trim().endsWith(")")) {
      if (args.length() <= 2) {
        return new float[0];
      }
      args = args.substring(1, args.length() - 2);
    }

    String[] split = args.split(",");
    float[] arguments = new float[split.length];

    for (int i = 0; i < split.length; i++) {
      arguments[i] = Float.parseFloat(split[i]);
    }

    return arguments;
  }

  private static AbstractStrategy parseStrategy(String strategyConfig) {
    Matcher matcher = ARGUMENTS_PATTERN.matcher(strategyConfig);

    String[] nameOnly = strategyConfig.split(ARGUMENTS_REGEX);
    if (nameOnly.length != 1) {
      System.err.printf("Error, unable to parse strategy name in line '%s'", strategyConfig);
      System.exit(-6);
    }
    String name = nameOnly[0];
    if (name.isBlank()) {
      System.err.printf("Strategy name may not be blank in line '%s'", strategyConfig);
      System.exit(-11);
    }

    if (!matcher.find()) {
      System.err.printf("No (..) arguments found in line '%s'.%n", strategyConfig);
      System.exit(-5);
    }

    String group = matcher.group();
    float[] arguments = parseArgsList(group);

    try {
      return switch (name) {
        case "RandomChoice" -> new RandomChoice();
        case "HyperPassive" -> new HyperPassive();
        case "HyperAggressive" -> new HyperAggressive();
        case "TitForTat" -> new TitForTat();
        case "TitForTwoTat" -> new TitForTwoTat();
        case "SlowTitForTat" -> new SlowTitForTat();
        case "ForgivingTitForTat" -> new ForgivingTitForTat(arguments[0]);
        default -> throw new IllegalStateException("No such strategy: %s".formatted(name));
      };
    } catch (IndexOutOfBoundsException outOfBoundsException) {
      System.err.printf("Insufficient number of arguments %d for %s", arguments.length, name);
      System.exit(-7);
    } catch (IllegalStateException illegalStateException) {
      System.err.println(illegalStateException.getMessage());
      System.exit(-8);
    }

    return null;
  }

  private static void startAllVersusAll(List<String> strategyStrings) {

    List<AbstractStrategy> abstractStrategies = new ArrayList<>();
    for (String strategy : strategyStrings) {
      AbstractStrategy abstractStrategy = parseStrategy(strategy);
      System.out.printf("Adding strategy: %s%n", abstractStrategy.getName());
      abstractStrategies.add(abstractStrategy);
    }

    AbstractStrategy[] array = new AbstractStrategy[abstractStrategies.size()];
    new AllVersusAllCompetition(abstractStrategies.toArray(array)).runDuelSet();
  }

  private static void startGrid(float[] gridArguments, List<String> strategyConfigs) {
    int size = (int) gridArguments[0];
    int roundsPerContest = (int) gridArguments[1];
    float corruptionChance = gridArguments[2];

    System.out.printf("Parsed GridCompetition(size=%d, roundsPerContest=%d, corruptionChance=%f)%n",
        size, roundsPerContest, corruptionChance);

    Map<AbstractStrategy, Character> strategyCharacterMap = new HashMap<>();
    for (String config : strategyConfigs) {

      String[] split = config.split(":");
      if (split.length != 2) {
        System.err.printf(
            "There must be exactly 2 configuration sections (':' delimited) per strategy-line. Found %d on line '%s'.%n",
            split.length, config);
        System.exit(-3);
      }

      if (split[0].length() != 1) {
        System.err.printf(
            "The first character section of a GridCompetition strategy must be a single identification character. '%s' in line '%s' does not comply",
            split[0], config);
        System.exit(-4);
      }
      char key = split[0].charAt(0);

      AbstractStrategy abstractStrategy = parseStrategy(split[1]);
      strategyCharacterMap.put(abstractStrategy, key);
    }

    StringBuilder builder = new StringBuilder();
    builder.append("Parsed Strategies:\n");
    strategyCharacterMap.forEach(
        (as, ch) -> builder.append(" - ").append(ch).append(":").append(as.getName()).append("\n"));
    System.out.println(builder);

    new GridCompetition(size, roundsPerContest, corruptionChance, strategyCharacterMap);
  }
}