package cum.jesus.cts.error;

public interface ErrorReporter {
    /**
     * Reports any errors and warnings that may have accumulated and then crashes with a fatal error.
     *
     * @param context context of the fatal error leading to crash
     */
    void fatal(ErrorContext context);

    /**
     * Adds an error to a collection of errors without crashing the program.
     * When {@link #spit()} is called and errors are present, the program will fail.
     *
     * @param context the context of the reported error
     */
    void error(ErrorContext context);

    /**
     * Adds a warning to a collection of warnings without crashing the program.
     * Unlike {@link #error(ErrorContext)}, which causes the program to fail during {@link #spit()}, a warning has no effect on the program.
     *
     * @param context the context of the reported warning
     */
    void warn(ErrorContext context);

    /**
     * Simply prints a note when spitting
     *
     * @param context the context of the note
     */
    void note(ErrorContext context);

    /**
     * Spits all warnings and errors.
     * If any errors are present, the program will fail.
     */
    void spit();
}
