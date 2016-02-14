package fr.loganbraga.hogwash;

import fr.loganbraga.hogwash.Parameters;
import fr.loganbraga.hogwash.Error.*;
import fr.loganbraga.hogwash.Language.Parser.Engine;
import java.io.*;
import org.fusesource.jansi.AnsiConsole;

public class Hogwash {
	private static final String VERSION = "0.1.0";

	public static void main(String[] args) throws Exception {
		AnsiConsole.systemInstall();

		QuickFailErrorReporter preER = new QuickFailErrorReporter(System.err, 1);

		Parameters parameters = new Parameters("Hogwash", VERSION, preER);
		parameters.parse(args);
		if (parameters.help || args.length == 0) {
			parameters.printHelp();
			System.exit(0);
		}

		String inputName = "<stdin>";
		InputStream is = System.in;
		if (!parameters.files.isEmpty()) {
			try {
				inputName = parameters.files.get(0);
				is = new FileInputStream(inputName);
			} catch (FileNotFoundException e) {
				BaseError error = new BaseError(e.getMessage());
				preER.addError(error);
			}
		}

		ErrorReporter er;
		if (parameters.quickFail) er = new QuickFailErrorReporter(System.err, 1);
		else er = new ErrorReporter(System.err);

		Engine parser = new Engine(is, inputName, er);
		parser.parse();
		if (er.hasErrors()) {
			er.report();
			System.exit(1);
		}

		AnsiConsole.systemUninstall();
	}
}
