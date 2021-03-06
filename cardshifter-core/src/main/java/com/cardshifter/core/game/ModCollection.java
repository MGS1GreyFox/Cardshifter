package com.cardshifter.core.game;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.zomis.cardshifter.ecs.usage.PhrancisGame;
import net.zomis.cardshifter.ecs.usage.PhrancisGameNewAttackSystem;
import net.zomis.cardshifter.ecs.usage.PhrancisGameWithSpells;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.cardshifter.ai.AIs;
import com.cardshifter.ai.ScoringAI;
import com.cardshifter.api.CardshifterConstants;
import com.cardshifter.core.modloader.DirectoryModLoader;
import com.cardshifter.core.modloader.Mod;
import com.cardshifter.core.modloader.ModNotLoadableException;
import com.cardshifter.modapi.ai.CardshifterAI;
import com.cardshifter.modapi.base.ECSMod;

/**
 * Class where the Mods and AIs are initialized.
 * 
 * @author Simon Forsberg
 */
public class ModCollection {

	private static final Logger logger = LogManager.getLogger(ModCollection.class);
	
	/**
	 * All the AIs to initialize.
	 */
	private final Map<String, CardshifterAI> ais = new LinkedHashMap<>();
	
	/**
	 * All the mods to initialize.
	 */
	private final Map<String, ECSMod> mods = new HashMap<>();
	
	/**
	 * Initializes the AIs and Mods and puts them in the collections.
	 */
	public ModCollection() {
		ais.put("Loser", new ScoringAI(AIs.loser()));
		ais.put("Idiot", new ScoringAI(AIs.idiot()));
		ais.put("Medium", new ScoringAI(AIs.medium(), AIs::mediumDeck));
		ais.put("Fighter", new ScoringAI(AIs.fighter(), AIs::fighterDeck));
		
		mods.put(CardshifterConstants.VANILLA, new PhrancisGameNewAttackSystem());
		mods.put("Cyborg-Spells", new PhrancisGameWithSpells());
	}
	
	/**
	 * Load all the external mods inside a directory
	 * 
	 * @param directory The directory to search for more mods.
	 */
	public void loadExternal(Path directory) {
		if (!Files.isDirectory(directory)) {
			logger.warn(directory + " not found. No external mods loaded");
			return;
		}
		DirectoryModLoader loader = new DirectoryModLoader(directory);
		List<String> loadableMods = loader.getAvailableMods();
		for (String modName : loadableMods) {
			try {
				Mod mod = loader.load(modName);
				mods.put(mod.getName(), mod);
			} catch (ModNotLoadableException e) {
				logger.warn("Unable to load mod " + modName, e);
			}
		}
	}
	
	/**
	 * 
	 * @return The CardshifterAI objects.
	 */
	public Map<String, CardshifterAI> getAIs() {
		return Collections.unmodifiableMap(ais);
	}
	
	/**
	 * 
	 * @return The ECSMod objects.
	 */
	public Map<String, ECSMod> getMods() {
		return Collections.unmodifiableMap(mods);
	}

	public Path getDefaultModLocation() {
		return new File(System.getProperty("user.home"), "cardshifter-mods").toPath();
	}
	
}
