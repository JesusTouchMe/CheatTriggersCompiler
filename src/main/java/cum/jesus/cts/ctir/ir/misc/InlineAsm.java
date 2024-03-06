package cum.jesus.cts.ctir.ir.misc;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.lexing.Lexer;
import cum.jesus.cts.asm.lexing.Token;
import cum.jesus.cts.asm.lexing.TokenType;
import cum.jesus.cts.asm.parsing.Parser;
import cum.jesus.cts.ctir.ir.Block;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.error.DefaultErrorReporter;
import cum.jesus.cts.error.ErrorReporter;
import cum.jesus.cts.util.NumberUtils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class InlineAsm extends Value {
    private Block parent;
    private String asmCode;
    private List<Integer> params;

    public InlineAsm(Block parent, int id, String asmCode, List<Value> params) {
        super(parent.getParent().getModule(), id);
        this.parent = parent;
        this.asmCode = asmCode;
        this.params = new ArrayList<>();
        for (Value param : params) {
            this.params.add(param.getId());
        }
    }

    @Override
    public boolean requiresRegister() {
        return false;
    }

    @Override
    public List<Integer> getOperands() {
        List<Integer> operands = new ArrayList<>();

        for (int param : params) {
            List<Integer> paramOperands = parent.getParent().getValue(param).getOperands();
            operands.addAll(paramOperands);
            operands.add(param);
        }

        return operands;
    }

    @Override
    public void print(PrintStream stream) {
        stream.printf("asm unsafe nooptimize \"%s\" (", asmCode.replace("\n", "\\n"));

        Iterator<Integer> it = params.iterator();
        while (it.hasNext()) {
            stream.print(parent.getParent().getValue(it.next()).ident());
            if (it.hasNext()) {
                stream.print(", ");
            }
        }
        stream.print(")");
    }

    @Override
    public String ident() {
        return "%undef";
    }

    @Override
    public void emit(List<AsmValue> values) {
        Lexer lexer = new Lexer(asmCode);
        List<Token> tokens = lexer.tokenize();

        ErrorReporter errorReporter = new DefaultErrorReporter();
        Parser parser = new Parser(parent.getParent().getModule().getName(), tokens, errorReporter)
                .withSpecialOperandParser((tokenStream -> {
                    assert tokenStream.current().getType() == TokenType.LEFT_BRACE;
                    tokenStream.consume();
                    assert tokenStream.current().getType() == TokenType.IMMEDIATE;
                    int value = NumberUtils.parseInt(tokenStream.consume().getText());
                    assert tokenStream.current().getType() == TokenType.RIGHT_BRACE;
                    tokenStream.consume();
                    return parent.getEmittedValue(params.get(value));
                }));
        List<AsmValue> asmValues = parser.parse();

        values.addAll(asmValues);
    }
}
