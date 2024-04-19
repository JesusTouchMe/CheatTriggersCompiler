package cum.jesus.cts.parsing.ast.expression;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.ctir.ir.Function;
import cum.jesus.cts.ctir.ir.Value;
import cum.jesus.cts.ctir.ir.instruction.AllocaInst;
import cum.jesus.cts.ctir.ir.instruction.Instruction;
import cum.jesus.cts.ctir.type.FunctionType;
import cum.jesus.cts.ctir.type.PointerType;
import cum.jesus.cts.ctir.type.Type;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.util.NameManglingUtils;

import java.util.*;

public final class CallExpression extends AstNode {
    private AstNode callee;
    private List<AstNode> params;

    public CallExpression(List<String> annotations, AstNode callee, List<AstNode> params) {
        super(annotations);
        this.callee = callee;
        this.params = params;
    }

    @Override
    public Value emit(Module module, Builder builder, Environment scope) {
        List<Value> argValues = new ArrayList<>(params.size());

        List<Type> argTypes = new ArrayList<>(params.size());
        List<cum.jesus.cts.type.Type> paramTypes = new ArrayList<>(params.size());

        for (AstNode param : params) {
            Value value = param.emit(module, builder, scope);

            if (param.getType().isStructType()) {
                //parameters.add(builder.createAddrOf((AllocaInst) Module.getPointerOperand(value)));
                Instruction inst = (Instruction) value;
                Value ptr = Module.getPointerOperand(inst);

                inst.eraseFromParent();

                argValues.add(builder.createAddrOf((AllocaInst) ptr));
            } else {
                argValues.add(value);
            }

            argTypes.add(param.getType().getIRType());
            paramTypes.add(param.getType());
        }

        Value calleeValue = null;
        Type returnType = null;

        if (callee instanceof Variable) {
            if (cum.jesus.cts.type.Type.exists(((Variable) callee).getName())) {
                String name = ((Variable) callee).getName();
                String mangledName = NameManglingUtils.getMangledFunction(Arrays.asList(name, name), paramTypes);

                Function func = scope.findFunction(mangledName).orElse(null);

                type = cum.jesus.cts.type.Type.get(name);
                returnType = type.getIRType();
                calleeValue = func;
            } else {
                String name = ((Variable) callee).getName();
                String mangledName = NameManglingUtils.getMangledFunction(Collections.singletonList(name), paramTypes);

                Function func = scope.findFunction(mangledName).orElse(null);

                returnType = func.getReturnType();
                type = new cum.jesus.cts.type.Type(returnType);

                calleeValue = func;
            }
        } else if (callee instanceof BinaryExpression) { // class or struct method members
            BinaryExpression binOp = (BinaryExpression) callee;

            if (binOp.getOperator() == BinaryExpression.Operator.MEMBER_ACCESS) {
                String methodName = ((Variable) binOp.getRight()).getName();
                String mangledName;

                if (binOp.getLeft() instanceof Variable && Environment.scopes.containsKey(((Variable) binOp.getLeft()).getName())) {
                    Environment leftScope = Environment.scopes.get(((Variable) binOp.getLeft()).getName());
                    mangledName = NameManglingUtils.getMangledFunction(Collections.singletonList(methodName), paramTypes);

                    calleeValue = binOp.getRight().emit(Environment.modules.get(((Variable) binOp.getLeft()).getName()), builder, leftScope);
                    assert calleeValue instanceof Function;

                    returnType = ((Function) calleeValue).getReturnType();
                    if (returnType.isStructType()) {
                        type = cum.jesus.cts.type.StructType.findStructType(returnType.getName());
                    } else {
                        type = new cum.jesus.cts.type.Type(returnType);
                    }
                } else {
                    Value value = binOp.getLeft().emit(module, builder, scope);
                    if (!binOp.getLeft().getType().isStructType()) {
                        params.add(0, binOp.getLeft());
                        argValues.add(0, value);

                        argTypes.add(0, params.get(0).getType().getIRType());
                        paramTypes.add(0, params.get(0).getType());

                        mangledName = NameManglingUtils.getMangledFunction(Collections.singletonList(methodName), paramTypes, true);
                    } else {
                        Value self = Module.getPointerOperand(value);
                        String className = binOp.getLeft().getType().getIRType().getName();
                        if (className.contains(".")) {
                            className = className.substring(0, className.indexOf('.'));
                        }

                        if (self == null) {
                            self = builder.createAlloca(binOp.getLeft().getType().getIRType());
                            builder.createStore(self, value);
                        }

                        params.add(0, binOp.getLeft());
                        argValues.add(0, self);

                        argTypes.add(0, PointerType.get(params.get(0).getType().getIRType()));
                        paramTypes.add(0, params.get(0).getType());

                        mangledName = NameManglingUtils.getMangledFunction(Arrays.asList(className, methodName), paramTypes);
                    }

                    Function func = scope.findFunction(mangledName).orElse(null);

                    returnType = func.getReturnType();
                    if (returnType.isStructType()) {
                        type = cum.jesus.cts.type.StructType.findStructType(returnType.getName());
                    } else {
                        type = new cum.jesus.cts.type.Type(returnType);
                    }

                    calleeValue = func;
                }
            }
        } else {
            calleeValue = callee.emit(module, builder, scope);
            returnType = calleeValue.getType();
            type = new cum.jesus.cts.type.Type(returnType);
        }

        FunctionType functionType = FunctionType.get(returnType, argTypes);

        return builder.createCall(calleeValue.getModule(), functionType, calleeValue, argValues);
    }

    @Override
    public String toString(int indentationLevel) {
        StringBuilder sb = new StringBuilder();
        sb.append("(call \"").append(callee).append("\" (list ");

        Iterator<AstNode> it = params.iterator();
        while (it.hasNext()) {
            AstNode param = it.next();

            sb.append(param.toString(indentationLevel + 1));

            if (it.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append("))");
        return sb.toString();
    }
}
