package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.DebugMessage;
import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.LangHelper;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.BasePageableGUI;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildMember;
import com.github.julyss2019.mcsp.julyguild.logger.JulyGuildLogger;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.request.Request;
import com.github.julyss2019.mcsp.julyguild.request.RequestManager;
import com.github.julyss2019.mcsp.julyguild.request.entities.JoinRequest;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuildJoinCheckGUI extends BasePageableGUI {
    private final JulyGuild plugin = JulyGuild.inst();
    private final RequestManager requestManager = plugin.getRequestManager();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildJoinCheckGUI");
    private final ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("GuildJoinCheckGUI");
    private final Player bukkitPlayer = getBukkitPlayer();
    private List<Integer> itemIndexes; // 请求物品位置
    private int itemIndexCount; // 请求物品位置数量
    private final GuildMember guildMember;
    private final Guild guild;

    private List<Request> requests;
    private int requestCount;

    public GuildJoinCheckGUI(@Nullable GUI lastGUI, @NotNull GuildMember guildMember) {
        super(lastGUI, Type.PLAYER_JOIN_CHECK, guildMember.getGuildPlayer());

        this.guildMember = guildMember;
        this.guild = guildMember.getGuild();

        JulyGuildLogger.debug("开始: 加载 'items.request.indexes'.");
        this.itemIndexes = Util.getIndexes(thisGUISection.getString("items.request.indexes"));
        JulyGuildLogger.debug("结束: 加载 'items.request.indexes'.");

        this.itemIndexCount = itemIndexes.size();
    }

    @Override
    public void update() {
        this.requests = guild.getReceivedRequests().stream().filter(request -> request.getType() == Request.Type.JOIN).collect(Collectors.toList());
        this.requestCount = requests.size();

        setPageCount(requestCount % itemIndexCount == 0 ? requestCount / itemIndexCount : requestCount / itemIndexCount + 1);
    }

    @Override
    public Inventory createInventory() {
        Map<Integer, Request> indexMap = new HashMap<>();
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder();

        guiBuilder.listener(new InventoryListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                int index = event.getRawSlot();

                if (indexMap.containsKey(index)) {
                    Request request = indexMap.get(index);
                    GuildPlayer sender = (GuildPlayer) request.getSender();
                    InventoryAction action = event.getAction();

                    if (!request.isValid()) {
                        Util.sendMsg(bukkitPlayer, thisLangSection.getString("invalid"));
                        reopen(20L);
                        return;
                    }

                    if (action == InventoryAction.PICKUP_ALL) {
                        requestManager.deleteRequest(request);
                        guild.addMember(sender);

                        guild.broadcastMessage(PlaceholderText.replacePlaceholders(thisLangSection.getString("accept.broadcast"), new PlaceholderContainer()
                                .add("player", sender.getName())));
                        reopen(20L);
                        return;
                    }

                    if (action == InventoryAction.PICKUP_HALF) {
                        requestManager.deleteRequest(request);
                        Util.sendMsg(bukkitPlayer, PlaceholderText.replacePlaceholders(thisLangSection.getString("deny.approver"), new PlaceholderContainer()
                                .add("player", sender.getName())));
                        reopen(20L);
                    }
                }
            }
        });

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_BASIC);
        guiBuilder.fromConfig(thisGUISection, bukkitPlayer, new PlaceholderContainer()
                        .add("page", getCurrentPage() + 1)
                        .add("total_page", getPageCount()));
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_BASIC);

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.page_items");
        guiBuilder.pageItems(thisGUISection.getConfigurationSection("items.page_items"), this);
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.page_items");

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

        if (getPageCount() > 0) {
            int requestCounter = getCurrentPage() * itemIndexes.size();
            int loopCount = requestCount - requestCounter < itemIndexCount ? requestCount - requestCounter : itemIndexCount; // 循环次数，根据当前能够显示的数量决定

            for (int i = 0; i < loopCount; i++) {
                JoinRequest request = (JoinRequest) requests.get(requestCounter++);
                GuildPlayer sender = request.getSender();

                JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.request.icon");
                ItemBuilder itemBuilder = GUIItemManager.getItemBuilder(thisGUISection.getConfigurationSection("items.request.icon"), sender.getOfflineBukkitPlayer(), new PlaceholderContainer()
                        .add("sender_name", sender.getName())
                        .add("send_time", LangHelper.Global.getDateTimeFormat().format(request.getCreationTime())));
                JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.request.icon");

                guiBuilder.item(itemIndexes.get(i), itemBuilder.build());
                indexMap.put(itemIndexes.get(i), request);
            }
        }

        return guiBuilder.build();
    }

    @Override
    public boolean canUse() {
        return guildMember.isValid();
    }
}
