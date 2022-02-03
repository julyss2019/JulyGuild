package com.github.julyss2019.mcsp.julyguild.command;

import com.github.julyss2019.mcsp.julyguild.logger.JulyGuildLogger;
import com.github.julyss2019.mcsp.julyguild.logger.LoggerLevel;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.commandv2.JulyCommand;
import com.github.julyss2019.mcsp.julylibrary.commandv2.MainCommand;
import com.github.julyss2019.mcsp.julylibrary.commandv2.SubCommand;
import org.bukkit.command.CommandSender;

@MainCommand(firstArg = "logger", description = "日志相关")
public class LoggerCommand implements JulyCommand {
	@SubCommand(firstArg = "debug", description = "切换 logger 级别为 DEBUG/INFO.", length = 0, permission = "JulyGuild.admin")
	public void toggle(CommandSender sender, String[] args) {
		if (JulyGuildLogger.getLevel() == LoggerLevel.DEBUG) {
			JulyGuildLogger.setLevel(LoggerLevel.INFO);
			Util.sendMsg(sender, "当前 logger 级别: INFO.");
		} else {
			JulyGuildLogger.setLevel(LoggerLevel.DEBUG);
			Util.sendMsg(sender, "当前 logger 级别: DEBUG.");
			Util.sendMsg(sender, "&e这会在后台显示更多的信息来帮助你排查错误.");
		}
	}
}
