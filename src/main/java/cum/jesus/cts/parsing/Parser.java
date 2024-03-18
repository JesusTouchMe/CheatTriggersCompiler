package cum.jesus.cts.parsing;

import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.environment.Symbol;
import cum.jesus.cts.lexing.SourceLocation;
import cum.jesus.cts.lexing.Token;
import cum.jesus.cts.lexing.TokenType;
import cum.jesus.cts.parsing.ast.AbstractSyntaxTree;
import cum.jesus.cts.parsing.ast.AstNode;
import cum.jesus.cts.parsing.ast.builtin.CodeBuiltin;
import cum.jesus.cts.parsing.ast.expression.*;
import cum.jesus.cts.parsing.ast.global.*;
import cum.jesus.cts.parsing.ast.statement.*;
import cum.jesus.cts.type.StructType;
import cum.jesus.cts.type.Type;
import cum.jesus.cts.util.Pair;

import java.util.*;

public final class Parser {
    private Environment scope;
    public SortedMap<String, Symbol> globalSymbols;
    private final List<Token> tokens;
    private int pos = 0;
    private List<String> annotations;

    public Parser(List<Token> tokens, Environment scope) {
        this.tokens = tokens;
        this.scope = scope;
        globalSymbols = new TreeMap<>();
        annotations = new ArrayList<>();
    }

    public AbstractSyntaxTree parse() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree();

        while (pos < tokens.size() && current().getType() != TokenType.EOF) {
            AstNode global = global();
            if (global != null) {
                ast.add(global);
            }
        }

        return ast;
    }

    private int getBinaryOperatorPrecedence(TokenType type) {
        switch (type) {
            case LEFT_PAREN:
                return 55;

            case DOT:
                return 50;

            case STAR:
            case SLASH:
                return 40;

            case PLUS:
            case MINUS:
                return 35;

            case DOUBLE_EQUALS:
            case BANG_EQUALS:
            case LEFT_ANGLE_BRACKET:
            case RIGHT_ANGLE_BRACKET:
            case LEFT_ANGLE_BRACKET_EQUALS:
            case RIGHT_ANGLE_BRACKET_EQUALS:
                return 25;

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
            case ASPERAND:
                parseAnnotation();
                return null;
            case KEYWORD_MODULE:
                return parseModule();
            case KEYWORD_IMPORT:
                return parseImport();
            case KEYWORD_NATIVE:
                return parseNative();
            case KEYWORD_FUNC:
                return parseFunction();
            case KEYWORD_STRUCT:
                return parseStruct();

            default:
                throw new RuntimeException("Unexpected token: " + current().getType() + ". Expected global function or variable. " + current().getSourceLocation());
        }
    }

    private AstNode expr(int precedence) {
        AstNode lhs;
        int unaryOperatorPrecedence = getUnaryOperatorPrecedence(current().getType());
        if (unaryOperatorPrecedence != 0 && unaryOperatorPrecedence >= precedence) {
            TokenType operator = consume().getType();
            lhs = new UnaryExpression(replaceAnnotations(), expr(unaryOperatorPrecedence), operator);
        } else {
            lhs = parsePrimary();
        }

        while (true) {
            int binaryOperatorPrecedence = getBinaryOperatorPrecedence(current().getType());
            if (binaryOperatorPrecedence < precedence) {
                break;
            }

            TokenType operator = consume().getType();
            AstNode rhs = null;

            if (operator == TokenType.DOT) {
                rhs = new Variable(replaceAnnotations(), consume().getText(), null);
            } else if (operator == TokenType.LEFT_PAREN) {
                lhs = parseCall(lhs);
            } else {
                rhs = expr(binaryOperatorPrecedence);
            }

            if (operator != TokenType.LEFT_PAREN) {
                lhs = new BinaryExpression(replaceAnnotations(), lhs, operator, rhs);
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

            case ASPERAND:
                parseAnnotation();
                return parsePrimary();

            case KEYWORD_IF:
                return parseIfStatement();

            case LEFT_BRACE:
                return parseCompoundStatement();

            case IDENTIFIER:
                return parseVariable();

            case INTEGER_LITERAL:
                return parseIntegerLiteral();

            case STRING_LITERAL:
                return parseStringLiteral();

            case LEFT_PAREN:
                return parseParen();

            case BUILTIN_CODE:
                return parseBuiltinCode();

            default:
                throw new RuntimeException("Primary issue: expected a primary expression token, got " + current().getType());
        }
    }

    private Type parseType() {
        //expect(TokenType.TYPE);
        return Type.get(consume().getText());
    }

    private void parseAnnotation() {
        expect(TokenType.ASPERAND);
        consume();
        expect(TokenType.IDENTIFIER);
        annotations.add(consume().getText());
    }

    private AstNode parseModule() {
        consume(); // module

        expect(TokenType.IDENTIFIER);
        String name = consume().getText();

        expect(TokenType.SEMICOLON);
        consume();

        return new ModuleStatement(replaceAnnotations(), name);
    }

    private AstNode parseImport() {
        consume();

        expect(TokenType.IDENTIFIER);
        String name = consume().getText();

        expect(TokenType.SEMICOLON);
        consume();

        return new ImportStatement(replaceAnnotations(), name);
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

        globalSymbols.put(name, new Symbol(type, name));

        for (FunctionArgument arg : args) {
            scope.symbols.put(arg.getName(), new Symbol(arg.getType(), arg.getName()));
        }

        if (current().getType() == TokenType.EQUALS) {
            consume();

            List<String> annotations = replaceAnnotations();

            AstNode value = expr();
            expect(TokenType.SEMICOLON);
            consume();

            if (!isTypeSpecified) {
                type = value.getType() != null ? value.getType() : Type.get("void");
            }

            globalSymbols.put(name, new Symbol(type, name));
            scope = outer;

            return new Function(annotations, type, name, args, Collections.singletonList(new ReturnStatement(annotations, value)), functionScope).singleStatement();
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

        scope = outer;

        return new Function(annotations, type, name, args, body, functionScope);
    }

    private AstNode parseNative() {
        List<String> annotations = replaceAnnotations();

        expect(TokenType.KEYWORD_NATIVE);
        consume();

        expect(TokenType.KEYWORD_FUNC);
        consume();

        expect(TokenType.LEFT_ANGLE_BRACKET);
        consume();

        Type type = parseType();
        expect(TokenType.RIGHT_ANGLE_BRACKET);
        consume();

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

        expect(TokenType.SEMICOLON);
        consume();

        globalSymbols.put(name, new Symbol(type, name));

        return new NativeFunction(annotations, type, name, args);
    }

    private AstNode parseStruct() {
        consume();
        String name = consume().getText();

        StructType structType = new StructType(name, new ArrayList<>());
        Type.put(name, structType);

        expect(TokenType.LEFT_BRACE);
        consume();

        List<Pair<Type, String>> structTypeFields = new ArrayList<>();
        List<StructDefinition.Field> fields = new ArrayList<>();

        while (current().getType() != TokenType.RIGHT_BRACE) {
            Type fieldType = parseType();
            String fieldName = consume().getText();

            expect(TokenType.SEMICOLON);
            consume();

            structTypeFields.add(new Pair<>(fieldType, fieldName));
            fields.add(new StructDefinition.Field(StructDefinition.AccessLevel.PUBLIC, fieldType, fieldName));
        }
        consume();

        structType.setBody(structTypeFields);

        AstNode res = new StructDefinition(replaceAnnotations(), name, fields);
        res.setType(structType);
        return res;
    }

    private AstNode parseReturn() {
        expect(TokenType.KEYWORD_RETURN);
        consume();

        if (current().getType() == TokenType.SEMICOLON) {
            return new ReturnStatement(replaceAnnotations(), null);
        }

        return new ReturnStatement(replaceAnnotations(), expr());
    }

    private AstNode parseVariableDeclaration() {
        List<String> annotations = replaceAnnotations();

        Type type = parseType();

        expect(TokenType.IDENTIFIER);
        String name = consume().getText();

        scope.symbols.put(name, new Symbol(type, name));

        if (current().getType() == TokenType.SEMICOLON) {
            return new VariableDeclaration(annotations, type, name, null);
        }

        expect(TokenType.EQUALS);
        consume();

        AstNode initValue = expr();
        return new VariableDeclaration(annotations, type, name, initValue);
    }

    private AstNode parseIfStatement() {
        consume();

        List<String> annotations = replaceAnnotations();

        AstNode condition = expr();
        AstNode body = expr();
        AstNode elseBody = null;

        if (peek(1).getType() == TokenType.KEYWORD_ELSE) {
            expect(TokenType.SEMICOLON);
            consume();

            consume(); // else

            elseBody = expr();
        }

        return new IfStatement(annotations, condition, body, elseBody);
    }

    private AstNode parseCompoundStatement() {
        consume();

        List<String> annotations = replaceAnnotations();

        Environment outer = scope;
        Environment scope = new Environment(outer);
        this.scope = scope;

        List<AstNode> body = new ArrayList<>();
        while (current().getType() != TokenType.RIGHT_BRACE) {
            body.add(expr());
            expect(TokenType.SEMICOLON);
            consume();
        }
        consume();

        this.scope = outer;

        tokens.add(pos, new Token(new SourceLocation(current().getSourceLocation()), TokenType.SEMICOLON, ";"));

        return new CompoundStatement(annotations, body, scope);
    }

    private AstNode parseVariable() {
        List<String> annotations = replaceAnnotations();

        String name = consume().getText();

        Optional<Symbol> symbol = scope.findSymbol(name);
        if (!symbol.isPresent()) {
            symbol = Optional.ofNullable(globalSymbols.get(name));
        }

        return symbol.map(value -> new Variable(annotations, name, value.getType())).orElse(null);
    }

    private AstNode parseIntegerLiteral() {
        return new IntegerLiteral(replaceAnnotations(), Long.parseLong(consume().getText()));
    }

    private AstNode parseStringLiteral() {
        expect(TokenType.STRING_LITERAL);
        StringBuilder sb = new StringBuilder().append(consume().getText());
        while (current().getType() == TokenType.STRING_LITERAL) {
            sb.append(consume().getText());
        }
        return new StringLiteral(replaceAnnotations(), sb.toString());
    }

    private AstNode parseParen() {
        consume();
        AstNode expr = expr();
        expect(TokenType.RIGHT_PAREN);
        consume();
        return expr;
    }

    private AstNode parseCall(AstNode callee) {
        List<String> annotations = replaceAnnotations();

        List<AstNode> params = new ArrayList<>();
        while (current().getType() != TokenType.RIGHT_PAREN) {
            params.add(expr());
            if (current().getType() != TokenType.RIGHT_PAREN) {
                expect(TokenType.COMMA);
                consume();
            }
        }
        consume();

        return new CallExpression(annotations, callee, params);
    }

    private AstNode parseBuiltinCode() {
        consume(); // _code
        expect(TokenType.LEFT_PAREN);
        consume();

        List<String> annotations = replaceAnnotations();

        expect(TokenType.STRING_LITERAL); // the asm code string
        String asmCode = ((StringLiteral) parseStringLiteral()).getText();

        if (current().getType() == TokenType.RIGHT_PAREN) {
            consume();
            return new CodeBuiltin(annotations, asmCode, new ArrayList<>());
        }

        expect(TokenType.COMMA);
        consume();

        List<AstNode> params = new ArrayList<>();
        while (current().getType() != TokenType.RIGHT_PAREN) {
            params.add(expr());
            if (current().getType() != TokenType.RIGHT_PAREN) {
                expect(TokenType.COMMA);
                consume();
            }
        }
        consume();

        return new CodeBuiltin(annotations, asmCode, params);
    }

    private void expect(TokenType type) {
        if (current().getType() != type) {
            System.out.println("Expected " + type + ", got " + current().getText() + " (" + current().getType() + ")");
            System.out.printf("At %s:%s\n", current().getSourceLocation().line, current().getSourceLocation().column);
            System.exit(1);
        }
    }

    private List<String> replaceAnnotations() {
        List<String> tmp = annotations;
        annotations = new ArrayList<>();
        return tmp;
    }

    private Token current() {
        return tokens.get(pos);
    }

    private Token consume() {
        Token token = tokens.get(pos++);
        if (current().getType() == TokenType.IDENTIFIER && Type.exists(current().getText())) {
            current().setType(TokenType.TYPE);
        }
        return token;
    }

    private Token peek(int offset) {
        return tokens.get(pos + offset);
    }
}
