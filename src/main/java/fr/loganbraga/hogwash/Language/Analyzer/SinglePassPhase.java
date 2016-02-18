package fr.loganbraga.hogwash.Language.Analyzer;

import fr.loganbraga.hogwash.Language.Symbols.Scope;
import fr.loganbraga.hogwash.Language.Parser.*;
import fr.loganbraga.hogwash.Error.*;
import org.antlr.v4.runtime.Token;

public abstract class SinglePassPhase extends HogwashBaseListener {

	private boolean inLoop;
	protected ErrorReporter er;
	protected Scope currentScope;
	protected SymbolTable st;

	public SinglePassPhase(SymbolTable st, ErrorReporter er) {
		this.inLoop = false;
		this.er = er;
		this.st = st;
		this.currentScope = null;
	}

	@Override
	public void enterLoopingStatement(HogwashParser.LoopingStatementContext ctx) {
		this.inLoop = true;
	}

	@Override
	public void exitLoopingStatement(HogwashParser.LoopingStatementContext ctx) {
		this.inLoop = false;
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

	protected boolean isInLoop() {
		return this.inLoop;
	}

	public SymbolTable getSymbolTable() {
		return this.st;
	}

}
