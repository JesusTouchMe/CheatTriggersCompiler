package cum.jesus.cheattriggers.compiler.parsing.ast.statement

import cum.jesus.cheattriggers.compiler.Scope
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNodeType

data class Compound(val statements: ArrayList<AstNode>, val scope: Scope) : AstNode(AstNodeType.COMPOUND) {
    override fun toString(): String {
        var res = "({"
        for (statement in statements) {
            res += "\n"
            res += statement.toString()
        }
        res += "\n})"

        return res
    }
}