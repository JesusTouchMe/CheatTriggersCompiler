package cum.jesus.cts.asm.instruction.operand;

import cum.jesus.cts.asm.instruction.Operand;

public final class StringOperand extends Operand {
    private final String text;

    public StringOperand(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public Operand clone() {
        return new StringOperand(text);
    }
}
