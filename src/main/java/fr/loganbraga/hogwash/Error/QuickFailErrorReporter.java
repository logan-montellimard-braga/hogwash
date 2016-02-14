package fr.loganbraga.hogwash.Error;

import fr.loganbraga.hogwash.Error.BaseError;
import java.io.PrintStream;

public class QuickFailErrorReporter extends ErrorReporter {

	protected int maxErrors;

	public QuickFailErrorReporter(PrintStream out, int maxErrors) {
		super(out);
		this.maxErrors = maxErrors;
	}

	public QuickFailErrorReporter(int maxErrors) {
		super();
		this.maxErrors = maxErrors;
	}

	public QuickFailErrorReporter() {
		this(1);
	}

	public QuickFailErrorReporter(PrintStream out) {
		this(out, 1);
	}

	@Override
	public void addError(BaseError error) {
		super.addError(error);
		if (this.errors.size() >= this.maxErrors - 1) {
			super.report();
			System.exit(1);
		}
	}
}
