package com.netease.tools.util;

import com.google.gson.JsonObject;
import org.apache.http.util.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zyl06 on 20/02/2017.
 */
public class ConfigUtil {
    private static final Map<String, String> mConfig = new HashMap<String, String>();

    public static String getGitPath() {
        return getConfig("git");
    }

    public static void setGitPath(String path) {
        if (!TextUtils.isEmpty(path)) {
            setConfig("git", path);
        }
    }

    public static String getMipmapGitPath() {
        return getConfig("mipmap_git_path");
    }

    public static void setMipmapGitPath(String path) {
        if (!TextUtils.isEmpty(path)) {
            setConfig("mipmap_git_path", path);
        }
    }

    static String getConfig(String key) {
        if (mConfig.get(key) != null) {
            return mConfig.get(key);
        }
        String result = getConfigFromFile(key);
        if (result != null) {
            mConfig.put(key, result);
        }
        return result;
    }

    static void setConfig(String key, String path) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(path)) {
            return;
        }

        String oldPath = mConfig.get(key);
        if (!path.equals(oldPath)) {
            JsonObject object = new JsonObject();
            for (Map.Entry<String, String> entry : mConfig.entrySet()) {
                object.addProperty(entry.getKey(), entry.getValue());
            }
            object.addProperty(key, path);
            Fio.writeToFile(getConfigPath(), object.toString(), false);
            mConfig.put(key, path);
        }
    }

    private static String getConfigFromFile(String name) {
        String strConfig = Fio.readStringFile(getConfigPath());
        if (!TextUtils.isEmpty(strConfig)) {
            mConfig.putAll(GsonUtil.parseData(strConfig));
            return mConfig.get(name);
        }

        return null;
    }

    private static String getConfigPath() {
        return CommandUtil.isWindows() ? getConfigPath_Windows() : getConfigPath_Mac();
    }

    private static String getConfigPath_Windows() {
        return PathUtil.get("c:Users", "Public", "netease_plugin.config");
    }

    private static String getConfigPath_Mac() {
        return PathUtil.get("/Users/zyl06/config", "netease_plugin.config");
    }
}
