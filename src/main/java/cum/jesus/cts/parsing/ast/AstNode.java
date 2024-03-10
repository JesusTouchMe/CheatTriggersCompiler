package cum.jesus.cts.parsing.ast;

import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.type.Type;

import java.util.List;

public abstract class AstNode {
    protected Type type;
    protected List<String> annotations;

    protected AstNode(List<String> annotations) {
        this.annotations = annotations;
    }

    public abstract Value emit(Module module, Builder builder, Environment scope);

    public abstract String toString(int indentationLevel);

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void printAnnotations() {
        System.out.println(annotations);
    }
}
