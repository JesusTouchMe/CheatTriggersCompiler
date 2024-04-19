package cum.jesus.cts.asm.lexing;

import cum.jesus.cts.asm.instruction.operand.Register;
import cum.jesus.cts.lexing.SourceLocation;
import cum.jesus.cts.util.CharUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public final class Lexer {
    private String text;
    private int pos = 0;

    private int line = 1;
    private int column = 1;

    private static final HashSet<String> instructions = new HashSet<String>() {{
        add("nop");
        add("newl");

        add("push");
        add("pop");
        add("dup");
        add("alca");
        add("frea");

        add("mov");
        add("movz");

        add("alc");
        add("fre");
        add("lod");
        add("str");

        add("add");
        add("sub");
        add("mul");
        add("div");
        add("and");
        add("or");
        add("xor");
        add("shl");
        add("shr");
        add("land");
        add("lor");
        add("lxor");

        add("inc");
        add("dec");

        add("not");
        add("neg");
        add("lnot");

        add("cmpeq");
        add("cmpne");
        add("cmplt");
        add("cmpgt");
        add("cmplte");
        add("cmpgte");

        add("jmp");
        add("jit");
        add("jiz");

        add("call");
        add("ret");
        add("int");

        add("cld");
        add("cst");
    }};

    private static final HashSet<String> constPoolInstructions = new HashSet<String>() {{
        add("number");
        add("string");
        add("function");
    }};

    public Lexer(String text) {
        this.text = text;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < text.length()) {
            Optional<Token> token = nextToken();
            token.ifPresent(tokens::add);
            consume();
        }

        tokens.add(new Token(new SourceLocation(line, column), TokenType.END));
        return tokens;
    }

    private Optional<Token> nextToken() {
        SourceLocation startLocation = new SourceLocation(line, column);

        if (Character.isLetter(current()) || current() == '_' || current() == '.') {
            StringBuilder sb = new StringBuilder().append(current());

            while (Character.isLetterOrDigit(peek(1)) || peek(1) == '_') {
                consume();
                sb.append(current());
            }

            String str = sb.toString();
            if (instructions.contains(str)) {
                return Optional.of(new Token(startLocation, TokenType.INSTRUCTION, str));
            }

            if (constPoolInstructions.contains(str)) {
                return Optional.of(new Token(startLocation, TokenType.CONSTANT, str));
            }

            for (String reg : Register.registerNames) {
                if (str.equals(reg)) {
                    return Optional.of(new Token(startLocation, TokenType.REGISTER, str));
                }
            }

            if (str.equals("byte") || str.equals("word") || str.equals("dword") || str.equals("qword")) {
                return Optional.of(new Token(startLocation, TokenType.SIZE, str));
            }

            return Optional.of(new Token(startLocation, TokenType.IDENTIFIER, str));
        }

        if (current() == '\n' || current() == '\r' || current() == ' ' || current() == '\t') {
            return Optional.empty();
        }

        if (Character.isDigit(current())) {
            StringBuilder sb = new StringBuilder().append(current());

            if (current() == '0') {
                if (peek(1) == 'x') {
                    consume();
                    sb.append(current());

                    while (CharUtils.isXDigit(peek(1))) {
                        consume();
                        sb.append(current());
                    }
                } else if (peek(1) == 'b') {
                    consume();
                    sb.append(current());

                    while(peek(1) == '0' || peek(1) == '1') {
                        consume();
                        sb.append(current());
                    }
                } else {
                    while (peek(1) >= '0' && peek(1) <= '7') {
                        consume();
                        sb.append(current());
                    }
                }
            } else {
                while (Character.isDigit(peek(1))) {
                    consume();
                    sb.append(current());
                }
            }

            return Optional.of(new Token(startLocation, TokenType.IMMEDIATE, sb.toString()));
        }

        switch (current()) {
            case '$':
                return Optional.of(new Token(startLocation, TokenType.DOLLAR));

            case '+':
                return Optional.of(new Token(startLocation, TokenType.PLUS));
            case '-':
                return Optional.of(new Token(startLocation, TokenType.MINUS));
            case '*':
                return Optional.of(new Token(startLocation, TokenType.STAR));
            case '/':
                if (peek(1) == '/') {
                    while (current() != '\n') {
                        consume();
                    }
                    return Optional.empty();
                } else if (peek(1) == '*') {
                    pos += 2;
                    while (current() != '*' && peek(1) != '/') {
                        consume();
                    }
                    consume();
                    return Optional.empty();
                }

                return Optional.of(new Token(startLocation, TokenType.SLASH));

            case '(':
                return Optional.of(new Token(startLocation, TokenType.LEFT_PAREN));
            case ')':
                return Optional.of(new Token(startLocation, TokenType.RIGHT_PAREN));
            case '[':
                return Optional.of(new Token(startLocation, TokenType.LEFT_BRACKET));
            case ']':
                return Optional.of(new Token(startLocation, TokenType.RIGHT_BRACKET));
            case '{':
                return Optional.of(new Token(startLocation, TokenType.LEFT_BRACE));
            case '}':
                return Optional.of(new Token(startLocation, TokenType.RIGHT_BRACE));

            case ',':
                return Optional.of(new Token(startLocation, TokenType.COMMA));
            case ':':
                return Optional.of(new Token(startLocation, TokenType.COLON));

            case '#':
                return Optional.of(new Token(startLocation, TokenType.HASH));

            case '"': {
                consume();
                StringBuilder sb = new StringBuilder();

                while (current() != '"') {
                    switch (current()) {
                        case '\\': {
                            consume();
                            switch (current()) {
                                case 'n':
                                    sb.append('\n');
                                    break;
                                case '\'':
                                    sb.append('\'');
                                    break;
                                case '"':
                                    sb.append('"');
                                    break;
                                case '\\':
                                    sb.append('\\');
                                    break;
                                case '0':
                                    sb.append('\0');
                                    break;
                                default:
                                    return Optional.of(new Token(startLocation, TokenType.ERROR));
                            }
                            break;
                        }

                        default:
                            sb.append(current());
                    }

                    consume();
                }

                return Optional.of(new Token(startLocation, TokenType.STRING, sb.toString()));
            }

            default:
                return Optional.of(new Token(startLocation, TokenType.ERROR));
        }
    }

    private char current() {
        return text.charAt(pos);
    }

    private char consume() {
        char c = text.charAt(pos++);

        if (c == '\n') {
            column = 1;
            line += 1;
        } else {
            column += 1;
        }

        return c;
    }

    private char peek(int offset) {
        try {
            return text.charAt(pos + offset);
        } catch (StringIndexOutOfBoundsException ignored) {
            return ' ';
        }
    }
}
