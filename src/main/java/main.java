import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class Main {

    private final Selector selector;

    private final ServerSocketChannel serverSocketChannel;

    public Main(int port) throws IOException {
        selector = Selector.open();

        // 创建 ServerSocketChannel 并绑定到 8000 端口
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Starting server , listening port on " + port);
    }

    public void listen() throws IOException {
        while (true) {
            // 等待就绪的通道
            selector.select();
            // 获取就绪的 SelectionKey 集合
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if (key.isAcceptable()) {
                    // 接受新的连接
                    acceptNewConnection(serverSocketChannel, selector);
                } else if (key.isReadable()) {
                    // 处理请求
                    handleRequest(key);
                }
                // 移除已处理的 SelectionKey
                it.remove();
            }
        }
    }

    private static void acceptNewConnection(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private static void handleRequest(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        StringBuilder request = new StringBuilder();
        boolean endOfRequest = false;

        while (!endOfRequest && socketChannel.read(buffer) != -1) {
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            String data = new String(bytes, StandardCharsets.UTF_8);
            request.append(data);

            // 检查是否读到了请求的结束标志
            if (data.endsWith("\r\n\r\n")) {
                endOfRequest = true;
            }

            buffer.clear();
        }

        if (endOfRequest) {
            // 处理请求
            processRequest(socketChannel, request.toString());
        }
    }

    private static void processRequest(SocketChannel socketChannel, String request) throws IOException {
        // 分析请求
        String[] parts = request.split(" ");
        String method = parts[0];
        String path = parts[1];

        System.out.println("Request Path: " + path);

        // 构造响应
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=UTF-8\r\n" +
                "Content-Length: 13\r\n" +
                "\r\n" +
                "Hello, World!";

        // 发送响应
        sendResponse(socketChannel, response);
    }

    private static void sendResponse(SocketChannel socketChannel, String response) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
        socketChannel.write(buffer);
        socketChannel.close();
    }

    public static void main(String[] args) throws IOException {
        int port = 8000;
        new Main(port).listen();
    }

}
