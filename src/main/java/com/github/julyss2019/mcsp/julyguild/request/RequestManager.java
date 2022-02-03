package com.github.julyss2019.mcsp.julyguild.request;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julylibrary.utils.YamlUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RequestManager {
    private final JulyGuild plugin = JulyGuild.inst();
    private Map<UUID, Request> requestMap = new HashMap<>();
    private Map<Sender, List<Request>> sentMap = new HashMap<>();
    private Map<Receiver, List<Request>> receiveMap = new HashMap<>();

    public RequestManager() {}

    public Collection<Request> getRequests() {
        return new HashSet<>(requestMap.values());
    }

    /**
     * 发送请求
     * @param request
     */
    public void sendRequest(@NotNull Request request) {
        if (getRequest(request.getUuid()) != null) {
            throw new RuntimeException("该请求已经发送过了");
        }

        File file = new File(plugin.getDataFolder(), "data" + File.separator + "requests" + File.separator + request.getUuid() + ".yml");
        YamlConfiguration yml = YamlUtil.loadYaml(file, StandardCharsets.UTF_8);

        request.onSave(yml);
        YamlUtil.saveYaml(yml, file, StandardCharsets.UTF_8);
        handleRequest(request);
    }

    /**
     * 卸载请求
     * @param request
     */
    public void unloadRequest(@NotNull Request request) {
        if (!isLoaded(request.getUuid())) {
            throw new RuntimeException("请求未载入");
        }

        requestMap.remove(request.getUuid());
        sentMap.get(request.getSender()).remove(request);
        receiveMap.get(request.getReceiver()).remove(request);
    }

    /**
     * 摧毁请求
     * @param request
     */
    public void deleteRequest(@NotNull Request request) {
        if (!isLoaded(request.getUuid())) {
            throw new RuntimeException("该请求未被载入");
        }

        File file = new File(plugin.getDataFolder(), "data" + File.separator + "requests" + File.separator + request.getUuid() + ".yml");

        if (!file.delete()) {
            throw new RuntimeException("文件删除失败: " + file.getAbsolutePath());
        }

        unloadRequest(request);
    }

    /**
     * 载入所有请求
     */
    public void loadRequests() {
        requestMap.clear();
        receiveMap.clear();
        sentMap.clear();

        File[] files = new File(plugin.getDataFolder(), "data" + File.separator + "requests").listFiles();

        if (files != null) {
            for (File file : files) {
                loadRequest(file);
            }
        }
    }

    /**
     * 是否已载入请求
     * @param uuid
     * @return
     */
    private boolean isLoaded(@NotNull UUID uuid) {
        return requestMap.containsKey(uuid);
    }

    /**
     * 载入请求
     * @param file
     */
    private void loadRequest(@NotNull File file) {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        Request.Type type = Request.Type.valueOf(yml.getString("type"));
        Request request;

        try {
            request = type.getClazz().newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }

        request.onLoad(yml);

        if (isLoaded(request.getUuid())) {
            throw new RuntimeException("请求已载入");
        }

        handleRequest(request);
    }

    /**
     * 潮衣库请求
     * @param request
     */
    private void handleRequest(@NotNull Request request) {
        Sender sender = request.getSender();
        Receiver receiver = request.getReceiver();

        if (!sentMap.containsKey(sender)) {
            sentMap.put(sender, new ArrayList<>());
        }

        if (!receiveMap.containsKey(receiver)) {
            receiveMap.put(receiver, new ArrayList<>());
        }

        sentMap.get(sender).add(request);
        receiveMap.get(receiver).add(request);
        requestMap.put(request.getUuid(), request);
    }

    /**
     * 得到发送的请求
     * @param sender
     * @return
     */
    public List<Request> getSentRequests(@NotNull Sender sender) {
        return new ArrayList<>(sentMap.getOrDefault(sender, new ArrayList<>()));
    }

    /**
     * 得到接收的请求
     * @param receiver
     * @return
     */
    public List<Request> getReceivedRequests(@NotNull Receiver receiver) {
        return new ArrayList<>(receiveMap.getOrDefault(receiver, new ArrayList<>()));
    }

    /**
     * 得到请求
     * @param uuid
     * @return
     */
    public Request getRequest(@NotNull UUID uuid) {
        return requestMap.get(uuid);
    }
}
