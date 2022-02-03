package com.github.julyss2019.mcsp.julyguild.config.gui.item;

import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;

public class PriorityItem {
    private int priority;
    private ItemBuilder itemBuilder;

    public PriorityItem(int priority, ItemBuilder itemBuilder) {
        this.priority = priority;
        this.itemBuilder = itemBuilder;
    }

    public int getPriority() {
        return priority;
    }

    public ItemBuilder getItemBuilder() {
        return itemBuilder;
    }
}
