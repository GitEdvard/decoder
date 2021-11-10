package nl.thehyve;

import java.io.IOException;

public class Decoder {
    public static void main() throws IOException {
        DecoderInterface decoder = new CompressingDecoder();
        String useSimple = System.getenv("USE_TRIVIAL_IMPLEMENTATION");
        if (useSimple != null && useSimple.equals("1")) {
            decoder = new SimpleDecoder();
        }

        decoder.decode(System.in, new ByteArrayRingBuffer(System.out), System.err);
    }
}
