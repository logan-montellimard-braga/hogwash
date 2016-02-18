package fr.loganbraga.hogwash.Language;

import fr.loganbraga.hogwash.Error.*;
import fr.loganbraga.hogwash.Language.Parser.Engine;
import fr.loganbraga.hogwash.Language.Analyzer.StaticAnalyzer;
import fr.loganbraga.hogwash.Language.Analyzer.SymbolTable;
import java.io.*;
import java.util.Observer;
import java.util.Observable;

public class Compiler extends Observable {

	protected static final String DEFAULT_TARGET = "bash";
	protected SymbolTable st;
	protected File file;
	protected ErrorReporter er;

	public enum Message {
		END_INIT,
		END_PARSING,
		END_ANALYSIS
	}

	public Compiler(File file, ErrorReporter er, SymbolTable st) {
		this(file, er);
		this.st = st;
	}
	
	public Compiler(File file, ErrorReporter er) {
		this.file = file;
		this.er = er;
		this.st = null;
	}

	public void compile() {
		FileInputStream is;
		Engine parser = new Engine(this.file, this.er);
		this.setChanged();
		this.notifyObservers(Message.END_INIT);

		parser.parse();
		this.setChanged();
		this.notifyObservers(Message.END_PARSING);

		StaticAnalyzer analyzer = new StaticAnalyzer(
				DEFAULT_TARGET, parser.getTree(), this.er);
		analyzer.analyze(this.st, this.file);
		this.setChanged();
		this.notifyObservers(Message.END_ANALYSIS);
	}

	public ErrorReporter getErrorReporter() {
		return this.er;
	}

}
