package nl.thehyve;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public interface DecoderInterface {

    default void decode(InputStream encodedStream, ByteArrayRingBuffer decodedStream, PrintStream reEncodedStream) throws IOException {
        int length;
        byte[] pair = new byte[2];
        while ((length = encodedStream.read(pair, 0, 2)) != -1) {
            if (length < 2) {
                decodedStream.write(0x3f);
            } else {
                decode(pair, decodedStream, reEncodedStream);
            }
        }
    }

    default void decode(byte[] pair, ByteArrayRingBuffer decodedStream, OutputStream reEncodedStream) throws IOException {
        // a pair is invalid if the offset is larger than 0 and the length is larger than the offset
        if ((pair[0] > 0 && pair[1] > pair[0])) {
	    // I would like the validation of the input gathered in one place 
            decodedStream.write(0x3f);
	    // I would like the encoding algorithm be able to be used on its own. Here it's dependent upon an already encoded sequence. 
            reEncodedStream.write(new byte[]{0x00, 0x3f});
            return;
        }

        decodeValidPair(pair, decodedStream, reEncodedStream);
    }

    void decodeValidPair(byte[] pair, ByteArrayRingBuffer decodedStream, OutputStream reEncodedStream) throws IOException;
}
