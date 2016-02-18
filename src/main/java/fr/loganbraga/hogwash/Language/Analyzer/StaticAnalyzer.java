package fr.loganbraga.hogwash.Language.Analyzer;

import fr.loganbraga.hogwash.Language.Analyzer.SymbolTable;
import fr.loganbraga.hogwash.Language.Imports.ModuleImporter;
import fr.loganbraga.hogwash.Error.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StaticAnalyzer {

	protected String shell;
	protected ParseTree tree;
	protected ErrorReporter er;
	
	public StaticAnalyzer(String shell, ParseTree tree, ErrorReporter er) {
		this.shell = shell;
		this.tree = tree;
		this.er = er;
	}

	public void analyze(SymbolTable st, File currentFile) {
		if (st == null) st = new SymbolTable(this.populateBuiltins());
		ModuleImporter mi = new ModuleImporter(st, currentFile, this.er);
		DefinePhase def = new DefinePhase(st, this.er);
		ReferencePhase ref = new ReferencePhase(st, this.er);
		DeadCodeFinder dcf = new DeadCodeFinder(st, this.er);

		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(mi, this.tree);
		walker.walk(def, this.tree);
		walker.walk(ref, this.tree);
		dcf.process();
	}

	protected List<String> populateBuiltins() {
		List<String> names = new ArrayList<String>();

		String fileName = "/bootstrap/" + this.shell + "_builtins.txt";
		InputStream is = StaticAnalyzer.class.getResourceAsStream(fileName);
		if (is == null) {
			BaseError error = new BaseError(new ErrorMessage(
						ErrorKind.BUILTINS_BOOTSTRAP, "bash"));
			this.er.addError(error);
			return names;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String name;
		try {
			while ((name = br.readLine()) != null) names.add(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return names;
	}

}
