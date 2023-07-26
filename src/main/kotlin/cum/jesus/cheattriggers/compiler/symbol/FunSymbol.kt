package cum.jesus.cheattriggers.compiler.symbol

data class FunSymbol(override val name: String) : Symbol {
    override fun equals(other: Any?) = eq(other)

    override fun hashCode() = name.hashCode()
}