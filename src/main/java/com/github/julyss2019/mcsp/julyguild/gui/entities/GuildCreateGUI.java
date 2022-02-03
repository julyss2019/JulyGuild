package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.DebugMessage;
import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.gui.BaseConfirmGUI;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.guild.GuildManager;
import com.github.julyss2019.mcsp.julyguild.logger.JulyGuildLogger;
import com.github.julyss2019.mcsp.julyguild.thirdparty.economy.PlayerPointsEconomy;
import com.github.julyss2019.mcsp.julyguild.thirdparty.economy.VaultEconomy;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import com.github.julyss2019.mcsp.julylibrary.message.Title;
import com.github.julyss2019.mcsp.julylibrary.text.JulyText;
import com.github.julyss2019.mcsp.julylibrary.utilv2.NMSUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildCreateGUI extends BasePlayerGUI {
    private final String guildName;
    private final Player bukkitPlayer = getBukkitPlayer();
    private final JulyGuild plugin = JulyGuild.inst();
    private final GuildManager guildManager = plugin.getGuildManager();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildCreateGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("GuildCreateGUI");

    private final PlayerPointsEconomy playerPointsEconomy = plugin.getPlayerPointsEconomy();
    private final VaultEconomy vaultEconomy = plugin.getVaultEconomy();

    protected GuildCreateGUI(@Nullable GUI lastGUI, @NotNull GuildPlayer guildPlayer, @NotNull String guildName) {
        super(lastGUI, Type.CREATE, guildPlayer);

        this.guildName = guildName;
    }

    /**
     * 不在公会就允许使用
     * @return
     */
    @Override
    public boolean canUse() {
        return !guildPlayer.isInGuild();
    }

    @Override
    public Inventory createInventory() {
        PlaceholderContainer moneyPlaceholderContainer = new PlaceholderContainer()
                .add("name", guildName)
                .add("guild_name", guildName)
                .add("price", MainSettings.getGuildCreatePriceMoneyAmount())
                .add("cost", MainSettings.getGuildCreatePriceMoneyAmount())
                .add("money_cost", MainSettings.getGuildCreatePriceMoneyAmount());
        PlaceholderContainer pointsPlaceholderContainer = new PlaceholderContainer()
                .add("name", guildName)
                .add("guild_name", guildName)
                .add("price", MainSettings.getGuildCreatePricePointsAmount())
                .add("cost", MainSettings.getGuildCreatePricePointsAmount())
                .add("points_cost", MainSettings.getGuildCreatePricePointsAmount());

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_BASIC);

        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder()
                .fromConfig(thisGUISection, bukkitPlayer);

        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_BASIC);

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.money");
        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.money"), bukkitPlayer, moneyPlaceholderContainer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (!vaultEconomy.has(bukkitPlayer, MainSettings.getGuildCreatePriceMoneyAmount())) {
                            Util.sendMsg(bukkitPlayer, PlaceholderText.replacePlaceholders(thisLangSection.getString("money.not_enough"), new PlaceholderContainer()
                                    .add("need", MainSettings.getGuildCreatePriceMoneyAmount() - vaultEconomy.getBalance(bukkitPlayer))));
                            reopen(60L);
                            return;
                        }


                        new BaseConfirmGUI(GuildCreateGUI.this, guildPlayer, thisGUISection.getConfigurationSection("items.money.ConfirmGUI"), moneyPlaceholderContainer) {
                            @Override
                            public boolean canUse() {
                                return !guildPlayer.isInGuild() && plugin.isVaultEconomyHooked() && vaultEconomy.has(bukkitPlayer, MainSettings.getGuildCreatePriceMoneyAmount());
                            }

                            @Override
                            public void onConfirm() {
                                close();
                                vaultEconomy.withdraw(bukkitPlayer, MainSettings.getGuildCreatePriceMoneyAmount());
                                createGuild(guildPlayer, guildName);
                            }

                            @Override
                            public void onCancel() {
                                back();
                            }
                        }.open();
                    }
                });
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.money");

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.points");
        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.points"), bukkitPlayer, pointsPlaceholderContainer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (playerPointsEconomy == null) {
                            Util.sendMsg(bukkitPlayer, "&cPlayerPoints 未启用.");
                            reopen(40L);
                            return;
                        }

                        if (!playerPointsEconomy.has(bukkitPlayer, MainSettings.getGuildCreatePricePointsAmount())) {
                            Util.sendMsg(bukkitPlayer, PlaceholderText.replacePlaceholders(thisLangSection.getString("points.not_enough"), new PlaceholderContainer()
                                    .add("need", String.valueOf(MainSettings.getGuildCreatePricePointsAmount() - playerPointsEconomy.getBalance(bukkitPlayer)))));
                            reopen(40L);
                            return;
                        }

                        new BaseConfirmGUI(GuildCreateGUI.this, guildPlayer, plugin.getGUIYaml("GuildCreateGUI").getConfigurationSection("items.points.ConfirmGUI"), pointsPlaceholderContainer) {
                            @Override
                            public boolean canUse() {
                                return !guildPlayer.isInGuild() && plugin.isPlayerPointsHooked() && playerPointsEconomy.has(bukkitPlayer, MainSettings.getGuildCreatePricePointsAmount());
                            }

                            @Override
                            public void onConfirm() {
                                close();
                                playerPointsEconomy.withdraw(bukkitPlayer, MainSettings.getGuildCreatePricePointsAmount());
                                createGuild(guildPlayer, guildName);
                            }

                            @Override
                            public void onCancel() {
                                back();
                            }
                        }.open();
                    }
                });
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.points");

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.back");
        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer), new ItemListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (canBack()) {
                            back();
                        }
                    }
                });
        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.back");

        return guiBuilder.build();
    }

    private void createGuild(GuildPlayer guildPlayer, String guildName) {
        Player bukkitPlayer = guildPlayer.getBukkitPlayer();
        PlaceholderContainer placeholderContainer = new PlaceholderContainer()
                .add("player", bukkitPlayer.getName())
                .add("guild_name", guildName);

        guildManager.createGuild(guildPlayer, guildName);
        JulyMessage.broadcastColoredMessage(PlaceholderText.replacePlaceholders(thisLangSection.getString("success.broadcast"), placeholderContainer));

        if (JulyMessage.isTitleEnabled()) {
            if (NMSUtil.compareVersion("v1_17_R1") >= 0) {
                bukkitPlayer.sendTitle(JulyText.getColoredText(PlaceholderText.replacePlaceholders(thisLangSection.getString("success.title"), placeholderContainer))
                        , JulyText.getColoredText(PlaceholderText.replacePlaceholders(thisLangSection.getString("success.subtitle"), placeholderContainer)), 0, 20, 20);
            } else {
                JulyMessage.sendTitle(bukkitPlayer, new Title.Builder().text(PlaceholderText.replacePlaceholders(thisLangSection.getString("success.title"), placeholderContainer)).colored().build());
                JulyMessage.sendTitle(bukkitPlayer, new Title.Builder().type(Title.Type.SUBTITLE).text(PlaceholderText.replacePlaceholders(thisLangSection.getString("success.subtitle"), placeholderContainer)).colored().build());
            }
        } else {
            Util.sendMsg(bukkitPlayer, thisLangSection.getString("success.msg"));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                new MainGUI(guildPlayer).open();
            }
        }.runTaskLater(plugin, 20L * 3L);
    }
}
