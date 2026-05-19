package com.futurelin.occultism_ritualmaster.common.item;

import com.futurelin.occultism_ritualmaster.TranslationKeys;
import com.futurelin.occultism_ritualmaster.registry.OrmDataComponentsRegistry;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.multiblock.matcher.AnyMatcher;
import com.klikli_dev.modonomicon.multiblock.matcher.DisplayOnlyMatcher;
import com.klikli_dev.occultism.registry.OccultismBlocks;
import com.klikli_dev.occultism.registry.OccultismRecipes;
import com.klikli_dev.occultism.registry.OccultismTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.*;

public class SealedPentacle extends Item {

    public SealedPentacle(Properties properties) {
        super(properties);
    }

    protected InteractionResult sealedPentacle(UseOnContext context) {
        var recipes = context.getLevel().getRecipeManager().getAllRecipesFor(OccultismRecipes.RITUAL_TYPE.get());

        Map<ResourceLocation, Multiblock> pentaclesById = new HashMap<>();

        for (var recipe : recipes) {
            var pentacle = recipe.value().getPentacle();
            pentaclesById.putIfAbsent(pentacle.getId(), pentacle);
        }

        record Match(ResourceLocation pentacleId, Collection<Multiblock.SimulateResult> results) {}
        List<Match> matches = new ArrayList<>();

        for (var entry : pentaclesById.entrySet()) {
            var rotation = entry.getValue().validate(context.getLevel(), context.getClickedPos());
            if (rotation == null) continue;
            var simulate = entry.getValue().simulate(context.getLevel(), context.getClickedPos(), rotation, false, false);
            matches.add(new Match(entry.getKey(), simulate.getSecond()));
        }

        if (!matches.isEmpty()) {
            var best = matches.stream()
                    .max(Comparator.comparingInt(m -> m.results().size()))
                    .orElseThrow();

            for (var targetMatcher : best.results()) {
                if (targetMatcher.getStateMatcher().getType().equals(AnyMatcher.TYPE)
                        || targetMatcher.getStateMatcher().getType().equals(DisplayOnlyMatcher.TYPE))
                    continue;

                var blockState = context.getLevel().getBlockState(targetMatcher.getWorldPosition());

                if (blockState.isAir())
                    continue;

                if (!blockState.is(OccultismTags.Blocks.PENTACLE_MATERIALS))
                    continue;

                context.getLevel().setBlock(targetMatcher.getWorldPosition(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }

            List<ResourceLocation> pentacleIds = matches.stream()
                    .map(Match::pentacleId)
                    .toList();
            context.getItemInHand().set(OrmDataComponentsRegistry.SEALED_PENTACLE.get(), pentacleIds);
        }

        return InteractionResult.SUCCESS;
    }

    protected InteractionResult useOnServerSide(UseOnContext context) {
        if (context.getLevel().getBlockState(context.getClickedPos()).is(OccultismBlocks.GOLDEN_SACRIFICIAL_BOWL.get())
                || context.getLevel().getBlockState(context.getClickedPos()).is(OccultismBlocks.IESNIUM_SACRIFICIAL_BOWL.get())
                || context.getLevel().getBlockState(context.getClickedPos()).is(OccultismBlocks.DARK_GOLDEN_SACRIFICIAL_BOWL.get())
                || context.getLevel().getBlockState(context.getClickedPos()).is(OccultismBlocks.DARK_IESNIUM_SACRIFICIAL_BOWL.get())
                || context.getLevel().getBlockState(context.getClickedPos()).is(OccultismBlocks.ELDRITCH_CHALICE.get())
                || context.getLevel().getBlockState(context.getClickedPos()).is(OccultismBlocks.CELESTIAL_CHALICE.get())) {
            return this.sealedPentacle(context);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        List<ResourceLocation> sealed = context.getItemInHand().get(OrmDataComponentsRegistry.SEALED_PENTACLE.get());
        if (sealed != null && !sealed.isEmpty()) {
            return InteractionResult.SUCCESS;
        }

        if (context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            return useOnServerSide(context);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        List<ResourceLocation> sealed = stack.get(OrmDataComponentsRegistry.SEALED_PENTACLE.get());
        return sealed != null && !sealed.isEmpty();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        List<ResourceLocation> pentacleIds = stack.get(OrmDataComponentsRegistry.SEALED_PENTACLE.get());
        if (pentacleIds != null && !pentacleIds.isEmpty()) {
            for (ResourceLocation pentacleId : pentacleIds) {
                tooltip.add(Component.translatable("multiblock." + pentacleId.getNamespace() + "." + pentacleId.getPath()).withStyle(ChatFormatting.GOLD));
            }
        } else {
            tooltip.add(Component.translatable(TranslationKeys.SEALED_PENTACLE_EMPTY).withStyle(ChatFormatting.GRAY));
        }

        tooltip.add(Component.translatable(TranslationKeys.SEALED_PENTACLE_TOOLTIP));
    }
}
