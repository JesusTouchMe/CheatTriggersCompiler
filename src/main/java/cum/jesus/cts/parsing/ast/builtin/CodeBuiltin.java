package cum.jesus.cts.parsing.ast.builtin;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.type.Type;

import java.util.ArrayList;
import java.util.List;

public final class CodeBuiltin extends AstNode {
    private String asmCode;
    private List<AstNode> params;

    public CodeBuiltin(String asmCode, List<AstNode> params) {
        this.asmCode = asmCode;
        this.params = params;

        super.type = Type.getVoidType();
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        int requiredParams = -1;
        StringBuilder sb = new StringBuilder();
        boolean getNumbers = false;

        for (char c : asmCode.toCharArray()) {
            if (c == '{') {
                getNumbers = true;
                sb.setLength(0);
            } else if (c == '}') {
                if (sb.length() > 0) {
                    int number = Integer.parseInt(sb.toString());
                    if (number > requiredParams) {
                        requiredParams = number;
                    }
                    sb.setLength(0);
                }
                getNumbers = false;
            } else if (Character.isDigit(c) && getNumbers) {
                sb.append(c);
            } else {
                sb.setLength(0);
            }
        }
        sb = null;

        if (requiredParams + 1 != params.size()) {
            throw new IllegalArgumentException("Amount of parameters doesn't match the required amount of parameters");
        }

        List<Value> parameters = new ArrayList<>();
        for (AstNode param : params) {
            parameters.add(param.emit(module, builder, scope));
        }

        return builder.createInlineAsm(asmCode, parameters);
    }

    @Override
    public String toString() {
        return "asm {\n" + asmCode + "\n}";
    }
}
