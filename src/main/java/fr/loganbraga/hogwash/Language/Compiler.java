package fr.loganbraga.hogwash.Language;

import fr.loganbraga.hogwash.Error.*;
import fr.loganbraga.hogwash.Language.Parser.Engine;
import fr.loganbraga.hogwash.Language.Analyzer.StaticAnalyzer;
import fr.loganbraga.hogwash.Language.Symbols.SymbolTable;
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
		this.er.setInputName(this.file.getPath());

		FileInputStream is;
		Engine parser = null;
		try {
			is = new FileInputStream(this.file);
			parser = new Engine(is, er);
		} catch (IOException e) {
			BaseError error = new BaseError(
					new ErrorMessage(ErrorKind.BASE_ERROR, e.getMessage()));
			this.er.addError(error);
			this.setChanged();
			this.notifyObservers(Message.END_INIT);
			return;
		}

		parser.parse();
		this.setChanged();
		this.notifyObservers(Message.END_PARSING);

		StaticAnalyzer analyzer = new StaticAnalyzer(
				DEFAULT_TARGET, parser.getTree(), this.er);
		analyzer.analyze(this.st);
		this.setChanged();
		this.notifyObservers(Message.END_ANALYSIS);
	}

	public ErrorReporter getErrorReporter() {
		return this.er;
	}

}
