
package com.cardshifter.core.cardloader;

import java.util.Collection;
import java.util.function.Supplier;

import com.cardshifter.modapi.attributes.ECSAttribute;
import com.cardshifter.modapi.base.Entity;
import com.cardshifter.modapi.resources.ECSResource;

/**
 * An interface to be implemented by cardloaders.
 *
 * @author Frank van Heeswijk
 * @param <I>	The input file type of the card loader on each individual load action
 */
public interface CardLoader<I> {
	/**
	 * Loads cards from the given input resource.
	 * 
	 * This is done by mapping the input resources to the given resources.
	 * 
	 * @param input	The input resource
	 * @param entitySupplier	A supplier function that supplies a new entity
	 * @param resources	The resources to be transformed to, may b enull to indicate no resources present
	 * @param attributes	The attributes to be transformed to, may be null to indicate no attributes present
	 * @return	A collection of Entity instances
	 * @throws CardLoadingException		If an error occured while loading the cards or a resource could not be mapped
	 */
	Collection<Entity> loadCards(I input, Supplier<Entity> entitySupplier, ECSResource[] resources, ECSAttribute[] attributes) throws CardLoadingException;
}
