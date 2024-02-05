package cum.jesus.cts.lexing;

import cum.jesus.cts.type.Type;

import java.util.*;

public final class Lexer {
    private String text;
    private int pos = 0;

    private static final Map<String, TokenType> keywords = new HashMap<String, TokenType>() {{
       put("func", TokenType.KEYWORD_FUNC);
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

        tokens.add(new Token(TokenType.EOF, ""));

        return tokens;
    }

    private Optional<Token> nextToken() {
        if (Character.isLetter(current()) || current() == '_') {
            StringBuilder sb = new StringBuilder().append(current());

            while (Character.isLetterOrDigit(peek(1)) || peek(1) == '_') {
                consume();
                sb.append(current());
            }

            String str = sb.toString();

            if (keywords.containsKey(str)) {
                return Optional.of(new Token(keywords.get(str), str));
            }

            if (Type.exists(str)) {
                return Optional.of(new Token(TokenType.TYPE, str));
            }

            return Optional.of(new Token(TokenType.IDENTIFIER, str));
        }

        if (Character.isDigit(current())) {
            StringBuilder sb = new StringBuilder().append(current());

            while (Character.isDigit(peek(1))) {
                consume();
                sb.append(current());
            }

            return Optional.of(new Token(TokenType.INTEGER_LITERAL, sb.toString()));
        }

        if (current() == '\n' || current() == '\r' || current() == ' ' || current() == '\t') {
            return Optional.empty();
        }

        switch (current()) {
            case '(':
                return Optional.of(new Token(TokenType.LEFT_PAREN, "("));
            case ')':
                return Optional.of(new Token(TokenType.RIGHT_PAREN, ")"));

            case '[':
                return Optional.of(new Token(TokenType.LEFT_BRACKET, "["));
            case ']':
                return Optional.of(new Token(TokenType.RIGHT_BRACKET, "]"));

            case '{':
                return Optional.of(new Token(TokenType.LEFT_BRACE, "{"));
            case '}':
                return Optional.of(new Token(TokenType.RIGHT_BRACE, "}"));

            case '<':
                return Optional.of(new Token(TokenType.LEFT_ANGLE_BRACKET, "<"));
            case '>':
                return Optional.of(new Token(TokenType.RIGHT_ANGLE_BRACKET, ">"));

            case ';':
                return Optional.of(new Token(TokenType.SEMICOLON, ";"));
            case ',':
                return Optional.of(new Token(TokenType.COMMA, ","));

            case '+':
                return Optional.of(new Token(TokenType.PLUS, "+"));
            case '-':
                return Optional.of(new Token(TokenType.MINUS, "-"));
            case '*':
                return Optional.of(new Token(TokenType.STAR, "*"));
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

                return Optional.of(new Token(TokenType.SLASH, "/"));

            case '=':
                return Optional.of(new Token(TokenType.EQUALS, "="));
        }

        return Optional.of(new Token(TokenType.ERROR, String.valueOf(current())));
    }

    private char current() {
        return text.charAt(pos);
    }

    private char consume() {
        return text.charAt(pos++);
    }

    private char peek(int offset) {
        try {
            return text.charAt(pos + offset);
        } catch (StringIndexOutOfBoundsException ignored) {
            return ' ';
        }
    }
}
