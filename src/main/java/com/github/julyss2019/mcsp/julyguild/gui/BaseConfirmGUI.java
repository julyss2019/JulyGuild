package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.DebugMessage;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.logger.JulyGuildLogger;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseConfirmGUI extends BasePlayerGUI {
    private final ConfigurationSection section;
    private final Player bukkitPlayer = getBukkitPlayer();
    private final PlaceholderContainer confirmPlaceholderContainer;

    protected BaseConfirmGUI(@Nullable GUI lastGUI, @NotNull GuildPlayer guildPlayer, @NotNull ConfigurationSection section) {
        this(lastGUI, guildPlayer, section, null);
    }

    protected BaseConfirmGUI(@Nullable GUI lastGUI, @NotNull GuildPlayer guildPlayer, @NotNull ConfigurationSection section, @Nullable PlaceholderContainer confirmPlaceholderContainer) {
        super(lastGUI, Type.CONFIRM, guildPlayer);

        this.section = section;
        this.confirmPlaceholderContainer = confirmPlaceholderContainer;
    }

    public PlaceholderContainer getConfirmPlaceholderContainer() {
        return confirmPlaceholderContainer;
    }

    @Override
    public abstract boolean canUse();

    public abstract void onConfirm();

    public abstract void onCancel();

    @Override
    public Inventory createInventory() {
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder();

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_BASIC);
        guiBuilder.fromConfig(section, confirmPlaceholderContainer);
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_BASIC);

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.cancel");
        guiBuilder.item(GUIItemManager.getIndexItem(section.getConfigurationSection("items.cancel"), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        onCancel();
                    }
                });
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.cancel");

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.confirm");
        guiBuilder.item(GUIItemManager.getIndexItem(section.getConfigurationSection("items.confirm"), bukkitPlayer, confirmPlaceholderContainer), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                onConfirm();
            }
        });
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.confirm");

        return guiBuilder.build();
    }
}
