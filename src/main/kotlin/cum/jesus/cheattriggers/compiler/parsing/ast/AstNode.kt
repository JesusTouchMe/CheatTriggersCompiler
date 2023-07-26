package cum.jesus.cheattriggers.compiler.parsing.ast

enum class AstNodeType {
    INTEGER,
    FLOAT,

    BINARY_EXPRESSION,
    UNARY_EXPRESSION,

    COMPOUND,
    IF, WHILE, FOR,

    VARIABLE_DECLARATION,
    VARIABLE,
    FUNCTION,
    CALL,
}

interface IPrimitive {
    val value: Any;
}

open class AstNode(val nodeType: AstNodeType) {

}