package com.kltyton.eugeneshorsewhistle;

import com.kltyton.eugeneshorsewhistle.Config.ModConfig;
import com.kltyton.eugeneshorsewhistle.init.EugenesHorseWhistleModKeyMappingsServer;
import com.kltyton.eugeneshorsewhistle.init.EugenesHorseWhistleModwhistle;
import com.kltyton.eugeneshorsewhistle.init.EugenesHorseWhistleModSounds;
import com.kltyton.eugeneshorsewhistle.init.HorseEventRegister;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kltyton.eugeneshorsewhistle.item.ModItems;

public class EugenesHorseWhistle implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "eugenes_horse_whistle";
	@Override
	public void onInitialize() {
		LOGGER.info("Initializing EugenesHorseWhistle");
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
		EugenesHorseWhistleModwhistle.load();
		EugenesHorseWhistleModKeyMappingsServer.serverLoad();
		EugenesHorseWhistleModSounds.load();
		ModItems.registerModItems();
		HorseEventRegister.load();
	}
}