package org.leo.im.notification;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池持有者
 * 
 * @author Leo
 * @date 2018/3/30
 */
public class ThreadPoolHolder {

    private static final int THREAD_POOL_THREADS;

    private static final int THREAD_POOL_QUEUES;

    static {
        Integer threads = Integer.getInteger("thread.pool.threads");
        Integer queues = Integer.getInteger("thread.pool.queues");
        THREAD_POOL_THREADS = threads == null ? Runtime.getRuntime().availableProcessors() * 2 : threads;
        THREAD_POOL_QUEUES = queues == null ? 512 : queues;
    }

    // 业务线程池
    private final static ExecutorService THREAD_POOL = new ThreadPoolExecutor(THREAD_POOL_THREADS, THREAD_POOL_THREADS,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(THREAD_POOL_QUEUES), new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "handlerThread_" + this.threadIndex.incrementAndGet());
                }
            }, new ThreadPoolExecutor.DiscardPolicy());

    /**
     * 得到线程池
     * 
     * @return
     */
    public static ExecutorService getThreadPool() {
        return THREAD_POOL;
    }

}
