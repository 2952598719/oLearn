package IO.BIO;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BioServer {

    // BIO只能新建线程并处理，如果串行处理就会卡死在某个不产生数据的Socket上

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ServerSocket serverSocket = new ServerSocket(6666);
        log.info("BIO服务器启动了...");
        // 下面就是BIO的核心，while不断获取socket，交给
        while (true) {
            // 获取客户端连接socket
            log.info("等待客户端连接...");
            Socket socket = serverSocket.accept();
            log.info("客户端已连接，地址{}", socket.getRemoteSocketAddress());
            // 创建新线程处理
            executorService.execute(() -> {
                handle(socket);
            });
        }
    }

    private static void handle(Socket socket) {
        try {
            log.info("线程 {} 正在处理", Thread.currentThread().getName());
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();
            while (true) {
                int read = inputStream.read(bytes);
                if(read != -1){
                    log.info("收到数据: {}", new String(bytes, 0, read));
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            log.error("处理socket过程中出现错误 {}", e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log.error("socket关闭失败 {}", e.getMessage());
            }
        }
    }

}
