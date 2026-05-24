package com.futurelin.occultism_ritualmaster.mixin.occultism;

import com.futurelin.occultism_ritualmaster.common.entity.RitualmasterEntity;
import com.klikli_dev.occultism.integration.waila.SpiritComponentProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

@Mixin(value = SpiritComponentProvider.class, remap = false)
public abstract class SpiritComponentProviderMixin {

    @Inject(
            method = "appendTooltip(Lsnownee/jade/api/ITooltip;Lsnownee/jade/api/EntityAccessor;Lsnownee/jade/api/config/IPluginConfig;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void skipRitualmaster(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig, CallbackInfo ci) {
        if (entityAccessor.getEntity() instanceof RitualmasterEntity) {
            ci.cancel();
        }
    }
}
