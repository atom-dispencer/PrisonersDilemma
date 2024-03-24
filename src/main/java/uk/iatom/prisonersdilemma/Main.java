package uk.iatom.prisonersdilemma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
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

  private static final Map<String, BiConsumer<List<Float>, List<String>>> competitionSuppliers = new HashMap<>();
  private static final Map<String, Function<List<Float>, AbstractStrategy>> strategySuppliers = new HashMap<>();

  public static void main(String[] args) {

    competitionSuppliers.put("AllVersusAll", AllVersusAllCompetition::startAllVersusAll);
    competitionSuppliers.put("Grid", GridCompetition::startGrid);

    strategySuppliers.put("RandomChoice", (arguments) -> new RandomChoice());
    strategySuppliers.put("HyperPassive", (arguments) -> new HyperPassive());
    strategySuppliers.put("HyperAggressive", (arguments) -> new HyperAggressive());
    strategySuppliers.put("TitForTat", (arguments) -> new TitForTat());
    strategySuppliers.put("TitForTwoTat", (arguments) -> new TitForTwoTat());
    strategySuppliers.put("SlowTitForTat", (arguments) -> new SlowTitForTat());
    strategySuppliers.put("ForgivingTitForTat", ForgivingTitForTat::createForgivingTitForTat);

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

    Matcher argumentsMatcher = ARGUMENTS_PATTERN.matcher(typeString.trim());
    if (!argumentsMatcher.find()) {
      System.err.println("No competition arguments found. Must be at least ().");
      System.exit(-8);
    }
    String group = argumentsMatcher.group(1);
    List<Float> competitionArguments = parseArgsList(group);

    Pattern pattern = Pattern.compile("(?<=Competition:)(.+)(?=\\()");
    Matcher nameMatcher = pattern.matcher(typeString);

    if (!nameMatcher.find()) {
      System.err.printf("Could not parse competition name in %s.%n", typeString);
      System.exit(-16);
    }
    String competitionName = nameMatcher.group(1);

    if (!competitionSuppliers.containsKey(competitionName)) {
      System.err.printf("'%s' is not a valid competition name.%n", competitionName);
      System.exit(-17);
    }
    BiConsumer<List<Float>, List<String>> competitionSupplier = competitionSuppliers.get(
        competitionName);

    competitionSupplier.accept(competitionArguments, strategies);
  }

  private static List<Float> parseArgsList(String args) throws NumberFormatException {

    if (args.trim().startsWith("(") && args.trim().endsWith(")")) {
      if (args.length() <= 2) {
        return new ArrayList<>();
      }
      args = args.substring(1, args.length() - 1);
    }

    String[] split = args.split(",");
    List<Float> arguments = new ArrayList<>();

    for (String s : split) {
      String maybeFloat = s.trim();
      try {
        arguments.add(Float.parseFloat(maybeFloat));
      } catch (NumberFormatException numberFormatException) {
        System.err.printf("Unable to parse String '%s' to Float.%n", maybeFloat);
        System.exit(-14);
      }
    }

    return arguments;
  }

  public static AbstractStrategy parseStrategy(String strategyConfig) {
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
    List<Float> arguments = parseArgsList(group);

    try {

      if (!strategySuppliers.containsKey(name)) {
        throw new IllegalStateException("No such strategy: %s".formatted(name));
      }

      Function<List<Float>, AbstractStrategy> strategy = strategySuppliers.get(name);

      List<Float> floats = new ArrayList<>(arguments);
      return strategy.apply(floats);

    } catch (IndexOutOfBoundsException outOfBoundsException) {
      System.err.printf("Insufficient number of arguments %d for %s", arguments.size(), name);
      System.exit(-7);
    } catch (IllegalStateException illegalStateException) {
      System.err.println(illegalStateException.getMessage());
      System.exit(-8);
    }

    return null;
  }

}