package fr.loganbraga.hogwash.Language.Analyzer;

import fr.loganbraga.hogwash.Language.Symbols.Scope;
import fr.loganbraga.hogwash.Language.Parser.*;
import fr.loganbraga.hogwash.Error.*;
import org.antlr.v4.runtime.Token;

public class SinglePassPhase extends HogwashBaseListener {

	protected ErrorReporter er;
	protected Scope currentScope;
	protected SymbolTable st;

	public SinglePassPhase(SymbolTable st, ErrorReporter er) {
		this.er = er;
		this.st = st;
		this.currentScope = null;
	}

	protected void generateError(Token token, ErrorMessage message) {
		this.generateError(token, message, ErrorLevel.ERROR);
	}

	protected void generateError(Token token, ErrorMessage message, ErrorLevel level) {
		int line = token.getLine();
		int charPosStart = token.getCharPositionInLine();
		int charPosStop = charPosStart + token.getText().length() - 1;

		String input = token.getInputStream().toString();
		String inputName = ((NamedInputStream) token.getInputStream()).getName();

		BaseError error = new LineCharError(message, inputName,
				input, line, charPosStart, charPosStart, charPosStop);
		error.setLevel(level);
		this.er.addError(error);
	}

	public SymbolTable getSymbolTable() {
		return this.st;
	}

}
