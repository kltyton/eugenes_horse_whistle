package com.kltyton.eugeneshorsewhistle;

import com.kltyton.eugeneshorsewhistle.init.EugenesHorseWhistleModProcedures;
import com.kltyton.eugeneshorsewhistle.init.EugenesHorseWhistleModSounds;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.kltyton.eugeneshorsewhistle.init.EugenesHorseWhistleModKeyMappingsServer;

import net.fabricmc.api.ModInitializer;

public class EugenesHorseWhistleMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "eugenes_horse_whistle";

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing EugenesHorseWhistleMod");

		EugenesHorseWhistleModProcedures.load();

		EugenesHorseWhistleModKeyMappingsServer.serverLoad();

		EugenesHorseWhistleModSounds.load();

	}
}