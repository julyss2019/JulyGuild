package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.DebugMessage;
import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.PriorityConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.PriorityItem;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildMemberSign;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildPermission;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildPosition;
import com.github.julyss2019.mcsp.julyguild.logger.JulyGuildLogger;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatInterceptor;
import com.github.julyss2019.mcsp.julylibrary.chat.ChatListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import com.github.julyss2019.mcsp.julylibrary.message.JulyText;
import com.github.julyss2019.mcsp.julylibrary.message.Title;
import com.github.julyss2019.mcsp.julylibrary.utilv2.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GuildMineGUI extends BasePlayerGUI {
    private final JulyGuild plugin = JulyGuild.inst();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildMineGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("GuildMineGUI");
    private final Player bukkitPlayer;
    private final GuildPosition guildPosition;
    private final GuildMember guildMember;
    private final GuildMemberSign guildMemberSign;
    private final Guild guild;

    public GuildMineGUI(@Nullable GUI lastGUI, @NotNull GuildMember guildMember) {
        super(lastGUI, Type.MINE, guildMember.getGuildPlayer());

        this.guildMember = guildMember;
        this.guildMemberSign = guildMember.getSign();
        this.bukkitPlayer = guildPlayer.getBukkitPlayer();
        this.guildPosition = guildMember.getPosition();
        this.guild = guildMember.getGuild();
    }

    @Override
    public Inventory createInventory() {
        PriorityConfigGUI.Builder guiBuilder = new PriorityConfigGUI.Builder();

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_BASIC);
        guiBuilder.fromConfig(thisGUISection, bukkitPlayer);
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_BASIC);

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.back");
        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (canBack()) {
                            back();
                        }
                    }
                });
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.back");

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.guild_info");
        guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_info"), bukkitPlayer, new PlaceholderContainer().addGuildPlaceholders(guild)));
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.guild_info");

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.self_info");
        guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.self_info"), bukkitPlayer, new PlaceholderContainer().addGuildMemberPlaceholders(guildMember)));
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.self_info");

        {
            String path = "items.guild_members." + ((guildMember.hasPermission(GuildPermission.MEMBER_KICK) || guildMember.hasPermission(GuildPermission.MANAGE_PERMISSION)) ? "manager" : "member");

            JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, path);
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection(path), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();
                    new GuildMemberListGUI(GuildMineGUI.this, guild, guildMember).open();
                }
            });
            JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, path);
        }

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.guild_donate");
        guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_donate"), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        close();
                        new GuildDonateGUI(GuildMineGUI.this, guildMember).open();
                    }
                });
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.guild_donate");

        // 公会主城
        {
            String path = "items.guild_spawn." + (guild.hasSpawn() ? "available" : "unavailable");

            JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, path);
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection(path), bukkitPlayer), guild.hasSpawn() ? new ItemListener() {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            executeGuildSpawn();
                        }
                    } : null);
            JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, path);
        }

        List<String> originalAnnouncements = guild.getAnnouncements().stream().map(s -> "§f" + s).collect(Collectors.toList());

        // 设置公会公告
        if (guildMember.hasPermission(GuildPermission.SET_ANNOUNCEMENTS)) {
            JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.guild_announcement.setter");
            PriorityItem priorityItem = GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_announcement.setter"), bukkitPlayer);

            List<String> lores = new ArrayList<>(originalAnnouncements);

            if (lores.size() == 0) {
                lores.addAll(MainSettings.getGuildAnnouncementDefault());
            }

            Optional.ofNullable(thisGUISection.getStringList("items.guild_announcement.setter.icon.append_lores")).orElse(new ArrayList<>()).forEach(s -> lores.add(PlaceholderText.replacePlaceholders(s, new PlaceholderContainer()
                    .add("split_str", MainSettings.getGuildAnnouncementSplitStr())
                    .add("max", MainSettings.getGuildAnnouncementMaxCount()))));

            priorityItem.getItemBuilder().lores(lores);
            guiBuilder.item(priorityItem, new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    executeSetGuildAnnouncement();
                }
            });
            JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.guild_announcement.setter");
            // 获取公会公告
        } else {
            JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.guild_announcement.getter");
            PriorityItem priorityItem = GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_announcement.getter"), bukkitPlayer);
            List<String> lores = new ArrayList<>(originalAnnouncements);

            if (lores.size() == 0) {
                lores.addAll(MainSettings.getGuildAnnouncementDefault());
            }

            priorityItem.getItemBuilder().lores(lores);
            guiBuilder.item(priorityItem);
            JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.guild_announcement.getter");
        }

        // 公会签到
        {
            boolean isSignedToday = guildMemberSign.isSignedToday();
            String path = "items.guild_sign." + (guildMemberSign.isSignedToday() ? "unavailable" : "available");

            JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, path);
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection(path), bukkitPlayer, new PlaceholderContainer()
                    .add("signed_count", guildMember.getSign().getSignedCount())), !isSignedToday ? new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    executeGuildSign();
                }
            } : null);
            JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, path);
        }

        // 公会商店
        if (guildMember.hasPermission(GuildPermission.USE_SHOP)) {
            JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.guild_shop");
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_shop"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    new GuildShopGUI(GuildMineGUI.this, guildMember).open();
                }
            });
            JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.guild_shop");
        }

        // 成员免伤
        if (guildMember.hasPermission(GuildPermission.SET_MEMBER_DAMAGE)) {
            String path = "items.guild_set_member_damage." + (guild.isMemberDamageEnabled() ? "turn_off" : "turn_on");

            JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, path);
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection(path), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    guild.setMemberDamageEnabled(!guild.isMemberDamageEnabled());
                    Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_set_member_damage." + (!guild.isMemberDamageEnabled() ? "turn_off" : "turn_on")));
                    reopen();
                }
            });
            JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, path);
        }

        // 入会审批
        if (guildMember.hasPermission(GuildPermission.PLAYER_JOIN_CHECK)) {
            JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.guild_join_check");
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_join_check"), bukkitPlayer), new ItemListener() {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            close();
                            new GuildJoinCheckGUI(GuildMineGUI.this, guildMember).open();
                        }
                    });
            JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.guild_join_check");
        }

        // 图标仓库
        if (guildMember.hasPermission(GuildPermission.USE_ICON_REPOSITORY)) {
            JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.guild_icon_repository");
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_icon_repository"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    close();
                    new GuildIconRepositoryGUI(GuildMineGUI.this, guildMember).open();
                }
            });
            JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.guild_icon_repository");
        }

        // 解散
        if (guildPosition == GuildPosition.OWNER) {
            JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.guild_delete");
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_delete"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    executeGuildDelete();
                }
            });
            JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.guild_delete");
        // 退出
        } else {
            JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.guild_leave");
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.guild_leave"), bukkitPlayer), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    executeGuildLeave();
                }
            });
            JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.guild_leave");
        }

        return guiBuilder.build();
    }

    private void executeSetGuildAnnouncement() {
        close();
        Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_announcement.set.input"), new PlaceholderContainer()
                .add("split_str", MainSettings.getGuildAnnouncementSplitStr())
                .add("max", MainSettings.getGuildAnnouncementMaxCount()));

        new ChatInterceptor.Builder().chatListener(new ChatListener() {
            @Override
            public void onChat(AsyncPlayerChatEvent event) {
                String msg = event.getMessage();

                if (msg.equalsIgnoreCase(MainSettings.getGuildAnnouncementInputCancelStr())) {
                    Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_announcement.set.cancelled"));
                    return;
                }

                String[] announcements = msg.split(MainSettings.getGuildAnnouncementSplitStr());

                if (announcements.length > MainSettings.getGuildAnnouncementMaxCount()) {
                    Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_announcement.set.max"), new PlaceholderContainer()
                            .add("len", announcements.length)
                            .add("max", MainSettings.getGuildAnnouncementMaxCount()));
                    return;
                }

                for (String announcement : announcements) {
                    Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_announcement.set.list"), new PlaceholderContainer()
                            .add("announcement", announcement));
                }

                Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_announcement.set.success"));
                guild.setAnnouncements(JulyText.getColoredTexts(Arrays.asList(announcements)));
            }
        }).player(bukkitPlayer).plugin(plugin).build().register();
    }

    private void executeGuildSign() {
        if (guildMemberSign.isSignedToday()) {
            reopen();
            return;
        }

        guildMemberSign.signToday();

        double gmoney = MainSettings.getGuildSignRewardGMoney();

        if (gmoney > 0) {
            guild.getGuildBank().deposit(GuildBank.BalanceType.GMONEY, gmoney);
            guildMember.addDonated(GuildBank.BalanceType.GMONEY, gmoney);
        }

        Optional.ofNullable(MainSettings.getGuildSignRewardCommands()).orElse(new ArrayList<>()).forEach(s -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("<player>", bukkitPlayer.getName()));
        });

        Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_sign.success"));
        reopen(40L);
    }

    private void executeGuildDelete() {
        close();
        Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_delete.confirm"), new PlaceholderContainer()
                .add("wait", MainSettings.getGuildDismissWait())
                .add("confirm_str", MainSettings.getGuildDismissConfirmStr()));
        new ChatInterceptor.Builder()
                .player(bukkitPlayer)
                .plugin(plugin)
                .timeout(MainSettings.getGuildDismissWait())
                .chatListener(new ChatListener() {
                    @Override
                    public void onChat(AsyncPlayerChatEvent event) {
                        if (ChatColor.stripColor(event.getMessage()).equalsIgnoreCase(MainSettings.getGuildDismissConfirmStr())) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (guild.isValid()) {
                                        guild.delete();
                                    }
                                }
                            }.runTask(plugin);

                            Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_delete.success"));
                        } else {
                            Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_delete.failed"));
                        }
                    }

                    @Override
                    public void onTimeout(AsyncPlayerChatEvent event) {
                        Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_delete.timeout"));
                    }
                }).build().register();
    }

    private void executeGuildLeave() {
        close();
        Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_leave.confirm"), new PlaceholderContainer()
                .add("wait", MainSettings.getGuildDismissWait())
                .add("confirm_str", MainSettings.getGuildDismissConfirmStr()));
        new ChatInterceptor.Builder()
                .player(bukkitPlayer)
                .plugin(plugin)
                .timeout(MainSettings.getGuildExitWait())
                .chatListener(new ChatListener() {
                    @Override
                    public void onChat(AsyncPlayerChatEvent event) {
                        if (ChatColor.stripColor(event.getMessage()).equalsIgnoreCase(MainSettings.getGuildExitConfirmStr())) {
                            guild.removeMember(guildMember);
                            Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_leave.success"));
                        } else {
                            Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_leave.failed"));
                        }
                    }

                    @Override
                    public void onTimeout(AsyncPlayerChatEvent event) {
                        Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_leave.timeout"));
                    }
                }).build().register();
    }

    private void executeGuildSpawn() {
        // 主城可能不存在了
        if (!guild.hasSpawn()) {
            reopen();
            return;
        }

        close();

        AtomicInteger atomicInteger = new AtomicInteger();
        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (atomicInteger.get() < MainSettings.getGuildSpawnTeleportWait()) {
                    if (JulyMessage.isTitleEnabled()) {
                        if (NMSUtil.compareVersion("v1_17_R1") >= 0) {
                            bukkitPlayer.sendTitle(
                                    com.github.julyss2019.mcsp.julylibrary.text.JulyText.getColoredText(PlaceholderText.replacePlaceholders(thisLangSection.getString("guild_spawn.count_down.title")
                                            , new PlaceholderContainer().add("count_down", MainSettings.getGuildSpawnTeleportWait() - atomicInteger.get())))
                                    , com.github.julyss2019.mcsp.julylibrary.text.JulyText.getColoredText(PlaceholderText.replacePlaceholders(thisLangSection.getString("guild_spawn.count_down.subtitle")
                                            , new PlaceholderContainer().add("count_down", MainSettings.getGuildSpawnTeleportWait() - atomicInteger.get()))), 0, 20, 20);
                        } else {
                            JulyMessage.sendTitle(bukkitPlayer, new Title.Builder()
                                    .text(PlaceholderText.replacePlaceholders(thisLangSection.getString("guild_spawn.count_down.title")
                                            , new PlaceholderContainer().add("count_down", MainSettings.getGuildSpawnTeleportWait() - atomicInteger.get())))
                                    .colored().build());
                            JulyMessage.sendTitle(bukkitPlayer, new Title.Builder()
                                    .type(Title.Type.SUBTITLE)
                                    .text(PlaceholderText.replacePlaceholders(thisLangSection.getString("guild_spawn.count_down.subtitle")
                                            , new PlaceholderContainer().add("count_down", MainSettings.getGuildSpawnTeleportWait() - atomicInteger.get())))
                                    .colored().build());
                        }
                    } else {
                        Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_spawn.count_down.msg"), new PlaceholderContainer().add("count_down", MainSettings.getGuildSpawnTeleportWait() - atomicInteger.get()));
                    }

                    atomicInteger.addAndGet(1);
                } else {
                    cancel();
                    bukkitPlayer.teleport(guild.getSpawn().getLocation());

                    if (JulyMessage.isTitleEnabled()) {
                        if (NMSUtil.compareVersion("v1_17_R1") >= 0) {
                            bukkitPlayer.sendTitle(
                                    com.github.julyss2019.mcsp.julylibrary.text.JulyText.getColoredText(thisLangSection.getString("guild_spawn.teleported.title"))
                                    , com.github.julyss2019.mcsp.julylibrary.text.JulyText.getColoredText(thisLangSection.getString("guild_spawn.teleported.subtitle"))
                                    , 0
                                    , 20
                                    , 20
                            );
                        } else {
                            JulyMessage.sendTitle(bukkitPlayer, new Title.Builder().text(thisLangSection.getString("guild_spawn.teleported.title")).colored().build());
                            JulyMessage.sendTitle(bukkitPlayer, new Title.Builder().type(Title.Type.SUBTITLE).text(thisLangSection.getString("guild_spawn.teleported.subtitle")).colored().build());
                        }
                    } else {
                        Util.sendMsg(bukkitPlayer, thisLangSection.getString("guild_spawn.teleported.msg"));
                    }

                    guildPlayer.setTeleportTask(null);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

        guildPlayer.setTeleportTask(bukkitTask);
    }

    @Override
    public boolean canUse() {
        return guildMember.isValid();
    }
}
