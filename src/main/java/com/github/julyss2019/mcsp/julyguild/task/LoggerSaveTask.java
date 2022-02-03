package com.github.julyss2019.mcsp.julyguild.task;

import com.github.julyss2019.mcsp.julyguild.logger.JulyGuildLogger;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

public class LoggerSaveTask extends BukkitRunnable {
	@Override
	public void run() {

		JulyGuildLogger.flushWriter();
	}
}
