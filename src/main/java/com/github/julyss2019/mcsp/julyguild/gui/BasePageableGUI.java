package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public abstract class BasePageableGUI extends BasePlayerGUI {
    private int currentPage = -1;
    private int pageCount;

    public BasePageableGUI(@Nullable GUI lastGUI, GUI.Type guiType, GuildPlayer guildPlayer) {
        super(lastGUI, guiType, guildPlayer);
    }

    @Override
    public GUI getLastGUI() {
        return lastGUI;
    }

    // 下一页
    public void nextPage() {
        if (!hasNextPage()) {
            throw new RuntimeException("没有下一页了");
        }

        setCurrentPage(getCurrentPage() + 1);
        close();
        open();
    }

    // 上一页
    public void previousPage() {
        if (!hasPreciousPage()) {
            throw new RuntimeException("没有上一页了");
        }

        setCurrentPage(getCurrentPage() - 1);
        close();
        open();
    }

    // 是否有下一页
    public boolean hasNextPage() {
        return getCurrentPage() < getPageCount() - 1;
    }

    // 是否有上一页
    public boolean hasPreciousPage() {
        return getCurrentPage() > 0;
    }

    // 打开指定页
    public void setCurrentPage(int page) {
        if (!isValidPage(page)) {
            throw new IllegalArgumentException("页数不合法");
        }

        this.currentPage = page;
    }

    // 得到总页数
    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;

        // 如果当前页数不合法，则进行调整
        if (!isValidPage(currentPage)) {
            if (pageCount == 0) {
                setCurrentPage(-1);
            } else if (isValidPage(currentPage - 1) ){
                setCurrentPage(currentPage - 1);
            } else if (isValidPage(currentPage + 1)) {
                setCurrentPage(currentPage + 1);
            } else {
                setCurrentPage(0);
            }
        }
    }

    // 得到当前页数
    public int getCurrentPage() {
        return currentPage;
    }

    // 是否是有效的页数
    public boolean isValidPage(int p) {
        if (pageCount == 0) {
            return p == -1;
        }

        return p >= 0 && p < pageCount;
    }

    /**
     * 数据和界面分离，通常是个List，在这 update() 并设置 pageCount，而 createInventory() 仅仅只是根据当前数据（页数）返回GUI
     */
    public abstract void update();

    @Override
    public void open() {
        update();

        super.open();
    }
}
