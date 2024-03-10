package cum.jesus.cts.parsing.ast.global;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.type.Type;

import java.util.Iterator;
import java.util.List;

public final class StructDefinition extends AstNode {
    private String name;
    private List<Field> fields;

    public StructDefinition(List<String> annotations, String name, List<Field> fields) {
        super(annotations);

        this.name = name;
        this.fields = fields;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        return null;
    }

    @Override
    public String toString(int indentationLevel) {
        StringBuilder sb = new StringBuilder();
        sb.append("(struct \"").append(name).append("\"\n");

        Iterator<Field> it = fields.iterator();
        while (it.hasNext()) {
            Field field = it.next();

            for (int i = 0; i < indentationLevel + 1; i++) {
                sb.append("  ");
            }

            sb.append("(decl \"").append(field.type.toString()).append("\" ");
            sb.append("\"").append(field.name).append("\")");

            if (it.hasNext()) {
                sb.append('\n');
            }
        }

        sb.append(')');

        return sb.toString();
    }

    public enum AccessLevel {
        PUBLIC,
        PRIVATE,
    }

    public static final class Field {
        public AccessLevel accessLevel;
        public Type type;
        public String name;

        public Field(AccessLevel accessLevel, Type type, String name) {
            this.accessLevel = accessLevel;
            this.type = type;
            this.name = name;
        }
    }
}
