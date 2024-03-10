package cum.jesus.cts.ctir.type;

import java.util.List;

public final class StructType extends Type {
    private List<Type> fieldTypes;

    StructType(String name) {
        super(0, name);
    }

    public static StructType get(String name) {
        return (StructType) Type.getStructType(name);
    }

    public List<Type> getFieldTypes() {
        return fieldTypes;
    }

    public void setFieldTypes(List<Type> fieldTypes) {
        this.fieldTypes = fieldTypes;

        for (Type type : fieldTypes) {
            size += type.getSize();
        }
    }
}
