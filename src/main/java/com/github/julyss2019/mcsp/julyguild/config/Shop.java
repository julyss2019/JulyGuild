package com.github.julyss2019.mcsp.julyguild.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class Shop {
	private String name;
	private YamlConfiguration yaml;

	public Shop(@NotNull String name, @NotNull YamlConfiguration yaml) {
		this.name = name;
		this.yaml = yaml;
	}

	public String getName() {
		return name;
	}

	public YamlConfiguration getYaml() {
		return yaml;
	}
}
