package fr.loganbraga.hogwash.Language.Imports;

import fr.loganbraga.hogwash.Language.Compiler;
import fr.loganbraga.hogwash.Language.Imports.ImportDirective;
import fr.loganbraga.hogwash.Language.Analyzer.SymbolTable;
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
    protected File currentFile;

	public ModuleImporter(SymbolTable st, File currentFile, ErrorReporter er) {
		this.st = st;
		this.currentFile = currentFile;
		this.er = er;
		this.imports = new ArrayList<ImportDirective>();
		try {
			this.st.addImport(currentFile);
		} catch (ModuleAlreadyImportedException e) {}
	}

	@Override
	public void exitImportDecl(HogwashParser.ImportDeclContext ctx) {
		Token tk = ctx.IMPORT().getSymbol();
		String path = ctx.string().StringLit().getText().substring(1);
		path = path.substring(0, path.length() - 1);
		try {
			ImportDirective id = new ImportDirective(path, this.currentFile);
			id.setToken(tk);
			this.imports.add(id);
		} catch (ModuleNotFoundException e) {
			ErrorMessage message = new ErrorMessage(ErrorKind.MODULE_NOT_FOUND, path);
			int line = tk.getLine();
			int charPosStart = tk.getCharPositionInLine();
			int charPosStop = charPosStart + tk.getText().length() - 1;
			String input = tk.getInputStream().toString();
			String inputName = ((NamedInputStream) tk.getInputStream()).getName();
			BaseError error = new LineCharError(message, inputName,
					input, line, charPosStart, charPosStart, charPosStop);
			this.er.addError(error);
		}
	}

	@Override
	public void exitCompilationUnit(HogwashParser.CompilationUnitContext ctx) {
		if (!this.imports.isEmpty()) this.importModules();
	}

	protected void importModules() {
		int maxErrors = this.er.getMaxErrors();
		this.er.setMaxErrors(1);

		for (ImportDirective id : this.imports) {
			try {
				this.st.addImport(id.getPath());
			} catch (ModuleAlreadyImportedException e) {
				ErrorMessage message = new ErrorMessage(
						ErrorKind.REDUNDANT_IMPORT, id.getPath());
				Token tk = id.getToken();
				int line = tk.getLine();
				int charPosStart = tk.getCharPositionInLine();
				int charPosStop = charPosStart + tk.getText().length() - 1;
				String input = tk.getInputStream().toString();
				String inputName = ((NamedInputStream) tk.getInputStream()).getName();
				BaseError error = new LineCharError(message, inputName,
						input, line, charPosStart, charPosStart, charPosStop);
				error.setLevel(ErrorLevel.WARNING);
				this.er.addError(error);
				continue;
			}

			Compiler compiler = new Compiler(id.getPath(), this.er, this.st);
			compiler.compile();

			this.cleanSymbolTable();
		}
		this.er.setMaxErrors(maxErrors);
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
