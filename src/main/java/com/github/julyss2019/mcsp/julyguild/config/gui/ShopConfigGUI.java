package com.github.julyss2019.mcsp.julyguild.config.gui;

import com.github.julyss2019.mcsp.julyguild.config.gui.item.IndexItem;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShopConfigGUI {
    public static class Builder extends InventoryBuilder {
        public ShopConfigGUI.Builder fromConfig(@NotNull ConfigurationSection section) {
            super.title(section.getString("title"));
            super.row(section.getInt("row"));
            return this;
        }

        public ShopConfigGUI.Builder item(@NotNull IndexItem indexItem) {
            return item(indexItem,null);
        }

        public ShopConfigGUI.Builder item(@NotNull IndexItem indexItem, @Nullable ItemListener itemListener) {
            super.item(indexItem.getIndex(), indexItem.getItemBuilder().build(), itemListener);
            return this;
        }
    }
}
