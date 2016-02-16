package fr.loganbraga.hogwash.Error;

import fr.loganbraga.hogwash.Error.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class ErrorReporter {

	protected static final int DEFAULT_MAX_ERRORS = 50;
	protected String inputName;
	protected int maxErrors;

	protected List<BaseError> errors;
	protected List<BaseError> warnings;

	public ErrorReporter(String inputName) {
		this(inputName, DEFAULT_MAX_ERRORS);
	}

	public ErrorReporter(String inputName, int maxErrors) {
		this.inputName = inputName;
		this.maxErrors = maxErrors;
		this.errors = new ArrayList<BaseError>();
		this.warnings = new ArrayList<BaseError>();
	}

	public void addError(BaseError error) {
		ErrorLevel level = error.getLevel();
		switch (level) {
			case ERROR:
				this.errors.add(error);
				if (this.errors.size() >= this.maxErrors - 1)
					throw new TooManyErrorsException(this);
				break;
			case WARNING:
				this.warnings.add(error);
				break;
		}
	}

	public boolean hasErrors() {
		return !this.errors.isEmpty();
	}

	public boolean hasWarnings() {
		return !this.warnings.isEmpty();
	}

	public String reportErrors() {
		return this.report(this.errors);
	}

	public String reportWarnings() {
		return this.report(this.warnings);
	}

	protected String report(List<BaseError> coll) {
		StringBuilder sb = new StringBuilder();
		Iterator<BaseError> it = coll.iterator();
		while (it.hasNext()) {
			sb.append(it.next() + "\n");
			if (it.hasNext()) sb.append("\n");
		}

		return sb.toString();
	}

	public String getInputName() {
		return this.inputName;
	}
}
