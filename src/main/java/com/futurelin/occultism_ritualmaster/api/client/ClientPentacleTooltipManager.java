package com.futurelin.occultism_ritualmaster.api.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ClientPentacleTooltipManager {
    private static final int ITEMS_PER_PAGE = 5;
    private static final int PAGE_CHANGE_TICKS = 40;

    private static List<MutableComponent> lastPentacles = List.of();

    public static List<MutableComponent> getLastPentacles() {
        return lastPentacles;
    }

    public static void rebuild(List<ResourceLocation> pentacleIds) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        List<MutableComponent> allPentacles = pentacleIds.stream()
                .distinct()
                .map(id -> Component.translatable("multiblock." + id.getNamespace() + "." + id.getPath())
                        .withStyle(ChatFormatting.WHITE))
                .toList();

        int totalPages = allPentacles.size() == ITEMS_PER_PAGE + 1 ? 1
                : Math.max(1, (int) Math.ceil((double) allPentacles.size() / ITEMS_PER_PAGE));

        int page = 0;
        if (totalPages > 1) {
            long now = mc.level.getGameTime();
            page = (int) ((now / PAGE_CHANGE_TICKS) % totalPages);
        }

        int startIdx = ITEMS_PER_PAGE + 1 == allPentacles.size()
                ? page * (ITEMS_PER_PAGE + 1) : page * ITEMS_PER_PAGE;
        int endIdx = ITEMS_PER_PAGE + 1 == allPentacles.size()
                ? Math.min(startIdx + ITEMS_PER_PAGE + 1, allPentacles.size())
                : Math.min(startIdx + ITEMS_PER_PAGE, allPentacles.size());

        List<MutableComponent> displayed = new ArrayList<>();
        for (int i = startIdx; i < endIdx; i++) {
            displayed.add(allPentacles.get(i).copy());
        }

        if (totalPages > 1) {
            displayed.add(Component.literal("Page ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.valueOf(page + 1)).withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal("/").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(String.valueOf(totalPages)).withStyle(ChatFormatting.YELLOW)));
        }

        lastPentacles = displayed;
    }
}