package nio.buffers;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class LengthPrefixedDemo {

    public static void main(String[] args) {
        String msg = "Привет! Как дела?";
        ByteBuffer buf = encodeMessage(msg);
        String out = decodeMessage(buf);
        System.out.println(out);
    }

    static ByteBuffer encodeMessage(String msg) {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        //30 length
        ByteBuffer buffer = ByteBuffer.allocate(4 + bytes.length);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        return buffer;
    }

    static String decodeMessage(ByteBuffer buf) {
        int len = buf.getInt();
        byte[] out = new byte[len];
        buf.get(out, 0, len);
        return new String(out, StandardCharsets.UTF_8);
    }
}
