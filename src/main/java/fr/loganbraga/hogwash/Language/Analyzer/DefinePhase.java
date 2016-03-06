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
		if (ctx.PUB() != null)
			visibility = FunctionVisibility.PUBLIC;

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
		boolean optionalArgsStarted = false;
		for (Symbol sym : this.currentScope.getAllSymbols()) {
			ArgumentSymbol arg = (ArgumentSymbol) sym;
			if (arg.hasDefault()) optionalArgsStarted = true;
			else if (optionalArgsStarted) {
				Token tk = arg.getToken();
				String name = tk.getText();
				ErrorMessage em = new ErrorMessage(ErrorKind.OPT_ARG_NOT_LAST, name);
				this.generateError(arg.getToken(), em);
				break;
			}
		}
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
		VariableSymbol var = this.defineVariable(t, name, true, false);
		if (var != null) {
			if (ctx.formalParameterDefaultValue() != null) {
				((ArgumentSymbol) var).setHasDefault(true);
			}
			var.setIsSet(true);
		}
	}

	@Override
	public void exitVariableDecl(HogwashParser.VariableDeclContext ctx) {
		Token name = ctx.name().Identifier().getSymbol();
		boolean mutable = ctx.MUT() != null;
		boolean exportable = ctx.PUB() != null;
		VariableSymbol var = this.defineVariable(ctx.typeDecl(), name, mutable, exportable);
		if (var != null && ctx.variableInit() != null) var.setIsSet(true);

		if (mutable && exportable) {
			ErrorMessage message = new ErrorMessage(ErrorKind.VAR_MUT_EXPORT, name.getText());
			this.generateError(name, message);
		}

		if (!mutable && ctx.variableInit() == null) {
			ErrorMessage message = new ErrorMessage(ErrorKind.CONST_NOT_SET, name.getText());
			this.generateError(name, message);
		}
	}

	protected VariableSymbol defineVariable(HogwashParser.TypeDeclContext ctx,
			Token nameToken, boolean mutable, boolean exportable) {
        String name = nameToken.getText();

		String typeStr = PrimitiveTypeSymbol.DEFAULT_TYPE.name().toLowerCase();
		if (ctx != null) typeStr = ctx.T_TYPE().getText();
		PrimitiveTypeSymbol type = (PrimitiveTypeSymbol) this.currentScope.resolve(typeStr);

		VariableSymbol var = new VariableSymbol(name, type, mutable, exportable);
		if (this.currentScope instanceof FunctionSymbol) {
			var = new ArgumentSymbol(name, type);
		}
		var.setToken(nameToken);
		try {
			this.currentScope.define(var);
		} catch (SymbolAlreadyExistsException e) {
			this.alreadyExistsError(nameToken, var);
			return null;
		}
		return var;
	}

	protected void alreadyExistsError(Token token, Symbol s) {
		String name = token.getText();
		ErrorMessage message = null;
		ErrorLevel level = ErrorLevel.ERROR;

		Symbol originalSym = this.currentScope.resolve(name);
		Token originalDef = originalSym.getToken();
		String originalDefFile = "";
		int originalDefLine;
		int originalDefChar;
		if (originalDef == null) {
			originalDefFile = "<builtin>";
			originalDefLine = 0;
			originalDefChar = 0;
		} else {
			NamedInputStream nis = (NamedInputStream) originalDef.getInputStream();
			originalDefFile = nis.getName();
			originalDefLine = originalDef.getLine();
			originalDefChar = originalDef.getCharPositionInLine() + 1;
		}

		if (s instanceof FunctionSymbol) {
			if (originalSym instanceof FunctionSymbol) {
				message = new ErrorMessage(ErrorKind.FUNC_ALREADY_DEF, name,
						originalDefFile, originalDefLine, originalDefChar);
			} else if (originalSym instanceof VariableSymbol) {
				message = new ErrorMessage(ErrorKind.FUNC_DEF_VAR, name,
						originalDefFile, originalDefLine, originalDefChar);
			}
		} else if (s instanceof VariableSymbol) {
			if (originalSym instanceof VariableSymbol) {
				message = new ErrorMessage(ErrorKind.VAR_ALREADY_DEF, name,
						originalDefFile, originalDefLine, originalDefChar);
				level = ErrorLevel.WARNING;
			} else if (originalSym instanceof FunctionSymbol) {
				message = new ErrorMessage(ErrorKind.VAR_DEF_FUNC, name,
						originalDefFile, originalDefLine, originalDefChar);
				level = ErrorLevel.ERROR;
			}
		}

		if (this.currentScope instanceof FunctionSymbol) {
			message = new ErrorMessage(ErrorKind.PARAM_NAME_TAKEN,
					name, this.currentScope.getScopeName());
			level = ErrorLevel.ERROR;
		}

		if (message != null) this.generateError(token, message, level);
	}

}
