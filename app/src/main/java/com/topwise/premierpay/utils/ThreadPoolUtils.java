package com.topwise.premierpay.utils;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Victor(xiedianxin)
 * @brief description
 * @date 2022-11-09
 */
public class ThreadPoolUtils {
    private static final int CORE_POOL_SIZE = 3;

    private static final int MAX_POOL_SIZE = 3;

    private static final Long KEEP_ALIVE_TIME = 0L;

    private static final int QUEUE_SIZE = 3;

    private static final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(QUEUE_SIZE));

    private ThreadPoolUtils() {

    }

    public static ThreadPoolExecutor getThreadPool() {
        return THREAD_POOL;
    }
}
