package cum.jesus.cts.asm.parsing;

import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.asm.instruction.Builder;
import cum.jesus.cts.asm.instruction.Label;
import cum.jesus.cts.asm.instruction.Operand;
import cum.jesus.cts.asm.instruction.fakes.ConstantPoolFake;
import cum.jesus.cts.asm.instruction.fakes.FunctionInstructionFake;
import cum.jesus.cts.asm.instruction.nooperandinstruction.DupInstruction;
import cum.jesus.cts.asm.instruction.nooperandinstruction.NewLInstruction;
import cum.jesus.cts.asm.instruction.nooperandinstruction.NopInstruction;
import cum.jesus.cts.asm.instruction.nooperandinstruction.RetInstruction;
import cum.jesus.cts.asm.instruction.singleoperandinstruction.*;
import cum.jesus.cts.asm.instruction.threeoperandinstruction.*;
import cum.jesus.cts.asm.instruction.twooperandinstruction.*;
import cum.jesus.cts.asm.lexing.Token;
import cum.jesus.cts.asm.lexing.TokenType;
import cum.jesus.cts.error.ErrorContext;
import cum.jesus.cts.error.ErrorReporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Parser {
    private String fileName;
    private TokenStream tokenStream;
    private ErrorReporter errorReporter;
    
    private Function<TokenStream, Operand> specialOperandParser; // {x} operands for _code for cts

    private Map<String, Supplier<AsmValue>> instructionParsers;
    private Map<String, Supplier<AsmValue>> constantPoolParsers;

    public Parser(String fileName, List<Token> tokens, ErrorReporter errorReporter) {
        this.fileName = fileName;
        this.tokenStream = new TokenStream(tokens, 0);
        this.errorReporter = errorReporter;

        specialOperandParser = null;

        instructionParsers = new HashMap<>();
        constantPoolParsers = new HashMap<>();

        instructionParsers.put("nop", () -> new Builder<NopInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, NopInstruction.class));
        instructionParsers.put("newl", () -> new Builder<NewLInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, NewLInstruction.class));

        instructionParsers.put("push", () -> new Builder<PushInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, PushInstruction.class));
        instructionParsers.put("pop", () -> new Builder<PopInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, PopInstruction.class));
        instructionParsers.put("dup", () -> new Builder<DupInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, DupInstruction.class));
        instructionParsers.put("alca", () -> new Builder<AlcaInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, AlcaInstruction.class));
        instructionParsers.put("frea", () -> new Builder<FreaInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, FreaInstruction.class));

        instructionParsers.put("mov", () -> new Builder<MovInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, MovInstruction.class));
        instructionParsers.put("movz", () -> new Builder<MovZInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, MovZInstruction.class));

        instructionParsers.put("alc", () -> new Builder<AlcInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, AlcInstruction.class));
        instructionParsers.put("fre", () -> new Builder<FreInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, FreInstruction.class));
        instructionParsers.put("lod", () -> new Builder<LodInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, LodInstruction.class));
        instructionParsers.put("str", () -> new Builder<StrInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, StrInstruction.class));

        instructionParsers.put("add", () -> new Builder<AddInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, AddInstruction.class));
        instructionParsers.put("sub", () -> new Builder<SubInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, SubInstruction.class));
        instructionParsers.put("mul", () -> new Builder<MulInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, MulInstruction.class));
        instructionParsers.put("div", () -> new Builder<DivInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, DivInstruction.class));
        instructionParsers.put("and", () -> new Builder<AndInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, AndInstruction.class));
        instructionParsers.put("or", () -> new Builder<OrInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, OrInstruction.class));
        instructionParsers.put("xor", () -> new Builder<XorInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, XorInstruction.class));
        instructionParsers.put("shl", () -> new Builder<ShlInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, ShlInstruction.class));
        instructionParsers.put("shr", () -> new Builder<ShrInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, ShrInstruction.class));
        instructionParsers.put("land", () -> new Builder<LAndInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, LAndInstruction.class));
        instructionParsers.put("lor", () -> new Builder<LOrInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, LOrInstruction.class));
        instructionParsers.put("lxor", () -> new Builder<LXorInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, LXorInstruction.class));

        instructionParsers.put("inc", () -> new Builder<IncInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, IncInstruction.class));
        instructionParsers.put("dec", () -> new Builder<DecInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, DecInstruction.class));

        instructionParsers.put("cmpeq", () -> new Builder<CmpEqInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, CmpEqInstruction.class));
        instructionParsers.put("cmpne", () -> new Builder<CmpNeInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, CmpNeInstruction.class));
        instructionParsers.put("cmplt", () -> new Builder<CmpLtInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, CmpLtInstruction.class));
        instructionParsers.put("cmpgt", () -> new Builder<CmpGtInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, CmpGtInstruction.class));
        instructionParsers.put("cmplte", () -> new Builder<CmpLteInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, CmpLteInstruction.class));
        instructionParsers.put("cmpgte", () -> new Builder<CmpGteInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, CmpGteInstruction.class));

        instructionParsers.put("jmp", () -> new Builder<JmpInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, JmpInstruction.class));
        instructionParsers.put("jit", () -> new Builder<JitInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, JitInstruction.class));
        instructionParsers.put("jiz", () -> new Builder<JizInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, JizInstruction.class));

        instructionParsers.put("call", () -> new Builder<CallInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, CallInstruction.class));
        instructionParsers.put("ret", () -> new Builder<RetInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, RetInstruction.class));
        instructionParsers.put("int", () -> new Builder<IntInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, IntInstruction.class));

        instructionParsers.put("cld", () -> new Builder<CldInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, CldInstruction.class));
        instructionParsers.put("cst", () -> new Builder<CstInstruction>(fileName, errorReporter, specialOperandParser).parse(tokenStream, CstInstruction.class));

        // they actually do the same either way lol but it's nicer having a sense of type control. number "hello world" is completely valid code but try not to do that :D
        constantPoolParsers.put("number", () -> new Builder<ConstantPoolFake>(fileName, errorReporter, specialOperandParser).parse(tokenStream, ConstantPoolFake.class));
        constantPoolParsers.put("string", () -> new Builder<ConstantPoolFake>(fileName, errorReporter, specialOperandParser).parse(tokenStream, ConstantPoolFake.class));
    }
    
    public Parser withSpecialOperandParser(Function<TokenStream, Operand> parser) {
        this.specialOperandParser = parser;
        return this;
    }

    public List<AsmValue> parse() {
        List<AsmValue> ret = new ArrayList<>();

        while (current().getType() != TokenType.END) {
            ret.add(parseStatement());
        }

        return ret;
    }

    private AsmValue parseStatement() {
        Token token = current();
        switch (token.getType()) {
            case ERROR:
                errorReporter.reportError(new ErrorContext(fileName, "Unknown symbol", token));
                break;

            case IDENTIFIER:
                if (peek(1).getType() == TokenType.LEFT_PAREN) {
                    return new Builder<FunctionInstructionFake>(fileName, errorReporter, specialOperandParser).parse(tokenStream, FunctionInstructionFake.class);
                }
                return new Builder<Label>(fileName, errorReporter, specialOperandParser).parse(tokenStream, Label.class);

            case INSTRUCTION: {
                Supplier<AsmValue> parser = instructionParsers.get(consume().getText());
                return parser.get();
            }

            case CONSTANT: {
                Supplier<AsmValue> parser = constantPoolParsers.get(consume().getText());
                return parser.get();
            }

            default:
                errorReporter.reportError(new ErrorContext(fileName, "Expected statement, found '" + (token.getText().isEmpty() ? token.getType() : token.getText()) + "'.", token));
                break;
        }

        return null;
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

    private Token peek(int offset) {
        return tokenStream.tokens.get(tokenStream.pos + offset);
    }
}
