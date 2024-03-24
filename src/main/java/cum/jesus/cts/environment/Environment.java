package cum.jesus.cts.environment;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Function;

import java.util.*;

public final class Environment {
    public static final Map<String, Environment> scopes = new HashMap<>();
    public static final Map<String, Module> modules = new HashMap<>();

    public Map<String, Function> functions = new HashMap<>();
    public Map<String, LocalSymbol> variables; // for codegen
    public SortedMap<String, Symbol> symbols; // for parser

    public Environment parent;

    public Environment(Environment parent) {
        this.variables = new HashMap<>();
        this.symbols = new TreeMap<>();
        this.parent = parent;
    }

    public Environment() {
        this(null);
    }

    public Optional<Function> findFunction(final String name) {
        Environment env = this;
        while (env != null) {
            if (env.functions.containsKey(name)) {
                return Optional.of(env.functions.get(name));
            }

            env = env.parent;
        }
        return Optional.empty();
    }

    public Optional<LocalSymbol> findVariable(final String name) {
        Environment env = this;
        while (env != null) {
            if (env.variables.containsKey(name)) {
                return Optional.of(env.variables.get(name));
            }

            env = env.parent;
        }
        return Optional.empty();
    }

    public Optional<Symbol> findSymbol(final String name) {
        Environment env = this;
        while (env != null) {
            if (env.symbols.containsKey(name)) {
                return Optional.of(env.symbols.get(name));
            }

            env = env.parent;
        }
        return Optional.empty();
    }
}
