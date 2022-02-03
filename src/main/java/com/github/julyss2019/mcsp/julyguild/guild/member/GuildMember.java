package com.github.julyss2019.mcsp.julyguild.guild.member;

import com.github.julyss2019.mcsp.julyguild.guild.*;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.request.Receiver;
import com.github.julyss2019.mcsp.julyguild.request.Sender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class GuildMember implements GuildHuman, Receiver, Sender {
    private Guild guild;
    private GuildPlayer guildPlayer;
    private UUID uuid;
    private ConfigurationSection section;
    private Set<GuildPermission> permissions = new HashSet<>();
    private long joinTime;
    private Map<GuildBank.BalanceType, BigDecimal> donatedMap = new HashMap<>();
    private GuildMemberSign sign;

    public GuildMember(@NotNull Guild guild, @NotNull GuildPlayer guildPlayer) {
        this.guild = guild;
        this.guildPlayer = guildPlayer;
        this.uuid = guildPlayer.getUuid();

        load();

        this.sign = new GuildMemberSign(this);
    }

    public GuildMemberSign getSign() {
        return sign;
    }

    private void load() {
        if (!guild.getYaml().contains("members")) {
            guild.getYaml().createSection("members");
        }

        this.section = guild.getYaml().getConfigurationSection("members").getConfigurationSection(uuid.toString());

        if (section.contains("permissions")) {
            List<String> permissions = section.getStringList("permissions");

            if (permissions != null) {
                permissions.forEach(s -> this.permissions.add(GuildPermission.valueOf(s)));
            }
        }

        if (section.contains("donated")) {
            for (String type : section.getConfigurationSection("donated").getKeys(false)) {
                GuildBank.BalanceType balanceType = GuildBank.BalanceType.valueOf(type);

                donatedMap.put(balanceType, new BigDecimal(section.getString("donated." + balanceType.name(), "0")));
            }
        }

        this.joinTime = section.getLong("join_time");
    }

    public String getName() {
        return getGuildPlayer().getName();
    }

    public UUID getUuid() {
        return uuid;
    }

    /**
     * 设置权限
     * @param guildPermission
     * @param b true 为设置 false 为删除
     */
    public void setPermission(@NotNull GuildPermission guildPermission, boolean b) {
        Set<GuildPermission> newGuildPermissions = getPermissions();

        if (b) {
            newGuildPermissions.add(guildPermission);
        } else {
            newGuildPermissions.remove(guildPermission);
        }

        section.set("permissions", newGuildPermissions.stream().map(Enum::name).collect(Collectors.toList()));
        this.permissions = newGuildPermissions;
        save();
    }

    public Set<GuildPermission> getPermissions() {
        return new HashSet<>(permissions);
    }

    public boolean hasPermission(@NotNull GuildPermission guildPermission) {
        return getPosition() == GuildPosition.OWNER || getPermissions().contains(guildPermission);
    }

    public void addDonated(@NotNull GuildBank.BalanceType balanceType, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("数量必须大于0");
        }

        setDonated(balanceType, getDonated(balanceType).add(new BigDecimal(amount)));
    }

    public BigDecimal getDonated(@NotNull GuildBank.BalanceType balanceType) {
        return donatedMap.getOrDefault(balanceType, new BigDecimal(0));
    }

    public void setDonated(@NotNull GuildBank.BalanceType balanceType, @NotNull BigDecimal value) {
        section.set("donated." + balanceType.name(), value.toString());
        save();
        donatedMap.put(balanceType, value);
    }

    public long getJoinTime() {
        return joinTime;
    }

    public Guild getGuild() {
        return guild;
    }

    public GuildPlayer getGuildPlayer() {
        return guildPlayer;
    }

    public boolean isOnline() {
        return getGuildPlayer().isOnline();
    }

    public GuildPosition getPosition() {
        return GuildPosition.MEMBER;
    }

    public boolean isValid() {
        return guild.isValid() && guild.isMember(uuid);
    }

    /**
     * 得到当前玩家节点
     * @return
     */
    public ConfigurationSection getSection() {
        return section;
    }

    public void save() {
        guild.save();
    }
}
