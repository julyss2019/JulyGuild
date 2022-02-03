package com.github.julyss2019.mcsp.julyguild.api.event;

import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildCreatedEvent extends Event {
    private Guild guild;
    private GuildPlayer guildPlayer;
    private static HandlerList handlerList = new HandlerList();

    public GuildCreatedEvent(@NotNull Guild guild, @NotNull GuildPlayer guildPlayer) {
        this.guild = guild;
        this.guildPlayer = guildPlayer;
    }

    public Guild getGuild() {
        return guild;
    }

    public GuildPlayer getGuildPlayer() {
        return guildPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
