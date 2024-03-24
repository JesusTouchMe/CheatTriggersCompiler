package cum.jesus.cts.parsing.ast.global;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.*;
import cum.jesus.cts.ctir.ir.Function;
import cum.jesus.cts.ctir.ir.instruction.AllocaInst;
import cum.jesus.cts.ctir.type.FunctionType;
import cum.jesus.cts.ctir.type.PointerType;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.environment.LocalSymbol;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.parsing.ast.statement.ReturnStatement;
import cum.jesus.cts.type.Type;
import cum.jesus.cts.util.NameManglingUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class StructDefinition extends AstNode {
    private String name;
    private List<Method> methods;
    private List<Field> fields;

    public StructDefinition(List<String> annotations, String name, List<Method> methods, List<Field> fields) {
        super(annotations);

        this.name = name;
        this.methods = methods;
        this.fields = fields;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        if (scope.parent != null) {
            System.out.println("STRUCT WARNING: SCOPE IS NOT GLOBAL");
        }

        for (Method method : methods) {
            boolean isConstructor = method.returnType == null;

            List<cum.jesus.cts.ctir.type.Type> paramTypes = new ArrayList<>();
            List<Type> argTypes = new ArrayList<>();
            Type returnType = (isConstructor) ? Type.get(name) : method.returnType;

            if (!isConstructor) {
                paramTypes.add(PointerType.get(Type.get(name).getIRType()));
                argTypes.add(Type.get(name));
            }

            for (FunctionArgument param : method.params) {
                paramTypes.add(param.getType().getIRType());
                argTypes.add(param.getType());
            }

            String mangledName = NameManglingUtils.mangleFunction(Arrays.asList(name, method.name), argTypes, returnType);

            FunctionType functionType = FunctionType.get(returnType.getIRType(), paramTypes);
            Function func = Function.create(functionType, module, mangledName);
            scope.functions.put(mangledName, func);

            if (!isConstructor) {
                func.getArgument(0).setName("this");
            }

            int i = (isConstructor) ? 0 : 1;

            for (; i < func.getArgs().size(); i++) {
                if (isConstructor) {
                    func.getArgument(i).setName(method.params.get(i).getName());
                } else {
                    func.getArgument(i).setName(method.params.get(i - 1).getName());
                }
            }

            Block entry = Block.create("", func);
            builder.setInsertPoint(entry);

            for (Argument arg : func.getArgs()) {
                AllocaInst alloca = builder.createAlloca(arg.getType(), arg.getName());
                builder.createStore(alloca, arg);
                method.scope.variables.put(arg.getName(), new LocalSymbol(alloca, new Type(arg.getType())));
            }

            AllocaInst self = null; // has to be initialized, but it will always have a value if it's a constructor
            if (isConstructor) {
                self = builder.createAlloca(Type.get(name).getIRType());
                method.scope.variables.put("this", new LocalSymbol(self, Type.get(name)));
            }

            for (AstNode node : method.body) {
                node.emit(module, builder, method.scope);
            }

            if (isConstructor) {
                builder.createRet(builder.createLoad(self));
            } else {
                if (method.body.isEmpty() || !(method.body.get(method.body.size() - 1) instanceof ReturnStatement)) {
                    builder.createRet(null);
                }
            }
        }

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

            if (it.hasNext() || !methods.isEmpty()) {
                sb.append('\n');
            }
        }

        Iterator<Method> mit = methods.iterator();
        while (mit.hasNext()) {
            Method method = mit.next();

            for (int i = 0; i < indentationLevel + 1; i++) {
                sb.append("  ");
            }

            if (method.returnType == null) {
                sb.append("(constructor\n");
            } else {
                sb.append("(fn \"").append(method.returnType).append("\" ");
                sb.append("\"").append(method.name).append("\"\n");
            }

            Iterator<FunctionArgument> fit = method.params.iterator();
            while (fit.hasNext()) {
                FunctionArgument arg = fit.next();

                for (int i = 0; i < indentationLevel + 1; i++) {
                    sb.append("  ");
                }

                sb.append(arg.toString());

                if (fit.hasNext()) {
                    sb.append("\n");
                } else if (!method.body.isEmpty()) {
                    sb.append("\n");
                }
            }

            Iterator<AstNode> bit = method.body.iterator();
            while (bit.hasNext()) {
                AstNode node = bit.next();

                for (int i = 0; i < indentationLevel + 1; i++) {
                    sb.append("  ");
                }

                sb.append(node.toString(indentationLevel + 1));

                if (bit.hasNext()) {
                    sb.append("\n");
                }
            }

            sb.append(')');

            if (mit.hasNext()) {
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

    public static final class Method {
        public AccessLevel accessLevel;
        public Type returnType;
        public String name;
        public List<FunctionArgument> params;
        public Environment scope;
        public List<AstNode> body;

        public Method(AccessLevel accessLevel, Type returnType, String name, List<FunctionArgument> params, Environment scope, List<AstNode> body) {
            this.accessLevel = accessLevel;
            this.returnType = returnType;
            this.name = name;
            this.params = params;
            this.scope = scope;
            this.body = body;
        }
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
