package com.github.julyss2019.mcsp.julyguild.guild;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class GuildSpawn {
    private Guild guild;
    private String worldName;
    private double x, y, z;
    private float yaw, pitch;

    GuildSpawn(@NotNull Guild guild) {
        this.guild = guild;

        ConfigurationSection section = guild.getYaml().getConfigurationSection("spawn");

        this.worldName = section.getString("world");
        this.x = section.getDouble("x");
        this.y = section.getDouble("y");
        this.z = section.getDouble("z");
        this.yaw = (float) section.getDouble("yaw");
        this.pitch = (float) section.getDouble("pitch");
    }

    public Guild getGuild() {
        return guild;
    }

    public String getWorldName() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            return null;
        }

        return new Location(world, x, y, z, yaw, pitch);
    }
}
