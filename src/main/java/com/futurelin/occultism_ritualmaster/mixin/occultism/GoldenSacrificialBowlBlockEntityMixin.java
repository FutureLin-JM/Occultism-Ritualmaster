package com.futurelin.occultism_ritualmaster.mixin.occultism;

import com.futurelin.occultism_ritualmaster.TranslationKeys;
import com.futurelin.occultism_ritualmaster.common.item.SealedPentacle;
import com.futurelin.occultism_ritualmaster.registry.OrmDataComponentsRegistry;
import com.klikli_dev.occultism.common.blockentity.GoldenSacrificialBowlBlockEntity;
import com.klikli_dev.occultism.registry.OccultismSounds;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GoldenSacrificialBowlBlockEntity.class)
public abstract class GoldenSacrificialBowlBlockEntityMixin {

    @Inject(method = "activate",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/klikli_dev/occultism/common/blockentity/GoldenSacrificialBowlBlockEntity;helpWithRitual(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/ItemStack;)Z"
            ),
            cancellable = true
    )
    private void sealedPentacleActivate(Level level, BlockPos pos, Player player, InteractionHand hand, Direction face, CallbackInfoReturnable<Boolean> cir) {
        ItemStack activationItem = player.getItemInHand(hand);
        if (activationItem.getItem() instanceof SealedPentacle) {
            var sealed = activationItem.get(OrmDataComponentsRegistry.SEALED_PENTACLE.get());
            if (sealed == null || sealed.isEmpty()) {
                ((ServerLevel) level)
                        .sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                10, 0.3, 0.3, 0.3, 0.03);

                level.playSound(null, pos, OccultismSounds.POOF.get(), SoundSource.PLAYERS, 1, 3);
                player.displayClientMessage(Component.translatable(TranslationKeys.SEALED_PENTACLE_SEALED), true);

                cir.setReturnValue(false);
            }
        }
    }

    @ModifyExpressionValue(
            method = "activate",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;isEmpty()Z"
            )
    )
    private boolean sealedPentaclePass(boolean original, @Local(name = "activationItem") ItemStack activationItem) {
        if (activationItem.getItem() instanceof SealedPentacle) {
            var sealed = activationItem.get(OrmDataComponentsRegistry.SEALED_PENTACLE.get());
            if (sealed == null || sealed.isEmpty()) {
                return false;
            }
        }
        return original;
    }
}
