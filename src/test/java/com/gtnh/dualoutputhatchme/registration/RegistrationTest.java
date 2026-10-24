package com.gtnh.dualoutputhatchme.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;

import org.junit.jupiter.api.Test;

import com.glodblock.github.nei.object.OrderStack;
import com.glodblock.github.nei.recipes.extractor.GregTech5RecipeExtractor;

import codechicken.nei.PositionedStack;

class RegistrationTest {

    @Test
    void includesEveryRegisteredCapacityForBothMEOutputs() {
        Item machine = new Item().setHasSubtypes(true);
        long[] capacities = { 8192L, 32768L, 131072L, 524288L, 2097152L, 8388608L, 33554432L, 134217728L };
        for (int metadata : new int[] { 2710, 2713 }) {
            ItemStack base = new ItemStack(machine, 1, metadata);
            List<IRecipe> recipes = new ArrayList<>();
            for (long capacity : capacities) {
                recipes.add(recipe(upgraded(base, metadata == 2713 ? capacity * 256L : capacity)));
            }
            recipes.add(recipe(upgraded(base, Long.MAX_VALUE)));

            ItemStack[] variants = Registration.getInputVariants(base, recipes);

            assertEquals(10, variants.length);
            assertFalse(variants[0].hasTagCompound());
            for (int i = 0; i < capacities.length; i++) {
                assertEquals(
                    metadata == 2713 ? capacities[i] * 256L : capacities[i],
                    variants[i + 1].getTagCompound()
                        .getLong("baseCapacity"));
            }
            assertEquals(
                Long.MAX_VALUE,
                variants[9].getTagCompound()
                    .getLong("baseCapacity"));
        }
    }

    @Test
    void filtersUnrelatedOutputsAndCopiesEachDistinctVariantAtRequiredCount() {
        Item machine = new Item().setHasSubtypes(true);
        ItemStack base = new ItemStack(machine, 1, 2710);
        ItemStack singularity = upgraded(base, Long.MAX_VALUE);
        singularity.stackSize = 4;
        ItemStack[] variants = Registration.getInputVariants(
            base,
            Arrays.asList(
                recipe(null),
                recipe(new ItemStack(new Item(), 1, 2710)),
                recipe(new ItemStack(machine, 1, 2713)),
                recipe(base),
                recipe(singularity),
                recipe(singularity.copy())));

        assertEquals(2, variants.length);
        assertEquals(1, variants[1].stackSize);
        assertEquals(4, singularity.stackSize);
        assertNotSame(base, variants[0]);
        assertNotSame(singularity.getTagCompound(), variants[1].getTagCompound());
        variants[1].getTagCompound()
            .setLong("baseCapacity", 1L);
        assertEquals(
            Long.MAX_VALUE,
            singularity.getTagCompound()
                .getLong("baseCapacity"));
        assertNull(base.getTagCompound());
    }

    @Test
    void fluidPatternNEITransferRetainsUpgradedCandidates() {
        ItemStack base = new ItemStack(new Item().setHasSubtypes(true), 1, 2710);
        ItemStack singularity = upgraded(base, Long.MAX_VALUE);
        ItemStack[] variants = Registration.getInputVariants(base, Collections.singletonList(recipe(singularity)));
        PositionedStack ingredient = new PositionedStack(variants, 0, 0, false);

        List<OrderStack<?>> inputs = new GregTech5RecipeExtractor(false)
            .getInputIngredients(Collections.singletonList(ingredient));

        assertEquals(1, inputs.size());
        assertEquals(
            2,
            inputs.get(0)
                .getItems().length);
        assertTrue(
            ItemStack.areItemStackTagsEqual(
                singularity,
                inputs.get(0)
                    .getItems()[1]));
        assertEquals(
            1,
            inputs.get(0)
                .getItems()[1].stackSize);
    }

    private static ItemStack upgraded(ItemStack base, long capacity) {
        ItemStack result = base.copy();
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong("baseCapacity", capacity);
        result.setTagCompound(tag);
        return result;
    }

    private static IRecipe recipe(ItemStack output) {
        return new ShapelessRecipes(output, Collections.emptyList());
    }
}
