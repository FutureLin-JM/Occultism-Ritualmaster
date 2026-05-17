package com.futurelin.occultism_ritualmaster.client.model.entity;

import com.futurelin.occultism_ritualmaster.OccultismRitualmaster;
import com.futurelin.occultism_ritualmaster.common.entity.RitualmasterEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RitualmasterModel extends DefaultedEntityGeoModel<RitualmasterEntity> {

    public RitualmasterModel() {
        super(ResourceLocation.fromNamespaceAndPath(OccultismRitualmaster.MOD_ID, "ritualmaster"));
    }
}
