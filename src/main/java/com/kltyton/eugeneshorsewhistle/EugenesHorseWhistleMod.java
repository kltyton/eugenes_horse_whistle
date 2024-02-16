package com.kltyton.eugeneshorsewhistle;

import com.kltyton.eugeneshorsewhistle.config.ModConfig;
import com.kltyton.eugeneshorsewhistle.init.EugenesHorseWhistleModKeyMappingsServer;
import com.kltyton.eugeneshorsewhistle.init.EugenesHorseWhistleModProcedures;
import com.kltyton.eugeneshorsewhistle.init.EugenesHorseWhistleModSounds;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EugenesHorseWhistleMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "eugenes_horse_whistle";

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing EugenesHorseWhistleMod");
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);

		EugenesHorseWhistleModProcedures.load();

		EugenesHorseWhistleModKeyMappingsServer.serverLoad();

		EugenesHorseWhistleModSounds.load();

	}
}