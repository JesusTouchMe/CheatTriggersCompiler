package cum.jesus.cts.parsing;

import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.environment.Symbol;
import cum.jesus.cts.lexing.Token;
import cum.jesus.cts.lexing.TokenType;
import cum.jesus.cts.parsing.ast.AbstractSyntaxTree;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.parsing.ast.expression.*;
import cum.jesus.cts.parsing.ast.global.Function;
import cum.jesus.cts.parsing.ast.global.FunctionArgument;
import cum.jesus.cts.parsing.ast.statement.ReturnStatement;
import cum.jesus.cts.parsing.ast.statement.VariableDeclaration;
import cum.jesus.cts.type.Type;

import java.util.*;

public final class Parser {
    private Environment scope;
    public SortedMap<String, Symbol> globalSymbols;
    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens, Environment scope) {
        this.tokens = tokens;
        this.scope = scope;
        globalSymbols = new TreeMap<String, Symbol>() {{
            put("println", new Symbol(Type.get("void"), "println"));
        }};
    }

    public AbstractSyntaxTree parse() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree();

        while (pos < tokens.size() && current().getType() != TokenType.EOF) {
            ast.add(global());
        }

        return ast;
    }

    private int getBinaryOperatorPrecedence(TokenType type) {
        switch (type) {
            case LEFT_PAREN:
                return 55;

            case STAR:
            case SLASH:
                return 40;

            case PLUS:
            case MINUS:
                return 35;

            case EQUALS:
                return 10;

            default:
                return 0;
        }
    }

    private int getUnaryOperatorPrecedence(TokenType type) {
        switch (type) {
            case PLUS:
            case MINUS:
                return 50;

            default:
                return 0;
        }
    }

    private AstNode global() {
        switch (current().getType()) {
            case TYPE:
                AstNode variableDeclaration = parseVariableDeclaration();
                expect(TokenType.SEMICOLON);
                consume();
                return variableDeclaration;
            case KEYWORD_FUNC:
                return parseFunction();

            default:
                throw new RuntimeException("Unexpected token: " + current().getType() + ". Expected global function or variable");
        }
    }

    private AstNode expr(int precedence) {
        AstNode lhs;
        int unaryOperatorPrecedence = getUnaryOperatorPrecedence(current().getType());
        if (unaryOperatorPrecedence != 0 && unaryOperatorPrecedence >= precedence) {
            TokenType operator = consume().getType();
            lhs = new UnaryExpression(expr(unaryOperatorPrecedence), operator);
        } else {
            lhs = parsePrimary();
        }

        while (true) {
            int binaryOperatorPrecedence = getBinaryOperatorPrecedence(current().getType());
            if (binaryOperatorPrecedence < precedence) {
                break;
            }

            TokenType operator = consume().getType();
            if (operator == TokenType.LEFT_PAREN) {
                lhs = parseCall(lhs);
            } else {
                AstNode rhs = expr(binaryOperatorPrecedence);
                lhs = new BinaryExpression(lhs, operator, rhs);
            }
        }

        return lhs;
    }

    private AstNode expr() {
        return expr(1);
    }

    private AstNode parsePrimary() {
        switch (current().getType()) {
            case KEYWORD_RETURN:
                return parseReturn();

            case TYPE:
                return parseVariableDeclaration();

            case IDENTIFIER:
                return parseVariable();

            case INTEGER_LITERAL:
                return parseIntegerLiteral();

            case LEFT_PAREN:
                return parseParen();

            default:
                throw new RuntimeException("Primary issue: expected a primary expression token, got " + current().getType());
        }
    }

    private Type parseType() {
        expect(TokenType.TYPE);
        return Type.get(consume().getText());
    }

    private AstNode parseFunction() {
        expect(TokenType.KEYWORD_FUNC);
        consume();

        Type type = Type.get("void");
        boolean isTypeSpecified = false;

        if (current().getType() == TokenType.LEFT_ANGLE_BRACKET) {
            consume();
            type = parseType();
            expect(TokenType.RIGHT_ANGLE_BRACKET);
            consume();
            isTypeSpecified = true;
        }

        expect(TokenType.IDENTIFIER);
        String name = consume().getText();

        List<FunctionArgument> args = new ArrayList<>();
        expect(TokenType.LEFT_PAREN);
        consume();

        while (current().getType() != TokenType.RIGHT_PAREN) {
            Type argType = parseType();
            String argName = consume().getText();
            args.add(new FunctionArgument(argType, argName));
            if (current().getType() != TokenType.RIGHT_PAREN) {
                expect(TokenType.COMMA);
                consume();
            }
        }
        consume();

        Environment outer = scope;
        Environment functionScope = new Environment(outer);
        scope = functionScope;

        for (FunctionArgument arg : args) {
            scope.symbols.put(arg.getName(), new Symbol(arg.getType(), arg.getName()));
        }

        if (current().getType() == TokenType.EQUALS) {
            consume();

            AstNode value = expr();
            expect(TokenType.SEMICOLON);
            consume();

            if (!isTypeSpecified) {
                type = value.getType();
            }

            globalSymbols.put(name, new Symbol(type, name));
            scope = outer;

            return new Function(type, name, args, Collections.singletonList(new ReturnStatement(value)), functionScope);
        }

        expect(TokenType.LEFT_BRACE);
        consume();

        List<AstNode> body = new ArrayList<>();
        while (current().getType() != TokenType.RIGHT_BRACE) {
            body.add(expr());
            expect(TokenType.SEMICOLON);
            consume();
        }
        consume();

        globalSymbols.put(name, new Symbol(type, name));
        scope = outer;

        return new Function(type, name, args, body, functionScope);
    }

    private AstNode parseReturn() {
        expect(TokenType.KEYWORD_RETURN);
        consume();

        if (current().getType() == TokenType.SEMICOLON) {
            return new ReturnStatement(null);
        }

        return new ReturnStatement(expr());
    }

    private AstNode parseVariableDeclaration() {
        Type type = parseType();

        expect(TokenType.IDENTIFIER);
        String name = consume().getText();

        scope.symbols.put(name, new Symbol(type, name));

        if (current().getType() == TokenType.SEMICOLON) {
            return new VariableDeclaration(type, name, null);
        }

        expect(TokenType.EQUALS);
        consume();

        AstNode initValue = expr();
        return new VariableDeclaration(type, name, initValue);
    }

    private AstNode parseVariable() {
        String name = consume().getText();

        Optional<Symbol> symbol = scope.findSymbol(name);
        if (!symbol.isPresent()) {
            symbol = Optional.ofNullable(globalSymbols.get(name));
        }

        return symbol.map(value -> new Variable(name, value.getType())).orElse(null);
    }

    private AstNode parseIntegerLiteral() {
        return new IntegerLiteral(Long.parseLong(consume().getText()));
    }

    private AstNode parseParen() {
        consume();
        AstNode expr = expr();
        expect(TokenType.RIGHT_PAREN);
        consume();
        return expr;
    }

    private AstNode parseCall(AstNode callee) {
        List<AstNode> params = new ArrayList<>();
        while (current().getType() != TokenType.RIGHT_PAREN) {
            params.add(expr());
            if (current().getType() != TokenType.RIGHT_PAREN) {
                expect(TokenType.COMMA);
                consume();
            }
        }
        consume();

        return new CallExpression(callee, params);
    }

    private void expect(TokenType type) {
        if (current().getType() != type) {
            throw new RuntimeException("Expected " + type + ", got " + current().getType());
        }
    }

    private Token current() {
        return tokens.get(pos);
    }

    private Token consume() {
        return tokens.get(pos++);
    }

    private Token peek(int offset) {
        return tokens.get(pos + offset);
    }
}
