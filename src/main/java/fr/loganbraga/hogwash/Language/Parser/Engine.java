package fr.loganbraga.hogwash.Language.Parser;

// Classes generated from ANTLR4 grammar
import fr.loganbraga.hogwash.Language.Parser.HogwashLexer;
import fr.loganbraga.hogwash.Language.Parser.HogwashParser;
import fr.loganbraga.hogwash.Language.Parser.NamedInputStream;

import fr.loganbraga.hogwash.Error.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.*;

public class Engine {

	protected HogwashParser parser;
	protected ParseTree tree;
	
	public Engine(File file, ErrorReporter er) {
		InputStream is = null;
		NamedInputStream input = null;
		try {
			is = new FileInputStream(file);
			input = new NamedInputStream(is, file.getPath());
		} catch (IOException e) {
			BaseError error = new BaseError(
					new ErrorMessage(ErrorKind.BASE_ERROR, e.getMessage()));
			er.addError(error);
			return;
		}

		HogwashLexer lexer = new HogwashLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		this.parser = new HogwashParser(tokens);

		this.parser.removeErrorListeners();
		this.parser.addErrorListener(new FormatterErrorListener(er));
	}

	public void parse() {
		this.parser.setBuildParseTree(true);
		this.tree = this.parser.compilationUnit();
	}

	public ParseTree getTree() {
		return this.tree;
	}
}
