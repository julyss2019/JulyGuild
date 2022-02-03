package com.github.julyss2019.mcsp.julyguild.command;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.commandv2.JulyCommand;
import com.github.julyss2019.mcsp.julylibrary.commandv2.MainCommand;
import com.github.julyss2019.mcsp.julylibrary.commandv2.SubCommand;
import org.bukkit.command.CommandSender;

@MainCommand(firstArg = "plugin", description = "插件相关")
public class PluginCommand implements JulyCommand {
    private JulyGuild plugin = JulyGuild.inst();

    @SubCommand(firstArg = "reload", description = "重载插件配置", length = 0, permission = "JulyGuild.admin")
    public void onReload(CommandSender cs, String[] args) {
        plugin.reloadPlugin();
        Util.sendMsg(cs, "&f配置重载完毕.");
    }

    @SubCommand(firstArg = "version", description = "插件版本", length = 0)
    public void onVersion(CommandSender cs, String[] args) {
        Util.sendMsg(cs, "&f插件版本: v" + JulyGuild.inst().getDescription().getVersion() + ".");
        Util.sendMsg(cs, "&f插件交流群: 786184610.");
    }
}
