package fr.loganbraga.hogwash.Language.Analyzer;

import fr.loganbraga.hogwash.Language.Parser.*;
import fr.loganbraga.hogwash.Language.Symbols.*;
import fr.loganbraga.hogwash.Error.*;
import org.antlr.v4.runtime.tree.*;

public class TypeChecker extends SinglePassPhase {

	protected ParseTreeProperty<Type> types;

	public TypeChecker(SymbolTable st, ErrorReporter er) {
		super(st, er);
		this.types = new ParseTreeProperty<Type>();
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
	public void enterInteger(HogwashParser.IntegerContext ctx) {
		String type = PrimitiveType.NUMBER.name().toLowerCase();
		this.registerTypeNode(ctx, type);
	}
	
	@Override
	public void enterFloating(HogwashParser.FloatingContext ctx) {
		String type = PrimitiveType.NUMBER.name().toLowerCase();
		this.registerTypeNode(ctx, type);
	}

	@Override
	public void enterCodeInsert(HogwashParser.CodeInsertContext ctx) {
		String type = PrimitiveType.ANY.name().toLowerCase();
		this.registerTypeNode(ctx, type);
	}

	@Override
	public void enterRegex(HogwashParser.RegexContext ctx) {
		String type = PrimitiveType.REGEX.name().toLowerCase();
		this.registerTypeNode(ctx, type);
	}

	@Override
	public void enterRawString(HogwashParser.RawStringContext ctx) {
		String type = PrimitiveType.STRING.name().toLowerCase();
		this.registerTypeNode(ctx, type);
	}

	@Override
	public void enterString(HogwashParser.StringContext ctx) {
		String type = PrimitiveType.STRING.name().toLowerCase();
		this.registerTypeNode(ctx, type);
	}

	@Override
	public void exitVariableDecl(HogwashParser.VariableDeclContext ctx) {
		String name = ctx.name().Identifier().getText();
	}

	protected void registerTypeNode(ParseTree tree, String type) {
		Symbol typeS = this.currentScope.resolve(type);
		typeS = this.currentScope.resolve(type);
		if (!(typeS instanceof Type)) {
			throw new RuntimeException(type);
		}
		this.types.put(tree, (Type) typeS);
	}

}
