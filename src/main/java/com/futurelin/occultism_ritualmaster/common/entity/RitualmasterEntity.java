package com.futurelin.occultism_ritualmaster.common.entity;

import com.futurelin.occultism_ritualmaster.OccultismRitualmaster;
import com.futurelin.occultism_ritualmaster.api.item.RitualmasterItemStackHandler;
import com.klikli_dev.occultism.common.entity.spirit.SpiritEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RitualmasterEntity extends SpiritEntity implements GeoEntity {

    public static final String DROPPED_BY_RITUALMASTER = OccultismRitualmaster.MOD_ID + ":dropped_by_ritualmaster";
    public static final String DROPPED_RESULT = OccultismRitualmaster.MOD_ID + ":dropped_result";
    public static final int INVENTORY_SIZE = 14;

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    public RitualmasterEntity(EntityType<? extends SpiritEntity> type, Level level) {
        super(type, level, new RitualmasterItemStackHandler(INVENTORY_SIZE, DROPPED_BY_RITUALMASTER));
        ((RitualmasterItemStackHandler) this.inventory).setEntity(this);
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (!player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (!this.level().isClientSide) {
                this.dropStoredItems();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.interactAt(player, vec, hand);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SpiritEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896)
                .add(Attributes.FOLLOW_RANGE, 50.0);
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "mainController", 0, this::animPredicate));
    }

    private <T extends GeoAnimatable> PlayState animPredicate(AnimationState<T> state) {
        if (this.swinging) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("attack"));
        }
        return state.setAndContinue(
                state.isMoving() ? RawAnimation.begin().thenPlay("walk") : RawAnimation.begin().thenPlay("idle"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animatableInstanceCache;
    }

    public void dropStoredItems() {
        for (int slot = 1; slot < this.inventory.getSlots(); slot++) {
            ItemStack stack = this.inventory.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            ItemEntity droppedItem = this.spawnAtLocation(stack.copy());
            if (droppedItem != null) {
                droppedItem.addTag(DROPPED_BY_RITUALMASTER);
            }
            this.inventory.setStackInSlot(slot, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }
}
