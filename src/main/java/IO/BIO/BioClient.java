package IO.BIO;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BioClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 6666);
        OutputStream outputStream = socket.getOutputStream();
        log.info("成功连接到BIO服务器");
        outputStream.write("Hello".getBytes());     // 默认就是UTF_8
        outputStream.flush();
        log.info("消息发送成功");
        socket.close();
    }

}
