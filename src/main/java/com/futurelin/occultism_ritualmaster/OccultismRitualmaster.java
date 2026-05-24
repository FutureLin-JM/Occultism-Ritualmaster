package com.futurelin.occultism_ritualmaster;

import com.futurelin.occultism_ritualmaster.common.entity.RitualmasterEntity;
import com.futurelin.occultism_ritualmaster.common.entity.job.RitualmasterJarBehavior;
import com.futurelin.occultism_ritualmaster.config.OrmConfig;
import com.futurelin.occultism_ritualmaster.registry.*;
import com.hollingsworth.arsnouveau.api.registry.JarBehaviorRegistry;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(OccultismRitualmaster.MOD_ID)
public class OccultismRitualmaster {
    public static final String MOD_ID = "occultism_ritualmaster";
    public static final Logger LOGGER = LogUtils.getLogger();

     public OccultismRitualmaster(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
         modEventBus.addListener(this::onEntityAttributeCreation);

         modContainer.registerConfig(ModConfig.Type.SERVER, OrmConfig.SERVER_SPEC);

         OrmDataComponentsRegistry.register(modEventBus);
         OrmEntitiesRegistry.register(modEventBus);
         OrmJobsRegistry.register(modEventBus);
         OrmItemsRegistry.register(modEventBus);
         OrmCreativeModTabs.register(modEventBus);

        modEventBus.addListener(this::onRegisterCapabilities);
    }

    private void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(Capabilities.ItemHandler.ENTITY, OrmEntitiesRegistry.RITUALMASTER.get(),
                (entity, ctx) -> entity.inventory);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (ModList.get().isLoaded("ars_ocultas")) {
            JarBehaviorRegistry.register(OrmEntitiesRegistry.RITUALMASTER.get(), new RitualmasterJarBehavior());
        }
    }

    private void onEntityAttributeCreation(final EntityAttributeCreationEvent event) {
        event.put(OrmEntitiesRegistry.RITUALMASTER.get(), RitualmasterEntity.createAttributes().build());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
