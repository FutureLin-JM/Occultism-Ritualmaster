package com.futurelin.occultism_ritualmaster.common.entity.job;

import com.futurelin.occultism_ritualmaster.api.item.RitualmasterItemStackHandler;
import com.futurelin.occultism_ritualmaster.api.mixin.accessor.IRitualRecipeAccessor;
import com.futurelin.occultism_ritualmaster.common.item.SealedPentacle;
import com.futurelin.occultism_ritualmaster.config.OrmConfig;
import com.futurelin.occultism_ritualmaster.registry.OrmDataComponentsRegistry;
import com.klikli_dev.occultism.common.entity.ai.goal.PickupItemsGoal;
import com.klikli_dev.occultism.common.entity.job.SpiritJob;
import com.klikli_dev.occultism.common.entity.spirit.SpiritEntity;
import com.klikli_dev.occultism.common.item.storage.SatchelItem;
import com.klikli_dev.occultism.crafting.recipe.RitualRecipe;
import com.klikli_dev.occultism.registry.OccultismItems;
import com.klikli_dev.occultism.registry.OccultismRecipes;
import com.klikli_dev.occultism.registry.OccultismSounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.futurelin.occultism_ritualmaster.common.entity.RitualmasterEntity.DROPPED_BY_RITUALMASTER;
import static com.futurelin.occultism_ritualmaster.common.entity.RitualmasterEntity.DROPPED_RESULT;

public class RitualmasterJob extends SpiritJob {

    protected PickupItemsGoal pickupItemsGoal;
    protected List<Ingredient> itemsToPickUp = new ArrayList<>();
    protected Optional<RitualRecipe> currentRecipe = Optional.empty();
    protected Optional<ResourceLocation> currentRecipeId = Optional.empty();
    protected RitualState ritualState = RitualState.IDLE;
    protected int ritualProgress = 0;
    protected int ritualDuration = 0;
    protected boolean needsItemUse = false;
    protected boolean needsSacrifice = false;
    protected boolean itemUseFulfilled = false;
    protected boolean sacrificeFulfilled = false;

    protected double scaleFactor = OrmConfig.SERVER.ritualDurationScaleFactor.get();
    protected int ritualMinWorkDuration = OrmConfig.SERVER.ritualMinWorkDuration.get();

    public Consumer<PlayerInteractEvent.RightClickItem> rightClickItemListener;
    public Consumer<LivingDeathEvent> livingDeathEventListener;

    public RitualmasterJob(SpiritEntity entity) {
        super(entity);
    }

    @Override
    protected void onInit() {
        this.entity.targetSelector.addGoal(1, this.pickupItemsGoal = new PickupItemsGoal(this.entity));
        this.itemsToPickUp = this.entity.level().getRecipeManager().getAllRecipesFor(OccultismRecipes.RITUAL_TYPE.get())
                .stream()
                .flatMap(holder -> {
                    RitualRecipe recipe = holder.value();
                    return Stream.concat(recipe.getIngredients().stream(),
                            Stream.of(recipe.getActivationItem()));
                })
                .collect(Collectors.toCollection(ArrayList::new));
        this.itemsToPickUp.add(Ingredient.of(OccultismItems.SATCHEL.get()));
        ((RitualmasterItemStackHandler) this.entity.inventory).setOnInventoryChanged(this::findRecipe);
        this.findRecipe();
    }

    @Override
    public void cleanup() {
        this.entity.targetSelector.removeGoal(this.pickupItemsGoal);
        if (this.entity.inventory instanceof RitualmasterItemStackHandler handler) {
            handler.setOnInventoryChanged(null);
        }
        if (this.rightClickItemListener != null) {
            NeoForge.EVENT_BUS.unregister(this.rightClickItemListener);
        }
        if (this.livingDeathEventListener != null) {
            NeoForge.EVENT_BUS.unregister(this.livingDeathEventListener);
        }
    }

    private boolean hasSatchel() {
        return this.entity.inventory.getStackInSlot(0).getItem() instanceof SatchelItem;
    }

    private List<ResourceLocation> getPentacles() {
        List<ResourceLocation> result = new ArrayList<>();
        ItemStack satchelStack = this.entity.inventory.getStackInSlot(0);
        if (!(satchelStack.getItem() instanceof SatchelItem)) {
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

    @Override
    public void update() {
        if (!hasSatchel()) {
            return;
        }
        switch (this.ritualState) {
            case IDLE -> {
                if (this.currentRecipe.isPresent() && checkPentacle(this.currentRecipe.get())) {
                    startRitual();
                }
            }
            case IN_PROGRESS -> {
                if (this.needsItemUse && !this.itemUseFulfilled) {
                    return;
                }
                if (this.needsSacrifice && !this.sacrificeFulfilled) {
                    return;
                }
                this.ritualProgress++;
                if (this.ritualProgress >= this.ritualDuration) {
                    finishRitual();
                }
            }
        }
    }

    private void startRitual() {
        RitualRecipe recipe = this.currentRecipe.get();
        this.entity.level().playSound(null, this.entity.blockPosition(), OccultismSounds.START_RITUAL.get(), SoundSource.BLOCKS, 1, 1);

        this.rightClickItemListener = this::onPlayerRightClickItem;
        this.livingDeathEventListener = this::onLivingDeath;
        NeoForge.EVENT_BUS.addListener(this.rightClickItemListener);
        NeoForge.EVENT_BUS.addListener(this.livingDeathEventListener);


        this.ritualDuration = Math.max((ritualMinWorkDuration), (int) (recipe.getDuration() * scaleFactor));
        this.ritualProgress = 0;

        this.needsItemUse = recipe.requiresItemUse();
        this.needsSacrifice = recipe.requiresSacrifice();
        this.itemUseFulfilled = !this.needsItemUse;
        this.sacrificeFulfilled = !this.needsSacrifice;

        this.ritualState = RitualState.IN_PROGRESS;
    }

    private void finishRitual() {
        if (this.currentRecipe.isEmpty()) {
            unregisterListeners();
            this.currentRecipe = Optional.empty();
            this.currentRecipeId = Optional.empty();
            this.ritualState = RitualState.IDLE;
            this.ritualProgress = 0;
            this.ritualDuration = 0;
            return;
        }

        RitualRecipe recipe = this.currentRecipe.get();

        ItemStack activationItem = findActivationItemStack(recipe);
        if (!activationItem.isEmpty()) {
            recipe.getRitual().finish(this.entity.level(), this.entity.blockPosition(), null, null, activationItem);
            consumeIngredients(recipe);
        }

        unregisterListeners();

        this.currentRecipe = Optional.empty();
        this.currentRecipeId = Optional.empty();
        this.ritualState = RitualState.IDLE;
    }

    private void unregisterListeners() {
        if (this.rightClickItemListener != null) {
            NeoForge.EVENT_BUS.unregister(this.rightClickItemListener);
            this.rightClickItemListener = null;
        }
        if (this.livingDeathEventListener != null) {
            NeoForge.EVENT_BUS.unregister(this.livingDeathEventListener);
            this.livingDeathEventListener = null;
        }
    }

    private ItemStack findActivationItemStack(RitualRecipe recipe) {
        for (int slot = 0; slot < this.entity.inventory.getSlots(); slot++) {
            ItemStack stack = this.entity.inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && recipe.getActivationItem().test(stack)) {
                return this.entity.inventory.extractItem(slot, 1, false);
            }
        }
        return ItemStack.EMPTY;
    }

    private void consumeIngredients(RitualRecipe recipe) {
        for (Ingredient ingredient : recipe.getIngredients()) {
            for (int slot = 1; slot < this.entity.inventory.getSlots(); slot++) {
                ItemStack stack = this.entity.inventory.getStackInSlot(slot);
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    this.entity.inventory.extractItem(slot, 1, false);
                    break;
                }
            }
        }
    }

    public void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (this.ritualState != RitualState.IN_PROGRESS || this.currentRecipe.isEmpty()) {
            return;
        }
        Player player = event.getEntity();
        if (!player.level().isClientSide()
                && this.currentRecipe.get().getRitual().isValidItemUse(event)) {
            this.itemUseFulfilled = true;
        }
    }

    public void onLivingDeath(LivingDeathEvent event) {
        if (this.ritualState != RitualState.IN_PROGRESS || this.currentRecipe.isEmpty()) {
            return;
        }
        LivingEntity entityLivingBase = event.getEntity();
        if (!entityLivingBase.level().isClientSide()
                && event.getSource().getEntity() instanceof Player
                && this.currentRecipe.get().getRitual().isValidSacrifice(entityLivingBase)) {
            this.sacrificeFulfilled = true;
        }
    }

    public void findRecipe() {
        if (!hasSatchel() || this.ritualState == RitualState.IN_PROGRESS) {
            return;
        }
        this.currentRecipe = Optional.empty();
        this.currentRecipeId = Optional.empty();
        for (RecipeHolder<RitualRecipe> holder : this.entity.level().getRecipeManager().getAllRecipesFor(OccultismRecipes.RITUAL_TYPE.get())) {
            RitualRecipe recipe = holder.value();
            if (((IRitualRecipeAccessor) recipe).matches(this.entity.inventory, this.entity.level(), this.entity)
                    && checkPentacle(recipe)) {
                this.currentRecipe = Optional.of(recipe);
                this.currentRecipeId = Optional.of(holder.id());
                break;
            }
        }
    }

    public boolean checkPentacle(RitualRecipe recipe) {
        return getPentacles().contains(recipe.getPentacleId());
    }

    @Override
    public List<Ingredient> getItemsToPickUp() {
        return this.itemsToPickUp;
    }

    @Override
    public CompoundTag writeJobToNBT(CompoundTag compound, HolderLookup.Provider provider) {
        compound.putInt("ritualState", this.ritualState.ordinal());
        compound.putInt("ritualProgress", this.ritualProgress);
        compound.putInt("ritualDuration", this.ritualDuration);
        compound.putBoolean("needsItemUse", this.needsItemUse);
        compound.putBoolean("needsSacrifice", this.needsSacrifice);
        compound.putBoolean("itemUseFulfilled", this.itemUseFulfilled);
        compound.putBoolean("sacrificeFulfilled", this.sacrificeFulfilled);
        this.currentRecipeId.ifPresent(id -> compound.putString("currentRecipe", id.toString()));
        return super.writeJobToNBT(compound, provider);
    }

    @Override
    public void readJobFromNBT(CompoundTag compound, HolderLookup.Provider provider) {
        super.readJobFromNBT(compound, provider);
        this.ritualState = compound.getInt("ritualState") == 0 ? RitualState.IDLE : RitualState.IN_PROGRESS;
        this.ritualProgress = compound.getInt("ritualProgress");
        this.ritualDuration = compound.getInt("ritualDuration");
        this.needsItemUse = compound.getBoolean("needsItemUse");
        this.needsSacrifice = compound.getBoolean("needsSacrifice");
        this.itemUseFulfilled = compound.getBoolean("itemUseFulfilled");
        this.sacrificeFulfilled = compound.getBoolean("sacrificeFulfilled");

        if (compound.contains("currentRecipe")) {
            String idString = compound.getString("currentRecipe");
            try {
                ResourceLocation recipeId = ResourceLocation.tryParse(idString);
                if (recipeId != null) {
                    var opt = this.entity.level().getRecipeManager().byKey(recipeId);
                    if (opt.isPresent() && opt.get().value() instanceof RitualRecipe ritualRecipe) {
                        this.currentRecipe = Optional.of(ritualRecipe);
                        this.currentRecipeId = Optional.of(recipeId);
                    } else {
                        this.currentRecipe = Optional.empty();
                        this.currentRecipeId = Optional.empty();
                        this.ritualState = RitualState.IDLE;
                    }
                } else {
                    this.currentRecipe = Optional.empty();
                    this.currentRecipeId = Optional.empty();
                    this.ritualState = RitualState.IDLE;
                }
            } catch (Exception e) {
                this.currentRecipe = Optional.empty();
                this.currentRecipeId = Optional.empty();
                this.ritualState = RitualState.IDLE;
            }
        }
    }

    @Override
    public boolean canPickupItem(ItemEntity entity) {
        if (entity.getTags().contains(DROPPED_BY_RITUALMASTER) || entity.getTags().contains(DROPPED_RESULT)) {
            return false;
        }

        ItemStack stack = entity.getItem();
        if (stack.isEmpty()) {
            return false;
        }
        if (!hasSatchel()) {
            return stack.getItem() instanceof SatchelItem;
        }
        return this.itemsToPickUp.stream().anyMatch(i -> i.test(stack));
    }

    protected enum RitualState {
        IDLE,
        IN_PROGRESS
    }
}