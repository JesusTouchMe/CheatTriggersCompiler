package cum.jesus.cts.parsing.ast.statement;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.GlobalVariable;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.ctir.ir.constant.Constant;
import cum.jesus.cts.ctir.ir.instruction.AllocaInst;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.environment.LocalSymbol;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.type.Type;

import java.util.List;

public final class VariableDeclaration extends AstNode {
    private String name;
    private AstNode value;
    private boolean isGlobal;

    public VariableDeclaration(List<String> annotations, Type type, final String name, AstNode value, boolean isGlobal) {
        super(annotations);
        this.name = name;
        this.value = value;
        this.isGlobal = isGlobal;

        super.type = type;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        if (isGlobal) {
            GlobalVariable global = module.getOrInsertGlobal(name, type.getIRType());

            Constant initializer = builder.createConstantInt(0, type.getIRType());

            if (value != null) {
                Value initValue = value.emit(module, builder, scope);
                if (initValue instanceof Constant) {
                    initializer = (Constant) initValue;
                } else {
                    throw new RuntimeException("Initializer element must be constant.");
                }
            }

            global.setInitializer(initializer);

            return null;
        } else {
            AllocaInst alloca = builder.createAlloca(type.getIRType());

            if (value != null) {
                Value initValue = value.emit(module, builder, scope);
                builder.createStore(alloca, initValue);
            }

            scope.variables.put(name, new LocalSymbol(alloca, type));

            return alloca;
        }
    }

    @Override
    public String toString(int indentationLevel) {
        if (value == null) {
            return "(decl \"" + type.toString() + "\" \"" + name + "\")";
        } else {
            return "(def \"" + type.toString() + "\" \"" + name + "\" " + value.toString(indentationLevel) + ')';
        }
    }
}
