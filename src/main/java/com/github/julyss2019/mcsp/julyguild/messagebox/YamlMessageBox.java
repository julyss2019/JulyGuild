package com.github.julyss2019.mcsp.julyguild.messagebox;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class YamlMessageBox implements MessageBox {
    private ConfigurationSection section;
    private Map<UUID, Message> messageMap = new HashMap<>();

    public YamlMessageBox(@NotNull ConfigurationSection section) {
        this.section = section;

        load();
    }

    private void load() {
        Set<String> keys = section.getKeys(false);

        if (keys == null) {
            return;
        }

        keys.forEach(s -> {
            ConfigurationSection messageSection = section.getConfigurationSection(s);

            BaseMessage msg = new BaseMessage(UUID.fromString(s), messageSection.getString("message"), messageSection.getLong("creation_time"));

            messageMap.put(msg.getUuid(), msg);
        });
    }

    public abstract void save();

    @Override
    public Collection<Message> getMessages() {
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public void removeMessage(@NotNull Message message) {
        if (!messageMap.containsKey(message.getUuid())) {
            throw new RuntimeException("没有这条消息");
        }

        section.set(message.getUuid().toString(), null);
        save();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        String uuidStr = UUID.randomUUID().toString();

        section.set(uuidStr + ".message", message);
        section.set(uuidStr + ".creation_time", System.currentTimeMillis());
        save();
    }
}
