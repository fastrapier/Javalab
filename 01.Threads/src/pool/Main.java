package pool;

public class Main {
    public static void main(String[] args) {
        ThreadPool threadPool = ThreadPool.newPool(2);

        threadPool.submit(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println(Thread.currentThread().getName() + " A");
            }
        });

        threadPool.submit(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println(Thread.currentThread().getName() + " B");
            }
        });
    }
}
