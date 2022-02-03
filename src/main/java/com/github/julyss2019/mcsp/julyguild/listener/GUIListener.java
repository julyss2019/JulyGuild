package com.github.julyss2019.mcsp.julyguild.listener;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.entities.MainGUI;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julylibrary.event.ItemClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GUIListener implements Listener {
    private static JulyGuild plugin = JulyGuild.inst();
    private static GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        GuildPlayer guildPlayer = guildPlayerManager.getGuildPlayer((Player) event.getPlayer());

        if (guildPlayer.isUsingGUI()) {
            guildPlayer.setUsingGUI(null);
        }
    }

    /**
     * GUI的点击检测
     * 每次点击都会检测是否能使用当前GUI
     * 如果不能使用则返回上个GUI（如果有）
     * @param event
     */
    @EventHandler
    public void onItemClickEvent(ItemClickEvent event) {
        InventoryClickEvent inventoryClickEvent = event.getInventoryClickEvent();
        GuildPlayer guildPlayer = guildPlayerManager.getGuildPlayer((Player) inventoryClickEvent.getWhoClicked());

        if (guildPlayer.isUsingGUI()) {
            GUI usingGUI = guildPlayer.getUsingGUI();

            // 判断能否使用当前GUI
            if (!usingGUI.canUse()) {
                event.setCancelled(true);

                if (usingGUI.canBack()) {
                    usingGUI.back();
                } else {
                    new MainGUI(guildPlayer).open();
                }
            }
        }
    }
}
