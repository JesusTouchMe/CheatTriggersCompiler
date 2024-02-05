package cum.jesus.cts.util;

import java.io.ByteArrayOutputStream;

public final class ByteBuffer extends ByteArrayOutputStream {
    public ByteBuffer(int size) {
        super(size);
    }

    public ByteBuffer() {
        this(32);
    }

    public byte[] getInternalBuffer() {
        return super.buf;
    }
}
