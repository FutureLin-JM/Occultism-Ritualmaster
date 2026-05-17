package com.futurelin.occultism_ritualmaster.registry;

import com.futurelin.occultism_ritualmaster.OccultismRitualmaster;
import com.futurelin.occultism_ritualmaster.TranslationKeys;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class OrmCreativeModTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OccultismRitualmaster.MOD_ID);

    public static final Supplier<CreativeModeTab> OCCULTISM_RITUALMASTER_TAB =
            CREATIVE_MODE_TABS.register("occultism_ritualmaster_tab", () ->
            CreativeModeTab.builder()
                    .icon(() -> OrmItemsRegistry.SEALED_PENTACLE.get().getDefaultInstance())
                    .title(Component.translatable(TranslationKeys.ITEM_GROUP))
                    .displayItems((parameters, output) ->
                            OrmItemsRegistry.ITEMS.getEntries().forEach(item -> output.accept(item.get())))
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
