package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.api.event.GuildDeletedEvent;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildOwner;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildPosition;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.request.Receiver;
import com.github.julyss2019.mcsp.julyguild.request.Request;
import com.github.julyss2019.mcsp.julyguild.request.Sender;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.utils.YamlUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parsii.eval.Parser;
import parsii.tokenizer.ParseException;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Guild implements Sender, Receiver {
    private final JulyGuild plugin = JulyGuild.inst();
    private final File file;
    private YamlConfiguration yaml;
    private boolean deleted;
    private UUID uuid;
    private String name;
    private GuildOwner owner;
    private Map<UUID, GuildMember> memberMap = new HashMap<>();
    private Map<UUID, GuildIcon> iconMap = new HashMap<>();
    private GuildIcon currentIcon;
    private GuildBank guildBank;
    private GuildMessageBox guildMessageBox;
    private List<String> announcements;
    private long createTime;
    private int additionMemberCount;
    private GuildSpawn spawn;
    private boolean memberDamageEnabled;
    private boolean valid = true;

    public Guild(File file) {
        this.file = file;

        if (!file.exists()) {
            throw new RuntimeException("公会不存在");
        }

        load();
    }

    /**
     * 载入
     * @return
     */
    private void load() {
        this.yaml = YamlConfiguration.loadConfiguration(file);
        this.deleted = yaml.getBoolean("deleted");

        if (isDeleted()) {
            return;
        }

        this.name = yaml.getString("name");
        this.uuid = UUID.fromString(yaml.getString("uuid"));
        this.guildBank = new GuildBank(this);

        if (!yaml.contains("message_box")) {
            yaml.createSection("message_box");
        }

        this.guildMessageBox = new GuildMessageBox(this);
        this.announcements = yaml.getStringList("announcements");
        this.createTime = yaml.getLong("creation_time");
        this.additionMemberCount = yaml.getInt("addition_member_count");
        this.memberDamageEnabled = yaml.getBoolean("member_pvp_enabled", true);

        if (yaml.contains("spawn")) {
            this.spawn = new GuildSpawn(this);
        }

        loadMembers();
        loadIcons();

        this.currentIcon = Optional.ofNullable(yaml.getString("current_icon")).map(s -> iconMap.get(UUID.fromString(s))).orElse(null);
    }

    public GuildSpawn getSpawn() {
        return spawn;
    }

    public void setSpawn(@NotNull Location location) {
        if (!yaml.contains("spawn")) {
            yaml.createSection("spawn");
        }

        YamlUtil.setLocationToSection(yaml.getConfigurationSection("spawn"), location);
        save();
        this.spawn = new GuildSpawn(this);
    }

    public boolean isMemberDamageEnabled() {
        return memberDamageEnabled;
    }

    public void setMemberDamageEnabled(boolean b) {
        yaml.set("member_damage_enabled", b);
        save();
        this.memberDamageEnabled = b;
    }

    public GuildMessageBox getGuildMessageBox() {
        return guildMessageBox;
    }

    private void loadMembers() {
        memberMap.clear();

        if (yaml.contains("members")) {
            for (String memberUuidStr : yaml.getConfigurationSection("members").getKeys(false)) {
                GuildPosition guildPosition = GuildPosition.valueOf(yaml
                        .getConfigurationSection("members")
                        .getConfigurationSection(memberUuidStr)
                        .getString("position"));
                UUID memberUuid = UUID.fromString(memberUuidStr);
                GuildPlayer guildPlayer = JulyGuild.inst().getGuildPlayerManager().getGuildPlayer(memberUuid);
                GuildMember member = guildPosition == GuildPosition.MEMBER
                        ? new GuildMember(this, guildPlayer)
                        : new GuildOwner(this, guildPlayer);

                memberMap.put(memberUuid, member);
                member.getGuildPlayer().pointGuild(this);

                if (member instanceof GuildOwner) {
                    this.owner = (GuildOwner) member;
                }
            }
        }
    }

    private void loadIcons() {
        iconMap.clear();

        if (yaml.contains("icons")) {
            for (String iconUuidStr : yaml.getConfigurationSection("icons").getKeys(false)) {
                UUID iconUuid = UUID.fromString(iconUuidStr);

                iconMap.put(iconUuid, new GuildIcon(this, iconUuid));
            }
        }
    }

    public void removeIcon(@NotNull GuildIcon guildIcon) {
        yaml.set("icons." + guildIcon.getUuid(), null);
        save();
        loadIcons();
    }


    public void setCurrentIcon(@Nullable GuildIcon guildIcon) {
        if (guildIcon == null) {
            yaml.set("current_icon", null);
            save();
            this.currentIcon = null;
            return;
        }

        UUID iconUuid = guildIcon.getUuid();

        if (!iconMap.containsKey(iconUuid)) {
            throw new RuntimeException("图标不存在");
        }

        yaml.set("current_icon", iconUuid.toString());
        save();
        this.currentIcon = guildIcon;
    }

    public GuildIcon giveIcon(@NotNull Material material, short durability, @Nullable String firstLore, @Nullable String displayName) {
        UUID uuid = UUID.randomUUID();

        if (!yaml.contains("icons." + uuid)) {
            yaml.createSection("icons." + uuid);
        }

        ConfigurationSection iconSection = yaml.getConfigurationSection("icons." + uuid);

        iconSection.set("material", material.name());
        iconSection.set("durability", durability);
        iconSection.set("first_lore", firstLore);
        iconSection.set("display_name", displayName);
        save();
        loadIcons();
        return iconMap.get(uuid);
    }

    public boolean hasSpawn() {
        return spawn != null;
    }

    /**
     * 得到公会唯一标识符
     * @return
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * 是否已被解散
     * @return
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * 得到公会银行
     * @return
     */
    public GuildBank getGuildBank() {
        return guildBank;
    }

    /**
     * 设置主人
     * 旧主人将变成普通成员
     * @param newOwner
     */
    public void setOwner(@NotNull GuildMember newOwner) {
        GuildMember oldOwner = owner;
        UUID newOwnerUuid = newOwner.getUuid();

        if (newOwner.equals(owner)) {
            throw new IllegalArgumentException("成员已是会长");
        }

        if (!memberMap.containsKey(newOwnerUuid)) {
            throw new IllegalArgumentException("成员不存在");
        }

        yaml.set("members." + newOwnerUuid + ".position", GuildPosition.OWNER.name());
        yaml.set("members." + oldOwner.getUuid() + ".position", GuildPosition.MEMBER.name());
        save();
        loadMembers();
    }

    /**
     * 是否为成员
     * @param guildPlayer
     * @return
     */
    public boolean isMember(@NotNull GuildPlayer guildPlayer) {
        return memberMap.containsKey(guildPlayer.getUuid());
    }

    public boolean isMember(@NotNull UUID uuid) {
        return memberMap.containsKey(uuid);
    }

    public GuildIcon getCurrentIcon() {
        return currentIcon;
    }

    public List<GuildIcon> getIcons() {
        return new ArrayList<>(iconMap.values());
    }

    public boolean isOwner(@NotNull GuildMember guildMember) {
        return owner.equals(guildMember);
    }

    /**
     * 是否为宗主
     * @param guildPlayer
     * @return
     */
    public boolean isOwner(@NotNull GuildPlayer guildPlayer) {
        return owner.getGuildPlayer().equals(guildPlayer);
    }

    public GuildMember getMember(@NotNull UUID uuid) {
        return memberMap.get(uuid);
    }

    /**
     * 得到成员
     * @param guildPlayer
     * @return
     */
    public GuildMember getMember(@NotNull GuildPlayer guildPlayer) {
        return getMember(guildPlayer.getUuid());
    }

    /**
     * 公会文件
     * @return
     */
    public File getFile() {
        return file;
    }

    /**
     * 公会名
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 成员数量
     * @return
     */
    public int getMemberCount() {
        return memberMap.size();
    }

    /**
     * 得到公会主人
     * @return
     */
    public GuildOwner getOwner() {
        return owner;
    }

    /**
     * 得到成员（包含主人）
     * @return
     */
    public List<GuildMember> getMembers() {
        return new ArrayList<>(memberMap.values());
    }

    /**
     * 添加成员
     * @param guildPlayer
     */
    public void addMember(@NotNull GuildPlayer guildPlayer) {
        UUID uuid = guildPlayer.getUuid();

        if (isMember(guildPlayer)) {
            throw new IllegalArgumentException("成员已存在");
        }

        yaml.set("members." + uuid + ".position", GuildPosition.MEMBER.name());
        yaml.set("members." + uuid + ".join_time", System.currentTimeMillis());
        save();
        loadMembers();
    }

    /**
     * 删除成员
     * @param guildMember
     */
    public void removeMember(@NotNull GuildMember guildMember) {
        if (guildMember instanceof GuildOwner) {
            throw new IllegalArgumentException("不能删除会长成员");
        }

        if (!isMember(guildMember.getGuildPlayer())) {
            throw new IllegalArgumentException("成员不存在");
        }

        yaml.set("members." + guildMember.getUuid(), null);
        save();
        loadMembers();
        guildMember.getGuildPlayer().pointGuild(null);
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid && !deleted;
    }

    /**
     * 删除公会
     * @return
     */
    public void delete() {
        yaml.set("deleted", true);
        YamlUtil.saveYaml(yaml, file, StandardCharsets.UTF_8);
        this.deleted = true;
        getMembers().forEach(guildMember -> {
            guildMember.getReceivedRequests().forEach(Request::delete);
            guildMember.getGuildPlayer().pointGuild(null);
        });
        getSentRequests().forEach(Request::delete);
        getReceivedRequests().forEach(Request::delete);
        plugin.getGuildManager().unloadGuild(this);
        Bukkit.getPluginManager().callEvent(new GuildDeletedEvent(this));
    }

    public int getMaxMemberCount() {
        return MainSettings.getGuildDefaultMaxMemberCount() + getAdditionMemberCount();
    }

    /**
     * 得到最大成员数
     * @return
     */
    public int getAdditionMemberCount() {
        return this.additionMemberCount;
    }

    /**
     * 设置最大成员数
     * @param additionMemberCount
     * @return
     */
    public void setAdditionMemberCount(int additionMemberCount) {
        yaml.set("addition_member_count", additionMemberCount);
        save();
        this.additionMemberCount = additionMemberCount;
    }

    /**
     * 得到创建时间
     * @return
     */
    public long getCreateTime() {
        return createTime;
    }

    /**
     * 设置公告
     * @param announcements
     */
    public void setAnnouncements(@NotNull List<String> announcements) {
        yaml.set("announcements", announcements);
        save();
        this.announcements = announcements;
    }

    /**
     * 得到公告
     * @return
     */
    public List<String> getAnnouncements() {
        return new ArrayList<>(announcements);
    }

    /**
     * 得到yaml
     * @return
     */
    public YamlConfiguration getYaml() {
        return yaml;
    }

    /**
     * 保存文件
     */
    public void save() {
        YamlUtil.saveYaml(yaml, file, StandardCharsets.UTF_8);
    }

    /**
     * 得到公会等级权重值
     * @return
     */
    public int getRank() {
        String formula = PlaceholderText.replacePlaceholders(MainSettings.getGuildRankFormula(), new PlaceholderContainer().addGuildPlaceholders(this));

        try {
            return (int) Parser.parse(formula).evaluate();
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("公会等级计算公式不合法: " + formula);
        }
    }

    /**
     * 广播消息（在线成员）
     * @param message
     */
    public void broadcastMessage(String message) {
        for (GuildMember member : getMembers()) {
            if (member.isOnline()) {
                Util.sendMsg(member.getGuildPlayer().getBukkitPlayer(), message);
            }
        }
    }

    /**
     * 得到在线的成员
     * @return
     */
    public List<GuildMember> getOnlineMembers() {
        return getMembers().stream().filter(GuildMember::isOnline).collect(Collectors.toList());
    }

    /**
     * 得到在线的成员数量
     * @return
     */
    public int getOnlineMemberCount() {
        return getOnlineMembers().size();
    }
}
