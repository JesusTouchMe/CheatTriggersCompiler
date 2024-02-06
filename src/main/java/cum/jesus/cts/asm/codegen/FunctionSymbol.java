package cum.jesus.cts.asm.codegen;

import java.io.IOException;
import java.io.OutputStream;

public final class FunctionSymbol {
    public final String name;
    public final int location;

    public FunctionSymbol(String name, int location) {
        this.name = name;
        this.location = location;
    }

    public void writeTo(OutputStream stream) throws IOException {
        stream.write(name.getBytes());
        stream.write(0);
        stream.write((location >> 24) & 0xFF);
        stream.write((location >> 16) & 0xFF);
        stream.write((location >> 8) & 0xFF);
        stream.write(location & 0xFF);
    }
}
