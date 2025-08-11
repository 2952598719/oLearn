package IO.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NioClient {

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        if (!socketChannel.connect(new InetSocketAddress("127.0.0.1", 6666))) {
            while(!socketChannel.finishConnect()) {
                log.info("未阻塞，可以做其他工作");
            }
        }
        log.info("成功连接到NIO服务器");
        ByteBuffer buffer = ByteBuffer.wrap("Hello".getBytes());
        socketChannel.write(buffer);
        log.info("消息发送成功");
        System.in.read();   // 让程序暂停一下，不然服务器可能来不及响应就退出了
    }

}