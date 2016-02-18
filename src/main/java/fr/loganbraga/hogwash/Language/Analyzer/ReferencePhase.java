package fr.loganbraga.hogwash.Language.Analyzer;

import fr.loganbraga.hogwash.Language.Symbols.*;
import fr.loganbraga.hogwash.Language.Parser.*;
import fr.loganbraga.hogwash.Error.*;
import org.antlr.v4.runtime.Token;

public class ReferencePhase extends SinglePassPhase {

	public ReferencePhase(SymbolTable st, ErrorReporter er) {
		super(st, er);
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
			ErrorMessage em = new ErrorMessage(ErrorKind.VAR_NOT_FOUND, name);
			this.generateError(tk, em);
		} else if (var instanceof FunctionSymbol) {
			ErrorMessage em = new ErrorMessage(ErrorKind.FUNC_AS_VAR, name);
			this.generateError(tk, em);
		} else {
			int referencePosition = tk.getTokenIndex();
			VariableSymbol v = (VariableSymbol) var;
			v.setIsUsed(true);
			if (referencePosition < v.getToken().getTokenIndex()) {
				ErrorMessage em = new ErrorMessage(ErrorKind.VAR_FORWARD_REF, name);
				this.generateError(tk, em);
			}
		}
	}

	@Override
	public void exitFuncCallExpression(HogwashParser.FuncCallExpressionContext ctx) {
		Token tk = ctx.Identifier().getSymbol();
		String name = tk.getText();
		Symbol func = this.currentScope.resolve(name);
		if (func == null) {
			ErrorMessage em = new ErrorMessage(ErrorKind.FUNC_NOT_FOUND, name);
			this.generateError(tk, em);
		} else if (func instanceof VariableSymbol) {
			ErrorMessage em = new ErrorMessage(ErrorKind.VAR_CALLED, name);
			this.generateError(tk, em);
		} else {
			func.setIsUsed(true);
		}
	}

	@Override
	public void exitExtFuncCallExpression(HogwashParser.ExtFuncCallExpressionContext ctx) {
		Token tk = ctx.ExtIdentifier().getSymbol();
		String name = tk.getText().substring(1);
		Symbol func = this.currentScope.resolve(name);
		if (func != null) {
			ErrorMessage em = new ErrorMessage(ErrorKind.EXT_FUNC_DEF, name);
			this.generateError(tk, em);
		}
	}

}
