package cum.jesus.cts.asm.codegen;

import cum.jesus.cts.util.ByteBuffer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class OutputBuffer {
    private final ByteBuffer buf;
    private final Map<String, Integer> symbols;

    private final List<FunctionSymbol> functionSymbols;
    private final List<ConstPoolEntry> constantPool;

    public OutputBuffer() {
        buf = new ByteBuffer();
        symbols = new HashMap<>();
        functionSymbols = new ArrayList<>();
        constantPool = new ArrayList<>();
    }

    public void writeb(byte b) {
        buf.write(b);
    }

    public void writes(short s) {
        buf.write((s >> 8) & 0xFF);
        buf.write(s & 0xFF);
    }

    public void writei(int i) {
        buf.write((i >> 24) & 0xFF);
        buf.write((i >> 16) & 0xFF);
        buf.write((i >> 8) & 0xFF);
        buf.write(i & 0xFF);
    }

    public void writel(long l) {
        buf.write((int) ((l >> 56) & 0xFF));
        buf.write((int) ((l >> 48) & 0xFF));
        buf.write((int) ((l >> 40) & 0xFF));
        buf.write((int) ((l >> 32) & 0xFF));
        buf.write((int) ((l >> 24) & 0xFF));
        buf.write((int) ((l >> 16) & 0xFF));
        buf.write((int) ((l >> 8) & 0xFF));
        buf.write((int) (l & 0xFF));
    }

    // polymorphic versions of the write functions

    public void write(byte b) {
        writeb(b);
    }

    public void write(short s) {
        writes(s);
    }

    public void write(int i) {
        writei(i);
    }

    public void write(long l) {
        writel(l);
    }

    public int getPosition() {
        return buf.size();
    }

    public void addSymbol(String name, int value) {
        symbols.put(name, value);
    }

    /**
     * Function symbol for the vm but yeah
     */
    public void addGlobalSymbol(final String name, int value) {
        functionSymbols.add(new FunctionSymbol(name, value));
    }

    public void declareNewFunction(final String name) {
        functionSymbols.add(new FunctionSymbol(name, getPosition()));
    }

    public void addConstant(ConstPoolEntry entry) {
        constantPool.add(entry);
    }

    public int getSymbol(final String name) {
        return symbols.getOrDefault(name, -1);
    }

    public boolean hasSymbol(final String name) {
        return symbols.containsKey(name);
    }

    public void patchForwardSymbol(final String name, OperandSize size, int location, int origin) {
        long symbol = getSymbol(name) - origin;
        byte[] buffer = buf.getInternalBuffer();
        switch (size) {
            case BYTE:
                buffer[location] = (byte) symbol;
                break;
            case WORD:
                buffer[location] = (byte) ((symbol >> 8) & 0xFF);
                buffer[location + 1] = (byte) (symbol & 0xFF);
                break;
            case DWORD:
                buffer[location + 3] = (byte) ((symbol >> 24) & 0xFF);
                buffer[location + 2] = (byte) ((symbol >> 16) & 0xFF);
                buffer[location + 1] = (byte) ((symbol >> 8) & 0xFF);
                buffer[location] = (byte) (symbol & 0xFF);
                break;
            case QWORD:
                buffer[location + 7] = (byte) ((symbol >> 56) & 0xFF);
                buffer[location + 6] = (byte) ((symbol >> 48) & 0xFF);
                buffer[location + 5] = (byte) ((symbol >> 40) & 0xFF);
                buffer[location + 4] = (byte) ((symbol >> 32) & 0xFF);
                buffer[location + 3] = (byte) ((symbol >> 24) & 0xFF);
                buffer[location + 2] = (byte) ((symbol >> 16) & 0xFF);
                buffer[location + 1] = (byte) ((symbol >> 8) & 0xFF);
                buffer[location] = (byte) (symbol & 0xFF);
                break;
            case NONE:
                break;
        }
    }

    public void emit(OutputStream stream) throws IOException {
        stream.write(new byte[] {'v', '1', 0, 'F', ' '});
        for (FunctionSymbol function : functionSymbols) {
            function.writeTo(stream);
        }
        stream.write(0);
        stream.write(0);

        for (ConstPoolEntry entry : constantPool) {
            entry.writeTo(stream);
        }
        stream.write(0);
        stream.write(0);

        stream.write(new byte[] { 'C', 'o', 'd', 'e' });

        buf.writeTo(stream);
    }
}
