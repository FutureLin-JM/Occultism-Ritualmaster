package com.futurelin.occultism_ritualmaster.api.mixin.accessor;

import com.klikli_dev.occultism.common.entity.spirit.SpiritEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

public interface IRitualRecipeAccessor {

    boolean matches(ItemStackHandler inv, Level level, SpiritEntity entity);
}