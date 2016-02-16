package fr.loganbraga.hogwash.Language.Analyzer;

import fr.loganbraga.hogwash.Language.Symbols.*;
import fr.loganbraga.hogwash.Language.Parser.*;
import fr.loganbraga.hogwash.Error.*;
import org.antlr.v4.runtime.Token;

public class ReferencePhase extends HogwashBaseListener {

	protected ErrorReporter er;
	protected SymbolTable st;
	protected Scope currentScope;

	public ReferencePhase(SymbolTable st, ErrorReporter er) {
		this.er = er;
		this.st = st;
		this.currentScope = null;
	}

	@Override
	public void enterCompilationUnit(HogwashParser.CompilationUnitContext ctx) {
		this.currentScope = this.st.getGlobalScope();
	}

	@Override
	public void enterFunctionDecl(HogwashParser.FunctionDeclContext ctx) {
		this.currentScope = this.st.getScope(ctx);
	}

	@Override
	public void exitFunctionDecl(HogwashParser.FunctionDeclContext ctx) {
		this.currentScope = this.currentScope.getEnclosingScope();
	}

	@Override
	public void enterBlock(HogwashParser.BlockContext ctx) {
		this.currentScope = this.st.getScope(ctx);
	}

	@Override
	public void exitBlock(HogwashParser.BlockContext ctx) {
		this.currentScope = this.currentScope.getEnclosingScope();
	}

	@Override
	public void exitIdentifierExpression(HogwashParser.IdentifierExpressionContext ctx) {
		Token tk = ctx.name().Identifier().getSymbol();
		String name = tk.getText();
		Symbol var = this.currentScope.resolve(name);
		if (var == null) {
			this.referenceError(tk, name,
					"variable `" + name + "` is not declared");
		} else if (var instanceof FunctionSymbol) {
			this.referenceError(tk, name,
					"function `" + name + "` is used as a variable");
		} else {
			int referencePosition = tk.getTokenIndex();
			VariableSymbol v = (VariableSymbol) var;
			v.setIsUsed(true);
			if (referencePosition < v.getToken().getTokenIndex())
				this.referenceError(tk, name,
						"variable `" + name + "` is used before its declaration");
		}
	}

	@Override
	public void exitFuncCallExpression(HogwashParser.FuncCallExpressionContext ctx) {
		Token tk = ctx.Identifier().getSymbol();
		String name = tk.getText();
		Symbol func = this.currentScope.resolve(name);
		if (func == null) {
			this.referenceError(tk, name,
					"function `" + name + "` does not exist");
		} else if (func instanceof VariableSymbol) {
			this.referenceError(tk, name,
					"variable `" + name + "` is used as a function");
		} else {
			func.setIsUsed(true);
		}
	}

	protected void referenceError(Token token, String name, String message) {
		int line = token.getLine();
		int charPosStart = token.getCharPositionInLine();
		int charPosStop = charPosStart + name.length() - 1;

		String input = token.getInputStream().toString();

		BaseError error = new LineCharError(message, this.er.getInputName(),
				input, line, charPosStart, charPosStart, charPosStop);
		this.er.addError(error);
	}

}
