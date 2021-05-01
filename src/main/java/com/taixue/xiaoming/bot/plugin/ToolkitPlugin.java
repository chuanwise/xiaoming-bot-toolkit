package com.taixue.xiaoming.bot.plugin;

import com.taixue.xiaoming.bot.core.plugin.XiaomingPluginImpl;
import com.taixue.xiaoming.bot.plugin.command.executor.ToolkitCommandExecutor;
import com.taixue.xiaoming.bot.plugin.data.ToolkitUserManager;

import java.io.File;

public class ToolkitPlugin extends XiaomingPluginImpl {
    public static ToolkitPlugin INSTANCE;

    private ToolkitCommandExecutor toolkitCommandExecutor = new ToolkitCommandExecutor();

    private ToolkitUserManager userManager;

    public ToolkitUserManager getUserManager() {
        return userManager;
    }

    public void loadUserManager() {
        userManager = getXiaomingBot().getFileSavedDataFactory().forFileOrProduce(
                new File(getDataFolder(), "users.json"),
                ToolkitUserManager.class,
                ToolkitUserManager::new
        );
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        getDataFolder().mkdirs();
        loadUserManager();
        getXiaomingBot().getCommandManager().register(toolkitCommandExecutor, this);
    }
}
