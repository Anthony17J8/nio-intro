package nio.buffers;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

public class CharsetEncoderDecoderDemo {

    public static void main(String[] args) throws CharacterCodingException {
        char a = 1055;

        System.out.println((byte)a);
        String input = "ПРИВЕТ!";
        ByteBuffer encode = encode(input);
        System.out.println(
                "Position=" + encode.position() + ", limit=" + encode.limit() + ", capacity=" + encode.capacity());

        String out = decode(encode);
        System.out.println("Результат: " + out);
    }

    private static ByteBuffer encode(String s) {
        return StandardCharsets.UTF_8.encode(s);
    }

    private static String decode(ByteBuffer buf) throws CharacterCodingException {
//        buf.flip();
        System.out.println("Декодируем....................");

        // buf.limit(9); бросает исключение если будем резать байтовый массив посередине символа
         buf.limit(2); //ok
        CharsetDecoder cd = StandardCharsets.UTF_8.newDecoder();
        return cd.decode(buf).toString();
    }
}
