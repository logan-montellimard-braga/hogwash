package fr.loganbraga.hogwash.Language.Analyzer;

import fr.loganbraga.hogwash.Language.Symbols.*;
import fr.loganbraga.hogwash.Language.Parser.*;
import fr.loganbraga.hogwash.Error.*;
import org.antlr.v4.runtime.Token;

public class DefinePhase extends HogwashBaseListener {

	protected ErrorReporter er;
	protected Scope currentScope;
	protected SymbolTable st;

	public DefinePhase(SymbolTable st, ErrorReporter er) {
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
		FunctionVisibility visibility = FunctionVisibility.PRIVATE;
		if (ctx.FunctionVisibility() != null) {
			switch(ctx.FunctionVisibility().getText()) {
				case "pub":
					visibility = FunctionVisibility.PUBLIC;
					break;
				default:
					visibility = FunctionVisibility.PRIVATE;
					break;
			}
		}

		String name = ctx.name().Identifier().getText();

		String type = PrimitiveTypeSymbol.DEFAULT_TYPE.name().toLowerCase();
		if (ctx.functionReturnType() != null)
			type = ctx.functionReturnType().funcTypeDecl().funcType.getText();
		Symbol typeS = this.currentScope.resolve(type);

		FunctionSymbol function = new FunctionSymbol(name,
				(PrimitiveTypeSymbol) typeS, this.currentScope, visibility);

		function.setToken(ctx.name().Identifier().getSymbol());

		try {
			this.currentScope.define(function);
		} catch (SymbolAlreadyExistsException e) {
			Token startToken = ctx.name().getStart();
			this.alreadyExistsError(startToken, name);
		}

		this.st.addScope(ctx, function);
		this.currentScope = function;
	}

	@Override
	public void exitFunctionDecl(HogwashParser.FunctionDeclContext ctx) {
		this.currentScope = this.currentScope.getEnclosingScope();
	}

	@Override
	public void enterBlock(HogwashParser.BlockContext ctx) {
		this.currentScope = new LocalScope(currentScope);
		this.st.addScope(ctx, this.currentScope);
	}

	@Override
	public void exitBlock(HogwashParser.BlockContext ctx) {
		this.currentScope = this.currentScope.getEnclosingScope();
	}

	@Override
	public void exitFormalParameter(HogwashParser.FormalParameterContext ctx) {
		HogwashParser.TypeDeclContext t = ctx.formalParameterType() != null
			? ctx.formalParameterType().typeDecl()
			: null;
		Token name = ctx.name().Identifier().getSymbol();
		this.defineVariable(t, name, true, false);
	}

	@Override
	public void exitVariableDecl(HogwashParser.VariableDeclContext ctx) {
		Token name = ctx.name().Identifier().getSymbol();
		boolean mutable = ctx.MUT() != null;
		boolean exportable = ctx.EXT() != null;
		this.defineVariable(ctx.typeDecl(), name, mutable, exportable);
	}

	protected void defineVariable(HogwashParser.TypeDeclContext ctx,
			Token nameToken, boolean mutable, boolean exportable) {
        String name = nameToken.getText();

		String typeStr = PrimitiveTypeSymbol.DEFAULT_TYPE.name().toLowerCase();
		if (ctx != null) typeStr = ctx.T_TYPE().getText();
		PrimitiveTypeSymbol type = (PrimitiveTypeSymbol) this.currentScope.resolve(typeStr);

		VariableSymbol var = new VariableSymbol(name, type, mutable, exportable);
		var.setToken(nameToken);
		try {
			this.currentScope.define(var);
		} catch (SymbolAlreadyExistsException e) {
			this.alreadyExistsError(nameToken, name);
		}
	}

	protected void alreadyExistsError(Token token, String name) {
		int line = token.getLine();
		int charPosStart = token.getCharPositionInLine();
		int charPosStop = charPosStart + name.length() - 1;

		String input = token.getInputStream().toString();
		ErrorMessage message = new ErrorMessage(ErrorKind.VAR_ALREADY_DEF, name, this.currentScope.getScopeName());
		ErrorLevel level = ErrorLevel.WARNING;

		if (this.currentScope instanceof FunctionSymbol) {
			message = new ErrorMessage(ErrorKind.PARAM_NAME_TAKEN, name, this.currentScope.getScopeName());
			level = ErrorLevel.ERROR;
		}

		BaseError error = new LineCharError(message, this.er.getInputName(),
				input, line, charPosStart, charPosStart, charPosStop);
		error.setLevel(level);
		this.er.addError(error);
	}

	public SymbolTable getSymbolTable() {
		return this.st;
	}

}
