package com.futurelin.occultism_ritualmaster.datagen;

import com.futurelin.occultism_ritualmaster.datagen.lang.OrmENUSProvider;
import com.futurelin.occultism_ritualmaster.datagen.lang.OrmZHCNProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class OrmDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new OrmENUSProvider(packOutput));
        generator.addProvider(event.includeClient(), new OrmZHCNProvider(packOutput));
        generator.addProvider(event.includeClient(), new OrmItemModelProvider(packOutput, existingFileHelper));
    }
}
