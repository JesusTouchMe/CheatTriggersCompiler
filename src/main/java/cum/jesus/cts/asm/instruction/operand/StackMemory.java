package cum.jesus.cts.asm.instruction.operand;

import cum.jesus.cts.asm.instruction.Operand;

public final class StackMemory extends Operand {
    /**
     * This address is not the actual address, rather the offset of regSB used by the VM memory controller to get stack memory
     */
    private final int address;

    public StackMemory(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }

    @Override
    public String ident() {
        return "[regSB+" + address + ']';
    }

    @Override
    public Operand clone() {
        return new StackMemory(address);
    }
}
