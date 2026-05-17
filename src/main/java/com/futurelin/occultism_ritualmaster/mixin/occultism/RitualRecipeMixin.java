package com.futurelin.occultism_ritualmaster.mixin.occultism;

import com.futurelin.occultism_ritualmaster.api.mixin.accessor.IRitualRecipeAccessor;
import com.klikli_dev.occultism.common.entity.spirit.SpiritEntity;
import com.klikli_dev.occultism.crafting.recipe.RitualRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = RitualRecipe.class, remap = false)
public abstract class RitualRecipeMixin implements IRitualRecipeAccessor {

    @Override
    @Unique
    public boolean matches(ItemStackHandler inv, Level level, SpiritEntity entity) {
        RitualRecipe self = (RitualRecipe) (Object) this;

        List<Ingredient> requirements = new ArrayList<>(self.getIngredients());
        requirements.add(self.getActivationItem());

        List<ItemStack> available = new ArrayList<>();
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                available.add(stack.copy());
            }
        }

        for (Ingredient requirement : requirements) {
            boolean matched = false;
            for (ItemStack stack : available) {
                if (!stack.isEmpty() && requirement.test(stack)) {
                    stack.shrink(1);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        return true;
    }
}