package uk.iatom.prisonersdilemma;

import uk.iatom.prisonersdilemma.competitions.AllVersusAllCompetition;
import uk.iatom.prisonersdilemma.competitions.GridCompetition;
import uk.iatom.prisonersdilemma.strategies.*;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        HashMap<AbstractStrategy, Character> strategiesMap = new HashMap<>();
        strategiesMap.put(new HyperAggressive(), 'x');
        strategiesMap.put(new HyperAggressive(), 'x');
        strategiesMap.put(new HyperAggressive(), 'x');
        strategiesMap.put(new HyperAggressive(), 'x');
        //strategiesMap.put(new HyperPassive(), '.');
        //strategiesMap.put(new RandomChoice(), '?');
        strategiesMap.put(new TitForTat(), '~');
        strategiesMap.put(new TitForTat(), '~');
        //strategiesMap.put(new TitForTwoTat(), '2');
        //strategiesMap.put(new SlowTitForTat(), 'S');
        //strategiesMap.put(new ForgivingTitForTat(0.2f), ',');
        //strategiesMap.put(new ForgivingTitForTat(0.1f), 'f');

//        AbstractStrategy[] strategiesArray = strategiesMap.keySet().toArray(new AbstractStrategy[0]);
//        AllVersusAllCompetition allVersusAllCompetition = new AllVersusAllCompetition(strategiesArray);
//        allVersusAllCompetition.runDuelSets(1000);

        GridCompetition gridCompetition = new GridCompetition(15, 100, 0.05f, strategiesMap);
        gridCompetition.runNewGridLifecycle(200, 1);
    }
}