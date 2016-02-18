package fr.loganbraga.hogwash.Language.Analyzer;

import fr.loganbraga.hogwash.Language.Symbols.*;
import fr.loganbraga.hogwash.Language.Parser.*;
import fr.loganbraga.hogwash.Error.*;
import org.antlr.v4.runtime.Token;

public class DefinePhase extends SinglePassPhase {

	public DefinePhase(SymbolTable st, ErrorReporter er) {
		super(st, er);
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
			this.alreadyExistsError(startToken, function);
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

		if (!mutable && ctx.variableInit() == null) {
			ErrorMessage message = new ErrorMessage(ErrorKind.CONST_NOT_SET, name.getText());
			this.generateError(name, message);
		}
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
			this.alreadyExistsError(nameToken, var);
		}
	}

	protected void alreadyExistsError(Token token, Symbol s) {
		String name = token.getText();
		ErrorMessage message;
		ErrorLevel level = ErrorLevel.ERROR;

		Token originalDef = this.currentScope.resolve(name).getToken();
		NamedInputStream nis = (NamedInputStream) originalDef.getInputStream();
		String originalDefFile = nis.getName();
		int originalDefLine = originalDef.getLine();
		int originalDefChar = originalDef.getCharPositionInLine() + 1;

		if (s instanceof FunctionSymbol) {
			Token originalFunc = this.currentScope.resolve(name).getToken();
			message = new ErrorMessage(ErrorKind.FUNC_ALREADY_DEF, name,
					originalDefFile, originalDefLine, originalDefChar);
		} else {
			message = new ErrorMessage(ErrorKind.VAR_ALREADY_DEF, name,
					originalDefFile, originalDefLine, originalDefChar);
			level = ErrorLevel.WARNING;
		}

		if (this.currentScope instanceof FunctionSymbol) {
			message = new ErrorMessage(ErrorKind.PARAM_NAME_TAKEN,
					name, this.currentScope.getScopeName());
			level = ErrorLevel.ERROR;
		}

		this.generateError(token, message, level);
	}

}
