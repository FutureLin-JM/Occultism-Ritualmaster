package com.futurelin.occultism_ritualmaster.datagen.lang;

import com.futurelin.occultism_ritualmaster.OccultismRitualmaster;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredItem;

public abstract class OrmLangProvider extends LanguageProvider {

    public OrmLangProvider(PackOutput output, String locale) {
        super(output, OccultismRitualmaster.MOD_ID, locale);
    }

    public void autoDummyFactory(DeferredItem<Item> dummy, String name, String description) {
        this.add(dummy.get(), "Ritual: " + name);
        this.addTooltip(dummy.get(), description);
        this.addRitualMessage(dummy, "conditions", "Not all requirements for this ritual are met.");
        this.addRitualMessage(dummy, "started", "Starting the ritual: " + name +".");
        this.addRitualMessage(dummy, "finished", "Ritual completed successfully: " + name +".");
        this.addRitualMessage(dummy, "interrupted", "Interruption in the ritual: " + name +".");
    }

    public void addTooltip(ItemLike key, String value) {
        this.add(key.asItem().getDescriptionId() + ".tooltip", value);
    }

    public void addRitualMessage(DeferredItem<Item> ritualDummy, String key, String message) {
        var ritualName = ritualDummy.getId().getPath().replace("ritual_dummy/", "");
        this.add("ritual.%s.%s".formatted(ritualDummy.getId().getNamespace(), ritualName) + "." + key, message);
    }
}
