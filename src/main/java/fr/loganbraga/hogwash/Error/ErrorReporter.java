package fr.loganbraga.hogwash.Error;

import fr.loganbraga.hogwash.Error.BaseError;
import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;
import java.util.Iterator;

public class ErrorReporter {

	protected List<BaseError> errors;
	protected PrintStream out;

	public ErrorReporter(PrintStream out) {
		this.out = out;
		this.errors = new ArrayList<BaseError>();
	}

	public ErrorReporter() {
		this(System.err);
	}

	public void addError(BaseError error) {
		this.errors.add(error);
	}

	public boolean hasErrors() {
		return !this.errors.isEmpty();
	}

	public void report() {
		Iterator<BaseError> it = this.errors.iterator();
		while (it.hasNext()) {
			this.out.println(it.next());
			if (it.hasNext()) this.out.println();
		}
	}
}
