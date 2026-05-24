package com.futurelin.occultism_ritualmaster.api.item;

import com.futurelin.occultism_ritualmaster.common.entity.RitualmasterEntity;

import com.klikli_dev.occultism.common.item.storage.SatchelItem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class RitualmasterItemStackHandler extends ItemStackHandler {

    private RitualmasterEntity entity;
    private final String dropTag;
    private boolean shifting;
    private boolean inShiftForward;

    public RitualmasterItemStackHandler(int size, String dropTag) {
        super(size);
        this.dropTag = dropTag;
    }

    public void setEntity(RitualmasterEntity entity) {
        this.entity = entity;
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot == 0) {
            if (!stack.isEmpty() && !(stack.getItem() instanceof SatchelItem)) {
                return stack;
            }
            return super.insertItem(slot, stack, simulate);
        }

        ItemStack existing = this.stacks.get(slot);
        if (!existing.isEmpty() && !ItemStack.isSameItemSameComponents(existing, stack)) {
            return stack;
        }

        if (!simulate && existing.isEmpty()) {
            this.shifting = true;
        }

        return super.insertItem(slot, stack, simulate);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        if (entity == null) {
            return;
        }
        if (this.inShiftForward) {
            return;
        }
        if (this.shifting) {
            this.shifting = false;
            Level level = entity.level();
            if (!level.isClientSide) {
                shiftForward();
            }
        }
        if (!entity.level().isClientSide && slot == 0) {
            entity.level().sendBlockUpdated(
                    entity.blockPosition(),
                    entity.level().getBlockState(entity.blockPosition()),
                    entity.level().getBlockState(entity.blockPosition()),
                    3);
        }
    }

    private void shiftForward() {
        this.inShiftForward = true;
        int last = this.stacks.size() - 1;

        ItemStack tail = this.stacks.get(last);
        if (!tail.isEmpty()) {
            dropItem(tail.copy());
            this.stacks.set(last, ItemStack.EMPTY);
        }

        for (int i = last - 1; i >= 1; i--) {
            this.stacks.set(i + 1, this.stacks.get(i).copy());
            this.stacks.set(i, ItemStack.EMPTY);
        }
        this.inShiftForward = false;
    }

    private void dropItem(ItemStack stack) {
        Level level = entity.level();
        if (level.isClientSide) {
            return;
        }
        ItemEntity droppedItem = entity.spawnAtLocation(stack, 0.2f);
        if (droppedItem != null) {
            droppedItem.addTag(dropTag);
        }
    }
}
