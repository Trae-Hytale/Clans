package me.trae.clans.shop;

import io.github.trae.hf.Module;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.ClansPlugin;

@AllArgsConstructor
@Getter
public class Shop implements Module<ClansPlugin, ShopManager> {

    private final String displayName;
}