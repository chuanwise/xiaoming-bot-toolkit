package com.taixue.xiaoming.bot.plugin.data;

import com.taixue.xiaoming.bot.core.data.JsonFileSavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ToolkitUserManager extends JsonFileSavedData {
    private Map<Long, ToolkitUserData> users = new HashMap<>();

    public Map<Long, ToolkitUserData> getUsers() {
        return users;
    }

    public void setUsers(Map<Long, ToolkitUserData> users) {
        this.users = users;
    }

    @Nullable
    public ToolkitUserData getUser(long qq) {
        return users.get(qq);
    }

    public ToolkitUserData getOrPutUser(long qq) {
        ToolkitUserData user = getUser(qq);
        if (Objects.isNull(user)) {
            user = new ToolkitUserData();
            users.put(qq, user);
        }
        return user;
    }
}
