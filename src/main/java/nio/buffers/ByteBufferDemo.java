package nio.buffers;

import java.nio.ByteBuffer;

public class ByteBufferDemo {
    public static void main(String[] args) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        // position=0, limit=capacity=8
        System.out.println(
                "Allocate(8): position=" + bb.position() + ", limit=" + bb.limit() + ", capacity=" + bb.capacity());

        bb.put((byte) 'H');
        bb.put((byte) 'e');
        bb.put((byte) 'l');
        // position=3, limit=capacity=8
        System.out.println(
                "Put 3 bytes: position=" + bb.position() + ", limit=" + bb.limit() + ", capacity=" + bb.capacity());

        bb.flip();
        // position=0, limit=3, capacity=8
        System.out.println(
                "Flip(): position=" + bb.position() + ", limit=" + bb.limit() + ", capacity=" + bb.capacity());

        byte one = bb.get();

        byte two = bb.get();

        // position=2, limit=3, capacity=8
        System.out.println(
                "Get two bytes: position=" + bb.position() + ", limit=" + bb.limit() + ", capacity=" + bb.capacity());

        bb.compact();
        // position=1, limit=capacity=8
        System.out.println(
                "Compact(): position=" + bb.position() + ", limit=" + bb.limit() + ", capacity=" + bb.capacity());

        bb.put((byte) 'l');
        bb.put((byte) 'o');
        // position=3, limit=capacity=8
        System.out.println(
                "Put 2 bytes: position=" + bb.position() + ", limit=" + bb.limit() + ", capacity=" + bb.capacity());

        bb.flip();
        // position=0, limit=3, capacity=8
        System.out.println(
                "Flip(): position=" + bb.position() + ", limit=" + bb.limit() + ", capacity=" + bb.capacity());

        System.out.println(one + ":" + (char) one);
        System.out.println(two + ":" + (char) two);

        while (bb.position() < bb.limit()) {
            byte b = bb.get();
            System.out.println(b + ":" + (char) b);
        }

        System.out.println(Integer.toHexString(128));
    }
}
