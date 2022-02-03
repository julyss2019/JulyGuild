package com.github.julyss2019.mcsp.julyguild.task;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.request.RequestManager;
import org.bukkit.scheduler.BukkitRunnable;

public class RequestCleanTask extends BukkitRunnable {
    private static JulyGuild plugin = JulyGuild.inst();
    private static RequestManager requestManager = plugin.getRequestManager();

    @Override
    public void run() {
        requestManager.getRequests().forEach(request -> {
            if (!request.isValid()) {
                requestManager.deleteRequest(request);
            }
        });
    }
}
