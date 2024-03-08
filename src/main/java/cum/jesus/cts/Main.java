package cum.jesus.cts;

import cum.jesus.cts.asm.codegen.OutputBuffer;
import cum.jesus.cts.asm.codegen.builder.OpcodeBuilder;
import cum.jesus.cts.asm.instruction.AsmValue;
import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.OptimizationLevel;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.environment.Environment;
import cum.jesus.cts.error.DefaultErrorReporter;
import cum.jesus.cts.error.ErrorReporter;
import cum.jesus.cts.lexing.Lexer;
import cum.jesus.cts.lexing.Token;
import cum.jesus.cts.parsing.Parser;
import cum.jesus.cts.parsing.ast.AbstractSyntaxTree;
import cum.jesus.cts.type.Type;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        //File input = new File("C:\\Users\\JesusTouchMe\\IdeaProjects\\CTS-Compiler\\test.cts");
        File input = new File("C:\\Users\\Jannik\\IdeaProjects\\CheatTriggersCompiler\\test.cts");

        if (!input.canRead()) {
            throw new IOException("Cannot read input file");
        }

        //File graphout = new File("C:\\Users\\JesusTouchMe\\IdeaProjects\\CTS-Compiler\\ctir.dot");
        File graphout = new File("C:\\Users\\Jannik\\IdeaProjects\\CheatTriggersCompiler\\ctir.dot");
        if (graphout.exists()) {
            graphout.delete();
        }

        graphout = null; // let the gc remove the file cuz we don't need it

        Type.init();

        String text = new String(Files.readAllBytes(input.toPath()));
        Lexer lexer = new Lexer(text);
        List<Token> tokens = lexer.tokenize();

        Environment globalScope = new Environment();

        Parser parser = new Parser(tokens, globalScope);
        AbstractSyntaxTree ast = parser.parse();

        Module module = new Module(input.getName());
        Builder builder = new Builder();

        ast.print(System.out);
        ast.emit(module, builder, globalScope);

        module.print(System.out);
        System.out.println();
        module.optimize(OptimizationLevel.HIGH);

        //File output = new File("C:\\Users\\JesusTouchMe\\IdeaProjects\\CTS-Compiler\\test.ct");
        File output = new File("C:\\Users\\Jannik\\IdeaProjects\\CheatTriggersCompiler\\test.ct");
        if (!output.exists()) {
            output.createNewFile();
        }
        if (!output.canWrite()) {
            throw new IOException("Cannot write to output file");
        }

        module.emit(new FileOutputStream(output, false));
    }

    private static void asm() throws IOException {
        File input = new File("C:\\Users\\JesusTouchMe\\IdeaProjects\\CTS-Compiler\\test.ctasm");
        if (!input.canRead()) {
            throw new IOException("Cannot read input file");
        }
        cum.jesus.cts.asm.lexing.Lexer lexer = new cum.jesus.cts.asm.lexing.Lexer(new String(Files.readAllBytes(input.toPath())));
        List<cum.jesus.cts.asm.lexing.Token> tokens = lexer.tokenize();

        ErrorReporter errorReporter = new DefaultErrorReporter();
        cum.jesus.cts.asm.parsing.Parser parser = new cum.jesus.cts.asm.parsing.Parser(input.getName(), tokens, errorReporter);

        List<AsmValue> values = parser.parse();
        OutputBuffer outputBuffer = new OutputBuffer();
        OpcodeBuilder builder = new OpcodeBuilder(outputBuffer);

        for (AsmValue value : values) {
            value.emit(builder);
        }

        builder.patchForwardLabels();

        File output = new File("C:\\Users\\JesusTouchMe\\IdeaProjects\\CTS-Interpreter\\test.ct");
        if (!output.exists()) {
            output.createNewFile();
        }
        if (!output.canWrite()) {
            throw new IOException("Cannot write to output file");
        }

        outputBuffer.emit(new FileOutputStream(output, false));

        System.exit(0);
    }
}
