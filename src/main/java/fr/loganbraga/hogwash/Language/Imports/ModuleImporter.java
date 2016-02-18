package fr.loganbraga.hogwash.Language.Imports;

import fr.loganbraga.hogwash.Language.Compiler;
import fr.loganbraga.hogwash.Language.Imports.ImportDirective;
import fr.loganbraga.hogwash.Language.Symbols.*;
import fr.loganbraga.hogwash.Language.Parser.*;
import fr.loganbraga.hogwash.Error.*;
import org.antlr.v4.runtime.Token;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class ModuleImporter extends HogwashBaseListener {
	
	protected List<ImportDirective> imports;
    protected ErrorReporter er;
    protected SymbolTable st;

	public ModuleImporter(SymbolTable st, ErrorReporter er) {
		this.st = st;
		this.er = er;
		this.imports = new ArrayList<ImportDirective>();
		this.st.addImport(new File(this.er.getInputName()));
	}

	@Override
	public void exitImportDecl(HogwashParser.ImportDeclContext ctx) {
		Token tk = ctx.IMPORT().getSymbol();
		String path = ctx.string().StringLit().getText().substring(1);
		path = path.substring(0, path.length() - 1);
		ImportDirective id = new ImportDirective(path);
		id.setToken(tk);
		this.imports.add(id);
	}

	@Override
	public void exitCompilationUnit(HogwashParser.CompilationUnitContext ctx) {
		if (!this.imports.isEmpty()) this.importModules();
	}

	protected void importModules() {
		String inputName = this.er.getInputName();
		int maxErrors = this.er.getMaxErrors();
		this.er.setMaxErrors(1);

		for (ImportDirective id : this.imports) {
			if (this.st.alreadyImported(id.getPath())) {
				ErrorMessage message = new ErrorMessage(
						ErrorKind.REDUNDANT_IMPORT, id.getPath());
				Token tk = id.getToken();
				int line = tk.getLine();
				int charPosStart = tk.getCharPositionInLine();
				int charPosStop = charPosStart + tk.getText().length() - 1;
				String input = tk.getInputStream().toString();
				BaseError error = new LineCharError(message, this.er.getInputName(),
						input, line, charPosStart, charPosStart, charPosStop);
				error.setLevel(ErrorLevel.WARNING);
				this.er.addError(error);
				continue;
			}
			this.st.addImport(id.getPath());

			Compiler compiler = new Compiler(id.getPath(), this.er, this.st);
			compiler.compile();

			this.cleanSymbolTable();
		}
		this.er.setMaxErrors(maxErrors);
		this.er.setInputName(inputName);
	}

	protected void cleanSymbolTable() {
		this.st.reset(false);
		GlobalScope scope = this.st.getGlobalScope();
		ArrayList<Symbol> toDelete = new ArrayList<Symbol>();
		for (Symbol symbol : scope.getAllSymbols()) {
			if (symbol instanceof FunctionSymbol) {
				FunctionSymbol func = (FunctionSymbol) symbol;
				if (func.getVisibility() == FunctionVisibility.PRIVATE)
					toDelete.add(symbol);
			} else toDelete.add(symbol);
		}

		for (Symbol symbol : toDelete) scope.undefine(symbol);
	}

}
