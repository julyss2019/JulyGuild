package com.github.julyss2019.mcsp.julyguild;

import com.github.julyss2019.mcsp.julyguild.guild.member.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildPosition;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julylibrary.message.DateTimeUnit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;

public class LangHelper {
    public static class Global {
        public static DateTimeUnit getDateTimeUnit() {
            ConfigurationSection section = JulyGuild.inst().getLangYaml().getConfigurationSection("Global");

            return new DateTimeUnit(section.getString("year"), section.getString("month"), section.getString("day"), section.getString("hour"), section.getString("minute"), section.getString("second"));
        }

        public static SimpleDateFormat getDateTimeFormat() {
            return new SimpleDateFormat(JulyGuild.inst().getLangYaml().getString("Global.date_time_format"));
        }

        public static String getPrefix() {
            return JulyGuild.inst().getLangYaml().getString("Global.prefix");
        }

        public static String getNickName(@NotNull GuildMember guildMember) {
            ConfigurationSection langSection = JulyGuild.inst().getLangYaml();
            String format = langSection.getString("Global.nick_name");

            return PlaceholderText.replacePlaceholders(format, new PlaceholderContainer()
                    .add("PERMISSION", langSection.getString("Guild.Position." + guildMember.getPosition().name().toLowerCase()))
                    .add("NAME", guildMember.getName()));
        }

        public static String getPositionName(@NotNull GuildPosition guildPosition) {
            return JulyGuild.inst().getLangYaml().getString("Guild.Position." + guildPosition.name().toLowerCase());
        }
    }
}
