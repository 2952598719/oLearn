package IO.AIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AioServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();
        AcceptHandler acceptHandler = new AcceptHandler(serverChannel);

        log.info("AIO服务器启动了...");
        serverChannel.bind(new InetSocketAddress(6667));
        serverChannel.accept(null, acceptHandler);
        latch.await();
        if (serverChannel.isOpen()) {
            serverChannel.close();
        }
    }

}

@Slf4j
@AllArgsConstructor
class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {

    private AsynchronousServerSocketChannel serverChannel;

    @Override
    public void completed(AsynchronousSocketChannel clientChannel, Object attachment) {
        serverChannel.accept(null, this);   // 在接收到新连接后，必须再次调用accept来接收下一个连接
        try {
            log.info("有新的客户端连接 {}", clientChannel.getRemoteAddress());
        } catch (IOException e) {
            log.error("客户端连接错误 {}", e.getMessage());
        }
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ReadHandler readHandler = new ReadHandler(clientChannel);
        clientChannel.read(buffer, buffer, readHandler);
    }

    @Override
    public void failed(Throwable exc, Object attachment) {
        log.error("接受连接失败 {}", exc.getMessage());
    }
}


@Slf4j
@AllArgsConstructor
class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel clientChannel;

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        if (result > 0) {
            attachment.flip();
            log.info("收到数据 {}", new String(attachment.array(), 0, attachment.limit()));
            // 在这里继续异步读取或写入
            attachment.clear();
            clientChannel.read(attachment, attachment, this);
        } else if (result < 0) { // 客户端关闭
            try {
                log.info("客户端断开连接");
                clientChannel.close();
            } catch (IOException e) {
                log.info("断开过程中出现问题 {}", e.getMessage());
            }
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        log.error("读取数据失败 {}", exc.getMessage());
    }
}