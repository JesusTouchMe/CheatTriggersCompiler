package cum.jesus.cts.type;

import cum.jesus.cts.ctir.type.Type;
import cum.jesus.cts.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class StructType extends cum.jesus.cts.type.Type {
    private static List<StructType> structTypes = new ArrayList<>();

    private String name;
    private List<Pair<cum.jesus.cts.type.Type, String>> fields;
    private int size;

    public StructType(String name, List<Pair<cum.jesus.cts.type.Type, String>> fields) {
        super(null);
        this.name = name;
        this.fields = fields;

        List<Type> fieldTypes = new ArrayList<>();

        for (Pair<cum.jesus.cts.type.Type, String> field : fields) {
            fieldTypes.add(field.first.getIRType());
        }

        cum.jesus.cts.ctir.type.StructType structType = cum.jesus.cts.ctir.type.StructType.get(name);
        structType.setFieldTypes(fieldTypes);
        irType = structType;
        size = (int) structType.getSize();
    }

    public static StructType findStructType(String name) {
        for (StructType type : structTypes) {
            if (type.name.equals(name)) {
                return type;
            }
        }

        return null;
    }

    public List<Pair<cum.jesus.cts.type.Type, String>> getBody() {
        return fields;
    }

    public void setBody(List<Pair<cum.jesus.cts.type.Type, String>> fields) {
        this.fields = fields;

        List<Type> fieldTypes = new ArrayList<>();

        for (Pair<cum.jesus.cts.type.Type, String> field : fields) {
            fieldTypes.add(field.first.getIRType());
        }

        ((cum.jesus.cts.ctir.type.StructType) irType).setFieldTypes(fieldTypes);
        size = (int) irType.getSize();
    }

    public Pair<Integer, Type> getMemberIndex(String member) {
        int i;
        for (i = 0; i < fields.size(); i++) {
            if (fields.get(i).second.equals(member)) {
                return new Pair<>(i, fields.get(i).first.getIRType());
            }
        }

        return new Pair<>(-1, null);
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean isStructType() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StructType that = (StructType) o;
        return size == that.size && Objects.equals(name, that.name) && Objects.equals(fields, that.fields) && Objects.equals(irType, that.irType);
    }
}
