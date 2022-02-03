package com.github.julyss2019.mcsp.julyguild.placeholder;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.LangHelper;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildMember;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.utils.TimeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于内部的占位符
 */
public class PlaceholderContainer {
    private Map<String, Placeholder> placeholderMap = new HashMap<>();

    public boolean hasPlaceholder(@NotNull String key) {
        return placeholderMap.containsKey(key);
    }

    public Placeholder getPlaceholder(@NotNull String key) {
        return placeholderMap.get(key);
    }

    public List<Placeholder> getPlaceholders() {
        return new ArrayList<>(placeholderMap.values());
    }

    /**
     * 添加定义好的公会占位符
     * @param guild
     * @return
     */
    public PlaceholderContainer addGuildPlaceholders(@NotNull Guild guild) {
        add("guild_name", guild.getName());
        add("guild_ranking", JulyGuild.inst().getCacheGuildManager().getRanking(guild));
        add("guild_owner", guild.getOwner().getName());
        add("guild_gmoney", Util.SIMPLE_DECIMAL_FORMAT.format(guild.getGuildBank().getBalance(GuildBank.BalanceType.GMONEY)));
        add("guild_online_member_count", guild.getOnlineMemberCount());
        add("guild_member_count", guild.getMemberCount());
        add("guild_max_member_count", guild.getMaxMemberCount());
        add("guild_creation_time", TimeUtil.YMD_SDF.format(guild.getCreateTime()));
        return this;
    }

    public PlaceholderContainer addGuildMemberPlaceholders(@NotNull GuildMember guildMember) {
        add("member_is_signed_today", guildMember.getSign().isSignedToday());
        add("member_signed_count", guildMember.getSign().getSignedCount());
        add("member_name", guildMember.getName());
        add("member_position", LangHelper.Global.getPositionName(guildMember.getPosition()));
        add("member_join_time", LangHelper.Global.getDateTimeFormat().format(guildMember.getJoinTime()));
        add("member_donated_gmoney", Util.SIMPLE_DECIMAL_FORMAT.format(guildMember.getDonated(GuildBank.BalanceType.GMONEY)));
        return this;
    }

    public PlaceholderContainer add(@NotNull String key, @NotNull Object value) {
        placeholderMap.put(key, new Placeholder(key, value));
        return this;
    }
}
