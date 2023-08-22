package org.apache.awt.imageio.stream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class MemoryCacheImageInputStream implements AutoCloseable {

    private final InputStream parent;

    private long streamPosition = 0;
    private byte bitBuffer = 0;
    private int bitBufferRemaining = 0;

    public MemoryCacheImageInputStream(InputStream parent) {
        super();
        this.parent = parent;
    }

    public long readBits(int bits) throws IOException {
        long result = 0;
        while (bits > 0) {
            if (bitBufferRemaining == 0) {
                // Refill buffer
                int nextByte = parent.read();
                if (nextByte == -1) throw new EOFException();
                streamPosition += 1;
                bitBuffer = (byte) nextByte;
                bitBufferRemaining = Byte.SIZE;
            }

            final int readCount = Math.min(bits, bitBufferRemaining);
            bitBufferRemaining -= readCount;
            final int readData = (bitBuffer >>> bitBufferRemaining) & ((1 << readCount) - 1);
            bits -= readCount;
            result |= ((long) readData) << bits;
        }

        return result;
    }

    public long getStreamPosition() {
        return streamPosition;
    }

    @Override
    public void close() throws IOException {
        parent.close();
    }
}
