package com.taixue.xiaoming.bot.plugin.command.executor;

import com.taixue.xiaoming.bot.api.annotation.Command;
import com.taixue.xiaoming.bot.api.annotation.CommandParameter;
import com.taixue.xiaoming.bot.api.annotation.RequirePermission;
import com.taixue.xiaoming.bot.api.url.UrlInCatCodeManager;
import com.taixue.xiaoming.bot.api.user.QQXiaomingUser;
import com.taixue.xiaoming.bot.api.user.XiaomingUser;
import com.taixue.xiaoming.bot.core.command.executor.CommandExecutorImpl;
import com.taixue.xiaoming.bot.plugin.ToolkitPlugin;
import com.taixue.xiaoming.bot.plugin.data.ToolkitUserData;
import com.taixue.xiaoming.bot.plugin.data.ToolkitUserManager;
import com.taixue.xiaoming.bot.util.CommandWordUtil;
import com.taixue.xiaoming.bot.util.TimeUtil;
import love.forte.simbot.api.sender.MsgSender;

import java.util.List;
import java.util.Objects;

public class ToolkitCommandExecutor extends CommandExecutorImpl {
    private static final String TELL = "(传话|转告|tell)";

    @Command("(QQ|qq) {qq}")
    public void onQQ(final XiaomingUser user,
                     @CommandParameter("qq") final long qq) {
        user.sendMessage("他的 QQ 是：{}", qq);
    }

    @Command("(之后|after) {time}")
    public void onAfter(final XiaomingUser user,
                        @CommandParameter("time") final String timeString) {
        final long time = TimeUtil.parseTime(timeString);
        if (time == -1) {
            user.sendError("{}并不是一个合理的时间哦", timeString);
        } else {
            user.sendMessage("{}之后是{}", TimeUtil.toTimeString(time), TimeUtil.FORMAT.format(System.currentTimeMillis() + time));
        }
    }

    @Command("(图床|url) {remain}")
    public void onGetUrls(final XiaomingUser user,
                          @CommandParameter("remain") final String resource) {
        final UrlInCatCodeManager pictureManager = getXiaomingBot().getPictureManager();
        final List<String> catCodes = pictureManager.listCatCodes(resource);
        if (catCodes.isEmpty()) {
            user.sendMessage("这个消息中没有任何图片资源哦");
        } else if (catCodes.size() == 1) {
            user.sendMessage("这张图片的 URL 是：{}", pictureManager.requireUrl(catCodes.get(0)));
        } else {
            StringBuilder builder = new StringBuilder()
                    .append("检测到有 ").append(catCodes.size()).append(" 张图片，他们的 URL 分别是：");
            for (String catCode : catCodes) {
                builder.append("\n")
                        .append(pictureManager.requireUrl(catCode));
            }
            user.sendMessage(builder.toString());
        }
    }

    @Command(TELL + " {qq} {remain}")
    public void onTell(final QQXiaomingUser user,
                       @CommandParameter("qq") final long qq,
                       @CommandParameter("remain") final String message) {
        final ToolkitUserManager userManager = ToolkitPlugin.INSTANCE.getUserManager();

        final ToolkitUserData target = userManager.getUser(qq);
        final ToolkitUserData userData = userManager.getUser(user.getQQ());
        final boolean userEnable = Objects.isNull(userData) || userData.isEnableTell();
        final boolean targetEnable = Objects.isNull(target) || target.isEnableTell();
        if (userEnable && targetEnable) {
            if (user.sendPrivateMessage(qq, "{} 委托小明转告一个消息：{}", user.getCompleteName(), message)) {
                user.sendMessage("成功转告消息");
            }
        } else if (!userEnable) {
            user.sendMessage("开启了传话功能后才可以给别人传话哦，试试 #启动传话");
        } else {
            user.sendMessage("对方并没有开启传话功能，还是直接找他聊聊吧 {}", getXiaomingBot().getEmojiManager().get("happy"));
        }
    }

    @Command(CommandWordUtil.ENABLE_REGEX + TELL)
    public void onEnableTell(final QQXiaomingUser user) {
        final ToolkitUserManager userManager = ToolkitPlugin.INSTANCE.getUserManager();

        final ToolkitUserData userData = userManager.getUser(user.getQQ());
        final boolean userEnable = Objects.isNull(userData) || userData.isEnableTell();

        if (userEnable) {
            user.sendMessage("你已经开启了传话功能哦");
        } else {
            final ToolkitUserData data = userManager.getOrPutUser(user.getQQ());
            data.setEnableTell(true);
            user.sendMessage("成功开启传话功能，可以通过小明给别人传话啦");
            userManager.save();
        }
    }

    @Command(CommandWordUtil.DISABLE_REGEX + TELL)
    public void onDisableTell(final QQXiaomingUser user) {
        final ToolkitUserManager userManager = ToolkitPlugin.INSTANCE.getUserManager();

        final ToolkitUserData userData = userManager.getUser(user.getQQ());
        final boolean userEnable = Objects.isNull(userData) || userData.isEnableTell();

        if (userEnable) {
            final ToolkitUserData data = userManager.getOrPutUser(user.getQQ());
            data.setEnableTell(false);
            user.sendMessage("成功关闭传话功能");
            userManager.save();
        } else {
            user.sendMessage("你并没有开启传话功能哦");
        }
    }
}
