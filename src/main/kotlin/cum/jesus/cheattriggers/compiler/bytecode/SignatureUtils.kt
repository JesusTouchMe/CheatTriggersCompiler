package cum.jesus.cheattriggers.compiler.bytecode

import cum.jesus.cheattriggers.compiler.Scope
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.IPrimitive
import cum.jesus.cheattriggers.compiler.parsing.ast.expression.*
import cum.jesus.cheattriggers.compiler.parsing.ast.statement.*
import cum.jesus.cheattriggers.compiler.parsing.ast.statement.Function

/**
 * Utility class for generating signatures
 */
object SignatureUtils {
    const val END_TABLE: UByte = 0x01u
    const val GLOBAL_TABLE: UByte = 0x02u
    const val FAST_TABLE: UByte = 0x03u
    const val NAME_TABLE: UByte = 0x04u
    const val CONST_TABLE: UByte = 0x05u

    const val STD_AMOUNT: UByte = 0x06u

    const val FUNCTION_SIGNATURE: UByte = 0x06u
    const val PROGRAM: UByte = 0x07u

    const val BYTECODE: UByte = 0x08u

    val globalTable = LinkedHashMap<String, Int>()
    private var i = 0

    fun makeGlobals(globalScope: Scope): ByteArray {
        val bytes = ArrayList<UByte>()

        if (globalScope.orderedSymbols.isEmpty())
            return ByteArray(0)

        if (globalScope.stdSymbols > 0) {
            bytes.add(STD_AMOUNT)
            bytes.addAll(globalScope.stdSymbols.toString().toByteArray())
            bytes.add(NUL)
        }

        bytes.add(GLOBAL_TABLE)
        for (symbol in globalScope.orderedSymbols) {
            bytes.addAll(symbol.name.toByteArray())
            bytes.add(NUL)

            globalTable.put(symbol.name, i)
            i++
        }

        bytes.removeLast()
        bytes.add(END_TABLE)

        val byteArray = ByteArray(bytes.size)
        for (i in byteArray.indices) {
            byteArray[i] = bytes[i].toByte()
        }
        return byteArray
    }

    fun makeFunctionSignature(func: Function): Pair<FunctionSignature, ByteArray> {
        val bytes = ArrayList<UByte>()
        var fastIndex = 0
        val fastTable = LinkedHashMap<String, Int>()
        var nameIndex = 0
        val nameTable = LinkedHashMap<String, Int>()
        var constIndex = 0
        val constTable = LinkedHashMap<String, Int>()

        bytes.add(FUNCTION_SIGNATURE)
        bytes.addAll(func.name.toByteArray())
        bytes.add(NUL)

        // fast table
        if (func.args.isNotEmpty()) {
            bytes.add(FAST_TABLE)
            for (arg in func.args) {
                fastTable[arg] = fastIndex
                fastIndex++
                bytes.addAll(arg.toByteArray())
                bytes.add(NUL)
            }

            bytes.removeLast()
            bytes.add(END_TABLE)
        }

        // name table
        if (func.scope.orderedSymbols.isNotEmpty()) {
            bytes.add(NAME_TABLE)
            for (symbol in func.scope.varSymbols) {
                nameTable[symbol.name] = nameIndex
                nameIndex++
                bytes.addAll(symbol.name.toByteArray())
                bytes.add(NUL)
            }

            bytes.removeLast()
            bytes.add(END_TABLE)
        }

        // const table
        bytes.add(CONST_TABLE)
        constTable["null"] = constIndex
        constIndex++
        bytes.addAll("null".toByteArray())

        if (containsPrimitive(func)) {
            bytes.add(NUL)

            val primitiveValues = ArrayList<Any>()

            fun checkNodes(node: AstNode?) {
                fun primitiveCheck(node: AstNode?): Boolean {
                    if (node is IPrimitive) {
                        if (primitiveValues.contains(node.value)) return true

                        primitiveValues.add(node.value)
                        constTable[node.toString()] = constIndex
                        constIndex++
                        bytes.addAll(node.value.toString().toByteArray())
                        bytes.add(NUL)
                        return true
                    }
                    return false
                }

                if (primitiveCheck(node)) return

                when (node) {
                    is Compound -> {
                        node.statements.forEach { checkNodes(it) }
                    }

                    is BinaryExpression -> {
                        checkNodes(node.left)
                        checkNodes(node.right)
                    }

                    is UnaryExpression -> {
                        checkNodes(node.operand)
                    }

                    is FunctionCall -> {
                        node.args.forEach { checkNodes(it) }
                    }

                    is ForStatement -> {
                        checkNodes(node.init)
                        checkNodes(node.condition)
                        checkNodes(node.afterThought)
                        checkNodes(node.body)
                    }

                    is WhileStatement -> {
                        checkNodes(node.condition)
                        checkNodes(node.body)
                    }

                    is IfStatement -> {
                        checkNodes(node.condition)
                        checkNodes(node.body)
                        checkNodes(node.elseBody)
                    }

                    is VariableDeclaration -> {
                        checkNodes(node.value)
                    }
                }
            }

            checkNodes(func.body)

            bytes.removeLast()
        }

        bytes.add(END_TABLE)

        val byteArray = ByteArray(bytes.size)
        for (i in byteArray.indices) {
            byteArray[i] = bytes[i].toByte()
        }

        return Pair(FunctionSignature(fastTable, nameTable, constTable), byteArray)
    }

    private fun containsPrimitive(node: AstNode?): Boolean {
        if (node == null) return false

        when (node) {
            is IPrimitive -> return true
            is BinaryExpression -> return containsPrimitive(node.left) || containsPrimitive(node.right)
            is UnaryExpression -> return containsPrimitive(node.operand)
            is FunctionCall -> return node.args.any { containsPrimitive(it) }
            is Compound -> return node.statements.any { containsPrimitive(it) }
            is ForStatement -> {
                return containsPrimitive(node.init) || containsPrimitive(node.condition) || containsPrimitive(node.afterThought) ||
                        (node.init is VariableDeclaration && containsPrimitive(node.init.value)) ||
                        (node.condition is VariableDeclaration && containsPrimitive(node.condition.value)) ||
                        (node.afterThought is VariableDeclaration && containsPrimitive(node.afterThought.value)) ||
                        (node.body is Compound && containsPrimitive(node.body)) ||
                        (node.body is IPrimitive)
            }
            is Function -> return containsPrimitive(node.body)
            is IfStatement -> {
                return containsPrimitive(node.condition) || (node.body is Compound && containsPrimitive(node.body)) ||
                        (node.body is IPrimitive) || (node.elseBody is Compound && containsPrimitive(node.elseBody)) ||
                        (node.elseBody is IPrimitive)
            }
            is VariableDeclaration -> return node.value?.let { containsPrimitive(it) } ?: false
            is WhileStatement -> {
                return containsPrimitive(node.condition) || (node.body is Compound && containsPrimitive(node.body)) ||
                        (node.body is IPrimitive)
            }
        }

        return false
    }
}

data class FunctionSignature(val fastTable: LinkedHashMap<String, Int> = LinkedHashMap(), val nameTable: LinkedHashMap<String, Int> = LinkedHashMap(), val constTable: LinkedHashMap<String, Int> = LinkedHashMap()) {
    var statements: ArrayList<AstNode>? = null
        get() = if (field == null) ArrayList() else field

    fun setStatements(new: ArrayList<AstNode>) = apply {
        statements = new
    }

    /**
     * Will look up the given variable or constant in all the tables (including global)
     *
     * @return A pair containing the correct LOAD byte to use and the position of the variable and null if it wasn't found
     */
    fun lookup(name: String): Pair<UByte, Int>? {
        return when (name) {
            in fastTable -> Pair(LOAD_FAST, fastTable[name]!!)

            in nameTable -> Pair(LOAD_NAME, nameTable[name]!!)

            in SignatureUtils.globalTable -> Pair(LOAD_GLOBAL, SignatureUtils.globalTable[name]!!)

            in constTable -> Pair(LOAD_CONST, constTable[name]!!)

            else -> null
        }

    }
}

fun ArrayList<UByte>.addAll(elements: ByteArray) {
    for (byte in elements) {
        this.add(byte.toUByte())
    }
}
