package IO.BIO.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;

@Slf4j
public class BIOServer extends Thread {

    private static final int PORT = 8888;

    public static void main(String[] args) {
        new BIOServer().start();
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("服务器启动，监听端口 " + PORT);

            while (!serverSocket.isClosed()) {
                // 为每个客户端连接创建独立处理器
                BIOServerHandler handler = new BIOServerHandler(serverSocket.accept());
                handler.start(); // 启动线程处理客户端
            }
        } catch (IOException e) {
            log.error("serverSocket启动失败 {}", e.getMessage());
        }
    }

}
