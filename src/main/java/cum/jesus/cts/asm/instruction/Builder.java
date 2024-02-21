package cum.jesus.cts.asm.instruction;

import cum.jesus.cts.asm.instruction.fakes.ConstantPoolFake;
import cum.jesus.cts.asm.instruction.fakes.FunctionInstructionFake;
import cum.jesus.cts.asm.instruction.operand.*;
import cum.jesus.cts.asm.instruction.singleoperandinstruction.IntInstruction;
import cum.jesus.cts.asm.lexing.Token;
import cum.jesus.cts.asm.lexing.TokenType;
import cum.jesus.cts.asm.parsing.TokenStream;
import cum.jesus.cts.error.ErrorContext;
import cum.jesus.cts.error.ErrorReporter;
import cum.jesus.cts.util.NumberUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public final class Builder<T extends AsmValue> {
    private TokenStream tokenStream;
    private String fileName;
    private ErrorReporter errorReporter;
    private Function<TokenStream, Operand> specialOperandParser;

    public Builder(String fileName, ErrorReporter errorReporter, Function<TokenStream, Operand> specialOperandParser) {
        this.fileName = fileName;
        this.errorReporter = errorReporter;
        this.specialOperandParser = specialOperandParser;
    }

    public T parse(TokenStream tokenStream, Class<T> clazz) {
        this.tokenStream = tokenStream;

        if (NoOperandInstruction.class.isAssignableFrom(clazz)) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        } else if (SingleOperandInstruction.class.isAssignableFrom(clazz)) {
            if (IntInstruction.class.isAssignableFrom(clazz)) {
                try {
                    Constructor<T> constructor = clazz.getDeclaredConstructor(int.class, int.class, int.class, int.class);
                    Operand operand = parseOperand();
                    if (!(operand instanceof Immediate)) {
                        errorReporter.reportError(new ErrorContext(fileName, "Can only use immediate on 'int' instruction", current()));
                        return null; // happy intellij
                    }
                    Immediate imm = (Immediate) operand;
                    return constructor.newInstance((imm.imm32() >> 24) & 0xFF, (imm.imm32() >> 16) & 0xFF, (imm.imm32() >> 8) & 0xFF, imm.imm32() & 0xFF);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                         InstantiationException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    return clazz.getDeclaredConstructor(Operand.class).newInstance(parseOperand());
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                         InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (TwoOperandInstruction.class.isAssignableFrom(clazz)) {
            try {
                Operand left = parseOperand();
                expectToken(TokenType.COMMA, "Expected ','.");
                consume();
                Operand right = parseOperand();
                return clazz.getDeclaredConstructor(Operand.class, Operand.class).newInstance(left, right);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                    InstantiationException e) {
                throw new RuntimeException(e);
            }
        } else if (ThreeOperandInstruction.class.isAssignableFrom(clazz)) {
            try {
                Operand first = parseOperand();
                expectToken(TokenType.COMMA, "Expected ','.");
                consume();
                Operand second = parseOperand();
                expectToken(TokenType.COMMA, "Expected ','.");
                consume();
                Operand third = parseOperand();

                return clazz.getDeclaredConstructor(Operand.class, Operand.class, Operand.class).newInstance(first, second, third);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                     InstantiationException e) {
                throw new RuntimeException(e);
            }
        } else if (clazz == Label.class) {
            String name = consume().getText();
            expectToken(TokenType.COLON, "Expected ':'.");
            consume();

            try {
                return clazz.getDeclaredConstructor(String.class).newInstance(name);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                     InstantiationException e) {
                throw new RuntimeException(e);
            }
        } else if (clazz == ConstantPoolFake.class) {
            try {
                return clazz.getDeclaredConstructor(Operand.class).newInstance(parseOperand());
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                     InstantiationException e) {
                throw new RuntimeException(e);
            }
        } else if (clazz == FunctionInstructionFake.class) {
            String name = consume().getText();
            expectToken(TokenType.LEFT_PAREN, "Expected '('.");
            consume();
            expectToken(TokenType.RIGHT_PAREN, "Expected ')'.");
            consume();
            expectToken(TokenType.COLON, "Expected ':'.");
            consume();

            try {
                return clazz.getDeclaredConstructor(String.class).newInstance(name);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                     InstantiationException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    private Operand parseOperand() {
        switch (current().getType()) {
            case HASH:
                return parseConstPoolEntry();

            case IMMEDIATE:
            case IDENTIFIER:
            case DOLLAR:
                return parseImmediate();

            case LEFT_BRACKET:
                return parseMemory();

            case REGISTER:
                return parseRegister();

            case STRING:
                return parseString();

            case LEFT_BRACE:
                if (specialOperandParser != null) {
                    return specialOperandParser.apply(tokenStream);
                } else {
                    return null;
                }
        }

        return null;
    }

    private ConstPoolEntryOperand parseConstPoolEntry() {
        consume(); // #
        expectToken(TokenType.IMMEDIATE, "Expected number for constant entry");
        return new ConstPoolEntryOperand(NumberUtils.parseInt(consume().getText()));
    }

    private Immediate parseImmediate() {
        if (current().getType() == TokenType.IDENTIFIER) {
            String text = consume().getText();
            return new LabelOperand(text);
        } else if (current().getType() == TokenType.DOLLAR) {
            consume();
            return new LabelOperand("$");
        }
        return new Immediate(NumberUtils.parseLong(consume().getText()));
    }

    private Memory parseMemory() {
        consume(); // [
        Register reg = parseRegister();

        int displacement = 0;
        if (current().getType() == TokenType.PLUS) {
            consume();

            expectToken(TokenType.IMMEDIATE, "Expected immediate memory offset.");
            displacement = NumberUtils.parseInt(consume().getText());
        } else if (current().getType() == TokenType.MINUS) {
            consume();

            expectToken(TokenType.IMMEDIATE, "Expected immediate memory offset.");
            displacement = -NumberUtils.parseInt(consume().getText());
        }

        expectToken(TokenType.RIGHT_BRACKET, "Expected ']'.");
        consume();

        return new Memory(reg, (short) displacement);
    }

    private Register parseRegister() {
        return Register.get(consume().getText());
    }

    private StringOperand parseString() {
        return new StringOperand(consume().getText());
    }

    private void expectToken(TokenType type, String context) {
        Token token = current();
        if (token.getType() != type) {
            errorReporter.reportError(new ErrorContext(fileName, context, token));
        }
    }

    private Token current() {
        return tokenStream.tokens.get(tokenStream.pos);
    }

    private Token consume() {
        return tokenStream.tokens.get(tokenStream.pos++);
    }
}
