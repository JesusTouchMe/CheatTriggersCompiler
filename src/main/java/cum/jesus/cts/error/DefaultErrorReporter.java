package cum.jesus.cts.error;

import java.util.ArrayList;
import java.util.List;

public final class DefaultErrorReporter implements ErrorReporter {
    private final List<Context> contexts = new ArrayList<>();

    private void print(Level level, ErrorContext context) {
        System.err.printf("%s:%d:%d: %s: %s\n", context.file, context.location.line, context.location.column, level.text, context.message);
    }

    @Override
    public void fatal(ErrorContext context) {
        print(Level.FATAL, context);
        spit();

        System.exit(1);
    }

    @Override
    public void error(ErrorContext context) {
        contexts.add(new Context(Level.ERROR, context));
    }

    @Override
    public void warn(ErrorContext context) {
        contexts.add(new Context(Level.WARNING, context));
    }

    @Override
    public void note(ErrorContext context) {
        contexts.add(new Context(Level.NOTE, context));
    }

    @Override
    public void spit() {
        boolean hasError = false;

        for (Context context : contexts) {
            if (context.level == Level.ERROR) hasError = true;

            print(context.level, context.context);
        }

        if (hasError) System.exit(1);

        contexts.clear();
    }

    private static final class Context {
        Level level;
        ErrorContext context;

        Context(Level level, ErrorContext context) {
            this.level = level;
            this.context = context;
        }
    }

    private enum Level {
        NOTE("Note"),
        WARNING("Warn"),
        ERROR("Error"),
        FATAL("Fatal"),

        ;

        private final String text;

        Level(String text) {
            this.text = text;
        }
    }
}
