package com.github.nukc.adworker;

import android.content.Context;

import androidx.annotation.Nullable;

public class AdWorker {

    /**
     * 初始化设置
     *
     * @param context  Context
     * @param channel  渠道号
     * @param ttAppId  头条应用 ID
     * @param gdtAppId 广点通应用 ID
     */
    public static void setup(Context context, String channel, @Nullable String ttAppId, @Nullable String gdtAppId) {
        setup(context, channel, 60 * 60 * 24 * 1000 * 7, ttAppId, gdtAppId);
    }

    /**
     * 初始化设置
     *
     * @param context        Context
     * @param channel        渠道号
     * @param dbExpireMillis 广告选择器数据库数据过期时间，毫秒
     * @param ttAppId        头条应用 ID
     * @param gdtAppId       广点通应用 ID
     */
    public static void setup(Context context, String channel, int dbExpireMillis,
                             @Nullable String ttAppId, @Nullable String gdtAppId) {
        setup(context, channel, dbExpireMillis, ttAppId, gdtAppId, false);
    }

    /**
     * 初始化设置
     *
     * @param context            Context
     * @param channel            渠道号
     * @param dbExpireMillis     广告选择器数据库数据过期时间，毫秒
     * @param ttAppId            头条应用 ID
     * @param gdtAppId           广点通应用 ID
     * @param enableMultiProcess 是否开启多进程支持
     */
    public static void setup(Context context, String channel, int dbExpireMillis,
                             @Nullable String ttAppId, @Nullable String gdtAppId, Boolean enableMultiProcess) {
        //TODO
    }
}
