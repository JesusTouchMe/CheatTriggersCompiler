package cum.jesus.cts.ctir.ir.constant;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Value;

public abstract class Constant extends Value {
    public Constant(Module module, int id) {
        super(module, id);
    }
}
