package me.trae.clans.fields.loot.impl.item.interfaces;

import com.hypixel.hytale.server.core.inventory.ItemStack;

public interface IItemLoot {

    int getQuantity();

    ItemStack getItemStack();
}