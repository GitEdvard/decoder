package nl.thehyve;

import java.io.*;
import java.util.Arrays;

class ByteArrayRingBuffer extends PrintStream {

    final int capacity = 256; // the definition of the encoding does not require a larger buffer
    byte[] data;
    int pos = 0;
    boolean filled = false;

    public ByteArrayRingBuffer(OutputStream stream) {
        super(stream);
        data = new byte[capacity];
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) {
        for (int i = off; i < len; ++i) {
            write(b[i]);
        }
    }

    @Override
    public synchronized void write(int b) {
        if (pos == capacity) {
            filled = true;
            pos = 0;
        }
        data[pos++] = (byte) b;
        super.write((byte) b);
    }

    public byte[] toByteArray() {
        if (!filled) {
            return Arrays.copyOf(data, pos);
        }
        byte[] ret = new byte[capacity];
        System.arraycopy(data, pos, ret, 0, capacity - pos);
        System.arraycopy(data, 0, ret, capacity - pos, pos);
        return ret;
    }

//    public byte[] toByteArray(int len) {
//        if (!filled) {
//            return Arrays.copyOfRange(data, pos, len);
//        }
//        byte[] ret = new byte[len];
//        System.arraycopy(data, pos, ret, 0, capacity - pos);
//        System.arraycopy(data, 0, ret, capacity - pos, pos);
//        return ret;
//    }
}

