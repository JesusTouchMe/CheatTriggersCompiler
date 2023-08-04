package cum.jesus.cheattriggers.compiler.bytecode

const val BYTECODE_VERSION = "1"

/*
'VALUE' means the top of the stack and a number after 'VALUE' is that number below 'VALUE'
 */

/**
 * Standard NUL byte
 */
const val NUL: UByte = 0x00u

/**
 * Pops VALUE
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
 * VALUE ^^ VALUE1
 *
 * If optimisation is enabled, the compiler will attempt to replace any different xor implementation with just this byte
 */
const val BINARY_XOR: UByte = 0x18u

// Opcodes with no specific category
/**
 * Goes to the instruction after the next instruction
 *
 * Example:
 * GOTO
 *
 * LOAD_CONST 0 ; skipped
 *
 * LOAD_CONST 1 ; will be this instruction after GOTO
 *
 * This instruction will likely not be used often if at all, and is only there so that the logic exists for custom compilers or directly written bytecode
 */
const val GOTO: UByte = 0x1Eu

/**
 * Goes to position 'pos' in the instruction set
 *
 * Args (1):
 *  - pos: The position in the instruction set to goto
 *
 *  This instruction will likely not be used often if at all, and is only there so that the logic exists for custom compilers or directly written bytecode
 */
const val GOTO_1: UByte = 0x1Fu

/**
 * Jumps forward by 'delta' in the instruction set (safer GOTO_1)
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
 * Stores VALUE as a global variable and pops VALUE
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
 * Store VALUE as a local variable and pops VALUE
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
 * Stores VALUE in the name at 'namei' and pops VALUE
 *
 * Args (1):
 *  - namei: The index of the name
 */
const val STORE_NAME: UByte = 0x2Cu

/**
 * Push the value at 'namei' onto the stack
 *
 * Args (1):
 *  - namei: The index of the name
 */
const val LOAD_NAME: UByte = 0x2Du

/**
 * Delete the value at 'namei'
 *
 * Args (1):
 *  - namei: The index of the name
 */
const val DELETE_NAME: UByte = 0x2Eu

/**
 * Pushes the constant at 'consti' onto the stack. A constant is not a variable, but any "literals" used (for example "hello world" or 42)
 *
 * Args (1):
 *  - consti: The index of the constant
 */
const val LOAD_CONST: UByte = 0x2Fu

/**
 * Sets up a loop block on the block stack. The block will span from the next instruction with a size of 'delta' bytes
 *
 * Args (1):
 *  - delta: The size of the loop block
 */
const val SETUP_LOOP: UByte = 0x30u

/**
 * Call a function. The argument 'argc' is the amount of arguments the function has. The arguments will be found first on the stack. The right-most argument will be the one at the top of the stack. The function will be below the arguments on the stack. Pops the function and ann arguments and leaves the returned value on the stack (will automatically be popped if it's null)
 *
 * Args (1):
 *  - argc: The amount of arguments to search for on the stack
 */
const val CALL_FUNCTION: UByte = 0x31u

/**
 * Will compare VALUE and VALUE1 using 'cmp_op'
 *
 * Args (1):
 *  -cmp_op The "code" for the comparison (to prevent using many bytes for comparing)
 */
const val COMPARE: UByte = 0x032u

/**
 * Exits the program using VALUE as the exit code
 */
const val EXIT: UByte = 0x33u

// opcode: (name, arg amount in bytes (so a 2 byte single arg will still be 2))
val opcodeMap = mapOf<UByte, Pair<String, Int>>(
    NUL to ("NUL" to 0),
    POP to ("POP" to 0),
    UNARY_POSITIVE to ("UNARY_POSITIVE" to 0),
    UNARY_NEGATIVE to ("UNARY_NEGATIVE" to 0),
    UNARY_NOT to ("UNARY_NOT" to 0),
    BINARY_POW to ("BINARY_POW" to 0),
    BINARY_MUL to ("BINARY_MUL" to 0),
    BINARY_DIV to ("BINARY_DIV" to 0),
    BINARY_MOD to ("BINARY_MOD" to 0),
    BINARY_ADD to ("BINARY_ADD" to 0),
    BINARY_SUB to ("BINARY_SUB" to 0),
    BINARY_AND to ("BINARY_AND" to 0),
    BINARY_OR to ("BINARY_OR" to 0),
    BINARY_XOR to ("BINARY_XOR" to 0),
    GOTO to ("GOTO" to 0),
    GOTO_1 to ("GOTO_1" to 1),
    JUMP to ("JUMP" to 1),
    JUMP_IF_TRUE to ("JUMP_IF_TRUE" to 1),
    JUMP_IF_FALSE to ("JUMP_IF_FALSE" to 1),
    BREAK_LOOP to ("BREAK_LOOP" to 0),
    RETURN_VALUE to ("RETURN_VALUE" to 0),
    POP_BLOCK to ("POP_BLOCK" to 0),
    STORE_GLOBAL to ("STORE_GLOBAL" to 1),
    LOAD_GLOBAL to ("LOAD_GLOBAL" to 1),
    DELETE_GLOBAL to ("DELETE_GLOBAL" to 1),
    STORE_FAST to ("STORE_FAST" to 1),
    LOAD_FAST to ("LOAD_FAST" to 1),
    DELETE_FAST to ("DELETE_FAST" to 1),
    STORE_NAME to ("STORE_NAME" to 1),
    LOAD_NAME to ("LOAD_NAME" to 1),
    DELETE_NAME to ("DELETE_NAME" to 1),
    LOAD_CONST to ("LOAD_CONST" to 1),
    SETUP_LOOP to ("SETUP_LOOP" to 1),
    CALL_FUNCTION to ("CALL_FUNCTION" to 1),
    COMPARE to ("COMPARE" to 1),
    EXIT to ("EXIT" to 0)
)