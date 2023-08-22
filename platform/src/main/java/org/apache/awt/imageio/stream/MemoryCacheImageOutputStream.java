package org.apache.awt.imageio.stream;

import java.io.IOException;
import java.io.OutputStream;

public class MemoryCacheImageOutputStream implements AutoCloseable {

    private final OutputStream parent;

    private byte bitBuffer = 0;
    private int bitBufferCount = 0;

    public MemoryCacheImageOutputStream(OutputStream parent) {

        this.parent = parent;
    }

    public void writeBits(long bits, int numBits) throws IOException {
        while (numBits > 0) {
            int remainingBufferCapacity = Byte.SIZE - bitBufferCount;
            int writeBitAmount = Math.min(remainingBufferCapacity, numBits);
            int remainingBits = numBits - writeBitAmount;
            long write = (bits >>> remainingBits) & ((1L << writeBitAmount) - 1);
            bitBuffer |= write << (Byte.SIZE - writeBitAmount - bitBufferCount);
            bitBufferCount += writeBitAmount;
            numBits -= writeBitAmount;
            if (bitBufferCount == Byte.SIZE) {
                flushBits();
            } else break;
        }
    }

    private void flushBits() throws IOException {
        parent.write(bitBuffer);
        bitBuffer = 0;
        bitBufferCount = 0;
    }

    public void flush() throws IOException {
        if (bitBufferCount > 0) {
            flushBits();
        }
        parent.flush();
    }

    @Override
    public void close() throws IOException {
        if (bitBufferCount > 0) {
            flushBits();
        }
        parent.close();
    }
}
