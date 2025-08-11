package IO.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NioServer {

    // 这是串行操作，看上去似乎比BIO的并行效率会低一些
    // 不过也不能拿串行和并行比，要比的话下面的也可以切换成并行的Reactor模式，那样的话不仅内存占用少，效率也差不多，就远优于BIO了

    public static void main(String[] args) throws IOException {
        // 1.创建并配置serverSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();   // 和ServerSocket作用一样，只是NIO中用Channel组织
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));     // 绑定6666
        serverSocketChannel.configureBlocking(false);       // 非阻塞模式
        // 2.将serverSocketChannel注册到selector，关心ACCEPT事件
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.info("NIO服务器启动了...");
        // 3.循环处理连接
        while  (true) {
            if (selector.select(1000) == 0) {   // 等待一秒，
                continue;
            }

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    // 如果是个新连接，则将这个socketChannel以READ类型注册到selector中，之后就以READ逻辑处理
                    log.info("有新客户端连接...");
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                } else if (key.isReadable()) {  // 是
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                    try {
                        int readBytes = socketChannel.read(byteBuffer);
                        if (readBytes > 0) {
                            log.info("收到数据: {}", new String(byteBuffer.array(), 0, readBytes));
                        } else if(readBytes < 0) {
                            log.info("客户端 {} 断开连接", socketChannel.getRemoteAddress());
                            key.cancel();
                            socketChannel.close();
                        }
                        byteBuffer.clear();
                    } catch (IOException e) {
                        log.error("处理过程中出现错误 {}", e.getMessage());
                        key.cancel();
                        socketChannel.close();
                    }
                }
                iterator.remove();
            }
        }
    }

}