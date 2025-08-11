package IO.BIO.client;

import IO.BIO.ChannelHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class BIOClientHandler extends ChannelHandler {

    public BIOClientHandler(Socket socket) {
        super(socket, StandardCharsets.UTF_8);
    }

    @Override
    protected void onConnected() {
        System.out.println("成功连接服务器: " + socket.getRemoteSocketAddress());
    }

    @Override
    protected void onMessageReceived(String message) {
        System.out.println("[服务器响应] " + message);
    }

    @Override
    protected void onClosed() {
        try {
            socket.close();
        } catch (IOException e) {
            log.error("socket关闭失败 {}", e.getMessage());
        }
        System.out.println("连接已关闭");
    }

    @Override
    protected void onError(Throwable cause) {
        System.err.println("服务器通信错误: " + cause.getMessage());
    }

}