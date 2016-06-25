package fr.loganbraga.hogwash.Generator;

import fr.loganbraga.hogwash.Language.Parser.*;
import fr.loganbraga.hogwash.Error.*;
import org.stringtemplate.v4.*;
import org.antlr.v4.runtime.tree.ParseTree;
import java.util.List;
import java.util.ArrayList;

public class BashGenerator extends HogwashBaseVisitor<ST> implements Generator {

	protected ParseTree tree;
	protected List<ST> functions;
	protected List<ST> loneStatements;
	protected STGroup templates;
	protected ErrorReporter er;

	public BashGenerator(ParseTree tree, ErrorReporter er) {
		this.tree = tree;
		this.functions = new ArrayList<ST>();
		this.loneStatements = new ArrayList<ST>();
		this.templates = new STGroupFile("generators/bash.stg");
		this.er = er;
	}

	@Override
	public ST visitCompilationUnit(HogwashParser.CompilationUnitContext ctx) {
		super.visitCompilationUnit(ctx);
		return null;
	}

	@Override
	public ST visitFunctionDecl(HogwashParser.FunctionDeclContext ctx) {
		return null;
	}

	public String generate() {
		this.visit(this.tree);
		return null;
	}

}
