package cum.jesus.cts.asm.parsing;

import cum.jesus.cts.asm.lexing.Token;

import java.util.List;

public final class TokenStream {
    public final List<Token> tokens;
    public int pos;

    public TokenStream(List<Token> tokens, int pos) {
        this.tokens = tokens;
        this.pos = pos;
    }

    public Token current() {
        return tokens.get(pos);
    }

    public Token consume() {
        return tokens.get(pos++);
    }
}
