package com.example.astroworld;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class CactusJackMicItem extends Item {

    public CactusJackMicItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            if (!ConcertManager.start(player)) {
                player.sendMessage(Text.translatable("message.astroworld.already_running")
                        .formatted(Formatting.RED), true);
                return TypedActionResult.fail(stack);
            }
            user.getItemCooldownManager().set(this, ConcertManager.DURATION_TICKS);
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.astroworld.cactus_jack_mic.tooltip").formatted(Formatting.GOLD));
    }
}
