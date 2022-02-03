package com.github.julyss2019.mcsp.julyguild.guild.member;

import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GuildOwner extends GuildMember {
    public GuildOwner(@NotNull Guild guild, @NotNull GuildPlayer guildPlayer) {
        super(guild, guildPlayer);
    }

    @Override
    public GuildPosition getPosition() {
        return GuildPosition.OWNER;
    }

    @Override
    public void setPermission(@NotNull GuildPermission guildPermission, boolean b) {
        throw new RuntimeException("会长不允许被设置权限");
    }

    @Override
    public Set<GuildPermission> getPermissions() {
        return new HashSet<>(Arrays.asList(GuildPermission.values()));
    }
}
