package com.github.julyss2019.mcsp.julyguild.listener;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private GuildPlayerManager guildPlayerManager = JulyGuild.inst().getGuildPlayerManager();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GuildPlayer guildPlayer = guildPlayerManager.getGuildPlayer(player);

        guildPlayer.setKnownName(player.getName());
    }
}
