package fr.loganbraga.hogwash.Language.Parser;

// Classes generated from ANTLR4 grammar
import fr.loganbraga.hogwash.Language.Parser.HogwashLexer;
import fr.loganbraga.hogwash.Language.Parser.HogwashParser;

import fr.loganbraga.hogwash.Error.ErrorReporter;
import fr.loganbraga.hogwash.Error.QuickFailErrorReporter;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.InputStream;
import java.io.IOException;

public class Engine {

	protected HogwashParser parser;
	
	public Engine(InputStream is, String inputName, ErrorReporter er) throws IOException {
		ANTLRInputStream input = new ANTLRInputStream(is);
		HogwashLexer lexer = new HogwashLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		this.parser = new HogwashParser(tokens);

		this.parser.removeErrorListeners();
		this.parser.addErrorListener(new FormatterErrorListener(inputName, er));
	}

	public void parse() {
		this.parser.setBuildParseTree(true);
		ParseTree tree = this.parser.compilationUnit();
	}
}
