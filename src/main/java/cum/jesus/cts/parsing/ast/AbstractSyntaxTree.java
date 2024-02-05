package cum.jesus.cts.parsing.ast;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.environment.Environment;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public final class AbstractSyntaxTree {
    private final List<AstNode> nodes;

    public AbstractSyntaxTree() {
        this.nodes = new ArrayList<>();
    }

    public void put(AstNode node) {
        nodes.add(node);
    }

    public void emit(Module module, Builder builder, Environment scope) {
        for (AstNode node : nodes) {
            node.emit(module, builder, scope);
        }
    }

    public void print(PrintStream stream) {
        for (AstNode node : nodes) {
            stream.println(node.toString());
        }
    }
}
