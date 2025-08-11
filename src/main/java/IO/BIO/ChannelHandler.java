package IO.BIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

public abstract class ChannelHandler extends Thread {

    protected final Socket socket;

    protected final Charset charset;

    private OutputStream outputStream;

    public ChannelHandler(Socket socket, Charset charset) {
        this.socket = socket;
        this.charset = charset;
        setName("ChannelThread-" + socket.getRemoteSocketAddress());
    }

    @Override
    public void run() {
        onConnected();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), charset)
        )) {
            String message;
            while ((message = reader.readLine()) != null) {
                onMessageReceived(message);
            }
        } catch (IOException e) {
            onError(e);
        } finally {
            onClosed();
        }
    }

    // 写操作封装
    public synchronized void send(String message) {
        try {
            if (outputStream == null) {
                outputStream = socket.getOutputStream();
            }
            outputStream.write(message.getBytes(charset));
            outputStream.write('\n');
            outputStream.flush();
        } catch (IOException e) {
            onError(e);
        }
    }

    // 事件抽象方法
    protected abstract void onConnected();

    protected abstract void onMessageReceived(String message);

    protected abstract void onClosed();

    protected abstract void onError(Throwable cause);

}
