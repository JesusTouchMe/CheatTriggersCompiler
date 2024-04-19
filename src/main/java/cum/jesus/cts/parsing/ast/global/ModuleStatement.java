package cum.jesus.cts.parsing.ast.global;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;

import java.util.List;

public final class ModuleStatement extends AstNode {
    private final String name;

    public ModuleStatement(List<String> annotations, String name) {
        super(annotations);

        this.name = name;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        module.setName(name);
        Environment.modules.put(name, module);

        return null;
    }

    @Override
    public String toString(int indentationLevel) {
        return "(module " + name + ")";
    }
}
