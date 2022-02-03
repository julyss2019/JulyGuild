package com.github.julyss2019.mcsp.julyguild.logger;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JulyGuildLogger {
	public static void init() {
		File logFolder = new File(JulyGuild.inst().getDataFolder(), "logs");

		if (!logFolder.exists() && !logFolder.mkdirs()) {
			throw new RuntimeException("创建文件夹失败: " + logFolder.getAbsolutePath() + ".");
		}
	}

	private static LoggerLevel level = LoggerLevel.INFO;

	private static String lastFileName;
	private static FileWriter fileWriter;
	private static BufferedWriter bufferedWriter;

	public static LoggerLevel getLevel() {
		return level;
	}

	public static void setLevel(@NotNull LoggerLevel level) {
		JulyGuildLogger.level = level;
	}

	public static void debug(@NotNull String msg) {
		log(LoggerLevel.DEBUG,  msg);
	}

	public static void debug(@NotNull String msg, @NotNull Object... args) {
		log(LoggerLevel.DEBUG, String.format(msg, args));
	}

	public static void info(@NotNull String msg) {
		log(LoggerLevel.INFO, msg);
	}

	public static void warning(@NotNull String msg) {
		log(LoggerLevel.WARNING, msg);
	}

	public static void error(@NotNull String msg) {
		error(msg,null);
	}

	public static void error(@NotNull String msg, @Nullable RuntimeException e) {
		log(LoggerLevel.ERROR, msg);

		if (e != null) {
			throw e;
		}
	}

	private static void log(@NotNull LoggerLevel loggerLevel, @NotNull String msg) {
		if (loggerLevel.getLevel() >= JulyGuildLogger.level.getLevel()) {
			Util.sendConsoleMsg("§" + loggerLevel.color + "[" + loggerLevel.name() + "] " + msg);
			saveToDisk("[" + Util.HH_MM_SS_FORMAT.format(System.currentTimeMillis()) + "] [" + loggerLevel.name() + "] " + msg);
		}
	}

	public static boolean isWriterEnabled() {
		return bufferedWriter != null;
	}

	public static void flushWriter() {
		try {
			bufferedWriter.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void closeWriters() {
		if (fileWriter == null || bufferedWriter == null) {
			throw new RuntimeException("writer 不存在");
		}

		try {
			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		lastFileName = null;
		fileWriter = null;
		bufferedWriter = null;
	}

	private static void setNewWriter(@NotNull String fileName) {
		File file = new File(JulyGuild.inst().getDataFolder(), "logs" + File.separator + fileName + ".log");

		try {
			fileWriter = new FileWriter(file, true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		bufferedWriter = new BufferedWriter(fileWriter);
		lastFileName = fileName;
	}

	private static void saveToDisk(@NotNull String msg) {
		String newName = Util.YYYY_MM_DD_FORMAT.format(System.currentTimeMillis());

		if (lastFileName == null) {
			setNewWriter(newName);
		} else if (!lastFileName.equals(newName)) {
			flushWriter();
			closeWriters();
			setNewWriter(newName);
		}

		try {
			bufferedWriter.write(msg);
			bufferedWriter.newLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
