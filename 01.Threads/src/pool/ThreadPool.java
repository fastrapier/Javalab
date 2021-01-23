package pool;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ThreadPool {
    private Deque<Runnable> tasks;

    private PoolWorker[] pool;

    public static ThreadPool newPool(int threadsCount) {
        ThreadPool threadPool = new ThreadPool();
        threadPool.tasks = new ConcurrentLinkedDeque<>();
        threadPool.pool = new PoolWorker[threadsCount];

        for (int i = 0; i < threadPool.pool.length; i++) {
            threadPool.pool[i] = threadPool.new PoolWorker();
            threadPool.pool[i].start();
        }

        return threadPool;
    }

    public void submit(Runnable task) {
        // TODO: реализовать
        synchronized (tasks){
            tasks.add(task);
            tasks.notify();
        }
    }

    private class PoolWorker extends Thread {
        @Override
        public void run() {
            while (true) {
                // TODO: реализовать
                synchronized (tasks){
                    while (tasks.isEmpty()) {
                        try {
                            tasks.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                tasks.poll().run();
            }
        }
    }

}
