package cum.jesus.cheattriggers.compiler.bytecode

import cum.jesus.cheattriggers.compiler.parsing.ast.AstNode
import cum.jesus.cheattriggers.compiler.parsing.ast.AstNodeType.*
import cum.jesus.cheattriggers.compiler.parsing.ast.expression.*
import cum.jesus.cheattriggers.compiler.parsing.ast.expression.BinaryOperator.*
import cum.jesus.cheattriggers.compiler.parsing.ast.expression.UnaryOperator.*
import cum.jesus.cheattriggers.compiler.parsing.ast.statement.*
import cum.jesus.cheattriggers.compiler.parsing.ast.statement.Function
import cum.jesus.cheattriggers.compiler.util.Diagnostics
import cum.jesus.cheattriggers.compiler.util.addAllBytes

class ByteCodeGenerator(private val sig: FunctionSignature, private val optimise: Boolean = true) {
    private val utils = ByteCodeUtils.FunctionSpecifics(sig)

    fun generate(): ByteArray {
        val bytes = ArrayList<UByte>()

        for (node in sig.statements) {
            bytes.addAll(visit(node))
        }

        val byteArray = ByteArray(bytes.size)
        for (i in byteArray.indices) {
            byteArray[i] = bytes[i].toByte()
        }
        return byteArray
    }

    private fun visit(node: AstNode): ArrayList<UByte> {
        when (node.nodeType) {
            LONG, INTEGER, SHORT, BYTE, 
            DOUBLE, FLOAT -> {
                return visitNumberLiteral(node as INumberLiteral)
            }
            BINARY_EXPRESSION -> {
                return visitBinaryExpression(node as BinaryExpression)
            }
            UNARY_EXPRESSION -> {
                return visitUnaryExpression(node as UnaryExpression)
            }
            COMPOUND -> {
                return visitCompound(node as Compound)
            }
            IF -> {
                return visitIfStatement(node as IfStatement)
            }
            WHILE -> {
                return visitWhileStatement(node as WhileStatement)
            }
            FOR -> {
                return visitForStatement(node as ForStatement)
            }
            VARIABLE_DECLARATION -> {
                return visitVariableDeclaration(node as VariableDeclaration)
            }
            VARIABLE -> {
                return visitVariable(node as Variable)
            }
            FUNCTION -> {
                return visitFunction(node as Function)
            }
            CALL -> {
                return visitFunctionCall(node as FunctionCall)
            }
        }
    }

    private fun visitNumberLiteral(node: INumberLiteral): ArrayList<UByte> {
        val bytes = ArrayList<UByte>()

        val (code, name) = sig.lookup(node.toString()) ?: run {
            Diagnostics.error(Diagnostics.CompilerError("Number literal: '$node' does not exist"), true)
            error("unreachable")
        }

        bytes.add(code)
        bytes.add(name.toUByte())

        return bytes
    }

    private fun visitBinaryExpression(node: BinaryExpression): ArrayList<UByte> {
        val bytes = ArrayList<UByte>()

        val left = visit(node.left)
        val right = visit(node.right)

        bytes.addAll(left)
        bytes.addAll(right)
        
        when (node.operator) {
            POW -> bytes.add(BINARY_POW)
            ADD -> bytes.add(BINARY_ADD)
            SUB -> bytes.add(BINARY_SUB)
            MUL -> bytes.add(BINARY_MUL)
            DIV -> bytes.add(BINARY_DIV)
            AND -> bytes.add(BINARY_AND)
            OR -> bytes.add(BINARY_OR)
            XOR -> bytes.add(BINARY_XOR)
            EQUAL -> bytes.addAllBytes(ByteCodeUtils.compEqual())
            NOT_EQUAL -> bytes.addAllBytes(ByteCodeUtils.compNotEqual())
            LESS_THAN -> bytes.addAllBytes(ByteCodeUtils.compLT())
            GREATER_THAN -> bytes.addAllBytes(ByteCodeUtils.compGT())
            LESS_OR_EQUAL -> bytes.addAllBytes(ByteCodeUtils.compLTE())
            GREATER_OR_EQUAL -> bytes.addAllBytes(ByteCodeUtils.compGTE())
            ASSIGN -> {
                bytes.clear()

                if (node.left !is Variable) Diagnostics.error(Diagnostics.CompilerError("Variable expected"), true)

                bytes.addAll(utils.assignVariable((node.left as Variable).name, right))
            }
            POW_ASSIGN -> {
                bytes.clear()

                if (node.left !is Variable) Diagnostics.error(Diagnostics.CompilerError("Variable expected"), true)

                bytes.addAll(utils.binaryAssignVariable((node.left as Variable).name, right, BINARY_POW))
            }
            ADD_ASSIGN -> {
                bytes.clear()

                if (node.left !is Variable) Diagnostics.error(Diagnostics.CompilerError("Variable expected"), true)

                bytes.addAll(utils.binaryAssignVariable((node.left as Variable).name, right, BINARY_ADD))
            }
            SUB_ASSIGN -> {
                bytes.clear()

                if (node.left !is Variable) Diagnostics.error(Diagnostics.CompilerError("Variable expected"), true)

                bytes.addAll(utils.binaryAssignVariable((node.left as Variable).name, right, BINARY_SUB))
            }
            MUL_ASSIGN -> {
                bytes.clear()

                if (node.left !is Variable) Diagnostics.error(Diagnostics.CompilerError("Variable expected"), true)

                bytes.addAll(utils.binaryAssignVariable((node.left as Variable).name, right, BINARY_MUL))
            }
            DIV_ASSIGN -> {
                bytes.clear()

                if (node.left !is Variable) Diagnostics.error(Diagnostics.CompilerError("Variable expected"), true)

                bytes.addAll(utils.binaryAssignVariable((node.left as Variable).name, right, BINARY_DIV))
            }
        }

        return bytes
    }

    private fun visitUnaryExpression(node: UnaryExpression): ArrayList<UByte> {
        val bytes = ArrayList<UByte>()

        val operand = visit(node.operand)
        bytes.addAll(operand)

        when (node.operator) {
            NUMBER_SUBSTANTIATION -> {
                if (!optimise) bytes.add(UNARY_POSITIVE)
            }
            NUMBER_NEGATION -> bytes.add(UNARY_NEGATIVE)
            LOGICAL_NEGATION -> bytes.add(UNARY_NOT)
        }

        return bytes
    }

    private fun visitCompound(node: Compound): ArrayList<UByte> {
        val bytes = ArrayList<UByte>()

        for (statement in node.statements) {
            bytes.addAll(visit(statement))
        }

        return bytes
    }

    private fun visitIfStatement(node: IfStatement): ArrayList<UByte> {
        val bytes = ArrayList<UByte>()

        val condition = visit(node.condition)
        val ifBody = visit(node.body)
        val elseBody = if (node.elseBody != null) visit(node.elseBody) else ArrayList()

        // L 1 j 8 0 0 0 0 0 j 10 L 1 j 8 0 0 0 0 0 j 6 0 0 0 0 0 r

        bytes.addAll(condition)
        bytes.add(JUMP_IF_FALSE)
        bytes.add((ifBody.size + 3).toUByte()) // added 1, so it's in front of the if body and 2 more because there's a JUMP byte after the if

        bytes.addAll(ifBody)
        bytes.add(JUMP)
        bytes.add((elseBody.size + 1).toUByte())

        bytes.addAll(elseBody)

        return bytes
    }

    private fun visitWhileStatement(node: WhileStatement): ArrayList<UByte> {
        val bytes = ArrayList<UByte>()

        val condition = visit(node.condition)
        val body = visit(node.body)

        // s 10 L 1 jt 2 br 0 0 0 0 0 L 0 r

        body.addAll(condition)
        body.add(JUMP_IF_TRUE)
        body.add(2u)
        body.add(BREAK_LOOP)

        bytes.add(SETUP_LOOP)
        bytes.add((body.size).toUByte())

        bytes.addAll(body)

        bytes.add(POP_BLOCK)

        return bytes
    }

    private fun visitForStatement(node: ForStatement): ArrayList<UByte> {
        val bytes = ArrayList<UByte>()

        Diagnostics.warn("For loops are not yet implemented and will not generate any bytecode")

        return bytes
    }

    private fun visitVariableDeclaration(node: VariableDeclaration): ArrayList<UByte> {
        val bytes = ArrayList<UByte>()

        val name = node.name
        val value = node.value?.let { visit(it) }

        bytes.addAll(utils.assignVariable(name, value))

        return bytes
    }

    private fun visitVariable(node: Variable): ArrayList<UByte> {
        val bytes = ArrayList<UByte>()

        bytes.addAll(utils.loadVariable(node.name))

        return bytes
    }

    private fun visitFunction(node: Function): ArrayList<UByte> {
        Diagnostics.error(Diagnostics.CompilerError("Nested functions not supported"), true)
        error("unreachable")
    }

    private fun visitFunctionCall(node: FunctionCall): ArrayList<UByte> {
        val bytes = ArrayList<UByte>()

        val loadedFunction = visit(node.callee) // will load the function name to prepare for arguments
        val argLoaders = ArrayList<UByte>() // will add the argument loading codes in right order
        val argc = node.args.size

        for (arg in node.args) {
            argLoaders.addAll(visit(arg))
        }

        bytes.addAll(loadedFunction)
        bytes.addAll(argLoaders)
        bytes.add(CALL_FUNCTION)
        bytes.add(argc.toUByte())

        return bytes
    }
}