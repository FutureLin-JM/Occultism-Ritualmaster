package com.futurelin.occultism_ritualmaster.common.entity;

import com.futurelin.occultism_ritualmaster.OccultismRitualmaster;
import com.futurelin.occultism_ritualmaster.api.item.RitualmasterItemStackHandler;
import com.futurelin.occultism_ritualmaster.common.item.SealedPentacle;
import com.futurelin.occultism_ritualmaster.config.OrmConfig;
import com.futurelin.occultism_ritualmaster.registry.OrmDataComponentsRegistry;
import com.klikli_dev.occultism.common.entity.spirit.SpiritEntity;
import com.klikli_dev.occultism.common.item.storage.SatchelItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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

import java.util.ArrayList;
import java.util.List;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RitualmasterEntity extends SpiritEntity implements GeoEntity {

    public static final String DROPPED_BY_RITUALMASTER = OccultismRitualmaster.MOD_ID + ":dropped_by_ritualmaster";
    public static final String DROPPED_RESULT = OccultismRitualmaster.MOD_ID + ":dropped_result";
    private static final EntityDataAccessor<Boolean> DATA_PROCESSING = SynchedEntityData.defineId(RitualmasterEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> DATA_RECIPE_NAME = SynchedEntityData.defineId(RitualmasterEntity.class, EntityDataSerializers.STRING);

    public static int getInventorySize() {
        return OrmConfig.SERVER.ritualmasterInventorySize.get() + 2;
    }

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    public RitualmasterEntity(EntityType<? extends SpiritEntity> type, Level level) {
        super(type, level, new RitualmasterItemStackHandler(getInventorySize(), DROPPED_BY_RITUALMASTER));
        ((RitualmasterItemStackHandler) this.inventory).setEntity(this);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PROCESSING, false);
        builder.define(DATA_RECIPE_NAME, "");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("ProcessingRecipe", this.isProcessingRecipe());
        tag.putString("CurrentRecipeName", this.getCurrentRecipeName());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ProcessingRecipe")) {
            this.setProcessingRecipe(tag.getBoolean("ProcessingRecipe"));
        }
        if (tag.contains("CurrentRecipeName")) {
            this.setCurrentRecipeName(tag.getString("CurrentRecipeName"));
        }
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (!player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            super.interactAt(player, vec, hand);
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

    public List<ResourceLocation> getPentacles() {
        List<ResourceLocation> result = new ArrayList<>();
        ItemStack satchelStack = this.inventory.getStackInSlot(0);
        if (!hasSatchel()) {
            return result;
        }
        var contents = satchelStack.get(DataComponents.CONTAINER);
        if (contents == null) {
            return result;
        }
        for (ItemStack stack : contents.stream().toList()) {
            if (stack.getItem() instanceof SealedPentacle) {
                List<ResourceLocation> pentacleIds = stack.get(OrmDataComponentsRegistry.SEALED_PENTACLE.get());
                if (pentacleIds != null) {
                    result.addAll(pentacleIds);
                }
            }
        }
        return result;
    }

    public boolean hasSatchel() {
        return this.inventory.getStackInSlot(0).getItem() instanceof SatchelItem;
    }

    public boolean isProcessingRecipe() {
        return this.entityData.get(DATA_PROCESSING);
    }

    public void setProcessingRecipe(boolean processing) {
        this.entityData.set(DATA_PROCESSING, processing);
    }

    public String getCurrentRecipeName() {
        return this.entityData.get(DATA_RECIPE_NAME);
    }

    public void setCurrentRecipeName(String name) {
        this.entityData.set(DATA_RECIPE_NAME, name);
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
