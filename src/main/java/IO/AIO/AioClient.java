package IO.AIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AioClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);   // 在异步操作完成前，阻止主线程退出
        AsynchronousSocketChannel clientChannel = AsynchronousSocketChannel.open();     // 异步的Channel
        ConnectHandler connectHandler = new ConnectHandler(clientChannel, latch);

        log.info("发起异步连接");
        clientChannel.connect(new InetSocketAddress("127.0.0.1", 6667),
                null,   // 第二个参数会在回调时传给handler，此处不需要
                connectHandler);

        latch.await();  // 阻塞主线程，直到latch变为0（成功或失败）
        if (clientChannel.isOpen()) {
            clientChannel.close();
        }
    }

}

@Slf4j
@AllArgsConstructor
class ConnectHandler implements CompletionHandler<Void, Object> {   // 连接回调

    private AsynchronousSocketChannel clientChannel;

    private CountDownLatch latch;

    @Override
    public void completed(Void result, Object attachment) {     // 连接成功后调用此方法
        log.info("成功连接到AIO服务器，准备发送数据...");
        ByteBuffer buffer = ByteBuffer.wrap("Hello".getBytes(StandardCharsets.UTF_8));
        WriteHandler writeHandler = new WriteHandler(clientChannel, latch);
        clientChannel.write(buffer, buffer, writeHandler);
    }

    @Override
    public void failed(Throwable exc, Object attachment) {      // 连接失败后调用此方法
        log.error("连接服务器失败: {}", exc.getMessage());
        latch.countDown();  // 无论成功失败，都必须释放latch，否则主线程会永远阻塞
    }
}

@Slf4j
@AllArgsConstructor
class WriteHandler implements CompletionHandler<Integer, ByteBuffer> {  // 写入回调

    private AsynchronousSocketChannel clientChannel;

    private CountDownLatch latch;

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        if (buffer.hasRemaining()) {    // 如果buffer中还有数据没写完（比如一次网络调用没能发送所有数据）
            log.info("数据尚未写完，继续写...");
            clientChannel.write(buffer, buffer, this);
        } else {
            log.info("消息发送成功");
            latch.countDown();  // 释放latch，让主线程可以退出
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer buffer) {
        log.error("发送数据失败: {}", exc.getMessage());
        latch.countDown();
    }
}