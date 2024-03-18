package cum.jesus.cts.lexing;

import cum.jesus.cts.type.Type;

import java.util.*;

public final class Lexer {
    private String text;
    private int pos = 0;
    private int line = 1;
    private int column = 1;

    private static final Map<String, TokenType> keywords = new HashMap<String, TokenType>() {{
        put("module", TokenType.KEYWORD_MODULE);
        put("import", TokenType.KEYWORD_IMPORT);
        put("native", TokenType.KEYWORD_NATIVE);
        put("func", TokenType.KEYWORD_FUNC);
        put("return", TokenType.KEYWORD_RETURN);
        put("struct", TokenType.KEYWORD_STRUCT);
        put("if", TokenType.KEYWORD_IF);
        put("else", TokenType.KEYWORD_ELSE);
    }};

    private static final Map<String, TokenType> builtins = new HashMap<String, TokenType>() {{
        put("_code", TokenType.BUILTIN_CODE);
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

        tokens.add(new Token(new SourceLocation(line, column), TokenType.EOF, ""));

        return tokens;
    }

    private Optional<Token> nextToken() {
        SourceLocation startLocation = new SourceLocation(line, column);
        
        if (Character.isLetter(current()) || current() == '_') {
            StringBuilder sb = new StringBuilder().append(current());

            while (Character.isLetterOrDigit(peek(1)) || peek(1) == '_') {
                consume();
                sb.append(current());
            }

            String str = sb.toString();

            if (keywords.containsKey(str)) {
                return Optional.of(new Token(startLocation, keywords.get(str), str));
            }

            if (builtins.containsKey(str)) {
                return Optional.of(new Token(startLocation, builtins.get(str), str));
            }

            if (Type.exists(str)) {
                return Optional.of(new Token(startLocation, TokenType.TYPE, str));
            }

            return Optional.of(new Token(startLocation, TokenType.IDENTIFIER, str));
        }

        if (Character.isDigit(current())) {
            StringBuilder sb = new StringBuilder().append(current());

            while (Character.isDigit(peek(1))) {
                consume();
                sb.append(current());
            }

            return Optional.of(new Token(startLocation, TokenType.INTEGER_LITERAL, sb.toString()));
        }

        if (current() == '\n' || current() == '\r' || current() == ' ' || current() == '\t') {
            return Optional.empty();
        }

        switch (current()) {
            case '(':
                return Optional.of(new Token(startLocation, TokenType.LEFT_PAREN, "("));
            case ')':
                return Optional.of(new Token(startLocation, TokenType.RIGHT_PAREN, ")"));

            case '[':
                return Optional.of(new Token(startLocation, TokenType.LEFT_BRACKET, "["));
            case ']':
                return Optional.of(new Token(startLocation, TokenType.RIGHT_BRACKET, "]"));

            case '{':
                return Optional.of(new Token(startLocation, TokenType.LEFT_BRACE, "{"));
            case '}':
                return Optional.of(new Token(startLocation, TokenType.RIGHT_BRACE, "}"));

            case '<':
                if (peek(1) == '=') {
                    consume();
                    return Optional.of(new Token(startLocation, TokenType.LEFT_ANGLE_BRACKET_EQUALS, "<="));
                }

                return Optional.of(new Token(startLocation, TokenType.LEFT_ANGLE_BRACKET, "<"));
            case '>':
                if (peek(1) == '=') {
                    consume();
                    return Optional.of(new Token(startLocation, TokenType.RIGHT_ANGLE_BRACKET_EQUALS, ">="));
                }

                return Optional.of(new Token(startLocation, TokenType.RIGHT_ANGLE_BRACKET, ">"));

            case ';':
                return Optional.of(new Token(startLocation, TokenType.SEMICOLON, ";"));
            case ',':
                return Optional.of(new Token(startLocation, TokenType.COMMA, ","));
            case '.':
                return Optional.of(new Token(startLocation, TokenType.DOT, "."));

            case '+':
                return Optional.of(new Token(startLocation, TokenType.PLUS, "+"));
            case '-':
                return Optional.of(new Token(startLocation, TokenType.MINUS, "-"));
            case '*':
                return Optional.of(new Token(startLocation, TokenType.STAR, "*"));
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

                return Optional.of(new Token(startLocation, TokenType.SLASH, "/"));
            case '!':
                if (peek(1) == '=') {
                    consume();
                    return Optional.of(new Token(startLocation, TokenType.BANG_EQUALS, "!="));
                }

                return Optional.of(new Token(startLocation, TokenType.BANG, "!"));

            case '@':
                return Optional.of(new Token(startLocation, TokenType.ASPERAND, "@"));

            case '&':
                return Optional.of(new Token(startLocation, TokenType.AMPERSAND, "&"));

            case '=':
                if (peek(1) == '=') {
                    consume();
                    return Optional.of(new Token(startLocation, TokenType.DOUBLE_EQUALS, "<="));
                }

                return Optional.of(new Token(startLocation, TokenType.EQUALS, "="));

            case '"': {
                consume();
                StringBuilder sb = new StringBuilder();

                while (current() != '"') {
                    if (current() == '\\') {
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
                                return Optional.of(new Token(startLocation, TokenType.ERROR, String.valueOf(current())));
                        }
                    } else {
                        sb.append(current());
                    }

                    consume();
                }

                return Optional.of(new Token(startLocation, TokenType.STRING_LITERAL, sb.toString()));
            }
        }

        return Optional.of(new Token(startLocation, TokenType.ERROR, String.valueOf(current())));
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
