package cum.jesus.cts.parsing.ast;

import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.type.Type;

public abstract class AstNode {
    protected Type type;

    public abstract Value emit(Module module, Builder builder, Environment scope);

    @Override
    public abstract String toString();

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
