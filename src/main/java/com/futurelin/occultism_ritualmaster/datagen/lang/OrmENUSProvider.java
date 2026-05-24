package com.futurelin.occultism_ritualmaster.datagen.lang;

import com.futurelin.occultism_ritualmaster.OccultismRitualmaster;
import com.futurelin.occultism_ritualmaster.TranslationKeys;
import com.futurelin.occultism_ritualmaster.registry.OrmEntitiesRegistry;
import com.futurelin.occultism_ritualmaster.registry.OrmItemsRegistry;
import net.minecraft.data.PackOutput;

public class OrmENUSProvider extends OrmLangProvider {

    public OrmENUSProvider(PackOutput output) {
        super(output, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add(TranslationKeys.ITEM_GROUP, "Occultism: Ritualmaster");
        this.add(OrmEntitiesRegistry.RITUALMASTER.get().getDescriptionId(), "Ritualmaster");
        this.add("job." + OccultismRitualmaster.MOD_ID + ".ritualmaster", "Ritualmaster");

        autoDummyFactory(OrmItemsRegistry.RITUAL_DUMMY_SUMMON_RITUALMASTER, "Summon Ritualmaster", "The Ritualmaster automates ritual execution. Provide it with a §lSurprisingly Substantial Satchel§r containing the corresponding ritual Sealed Pentacle, and it will gather nearby suitable materials to perform the ritual on its own.");

        this.add(OrmItemsRegistry.SEALED_PENTACLE.get(), "Sealed Pentacle");
        this.add(TranslationKeys.SEALED_PENTACLE_EMPTY, "Empty");
        this.add(TranslationKeys.SEALED_PENTACLE_TOOLTIP, "Right-Click a Golden Ritual Bowl to seal a formed pentacle within it.");
        this.add(TranslationKeys.SEALED_PENTACLE_SEALED, "Pentacle Sealed");

        this.add(TranslationKeys.JADE_SHIFT_TOOLTIP, "Hold down [§lShift§r] to view available pentacles");
        this.add(TranslationKeys.JADE_PENTACLE_FOUND, "Pentacles found:");
        this.add(TranslationKeys.JADE_NO_PENTACLE_FOUND, "No pentacle found");
    }
}
