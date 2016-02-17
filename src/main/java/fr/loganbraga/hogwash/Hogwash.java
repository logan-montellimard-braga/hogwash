package fr.loganbraga.hogwash;

import fr.loganbraga.hogwash.Parameters;
import fr.loganbraga.hogwash.Error.*;
import fr.loganbraga.hogwash.Language.Parser.Engine;
import fr.loganbraga.hogwash.Language.Analyzer.StaticAnalyzer;
import java.io.*;
import java.util.ResourceBundle;
import org.fusesource.jansi.AnsiConsole;

public class Hogwash {
	protected static final String VERSION = "0.1.0";
	protected static ResourceBundle ERROR_KEYS;

	public void run(String[] args) {
		ErrorReporter preER = new ErrorReporter("<stdin>", 1, ERROR_KEYS);

		Parameters parameters = new Parameters("Hogwash", VERSION, preER);
		parameters.parse(args);
		if (parameters.help || args.length == 0) {
			System.out.println(parameters.printHelp());
			System.exit(0);
		}

		String inputName = "<stdin>";
		InputStream is = System.in;
		if (!parameters.files.isEmpty()) {
			try {
				inputName = parameters.files.get(0);
				is = new FileInputStream(inputName);
			} catch (FileNotFoundException e) {
				BaseError error = new BaseError(new ErrorMessage(ErrorKind.BASE_ERROR, e.getMessage()));
				preER.addError(error);
			}
		}

		this.process(parameters, inputName, is);
	}

	protected void process(Parameters parameters, String inputName, InputStream is) {
		ErrorReporter er;
		if (parameters.quickFail)
			er = new ErrorReporter(inputName, 1, ERROR_KEYS);
		else er = new ErrorReporter(inputName, 50, ERROR_KEYS);

		Engine parser = null;
		try {
			parser = new Engine(is, er);
		} catch (IOException e) {
			BaseError error = new BaseError(new ErrorMessage(ErrorKind.BASE_ERROR, e.getMessage()));
			er.addError(error);
			this.handleErrors(er);
		}
		parser.parse();
		this.handleErrors(er);

		StaticAnalyzer analyzer = new StaticAnalyzer("bash", parser.getTree(), er);
		analyzer.analyze();
		this.handleErrors(er);
	}

	protected void handleErrors(ErrorReporter er) {
		if (er.hasErrors()) {
			System.err.println(er.reportErrors());
			System.exit(1);
		}
		if (er.hasWarnings()) System.err.println(er.reportWarnings());
	}

	public static void main(String[] args) {
		Hogwash.ERROR_KEYS = ResourceBundle.getBundle("errors.ErrorMessages");

		AnsiConsole.systemInstall();

		Hogwash hogwash = new Hogwash();
		try {
			hogwash.run(args);
		} catch (TooManyErrorsException e) { 
			hogwash.handleErrors(e.getErrorReporter());
		}

		AnsiConsole.systemUninstall();
	}
}
