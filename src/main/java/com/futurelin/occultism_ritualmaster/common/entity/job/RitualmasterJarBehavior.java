package com.futurelin.occultism_ritualmaster.common.entity.job;

import com.futurelin.occultism_ritualmaster.common.entity.RitualmasterEntity;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.klikli_dev.occultism.api.OccultismAPI;
import com.mystchonky.arsocultas.content.spirit_jar.SpiritJarBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class RitualmasterJarBehavior extends SpiritJarBehaviour<RitualmasterEntity> {

    @Override
    public void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, MobJarTile tile) {
        if (!world.isClientSide) {
            ItemStack heldStack = player.getItemInHand(handIn);
            RitualmasterEntity spirit = this.entityFromJar(tile);
            if (player.isShiftKeyDown() && heldStack.isEmpty()) {
                this.openScreen(player, spirit, tile);
            } else if (heldStack.isEmpty()) {
                spirit.dropStoredItems();
            } else {
                if (this.canAcceptItemStack(spirit, heldStack)) {
                    ItemStack duplicate = heldStack.copy();
                    if (ItemHandlerHelper.insertItemStacked(spirit.inventory, duplicate, true).getCount() < duplicate.getCount()) {
                        ItemStack remaining = ItemHandlerHelper.insertItemStacked(spirit.inventory, duplicate, false);
                        heldStack.setCount(remaining.getCount());
                    }
                }
            }
        }
    }

    private boolean canAcceptItemStack(RitualmasterEntity spirit, ItemStack stack) {
        return OccultismAPI.get().getItemsToPickUp(spirit)
                .map(items -> items.stream().anyMatch(item -> item.test(stack)))
                .orElse(false);
    }
}
