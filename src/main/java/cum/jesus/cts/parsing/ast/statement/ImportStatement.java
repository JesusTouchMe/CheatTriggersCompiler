package cum.jesus.cts.parsing.ast.statement;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;

import java.util.List;

public final class ImportStatement extends AstNode {
    private final String name;

    public ImportStatement(List<String> annotations, String name) {
        super(annotations);
        this.name = name;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        module.addImport(name);
        return null;
    }

    @Override
    public String toString(int indentationLevel) {
        return "(import " + name + ")";
    }
}
