package nl.thehyve;

import java.io.IOException;
import java.io.OutputStream;

public class SimpleDecoder implements DecoderInterface {

    @Override
    public void decodeValidPair(byte[] pair, ByteArrayRingBuffer decodedStream, OutputStream reEncodedStream) throws IOException {
        if ((int) pair[0] == 0) {
            decodedStream.write(pair[1]);
            reEncodedStream.write(pair);
        } else {
            int pos = pair[0];
            int len = pair[1];
            byte[] lastWritten = decodedStream.toByteArray();
            decodedStream.write(lastWritten, lastWritten.length - pos, len);
            for (int i = lastWritten.length - pos; i < len; ++i) {
                reEncodedStream.write(0);
                reEncodedStream.write(lastWritten[i]);
            }
        }
    }
}
