package IO.BIO.server;

import IO.BIO.ChannelHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class BIOServerHandler extends ChannelHandler {

    private int clientCount = 0;

    public BIOServerHandler(Socket socket) {
        super(socket, StandardCharsets.UTF_8);
    }

    @Override
    protected void onConnected() {
        System.out.printf("[%s] 客户端接入 | %s%n",
                Thread.currentThread().getName(),
                socket.getRemoteSocketAddress());
        send("欢迎连接服务器! 你是第" + (++clientCount) + "个用户");
    }

    @Override
    protected void onMessageReceived(String message) {
        System.out.printf("[%s] 收到消息: %s%n",
                socket.getRemoteSocketAddress(), message);
        // 添加简单的业务逻辑处理
        if (message.equalsIgnoreCase("ping")) {
            send("pong");
        } else if (message.startsWith("echo:")) {
            send(message.substring(5));
        } else {
            send("[服务器应答] " + message);
        }
    }

    @Override
    protected void onClosed() {
        try {
            socket.close();
        } catch (IOException e) {
            log.error("关闭socket失败 {}", e.getMessage());
        }
        System.out.println("客户端断开: " + socket.getRemoteSocketAddress());
    }

    @Override
    protected void onError(Throwable cause) {
        System.err.println("客户端通信错误: " + cause.getMessage());
    }

}