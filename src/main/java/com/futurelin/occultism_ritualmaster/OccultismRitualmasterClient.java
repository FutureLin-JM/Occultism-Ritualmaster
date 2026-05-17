package com.futurelin.occultism_ritualmaster;

import com.futurelin.occultism_ritualmaster.client.render.entity.RitualmasterRenderer;
import com.futurelin.occultism_ritualmaster.registry.OrmEntitiesRegistry;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = OccultismRitualmaster.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = OccultismRitualmaster.MOD_ID, value = Dist.CLIENT)
public class OccultismRitualmasterClient {
    public OccultismRitualmasterClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        OccultismRitualmaster.LOGGER.info("HELLO FROM CLIENT SETUP");
        OccultismRitualmaster.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(OrmEntitiesRegistry.RITUALMASTER.get(), RitualmasterRenderer::new);
    }
}
