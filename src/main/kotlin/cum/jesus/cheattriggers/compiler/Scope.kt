package cum.jesus.cheattriggers.compiler

import cum.jesus.cheattriggers.compiler.symbol.FunSymbol
import cum.jesus.cheattriggers.compiler.symbol.VarSymbol
import java.util.NoSuchElementException

class Scope(val parent: Scope?) {
    val varSymbols = arrayListOf<VarSymbol>()
    val funSymbols = arrayListOf<FunSymbol>()

    fun findVarSymbol(name: String): VarSymbol {
        var env = this
        while (true) {
            val varSymbols = env.varSymbols
            val res = varSymbols.find { it.name == name }

            if (res != null)
                return res
            else if (env.parent != null)
                env = env.parent!!
            else
                throw NoSuchElementException("Unknown symbol: $name")
        }
    }

    fun findFunSymbol(name: String): FunSymbol {
        var env = this
        while (true) {
            val funSymbols = env.funSymbols
            val res = funSymbols.find { it.name == name }

            if (res != null)
                return res
            else if (env.parent != null)
                env = env.parent!!
            else
                throw NoSuchElementException("Unknown symbol: $name")
        }
    }
}