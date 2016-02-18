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
			if (s instanceof FunctionSymbol || s instanceof VariableSymbol) {
				if (!s.isUsed()) {
					if (s instanceof FunctionSymbol) {
						FunctionSymbol f = (FunctionSymbol) s;
						if (f.getVisibility() == FunctionVisibility.PUBLIC)
							continue;
					}
					if (input == null) {
						input = s.getToken().getInputStream().toString();
						inputName = ((NamedInputStream) s.getToken().getInputStream()).getName();
					}

					ErrorKind k = s instanceof FunctionSymbol
						? ErrorKind.FUNC_NEVER_CALLED
						: ErrorKind.VAR_NEVER_USED;

					Token token = s.getToken();
					int line = token.getLine();
					int charPos = token.getCharPositionInLine();
					int charPosStop = charPos + s.getName().length() - 1;
					ErrorMessage message = new ErrorMessage(k, s.getName());
					BaseError warn = new LineCharError(message, inputName,
							input, line, charPos, charPos, charPosStop);
					warn.setLevel(ErrorLevel.WARNING);
					this.er.addError(warn);
				}
			}
		}
	}

}
