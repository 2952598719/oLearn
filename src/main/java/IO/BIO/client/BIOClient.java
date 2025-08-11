package IO.BIO.client;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

@Slf4j
public class BIOClient extends Thread {

    private static final String SERVER_HOST = "localhost";

    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        new BIOClient().start();
    }

    public void run() {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            BIOClientHandler handler = new BIOClientHandler(socket);
            handler.start();  // 启动接收线程

            // 主线程处理用户输入
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("已连接服务器，输入消息 (输入'exit'退出):");
                while (true) {
                    String input = scanner.nextLine();
                    if ("exit".equalsIgnoreCase(input)) {
                        break;
                    }
                    handler.send(input);
                }
            }
        } catch (IOException e) {
            log.error("socket运行失败 {}", e.getMessage());
        }
    }

}