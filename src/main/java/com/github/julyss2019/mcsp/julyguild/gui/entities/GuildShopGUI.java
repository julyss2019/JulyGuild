package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.DebugMessage;
import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.LangHelper;
import com.github.julyss2019.mcsp.julyguild.config.Shop;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.ShopItemConfirmGUI;
import com.github.julyss2019.mcsp.julyguild.guild.*;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildPermission;
import com.github.julyss2019.mcsp.julyguild.logger.JulyGuildLogger;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.request.entities.TpAllRequest;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import com.github.julyss2019.mcsp.julylibrary.message.JulyText;
import com.github.julyss2019.mcsp.julylibrary.message.Title;
import com.github.julyss2019.mcsp.julylibrary.utilv2.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class GuildShopGUI extends BasePlayerGUI {
    private enum RewardType {
        GUILD_SET_SPAWN, GUILD_UPGRADE, GUILD_TP_ALL, GUILD_SHOP, GUILD_ICON, BACK, COMMANDS, NONE
    }

    private GuildMember guildMember;
    private String shopName;
    private YamlConfiguration yml;
    private Guild guild;
    private JulyGuild plugin = JulyGuild.inst();
    private ConfigurationSection tpAllLangSection = plugin.getLangYaml().getConfigurationSection("TpAll");
    private ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("Shop");
    private Player bukkitPlayer = getBukkitPlayer();

    /**
     * 使用引导Shop YAML
     * @param lastGUI
     * @param guildMember
     */
    protected GuildShopGUI(@Nullable GUI lastGUI, @NotNull GuildMember guildMember) {
        this(lastGUI, guildMember, Optional.ofNullable(JulyGuild.inst().getShop(MainSettings.getGuildShopLauncher())).orElseThrow(() -> new RuntimeException("引导商店不存在")));
    }

    protected GuildShopGUI(@Nullable GUI lastGUI, @NotNull GuildMember guildMember, @NotNull Shop shop) {
        super(lastGUI, Type.SHOP, guildMember.getGuildPlayer());

        this.guildMember = guildMember;
        this.yml = shop.getYaml();
        this.shopName = shop.getName();
        this.guild = guildMember.getGuild();
    }

    @Override
    public boolean canUse() {
        return guildMember.isValid() && guildMember.hasPermission(GuildPermission.USE_SHOP);
    }

    @Override
    public Inventory createInventory() {
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder();

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_BASIC);
        guiBuilder.fromConfig(yml, bukkitPlayer);
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_BASIC);

        JulyGuildLogger.debug("尝试载入商店 " + shopName + ".");
        if (yml.contains("items")) {
            for (String shopItemName : yml.getConfigurationSection("items").getKeys(false)) {
                ConfigurationSection shopItemSection = yml.getConfigurationSection("items").getConfigurationSection(shopItemName);
                String rewardTypeStr = shopItemSection.getString("reward_type");

                JulyGuildLogger.debug("尝试获取物品类型 '" + shopItemSection.getCurrentPath() + "'.");
                RewardType rewardType = RewardType.valueOf(rewardTypeStr);
                JulyGuildLogger.debug("获取物品类型完毕 '" + shopItemSection.getCurrentPath() + "'.");

                switch (rewardType) {
                    case NONE:
                        setNoneReward(shopItemSection, guiBuilder);
                        break;
                    case COMMANDS:
                        setCommandReward(shopItemSection, guiBuilder);
                        break;
                    case GUILD_SET_SPAWN:
                        setSetGuildSpawnReward(shopItemSection, guiBuilder);
                        break;
                    case GUILD_UPGRADE:
                        setGuildUpgradeReward(shopItemSection, guiBuilder);
                        break;
                    case BACK:
                        setBackReward(shopItemSection, guiBuilder);
                        break;
                    case GUILD_SHOP:
                        setShopReward(shopItemSection, guiBuilder);
                        break;
                    case GUILD_ICON:
                        setGuildIconReward(shopItemSection, guiBuilder);
                        break;
                    case GUILD_TP_ALL:
                        setGuildTpAllReward(shopItemSection, guiBuilder);
                        break;
                }

            }
        }

        JulyGuildLogger.debug("载入商店 " + shopName + " 完毕.");
        return guiBuilder.build();
    }

    private void setNoneReward(@NotNull ConfigurationSection shopItemSection, @NotNull IndexConfigGUI.Builder guiBuilder) {
        guiBuilder.item(shopItemSection.getInt("index") - 1, GUIItemManager.getItemBuilder(shopItemSection.getConfigurationSection("icon"), bukkitPlayer).build());
    }

    /**
     * 命令
     * @param shopItemSection
     * @param guiBuilder
     */
    private void setCommandReward(@NotNull ConfigurationSection shopItemSection, @NotNull IndexConfigGUI.Builder guiBuilder) {
        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, shopItemSection.getCurrentPath());
        ConfigurationSection sellSection = shopItemSection.getConfigurationSection("sell");
        double price = sellSection.getDouble("price");

        guiBuilder.item(GUIItemManager.getIndexItem(shopItemSection, bukkitPlayer, new PlaceholderContainer()
                .add("price", price)), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                if (checkGuildMoneyOrNotify(price)) {
                    new ShopItemConfirmGUI(GuildShopGUI.this, guildMember, sellSection.getConfigurationSection("ConfirmGUI"), new PlaceholderContainer().add("price", price), price) {
                        @Override
                        public void onPaid() {
                            sellSection.getStringList("commands").forEach(s -> {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s
                                        .replace("<player>", bukkitPlayer.getName()));
                            });

                            Util.sendMsg(getBukkitPlayer(), PlaceholderText.replacePlaceholders(sellSection.getString("success_message"), new PlaceholderContainer()
                                    .add("price", price)));
                            back(40L);
                        }
                    }.open();
                }
            }
        });
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, shopItemSection.getCurrentPath());
    }

    /**
     * 全员传送
     * @param shopItemSection
     * @param guiBuilder
     */
    private void setGuildTpAllReward(@NotNull ConfigurationSection shopItemSection, @NotNull IndexConfigGUI.Builder guiBuilder) {
        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, shopItemSection.getCurrentPath());
        ConfigurationSection sellSection = shopItemSection.getConfigurationSection("sell");
        double price = sellSection.getDouble("price");

        guiBuilder.item(GUIItemManager.getIndexItem(shopItemSection, bukkitPlayer, new PlaceholderContainer()
                .add("price", price)), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                if (checkGuildMoneyOrNotify(price)) {
                    // 必须有1个成员在线（不包括自己）
                    if (guild.getOnlineMembers().size() <= 1) {
                        Util.sendMsg(bukkitPlayer, tpAllLangSection.getString("no_available_players"));
                        reopen(40L);
                        return;
                    }

                    if (!MainSettings.getGuildTpAllSendWorlds().contains(bukkitPlayer.getWorld().getName())) {
                        Util.sendMsg(bukkitPlayer, tpAllLangSection.getString("no_send_world"));
                        reopen(40L);
                        return;
                    }

                    new ShopItemConfirmGUI(GuildShopGUI.this, guildMember, sellSection.getConfigurationSection("ConfirmGUI"), new PlaceholderContainer().add("price", price), price) {
                        @Override
                        public boolean canUse() {
                            return super.canUse()
                                    && getGuild().getOnlineMembers().size() > 1
                                    && MainSettings.getGuildTpAllSendWorlds().contains(guildMember.getGuildPlayer().getBukkitPlayer().getWorld().getName());
                        }

                        @Override
                        public void onPaid() {
                            List<GuildMember> receiverGuildMembers = guild.getMembers();

                            receiverGuildMembers.remove(guildMember); // 删除自己

                            receiverGuildMembers.forEach(receiverGuildMember -> {
                                Player receiverBukkitPlayer = receiverGuildMember.getGuildPlayer().getBukkitPlayer();

                                if (receiverBukkitPlayer == null) {
                                    return;
                                }

                                new TpAllRequest(guildMember, receiverGuildMember, bukkitPlayer.getLocation()).send();

                                if (JulyMessage.isTitleEnabled()) {
                                    if (NMSUtil.compareVersion("v1_17_R1") >= 0) {
                                        receiverBukkitPlayer.sendTitle(
                                                com.github.julyss2019.mcsp.julylibrary.text.JulyText.getColoredText(tpAllLangSection.getString("received.title"))
                                                , com.github.julyss2019.mcsp.julylibrary.text.JulyText.getColoredText(tpAllLangSection.getString("received.subtitle"))
                                                , 0
                                                , 20
                                                , 20
                                        );
                                    } else {
                                        new Title.Builder().text(tpAllLangSection.getString("received.title")).build().send(receiverBukkitPlayer);
                                        new Title.Builder().type(Title.Type.SUBTITLE).text(tpAllLangSection.getString("received.subtitle")).build().send(receiverBukkitPlayer);
                                    }
                                } else {
                                    Util.sendMsg(receiverBukkitPlayer, tpAllLangSection.getString("received.msg"), new PlaceholderContainer()
                                            .add("sender", guildMember.getName())
                                            .add("timeout", JulyText.secondToStr(MainSettings.getGuildTpAllTimeout(), LangHelper.Global.getDateTimeUnit()))
                                            .add("shift_count", MainSettings.getGuildTpAllSneakCount()));
                                }
                            });

                            Util.sendMsg(getBukkitPlayer(), PlaceholderText.replacePlaceholders(sellSection.getString("success_message"), new PlaceholderContainer()
                                    .add("count", receiverGuildMembers.size())
                                    .add("price", price)));
                            close();
                        }
                    }.open();
                }
            }
        });
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, shopItemSection.getCurrentPath());
    }

    /**
     * 公会图标
     * @param shopItemSection
     * @param guiBuilder
     */
    private void setGuildIconReward(@NotNull ConfigurationSection shopItemSection, @NotNull IndexConfigGUI.Builder guiBuilder) {
        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, shopItemSection.getCurrentPath());
        ConfigurationSection sellSection = shopItemSection.getConfigurationSection("sell");
        double price = sellSection.getDouble("price");

        guiBuilder.item(GUIItemManager.getIndexItem(shopItemSection, bukkitPlayer, new PlaceholderContainer()
                .add("price", price)), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                if (checkGuildMoneyOrNotify(price)) {
                    new ShopItemConfirmGUI(GuildShopGUI.this, guildMember, sellSection.getConfigurationSection("ConfirmGUI"), new PlaceholderContainer().add("price", price), price) {
                        @Override
                        public void onPaid() {
                            ConfigurationSection guildIconSection = sellSection.getConfigurationSection("guild_icon");

                            GuildIcon guildIcon = getGuild().giveIcon(Material.valueOf(guildIconSection.getString("material")), (short) guildIconSection.getInt("durability"), guildIconSection.getString("first_lore"), guildIconSection.getString("display_name"));

                            getGuild().setCurrentIcon(guildIcon);
                            Util.sendMsg(getBukkitPlayer(), PlaceholderText.replacePlaceholders(sellSection.getString("success_message"), new PlaceholderContainer()
                                    .add("display_name", guildIcon.getDisplayName())
                                    .add("price", price)));
                            back(40L);
                        }
                    }.open();
                }
            }
        });
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, shopItemSection.getCurrentPath());
    }

    /**
     * 设置商店奖励
     * @param shopItemSection
     * @param guiBuilder
     */
    private void setShopReward(@NotNull ConfigurationSection shopItemSection, @NotNull IndexConfigGUI.Builder guiBuilder) {
        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, shopItemSection.getCurrentPath());
        guiBuilder.item(GUIItemManager.getIndexItem(shopItemSection, bukkitPlayer), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                close();
                new GuildShopGUI(lastGUI, guildMember, plugin.getShop(shopItemSection.getString("shop"))).open();
            }
        });
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, shopItemSection.getCurrentPath());
    }

    /**
     * 设置返回奖励
     * @param shopItemSection
     * @param guiBuilder
     */
    private void setBackReward(@NotNull ConfigurationSection shopItemSection, @NotNull IndexConfigGUI.Builder guiBuilder) {
        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, shopItemSection.getCurrentPath());
        guiBuilder.item(GUIItemManager.getIndexItem(shopItemSection, bukkitPlayer), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                back();
            }
        });
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, shopItemSection.getCurrentPath());
    }

    /**
     * 设置公会升级奖励
     * @param shopItemSection
     * @param guiBuilder
     */
    private void setGuildUpgradeReward(@NotNull ConfigurationSection shopItemSection, @NotNull IndexConfigGUI.Builder guiBuilder) {
        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, shopItemSection.getCurrentPath());
        boolean available = guild.getMaxMemberCount() + 1 <= MainSettings.getGuildUpgradeMaxMemberCount();
        ConfigurationSection subItemSection = shopItemSection.getConfigurationSection(available ? "available" : "unavailable");

        if (available) {
            ConfigurationSection sellSection = shopItemSection.getConfigurationSection("sell");
            double price = sellSection.getDouble("price");

            JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, subItemSection.getCurrentPath());
            guiBuilder.item(GUIItemManager.getIndexItem(subItemSection, bukkitPlayer, new PlaceholderContainer()
                    .add("old", guild.getMaxMemberCount())
                    .add("new", guild.getMaxMemberCount() + 1)
                    .add("price", price)), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (checkGuildMoneyOrNotify(price)) {
                        new ShopItemConfirmGUI(GuildShopGUI.this, guildMember, sellSection.getConfigurationSection("ConfirmGUI"), new PlaceholderContainer().add("price", price), price) {
                            @Override
                            public boolean canUse() {
                                return super.canUse() && getGuild().getMaxMemberCount() + 1 <= MainSettings.getGuildUpgradeMaxMemberCount();
                            }

                            @Override
                            public void onPaid() {
                                getGuild().setAdditionMemberCount(getGuild().getAdditionMemberCount() + 1);
                                Util.sendMsg(getBukkitPlayer(), PlaceholderText.replacePlaceholders(sellSection.getString("success_message"), new PlaceholderContainer()
                                        .add("old", guild.getMaxMemberCount() - 1)
                                        .add("new", guild.getMaxMemberCount())
                                        .add("price", price)));
                                back(40L);
                            }
                        }.open();
                    }
                }
            });
            JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, subItemSection.getCurrentPath());
        } else {
            JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, subItemSection.getCurrentPath());
            guiBuilder.item(GUIItemManager.getIndexItem(subItemSection, bukkitPlayer));
            JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, subItemSection.getCurrentPath());
        }
        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, shopItemSection.getCurrentPath());
    }

    /**
     * 设置设置公会主城奖励
     * @param shopItemSection
     * @param guiBuilder
     */
    private void setSetGuildSpawnReward(@NotNull ConfigurationSection shopItemSection, @NotNull IndexConfigGUI.Builder guiBuilder) {
        ConfigurationSection sellSection = shopItemSection.getConfigurationSection("sell");
        double price = sellSection.getDouble("price");

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, shopItemSection.getCurrentPath());
        guiBuilder.item(GUIItemManager.getIndexItem(shopItemSection, bukkitPlayer, new PlaceholderContainer()
                .add("price", price)), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                if (checkGuildMoneyOrNotify(price)) {
                    new ShopItemConfirmGUI(GuildShopGUI.this, guildMember, sellSection.getConfigurationSection("ConfirmGUI"), new PlaceholderContainer().add("price", price), price) {
                        @Override
                        public void onPaid() {
                            getGuild().setSpawn(getBukkitPlayer().getLocation());
                            Util.sendMsg(getBukkitPlayer(), PlaceholderText.replacePlaceholders(sellSection.getString("success_message"), new PlaceholderContainer().add("price", Util.SIMPLE_DECIMAL_FORMAT.format(price))));
                            back(40L);
                        }
                    }.open();
                }
            }
        });
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, shopItemSection.getCurrentPath());
    }

    /**
     * 检查金币是否足够，不够则提示并返回false，否则返回true
     * @param price
     * @return
     */
    private boolean checkGuildMoneyOrNotify(double price) {
        if (!guild.getGuildBank().has(GuildBank.BalanceType.GMONEY, price)) {
            Util.sendMsg(bukkitPlayer, thisLangSection.getString("gmoney_not_enough"), new PlaceholderContainer()
                    .add("need", new BigDecimal(price).subtract(guild.getGuildBank().getBalance(GuildBank.BalanceType.GMONEY))));
            reopen(40L);
            return false;
        }

        return true;
    }
}
