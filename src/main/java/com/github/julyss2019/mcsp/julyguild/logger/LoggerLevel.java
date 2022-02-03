package com.github.julyss2019.mcsp.julyguild.logger;

public enum LoggerLevel {
	DEBUG(0, '9'), INFO(1, 'f'), WARNING(2, 'e'), ERROR(3, 'c');

	int level;
	char color;

	LoggerLevel(int level, char color) {
		this.level = level;
		this.color = color;
	}

	public char getColor() {
		return color;
	}

	public int getLevel() {
		return level;
	}
}
