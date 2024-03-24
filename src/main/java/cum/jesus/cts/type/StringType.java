package cum.jesus.cts.type;

import cum.jesus.cts.ctir.type.Type;

public final class StringType extends cum.jesus.cts.type.Type {
    public StringType() {
        super(Type.getStringType());
    }

    @Override
    public boolean isStringType() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StringType;
    }
}
