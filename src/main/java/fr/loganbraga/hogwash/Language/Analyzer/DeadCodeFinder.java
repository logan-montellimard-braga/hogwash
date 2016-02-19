package fr.loganbraga.hogwash.Language.Analyzer;

import fr.loganbraga.hogwash.Language.Symbols.*;
import fr.loganbraga.hogwash.Language.Parser.NamedInputStream;
import fr.loganbraga.hogwash.Error.*;
import java.util.List;
import java.util.Iterator;
import org.antlr.v4.runtime.Token;

public class DeadCodeFinder {

	protected ErrorReporter er;
	protected SymbolTable st;
	
	public DeadCodeFinder(SymbolTable st, ErrorReporter er) {
		this.st = st;
		this.er = er;
	}

	public void process() {
		List<Symbol> symbols = this.st.getAllSymbols();
		Iterator<Symbol> it = symbols.iterator();
		String input = null;
		String inputName = null;

		while (it.hasNext()) {
			Symbol s = it.next();
			if (s instanceof VariableSymbol)
				this.handleVariable((VariableSymbol) s);
			else if (s instanceof FunctionSymbol)
				this.handleFunction((FunctionSymbol) s);
		}
	}

	protected void handleVariable(VariableSymbol variable) {
		ErrorKind ek;
		if (variable.isExportable()) return;
		if (!variable.isUsed())
			ek = ErrorKind.VAR_NEVER_USED;
		else if (!variable.isSet())
			ek = ErrorKind.VAR_NEVER_SET;
		else return;

		ErrorMessage em = new ErrorMessage(ek, variable.getName());
		this.generateError(variable.getToken(), em);
	}

	protected void handleFunction(FunctionSymbol function) {
		if (function.getVisibility() == FunctionVisibility.PUBLIC) return;
		if (function.isUsed()) return;

		ErrorMessage em = new ErrorMessage(ErrorKind.FUNC_NEVER_CALLED,
				function.getName());
		this.generateError(function.getToken(), em);
	}

	protected void generateError(Token token, ErrorMessage message) {
		int line = token.getLine();
		int charPos = token.getCharPositionInLine();
		int charPosStop = charPos + token.getText().length() - 1;
		String input = token.getInputStream().toString();
		String inputName = ((NamedInputStream) token.getInputStream()).getName();
		BaseError warn = new LineCharError(message, inputName,
				input, line, charPos, charPos, charPosStop);
		warn.setLevel(ErrorLevel.WARNING);
		this.er.addError(warn);
	}

}
