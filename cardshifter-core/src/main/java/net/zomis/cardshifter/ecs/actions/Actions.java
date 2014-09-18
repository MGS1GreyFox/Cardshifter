package net.zomis.cardshifter.ecs.actions;

import java.util.List;
import java.util.stream.Collectors;

import net.zomis.cardshifter.ecs.base.ComponentRetriever;
import net.zomis.cardshifter.ecs.base.ECSGame;
import net.zomis.cardshifter.ecs.base.Entity;

public class Actions {

	private static final ComponentRetriever<ActionComponent> actions = ComponentRetriever.retreiverFor(ActionComponent.class);
	
	public static List<ECSAction> getPossibleActionsOn(Entity entity, Entity performer) {
		return actions.required(entity).getECSActions().stream().filter(action -> action.isAllowed(performer)).collect(Collectors.toList());
	}
	
	public static List<ECSAction> getPossibleActionsFor(Entity performer) {
		return getAllActions(performer.getGame()).stream().filter(action -> action.isAllowed(performer)).collect(Collectors.toList());
	}
	
	public static List<ECSAction> getAllActions(ECSGame game) {
		return game.getEntitiesWithComponent(ActionComponent.class)
			.stream()
			.flatMap(entity -> entity.getComponent(ActionComponent.class)
					.getECSActions().stream())
			.collect(Collectors.toList());
	}
	
}
