package com.futurelin.occultism_ritualmaster.registry;

import com.futurelin.occultism_ritualmaster.OccultismRitualmaster;
import com.futurelin.occultism_ritualmaster.common.entity.job.RitualmasterJob;
import com.klikli_dev.occultism.Occultism;
import com.klikli_dev.occultism.client.entities.SpiritJobClient;
import com.klikli_dev.occultism.common.entity.job.SpiritJobFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class OrmJobsRegistry {

    public static final ResourceKey<Registry<SpiritJobFactory>> JOBS_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Occultism.MODID, "spirit_job_factories"));
    public static final DeferredRegister<SpiritJobFactory> JOBS =
            DeferredRegister.create(JOBS_KEY, OccultismRitualmaster.MOD_ID);

    public static final DeferredHolder<SpiritJobFactory, SpiritJobFactory> RITUAL_MASTER = JOBS.register("ritualmaster",
            () -> new SpiritJobFactory(
                    RitualmasterJob::new,
                    SpiritJobClient.create()));

    public static void register(IEventBus eventBus) {
        JOBS.register(eventBus);
    }
}
