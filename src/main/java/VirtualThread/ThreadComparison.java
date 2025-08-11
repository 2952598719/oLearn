package VirtualThread;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 任务本身耗时1000ms
// 1000: 5032-1018
// 2000: 10049-1020
// 5000: 25108-1031
// 10000: 50196-1061
public class ThreadComparison {

    private static final int NUM_TASKS = 1000;

    public static void main(String[] args) {
        threadConsumption();
        virtualThreadConsumption();
    }

    public static void threadConsumption() {
        System.out.println("开始测试普通线程...");
        Instant threadStart = Instant.now();

        // 系统资源有限，线程池大小为200（如果系统资源无限，那用啥都没关系，但毕竟不可能）
        ExecutorService executor = Executors.newFixedThreadPool(200);
        for (int i = 1; i <= NUM_TASKS; i++) {
            executor.submit(ThreadComparison::blockingTask);
        }
        executor.shutdown();
        executor.close();

        Instant threadEnd = Instant.now();
        Duration duration = Duration.between(threadStart, threadEnd);
        System.out.printf("普通线程总耗时 %d ms\n\n", duration.toMillis());
    }

    public static void virtualThreadConsumption() {
        System.out.println("开始测试虚拟线程...");
        Instant threadStart = Instant.now();

        // 逻辑不用怎么改，线程池换一个即可
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 1; i <= NUM_TASKS; i++) {
            executor.submit(ThreadComparison::blockingTask);
        }
        executor.shutdown();
        executor.close();

        Instant threadEnd = Instant.now();
        Duration duration = Duration.between(threadStart, threadEnd);
        System.out.printf("虚拟线程总耗时 %d ms\n\n", duration.toMillis());
    }

    private static void blockingTask() {
        try {
            Thread.sleep(1000);     // 模仿网络/数据库操作
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}