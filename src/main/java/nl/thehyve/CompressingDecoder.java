package nl.thehyve;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class CompressingDecoder implements DecoderInterface {

    final int maxSize = 20;
    final int minSize = 5;
    byte[] reEncodeBuffer = new byte[maxSize];
    int pos = 0;

    @Override
    public void decodeValidPair(byte[] pair, ByteArrayRingBuffer decodedStream, OutputStream reEncodedStream) throws IOException {
        if ((int) pair[0] == 0) {
            decodedStream.write(pair[1]);
            reEncode(pair[1], decodedStream, reEncodedStream);
        } else {
            int pos = pair[0];
            int len = pair[1];
            byte[] lastWritten = decodedStream.toByteArray();
            decodedStream.write(lastWritten, lastWritten.length - pos, len);
            for (int i = lastWritten.length - pos; i < len; ++i) {
                reEncode(lastWritten[i], decodedStream, reEncodedStream);
            }
        }
    }

    private void reEncode(byte b, ByteArrayRingBuffer decodedStream, OutputStream reEncodedStream) throws IOException {
        if (pos == maxSize) {
            // buffer is full, we need to write now
            for (int i = 0; i < pos; i++) {
                reEncodedStream.write(0x00);
                reEncodedStream.write(reEncodeBuffer[i]);
            }
        } else {
            reEncodeBuffer[++pos] = b;
            if (pos > minSize) {
                for (int i = pos; i > minSize; --i) {
                    int subSeqPos;
                    byte[] sequence = Arrays.copyOfRange(reEncodeBuffer, pos - i, i);
                    if ((subSeqPos = indexOf(decodedStream.toByteArray(), sequence)) != -1) {
                        // subsequence exists in decoded output, so write a reference to that subsequence
                        reEncodedStream.write(subSeqPos);
                        reEncodedStream.write(i);
                        break;
                    }
                }
            }
        }
    }

    private int indexOf(byte[] outerArray, byte[] smallerArray) {
        for(int i = 0; i < outerArray.length - smallerArray.length+1; ++i) {
            boolean found = true;
            for(int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i+j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }
}
