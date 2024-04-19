package cum.jesus.cts.ctir.ir;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.OptimizationLevel;
import cum.jesus.cts.ctir.ir.Value;

public abstract class Global extends Value {
    public Global(Module module) {
        super(module, 0);
    }

    public abstract void optimize(OptimizationLevel optimizationLevel);
}
