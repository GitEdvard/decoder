package nl.thehyve;

import java.io.IOException;
import java.io.OutputStream;

public class SimpleDecoder implements DecoderInterface {

    @Override
    public void decodeValidPair(byte[] pair, ByteArrayRingBuffer decodedStream, OutputStream reEncodedStream) throws IOException {
        if ((int) pair[0] == 0) {
            decodedStream.write(pair[1]);
	    // Confusing..., the pair is already encoded, and now it's sent in to the re encoded algorithm.
	    // re-encoding should not be here, the name of the class indicates that it is a decoder. 
            reEncodedStream.write(pair);
        } else {
            int pos = pair[0];
            int len = pair[1];
            byte[] lastWritten = decodedStream.toByteArray();
	    // The streaming buffer has a limited length. What happen if pos (i.e. backward offset) goes
	    // before the buffer start?
            decodedStream.write(lastWritten, lastWritten.length - pos, len);
            for (int i = lastWritten.length - pos; i < len; ++i) {
		// This is the "trivial implementation" as mentioned in the task
		// I wouldn't mind this was placed in its own class, with a name indicating that. 
                reEncodedStream.write(0);
                reEncodedStream.write(lastWritten[i]);
            }
        }
    }
}
