package com.cardshifter.ai;

import net.zomis.aiscores.FScorer;
import net.zomis.aiscores.ScoreConfigFactory;
import net.zomis.aiscores.scorers.PredicateScorer;
import net.zomis.aiscores.scorers.Scorers;
import net.zomis.aiscores.scorers.SimpleScorer;
import net.zomis.cardshifter.ecs.usage.PhrancisGame;

import com.cardshifter.ai.phrancis.AttackAnalyze;
import com.cardshifter.modapi.actions.ECSAction;
import com.cardshifter.modapi.base.Entity;

public class AIs {
	
	private static FScorer<Entity, ECSAction> playActionScorer = new PredicateScorer<>(action -> action.getName().equals(PhrancisGame.PLAY_ACTION));

	public static ScoreConfigFactory<Entity, ECSAction> loser() {
		ScoreConfigFactory<Entity, ECSAction> config = new ScoreConfigFactory<>();
		config.withScorer(new PredicateScorer<>(action -> action.getName().equals(PhrancisGame.END_TURN_ACTION)));
		return config;
	}

	public static ScoreConfigFactory<Entity, ECSAction> idiot() {
		return new ScoreConfigFactory<>();
	}

	public static ScoreConfigFactory<Entity, ECSAction> medium() {
		ScoreConfigFactory<Entity, ECSAction> config = new ScoreConfigFactory<>();
		config.withScorer(new PredicateScorer<>(action -> action.getName().equals(PhrancisGame.USE_ACTION)), -10);
		config.withScorer(new PredicateScorer<>(action -> action.getName().equals(PhrancisGame.PLAY_ACTION)), 10);
		config.withScorer(new PredicateScorer<>(action -> action.getName().equals(PhrancisGame.ENCHANT_ACTION)), -10); // this AI does not enchant
		config.withScorer(new SimpleScorer<>(AttackAnalyze::scrapScore));
		config.withScorer(new SimpleScorer<>(AttackAnalyze::attackScore));
		return config;
	}

	public static ScoreConfigFactory<Entity, ECSAction> fighter() {
		ScoreConfigFactory<Entity, ECSAction> config = new ScoreConfigFactory<>();
		config.withScorer(Scorers.multiplication(playActionScorer, new SimpleScorer<>(AttackAnalyze::health)), 10);
		config.withScorer(Scorers.multiplication(playActionScorer, new SimpleScorer<>(AttackAnalyze::attack)), 2);
		config.withScorer(new PredicateScorer<>(action -> action.getName().equals(PhrancisGame.USE_ACTION)), -10);
		config.withScorer(new PredicateScorer<>(action -> action.getName().equals(PhrancisGame.SCRAP_ACTION)), -1);
		config.withScorer(Scorers.multiplication(new SimpleScorer<>(AttackAnalyze::scrapNeeded), 
				new SimpleScorer<>(AttackAnalyze::scrapIfCanGetKilled)));
//		config.withScorer(new SimpleScorer<>(AttackAnalyze::scrapScore));
		config.withScorer(new SimpleScorer<>(AttackAnalyze::attackScore));
		config.withScorer(new SimpleScorer<>(AttackAnalyze::enchantScore));
		return config;
	}

}
