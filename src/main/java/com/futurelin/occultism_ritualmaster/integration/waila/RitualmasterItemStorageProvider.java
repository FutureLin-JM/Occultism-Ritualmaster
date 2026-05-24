package com.futurelin.occultism_ritualmaster.integration.waila;

import com.futurelin.occultism_ritualmaster.common.entity.RitualmasterEntity;
import com.klikli_dev.occultism.Occultism;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.*;

import java.util.ArrayList;
import java.util.List;

public class RitualmasterItemStorageProvider implements IServerExtensionProvider<ItemStack>, IClientExtensionProvider<ItemStack, ItemView> {
    public static final RitualmasterItemStorageProvider INSTANCE = new RitualmasterItemStorageProvider();

    @Override
    public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        if (accessor.getTarget() instanceof RitualmasterEntity entity) {
            IItemHandler inventory = entity.getCapability(Capabilities.ItemHandler.ENTITY);
            if (inventory != null) {
                List<ItemStack> items = new ArrayList<>();
                for (int slot = 1; slot < inventory.getSlots(); slot++) {
                    ItemStack stack = inventory.getStackInSlot(slot);
                    if (!stack.isEmpty()) {
                        items.add(stack.copy());
                    }
                }
                if (!items.isEmpty()) {
                    return List.of(new ViewGroup<>(items));
                }
            }
        }
        return List.of();
    }

    @Override
    public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ItemStack>> groups) {
        return ClientViewGroup.map(groups, ItemView::new, null);
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(Occultism.MODID, "ritualmaster_storage");
    }
}