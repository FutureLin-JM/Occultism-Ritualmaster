package com.futurelin.occultism_ritualmaster.client.render.entity;

import com.futurelin.occultism_ritualmaster.client.model.entity.RitualmasterModel;
import com.futurelin.occultism_ritualmaster.common.entity.RitualmasterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RitualmasterRenderer extends GeoEntityRenderer<RitualmasterEntity> {

    public RitualmasterRenderer(EntityRendererProvider.Context context) {
        super(context, new RitualmasterModel());
        this.withScale(1.5f);
    }
}
