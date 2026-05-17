package com.futurelin.occultism_ritualmaster.datagen;

import com.futurelin.occultism_ritualmaster.OccultismRitualmaster;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class OrmItemModelProvider extends ItemModelProvider {

    public OrmItemModelProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, OccultismRitualmaster.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.getBuilder("item/ritual_dummy/summon_ritualmaster")
                .parent(new ModelFile.UncheckedModelFile("occultism:item/pentacle_summon"));

        this.getBuilder("item/sealed_pentacle")
                .parent(new ModelFile.UncheckedModelFile("occultism:item/pentacle_craft"));
    }
}
