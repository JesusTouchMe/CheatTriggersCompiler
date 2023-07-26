package cum.jesus.cheattriggers.compiler.symbol

interface Symbol {
    val name: String

    fun eq(other: Any?) = (other is Symbol && other.name == this.name)
}