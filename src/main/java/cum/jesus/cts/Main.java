package cum.jesus.cts;

import cum.jesus.cts.ctir.Module;
import cum.jesus.cts.ctir.ir.Builder;
import cum.jesus.cts.environment.Environment;
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
        File input = new File("C:\\Users\\JesusTouchMe\\IdeaProjects\\CTS-Interpreter\\test.cts");
        if (!input.canRead()) {
            throw new IOException("Cannot read input file");
        }

        File graphout = new File("C:\\Users\\JesusTouchMe\\IdeaProjects\\CTS-Interpreter\\ctir.dot");
        if (graphout.exists()) {
            graphout.delete();
        }

        Type.init();

        String text = new String(Files.readAllBytes(input.toPath()));
        Lexer lexer = new Lexer(text);
        List<Token> tokens = lexer.tokenize();

        Environment globalScope = new Environment();

        Parser parser = new Parser(tokens, globalScope);
        AbstractSyntaxTree ast = parser.parse();

        Module module = new Module(input.getName());
        Builder builder = new Builder();

        ast.emit(module, builder, globalScope);
        module.print(System.out);
        System.out.println();

        File output = new File("C:\\Users\\JesusTouchMe\\IdeaProjects\\CTS-Interpreter\\test.ct");
        if (!output.exists()) {
            output.createNewFile();
        }
        if (!output.canWrite()) {
            throw new IOException("Cannot write to output file");
        }

        module.emit(new FileOutputStream(output, false));
    }
}
