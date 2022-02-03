package com.github.julyss2019.mcsp.julyguild.thirdparty;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.LangHelper;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.guild.CacheGuildManager;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildMember;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * PAPI扩展
 */
public class PlaceholderAPIExpansion extends PlaceholderExpansion {
    private static final JulyGuild plugin = JulyGuild.inst();
    private static final CacheGuildManager cacheGuildManager = plugin.getCacheGuildManager();
    private static final GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();

    @Override
    public String getIdentifier() {
        return "guild";
    }


    @Override
    public String getAuthor() {
        return "July_ss";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        GuildPlayer guildPlayer = guildPlayerManager.getGuildPlayer(p.getUniqueId());
        Guild guild = guildPlayer.getGuild();
        boolean isInGuild = guild != null;

        if (params.equalsIgnoreCase("is_in_guild")) {
            return String.valueOf(isInGuild);
        }

        if (!isInGuild) {
            return MainSettings.getGuildPapiNonStr();
        }

        GuildMember guildMember = guild.getMember(guildPlayer);
        GuildBank guildBank = guild.getGuildBank();

        switch (params.toLowerCase()) {
            case "name":
                return guild.getName();
            case "member_signed_count":
                return String.valueOf(guildMember.getSign().getSignedCount());
            case "member_position":
                return LangHelper.Global.getPositionName(guildMember.getPosition());
            case "member_donated_gmoney":
                return LangHelper.Global.getDateTimeFormat().format(guildMember.getDonated(GuildBank.BalanceType.GMONEY));
            case "member_join_time":
                return LangHelper.Global.getDateTimeFormat().format(guildMember.getJoinTime());
            case "ranking":
                return String.valueOf(cacheGuildManager.getRanking(guild));
            case "owner":
                return guild.getOwner().getName();
            case "member_count":
                return String.valueOf(guild.getMemberCount());
            case "max_member_count":
                return String.valueOf(guild.getMaxMemberCount());
            case "creation_time":
                return LangHelper.Global.getDateTimeFormat().format(guild.getCreateTime());
            case "bank_gmoney":
                return guildBank.getBalance(GuildBank.BalanceType.GMONEY).toString();
            case "online_member_count":
                return String.valueOf(guild.getOnlineMembers().size());
        }

        return null;
    }

    @Override
    public boolean canRegister() {
        return Bukkit.getPluginManager().isPluginEnabled("JulyGuild");
    }
}
