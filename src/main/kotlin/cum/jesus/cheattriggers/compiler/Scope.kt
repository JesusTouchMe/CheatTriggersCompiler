package cum.jesus.cheattriggers.compiler

import cum.jesus.cheattriggers.compiler.symbol.FunSymbol
import cum.jesus.cheattriggers.compiler.symbol.Symbol
import cum.jesus.cheattriggers.compiler.symbol.VarSymbol

class Scope(val parent: Scope?) {
    val varSymbols = arrayListOf<VarSymbol>()
    val funSymbols = arrayListOf<FunSymbol>()
    val orderedSymbols = arrayListOf<Symbol>()

    val isGlobal = parent == null

    var stdSymbols = 0

    fun addSymbol(symbol: Symbol) {
        orderedSymbols.add(symbol)
        when (symbol) {
            is VarSymbol -> varSymbols.add(symbol)
            is FunSymbol -> funSymbols.add(symbol)
        }
    }

    fun addStdSymbol(symbol: Symbol) {
        require(symbol.name in std) { "Added a std symbol that is not an std symbol" }

        if (symbol in orderedSymbols) return

        orderedSymbols.add(0, symbol)
        when (symbol) {
            is VarSymbol -> varSymbols.add(0, symbol)
            is FunSymbol -> funSymbols.add(0, symbol)
        }

        stdSymbols++
    }

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

    fun hasSymbol(name: String): Boolean {
        var env = this
        while (true) {
            val symbols = env.orderedSymbols
            val res = symbols.find { it.name == name }

            if (res != null)
                return true
            else if (env.parent != null)
                env = env.parent!!
            else
                return false
        }
    }

    fun hasVarSymbol(name: String): Boolean {
        var env = this
        while (true) {
            val symbols = env.varSymbols
            val res = symbols.find { it.name == name }

            if (res != null)
                return true
            else if (env.parent != null)
                env = env.parent!!
            else
                return false
        }
    }

    fun hasFunSymbol(name: String): Boolean {
        var env = this
        while (true) {
            val symbols = env.funSymbols
            val res = symbols.find { it.name == name }

            if (res != null)
                return true
            else if (env.parent != null)
                env = env.parent!!
            else
                return false
        }
    }

    override fun toString(): String {
        return if (parent != null) "(vars$varSymbols, funs$funSymbols, parent$parent)" else "(vars$varSymbols, funs$funSymbols)"
    }
}