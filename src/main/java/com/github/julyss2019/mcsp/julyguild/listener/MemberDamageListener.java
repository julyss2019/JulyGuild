package com.github.julyss2019.mcsp.julyguild.listener;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 成员免伤
 */
public class MemberDamageListener implements Listener {
    private JulyGuild plugin = JulyGuild.inst();
    private ConfigurationSection langSection = plugin.getLangYaml();
    private GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();
    private Map<UUID, Long> msgIntervalMap = new HashMap<>();

    @EventHandler
    public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity target = event.getEntity();

        if (damager.getType() != EntityType.PLAYER || target.getType() != EntityType.PLAYER) {
            return;
        }

        Player damagerBukkitPlayer = (Player) damager;
        GuildPlayer damagerGuildPlayer = guildPlayerManager.getGuildPlayer(damagerBukkitPlayer);

        if (!damagerGuildPlayer.isInGuild()) {
            return;
        }

        Guild guild = damagerGuildPlayer.getGuild();

        if (!guild.isMember(target.getUniqueId())) {
            return;
        }

        UUID damagerUuid = damagerBukkitPlayer.getUniqueId();

        // 开启了成员免伤
        if (!guild.isMemberDamageEnabled()) {
            event.setCancelled(true);

            if (!msgIntervalMap.containsKey(damagerUuid) || (System.currentTimeMillis() - msgIntervalMap.get(damagerUuid)) / 1000L > MainSettings.getGuildMemberDamageDisableNoticeInterval()) {
                Util.sendMsg(damagerBukkitPlayer, langSection.getString("Guild.member_damage_disabled"));
                msgIntervalMap.put(damagerUuid, System.currentTimeMillis());
            }
        }
    }
}
