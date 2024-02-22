package cum.jesus.cts.asm;

public enum Types {
    BYTE(0x01),
    SHORT(0x02),
    INT(0x03),
    LONG(0x04),

    STRING(0x05),

    /**
     * Special type which associates a function handle at runtime when loading the constant pool. Only has a function name in file
     */
    FUNCTION(0x06),

    ;

    private final int typeCode;

    Types(int typeCode) {
        this.typeCode = typeCode;
    }

    public byte toByte() {
        return (byte) typeCode;
    }
}
