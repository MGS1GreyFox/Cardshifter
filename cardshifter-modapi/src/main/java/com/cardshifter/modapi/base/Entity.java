package com.cardshifter.modapi.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.cardshifter.modapi.events.EntityRemoveEvent;

public final class Entity {

	private Map<Class<? extends Component>, Component> components = new HashMap<>();
	
	private final int id;
	private final ECSGame game;

	private boolean removed;
	
	public Entity(ECSGame game, int id) {
		this.game = game;
		this.id = id;
	}

	public Entity addComponent(Component component) {
		components.put(component.getClass(), component);
		component.setEntity(this);
		return this;
	}
	
	public boolean hasComponent(Class<? extends Component> clazz) {
		return components.containsKey(clazz);
	}
	
	public <T extends Component> T getComponent(Class<T> clazz) {
		return clazz.cast(components.get(clazz));
	}
	
	public <T extends Component> T get(ComponentRetriever<T> retriever) {
		return retriever.get(this);
	}
	
	public int getId() {
		return id;
	}
	
	public ECSGame getGame() {
		return game;
	}

	public void addComponents(Component... components) {
		for (Component component : components) {
			this.addComponent(component);
		}
	}

	/**
	 * Get all components that are subtypes of a specific class
	 * 
	 * @param compoentClass
	 * @return
	 */
	public <T extends Component> Collection<T> getSuperComponents(Class<T> compoentClass) {
		return this.components.entrySet().stream()
				.filter(entry -> compoentClass.isAssignableFrom(entry.getKey()))
				.map(entry -> compoentClass.cast(entry.getValue()))
				.collect(Collectors.toList());
	}
	
	@Override
	public String toString() {
		return "Entity #" + id;
	}

	public void destroy() {
		getGame().executeEvent(new EntityRemoveEvent(this), () -> {
			components.clear();
			game.removeEntity(this);
			removed = true;
		});
	}

	/**
	 * Checks if this entity is removed
	 * 
	 * @return True if entity has been removed from the game, false if it still exists
	 */
	public boolean isRemoved() {
		return removed;
	}

	public void removeComponent(Class<? extends Component> component) {
		this.components.remove(component);
	}
	
	/**
	 * Copy this entity and all of it's copyable components
	 * 
	 * @see CopyableComponent
	 * 
	 * @return A copy of this entity
	 */
	public Entity copy() {
		if (isRemoved()) {
			throw new IllegalStateException("Unable to copy a removed entity");
		}
		Entity copy = game.newEntity();
		
		for (Component comp : components.values()) {
			if (comp instanceof CopyableComponent) {
				CopyableComponent copyable = (CopyableComponent) comp;
				copy.addComponent(copyable.copy(copy));
			}
		}
		
		return copy;
	}

	/**
	 * Apply an effect to this entity
	 * @param effect Effect to apply
	 * @return This entity
	 */
	public Entity apply(Consumer<Entity> effect) {
		effect.accept(this);
		return this;
	}
}
