package cum.jesus.cts.util;

import cum.jesus.cts.type.Type;

import java.util.ArrayList;
import java.util.List;

public final class NameManglingUtils {
    private static final List<FunctionSignature> mangledFunctions = new ArrayList<>();

    public static String mangleFunction(List<String> identifiers, List<Type> params, Type returnType, boolean isExtension) {
        if (identifiers.get(0).equals("main")) {
            mangledFunctions.add(new FunctionSignature(identifiers, params, returnType, isExtension));
            return identifiers.get(0);
        }

        StringBuilder sb = new StringBuilder("_Z"); // _Z is a reserved prefix so this won't fuck with anything
        if (identifiers.size() > 1) {
            sb.append("N"); // when there's more than 1 identifier for a function name, it means it's under some namespace. this means that one 1 more namespaces are present
        }

        for (String ident : identifiers) {
            sb.append(ident.length());
            sb.append(ident);
        }

        sb.append('A');
        if (isExtension) {
            sb.append('T');
        }

        sb.append(params.size());
        for (Type param : params) {
            sb.append(param.getMangleID());
        }

        sb.append('E');
        sb.append(returnType.getMangleID());

        mangledFunctions.add(new FunctionSignature(identifiers, params, returnType, isExtension));

        return sb.toString();
    }

    public static String mangleFunction(List<String> identifiers, List<Type> params, Type returnType) {
        return mangleFunction(identifiers, params, returnType, false);
    }

    private static String mangleFunction(FunctionSignature func, boolean isExtension) {
        StringBuilder sb = new StringBuilder("_Z"); // _Z is a reserved prefix so this won't fuck with anything
        if (func.identifiers.size() > 1) {
            sb.append("N"); // when there's more than 1 identifier for a function name, it means it's under some namespace. this means that one 1 more namespaces are present
        }

        for (String ident : func.identifiers) {
            sb.append(ident.length());
            sb.append(ident);
        }

        sb.append('A');
        if (isExtension) {
            sb.append('T');
        }

        sb.append(func.params.size());
        for (Type param : func.params) {
            sb.append(param.getMangleID());
        }

        sb.append('E');
        sb.append(func.returnType.getMangleID());

        return sb.toString();
    }

    private static String mangleFunction(FunctionSignature func) {
        return mangleFunction(func, false);
    }

    public static String getMangledFunction(List<String> identifiers, List<Type> params, boolean isExtension) {
        if (identifiers.get(0).equals("main")) {
            return identifiers.get(0);
        }

        StringBuilder sb = new StringBuilder();

        for (String ident : identifiers) {
            sb.append(ident.length());
            sb.append(ident);
        }

        String name = sb.toString();

        for (FunctionSignature func : mangledFunctions) {
            if (func.identifiers.equals(identifiers) && func.params.size() == params.size() && isExtension == func.isExtension) {
                boolean found = true;

                for (int i = 0; i < params.size(); i++) {
                    if (!func.params.get(i).getIRType().equals(params.get(i).getIRType())) {
                        found = false;
                        break;
                    }
                }

                if (found) {
                    return mangleFunction(func, isExtension);
                }
            }
        }

        return "ERROR";
    }

    public static String getMangledFunction(List<String> identifiers, List<Type> params) {
        return getMangledFunction(identifiers, params, false);
    }

    private static final class FunctionSignature {
        List<String> identifiers;
        List<Type> params;
        Type returnType;
        boolean isExtension;

        public FunctionSignature(List<String> identifiers, List<Type> params, Type returnType, boolean isExtension) {
            this.identifiers = identifiers;
            this.params = params;
            this.returnType = returnType;
            this.isExtension = isExtension;
        }
    }
}
