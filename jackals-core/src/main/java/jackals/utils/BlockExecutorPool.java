package jackals.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockExecutorPool extends Thread {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private ThreadPoolExecutor executor;
    private int threadNum = 2;
    private AtomicInteger threadAlive = new AtomicInteger(0);
    private ExecutorCompletionService<Integer> comp;

    public BlockExecutorPool(int threadNum) {
        logger.info("BlockExecutorPool");
        this.threadNum = threadNum;
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNum);
        comp = new ExecutorCompletionService<Integer>(executor);
        start();

    }

    public int getThreadAlive() {
        return threadAlive.get();
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
//                System.out.println("before take");
                comp.take();
                notifyThread();
            }
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
    }

    public boolean isShutdown() {
        return executor.isShutdown();
    }

    synchronized private void notifyThread() {
        threadAlive.decrementAndGet();
//        System.out.println("notifyAll" +threadAlive.get());
        notifyAll();

    }

    synchronized public void execute(final Runnable runnable) {
        try {
            while (threadAlive.get() >= threadNum) {
//            while (executor.getActiveCount() >= threadNum) {
//                System.out.println("before wait ");
                this.wait();
            }
            comp.submit(runnable, 0);
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
//        checkActiveCount();
        threadAlive.incrementAndGet();
    }

    synchronized private void checkActiveCount() {
        try {
            while (threadAlive.get() >= threadNum) {
//            while (executor.getActiveCount() >= threadNum) {
//                System.out.println("before wait ");
                this.wait();
            }
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
    }


    public synchronized void shutdown() {
        logger.info("pool shutdown 1 ");
        this.interrupt();
        logger.info("pool shutdown 2 ");
        executor.shutdown();
        logger.info("pool shutdown 3 ");
        this.notifyAll();
        logger.info("pool shutdown 4 ");

    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
//        test1();
        test2();

    }

    private static void test2() {
//        CountableThreadPool executor = new CountableThreadPool(1);
        BlockExecutorPool executor = new BlockExecutorPool(1);
        long s = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            System.out.println("for " + i);
            final int x = i;
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("完成" + x);
                }
            });
        }
        System.out.println("cost:" + (System.currentTimeMillis() - s));
        executor.shutdown();
    }


}