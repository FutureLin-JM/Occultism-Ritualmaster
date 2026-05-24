package com.futurelin.occultism_ritualmaster.integration.waila;

import com.futurelin.occultism_ritualmaster.TranslationKeys;
import com.futurelin.occultism_ritualmaster.api.client.ClientPentacleTooltipManager;
import com.futurelin.occultism_ritualmaster.common.entity.RitualmasterEntity;
import com.klikli_dev.occultism.Occultism;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.List;

public class RitualmasterProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    public static final RitualmasterProvider INSTANCE = new RitualmasterProvider();

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        if (entityAccessor.getEntity() instanceof RitualmasterEntity ritualmasterEntity) {
            if (ritualmasterEntity.getJobID() != null && !ritualmasterEntity.getJobID().isEmpty()) {
                String job = ritualmasterEntity.getJobID().split(":", 2)[1];
                iTooltip.add(Component.translatable("job.occultism_ritualmaster." + job));
            }

            if (entityAccessor.getServerData().getBoolean("Processing")) {
                String recipeName = entityAccessor.getServerData().getString("RecipeName");
                if (!recipeName.isEmpty()) {
                    int i = Math.max(recipeName.indexOf(":"), recipeName.indexOf("："));
                    iTooltip.add(Component.translatable("occultism.waila.current_ritual",
                            Component.literal(recipeName.substring(i + 2)).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.WHITE));
                }
            } else if (ritualmasterEntity.hasSatchel()) {
                Player player = entityAccessor.getPlayer();
                if (player == null) {
                    player = Minecraft.getInstance().player;
                }
                if (player != null && player.isShiftKeyDown()) {
                    List<ResourceLocation> pentacles = ritualmasterEntity.getPentacles();
                    ClientPentacleTooltipManager.rebuild(pentacles);
                    if (!ClientPentacleTooltipManager.getLastPentacles().isEmpty()) {
                        iTooltip.add(Component.translatable(TranslationKeys.JADE_PENTACLE_FOUND));
                        for (var text : ClientPentacleTooltipManager.getLastPentacles()) {
                            iTooltip.add(text);
                        }
                    } else {
                        iTooltip.add(Component.translatable(TranslationKeys.JADE_NO_PENTACLE_FOUND).withStyle(ChatFormatting.YELLOW));
                    }
                } else {
                    iTooltip.add(Component.translatable(TranslationKeys.JADE_SHIFT_TOOLTIP));
                }
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
        if (accessor.getEntity() instanceof RitualmasterEntity entity) {
            tag.putBoolean("Processing", entity.isProcessingRecipe());
            if (entity.isProcessingRecipe()) {
                tag.putString("RecipeName", entity.getCurrentRecipeName());
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(Occultism.MODID, "foliot");
    }

    @Override
    public int getDefaultPriority() {
        return 1000;
    }
}