package com.futurelin.occultism_ritualmaster.registry;

import com.futurelin.occultism_ritualmaster.OccultismRitualmaster;
import com.futurelin.occultism_ritualmaster.common.item.SealedPentacle;
import com.klikli_dev.occultism.common.item.DummyTooltipItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class OrmItemsRegistry {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OccultismRitualmaster.MOD_ID);

    public static final DeferredItem<Item> RITUAL_DUMMY_SUMMON_RITUALMASTER = ITEMS.register("ritual_dummy/summon_ritualmaster", () -> new DummyTooltipItem(defaultItemProperties()));

    public static final DeferredItem<Item> SEALED_PENTACLE = ITEMS.register("sealed_pentacle", () -> new SealedPentacle(defaultItemProperties().stacksTo(1)));

    public static Item.Properties defaultItemProperties() {
        return new Item.Properties();
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
