package com.github.julyss2019.mcsp.julyguild.listener;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import com.github.julyss2019.mcsp.julylibrary.message.Title;
import com.github.julyss2019.mcsp.julylibrary.text.JulyText;
import com.github.julyss2019.mcsp.julylibrary.utilv2.NMSUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleportListener implements Listener {
    private JulyGuild plugin = JulyGuild.inst();
    private ConfigurationSection langSection = plugin.getLangYaml();
    private GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player bukkitPlayer = event.getPlayer();
        GuildPlayer guildPlayer = guildPlayerManager.getGuildPlayer(bukkitPlayer);

        if (!guildPlayer.hasTeleportTask()) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
            guildPlayer.getTeleportTask().cancel();
            guildPlayer.setTeleportTask(null);

            if (JulyMessage.isTitleEnabled()) {
                if (NMSUtil.compareVersion("v1_17_R1") >= 0) {
                    bukkitPlayer.sendTitle(
                            JulyText.getColoredText(langSection.getString("GuildMineGUI.guild_spawn.cancelled.title"))
                            , JulyText.getColoredText(langSection.getString("GuildMineGUI.guild_spawn.cancelled.subtitle"))
                            , 0
                            , 20
                            , 20
                    );
                } else {
                    JulyMessage.sendTitle(bukkitPlayer, new Title.Builder().text(langSection.getString("GuildMineGUI.guild_spawn.cancelled.title")).colored().build());
                    JulyMessage.sendTitle(bukkitPlayer, new Title.Builder().type(Title.Type.SUBTITLE).text(langSection.getString("GuildMineGUI.guild_spawn.cancelled.subtitle")).colored().build());
                }
            } else {
                Util.sendMsg(bukkitPlayer, langSection.getString("GuildMineGUI.guild_spawn.cancelled.msg"));
            }
        }
    }
}
