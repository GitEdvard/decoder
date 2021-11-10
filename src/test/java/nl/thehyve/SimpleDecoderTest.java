package nl.thehyve;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class SimpleDecoderTest {

    @Test
    public void testDecodePair() throws IOException {
        PipedInputStream pipedStdout = new PipedInputStream();
        BufferedReader stdoutReader = new BufferedReader(
                new InputStreamReader(pipedStdout));
        ByteArrayRingBuffer stdout = new ByteArrayRingBuffer(new PipedOutputStream(pipedStdout));

        PipedInputStream pipedStderr = new PipedInputStream();
        BufferedReader stderrReader = new BufferedReader(
                new InputStreamReader(pipedStderr));
        BufferedOutputStream stderr = new BufferedOutputStream(
                new PipedOutputStream(pipedStderr));

        SimpleDecoder simpleDecoder = new SimpleDecoder();

        byte[] pair = {0x00, 0x61};
        simpleDecoder.decode(pair, stdout, stderr);
        stdout.flush();
        assertTrue(stdoutReader.ready());
        assertEquals(stdoutReader.read(), 0x61);

        byte[] pair2 = {0x00, 0x62};
        simpleDecoder.decode(pair2, stdout, stderr);
        stdout.flush();
        assertTrue(stdoutReader.ready());
        assertEquals(stdoutReader.read(), 0x62);

        byte[] pair3 = {0x02, 0x02};
        simpleDecoder.decode(pair3, stdout, stderr);
        stdout.flush();
        assertTrue(stdoutReader.ready());
        char[] decoded = new char[2];
        stdoutReader.read(decoded, 0, 2);
        assertArrayEquals(decoded, new char[] {0x61, 0x62});

        char[] reEncoded = new char[8];
        stderr.flush();
        stderrReader.read(reEncoded, 0, 8);
        assertArrayEquals(reEncoded, new char[] {0x00, 0x61, 0x00, 0x62, 0x00, 0x61, 0x00, 0x62});
    }
}