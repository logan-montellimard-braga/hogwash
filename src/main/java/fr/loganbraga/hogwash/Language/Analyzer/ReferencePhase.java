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
	public void enterForStatement(HogwashParser.ForStatementContext ctx) {
		this.currentScope = this.st.getScope(ctx);
	}

	@Override
	public void exitForStatement(HogwashParser.ForStatementContext ctx) {
		this.currentScope = this.currentScope.getEnclosingScope();
	}

	@Override
	public void enterForInStatement(HogwashParser.ForInStatementContext ctx) {
		this.currentScope = this.st.getScope(ctx);
	}

	@Override
	public void exitForInStatement(HogwashParser.ForInStatementContext ctx) {
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
	public void exitBreakStatement(HogwashParser.BreakStatementContext ctx) {
		if (!this.isInLoop()) {
			Token symbol = ctx.BREAK().getSymbol();
			ErrorMessage em = new ErrorMessage(ErrorKind.LOOP_ST_NO_LOOP, symbol.getText());
			this.generateError(symbol, em);
		}
	}

	@Override
	public void exitContinueStatement(HogwashParser.ContinueStatementContext ctx) {
		if (!this.isInLoop()) {
			Token symbol = ctx.CONTINUE().getSymbol();
			ErrorMessage em = new ErrorMessage(ErrorKind.LOOP_ST_NO_LOOP, symbol.getText());
			this.generateError(symbol, em);
		}
	}

	@Override
	public void exitForInSource(HogwashParser.ForInSourceContext ctx) {
		if (ctx.name() != null) {
			Token tk = ctx.name().Identifier().getSymbol();
			Symbol var = this.checkVariableReference(tk);
			if (var != null) var.setIsUsed(true);
		}
	}

	@Override
	public void exitIdentifierExpression(HogwashParser.IdentifierExpressionContext ctx) {
		Token tk = ctx.name().Identifier().getSymbol();
		Symbol var = this.checkVariableReference(tk);
		if (var != null) var.setIsUsed(true);
	}

	@Override
	public void exitAssignExpression(HogwashParser.AssignExpressionContext ctx) {
		Token tk = ctx.lhs().name().Identifier().getSymbol();
		Symbol var = this.checkVariableReference(tk);
		if (var instanceof VariableSymbol) {
			VariableSymbol variable = (VariableSymbol) var;
			variable.setIsSet(true);
			boolean mutable = variable.isMutable();
			if (!mutable) {
				ErrorMessage em = new ErrorMessage(ErrorKind.CONST_SET, tk.getText());
				this.generateError(tk, em);
			}
		}
	}

	@Override
	public void exitPostOpExpression(HogwashParser.PostOpExpressionContext ctx) {
		Token tk = ctx.lhs().name().Identifier().getSymbol();
		Symbol var = this.checkVariableReference(tk);
		if (var instanceof VariableSymbol) {
			VariableSymbol variable = (VariableSymbol) var;
			boolean mutable = variable.isMutable();
			if (!mutable) {
				ErrorMessage em = new ErrorMessage(ErrorKind.CONST_SET, tk.getText());
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
			return;
		} else if (func instanceof VariableSymbol) {
			ErrorMessage em = new ErrorMessage(ErrorKind.VAR_CALLED, name);
			this.generateError(tk, em);
			return;
		} else {
			func.setIsUsed(true);
			FunctionSymbol f = (FunctionSymbol) func;
			if (f.isBuiltin()) return;
			int argsSent = 0;
			if (ctx.arguments().argumentsList() != null) {
				argsSent = ctx.arguments().argumentsList().getChildCount();
			}
			if (argsSent > 1) { argsSent -= argsSent / 2; }
			int maxArity = f.getMaxArity();
			int minArity = f.getMinArity();
			if (argsSent > maxArity || argsSent < minArity) {
				ErrorMessage em = null;
				if (maxArity == minArity) {
					em = new ErrorMessage(
							ErrorKind.FUNC_WRONG_ARITY_F, name, argsSent, maxArity);
				} else {
					em = new ErrorMessage(
							ErrorKind.FUNC_WRONG_ARITY, name, argsSent, minArity, maxArity);
				}
				this.generateError(tk, em);
			}
		}
	}

	@Override
	public void exitExtFuncCallExpression(HogwashParser.ExtFuncCallExpressionContext ctx) {
		Token tk = ctx.ExtIdentifier().getSymbol();
		String name = tk.getText().substring(1);
		Symbol func = this.currentScope.resolve(name);
		if (func instanceof FunctionSymbol) {
			ErrorMessage em = new ErrorMessage(ErrorKind.EXT_FUNC_DEF, name);
			this.generateError(tk, em);
		}
	}

	@Override
	public void exitExtIdentifierExpression(HogwashParser.ExtIdentifierExpressionContext ctx) {
		Token tk = ctx.ExtIdentifier().getSymbol();
		String name = tk.getText().substring(1);
		Symbol var = this.currentScope.resolve(name);
		if (var instanceof VariableSymbol) {
			ErrorMessage em = new ErrorMessage(ErrorKind.EXT_VAR_DEF, name);
			this.generateError(tk, em);
		}
	}

	protected Symbol checkVariableReference(Token tk) {
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
			if (v.fromSameModule(tk) &&
					referencePosition < v.getToken().getTokenIndex()) {
				ErrorMessage em = new ErrorMessage(ErrorKind.VAR_FORWARD_REF, name);
				this.generateError(tk, em);
			}
		}
		return var;
	}

}
