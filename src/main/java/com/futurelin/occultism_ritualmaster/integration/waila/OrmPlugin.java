package com.futurelin.occultism_ritualmaster.integration.waila;

import com.futurelin.occultism_ritualmaster.common.entity.RitualmasterEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class OrmPlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerItemStorage(RitualmasterItemStorageProvider.INSTANCE, RitualmasterEntity.class);
        registration.registerEntityDataProvider(RitualmasterProvider.INSTANCE, RitualmasterEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(RitualmasterProvider.INSTANCE, RitualmasterEntity.class);
        registration.registerItemStorageClient(RitualmasterItemStorageProvider.INSTANCE);
    }
}