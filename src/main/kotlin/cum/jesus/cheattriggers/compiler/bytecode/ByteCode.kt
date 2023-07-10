package cum.jesus.cheattriggers.compiler.bytecode

/**
 * Utility class containing human-readable names for the different opcode values as well as a small amount of documentation on some of them
 */
object ByteCode {
    const val BYTECODE_VERSION = "1"

    /**
     * Standard NUL byte
     */
    const val NUL: UByte = 0x00u

    /**
     * Pops the top element on the stack
     */
    const val POP: UByte = 0x01u

    // Unary operations get the top element from the stack, pop it, modify it, and push the modified element back on the stack
    /**
     * Positive operator (+VALUE)
     *
     * This usually won't be found in any program unless optimisation has been disabled during compiling
     */
    const val UNARY_POSITIVE: UByte = 0x0Au

    /**
     * Negative operator (-VALUE)
     */
    const val UNARY_NEGATIVE: UByte = 0x0Bu

    /**
     * Inverts a boolean (!VALUE)
     */
    const val UNARY_NOT: UByte = 0x0Cu

    // Binary operations pop the 2 highest elements on the stack, apply whatever operation on them, and push the modified element back on the stack
    /**
     * VALUE ^ VALUE1
     */
    const val BINARY_POW: UByte = 0x10u

    /**
     * VALUE * VALUE1
     */
    const val BINARY_MUL: UByte = 0x11u

    /**
     * VALUE / VALUE1
     */
    const val BINARY_DIV: UByte = 0x12u

    /**
     * VALUE % VALUE1
     */
    const val BINARY_MOD: UByte = 0x13u

    /**
     * VALUE + VALUE1
     */
    const val BINARY_ADD: UByte = 0x14u

    /**
     * VALUE - VALUE1
     */
    const val BINARY_SUB: UByte = 0x15u

    /**
     * VALUE && VALUE1
     */
    const val BINARY_AND: UByte = 0x16u

    /**
     * VALUE || VALUE1
     */
    const val BINARY_OR: UByte = 0x17u

    /**
     * Though this isn't an implemented thing in the language, the compiler will still attempt to make this if it finds any pattern for xor (only when in optimise mode)
     */
    const val BINARY_XOR: UByte = 0x18u

    // Opcodes with no specific category
    /**
     * Goes to the previous instruction
     *
     * This instruction will likely not be used often if at all, and is only there so that the logic exists
     */
    const val GOTO: UByte = 0x1Eu

    /**
     * Goes to position 'pos' in the instruction set
     *
     * Args (1):
     *  - pos: The position in the instruction set to goto
     *
     *  This instruction will likely not be used often if at all, and is only there so that the logic exists
     */
    const val GOTO1: UByte = 0x1Fu

    /**
     * Jumps forward by 'delta' in the instruction set (safer GOTO1)
     *
     * Args (1):
     *  - delta: The amount to jump forward by
     */
    const val JUMP: UByte = 0x20U

    /**
     * Jumps forward by 'delta' in the instruction set if the top of the stack is true
     *
     * Args (1):
     *  - delta: The amount to jump forward by
     */
    const val JUMP_IF_TRUE: UByte = 0x21u

    /**
     * Jumps forward by 'delta' in the instruction set if the top of the stack is false
     *
     * Args (1):
     *  - delta: The amount to jump forward by
     */
    const val JUMP_IF_FALSE: UByte = 0x22u

    /**
     * Terminate a loop
     */
    const val BREAK_LOOP: UByte = 0x23u

    /**
     * Returns the top of the stack to the function caller
     */
    const val RETURN_VALUE: UByte = 0x24u

    /**
     * Removes a block from the block stack
     */
    const val POP_BLOCK: UByte = 0x25u

    /**
     * Stores the top of the stack as a global variable
     *
     * Args (1):
     *  - namei: The index of the name to store the variable at
     */
    const val STORE_GLOBAL: UByte = 0x26u

    /**
     * Push a reference to the global variable at 'namei' to the stack
     *
     * Args (1):
     *  - namei: The index of the name
     */
    const val LOAD_GLOBAL: UByte = 0x27u

    /**
     * Delete a global variable
     *
     * Args (1):
     *  - namei: The index of the name
     */
    const val DELETE_GLOBAL: UByte = 0x28u

    /**
     * Store the top of the stack as a local variable
     *
     * Args (1):
     *  - varnum: The index of the variable
     */
    const val STORE_FAST: UByte = 0x29u

    /**
     * Push a reference of the local variable at 'varnum' to the stack
     *
     * Args (1):
     *  - varnum: The index of the variable
     */
    const val LOAD_FAST: UByte = 0x2Au

    /**
     * Delete the local variable at 'varnum'
     *
     * Args (1):
     *  - varnum: The index of the variable
     */
    const val DELETE_FAST: UByte = 0x2Bu

    /**
     * Pushes the constant at 'consti' onto the stack. A constant is not a variable, but any "literals" used (for example "hello world" or 42)
     *
     * Args (1):
     *  - consti: The index of the constant
     */
    const val LOAD_CONST: UByte = 0x2Cu

    /**
     * Sets up a loop block on the block stack. The block will span from the current instruction with a size of 'delta' bytes
     *
     * Args (1):
     *  - delta: The size of the loop block
     */
    const val SETUP_LOOP: UByte = 0x2Du

    /**
     * Call a function. The argument 'argc' is the amount of arguments the function has. The arguments will be found first on the stack. The right-most argument will be the one at the top of the stack. The function will be below the arguments on the stack
     *
     * Args (1):
     *  - argc: The amount of arguments to search for on the stack
     */
    const val CALL_FUNCTION: UByte = 0x30u
}