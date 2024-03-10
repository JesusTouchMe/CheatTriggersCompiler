package cum.jesus.cts.type;

import cum.jesus.cts.ctir.type.Type;

public final class VoidType extends cum.jesus.cts.type.Type {
    public VoidType() {
        super(Type.getVoidType());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VoidType;
    }
}
