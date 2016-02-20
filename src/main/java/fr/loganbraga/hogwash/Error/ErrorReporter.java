package fr.loganbraga.hogwash.Error;

import fr.loganbraga.hogwash.Error.*;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class ErrorReporter {

	protected int maxErrors;
	protected ResourceBundle errorKeys;

	protected List<BaseError> errors;
	protected List<BaseError> warnings;

	public ErrorReporter(int maxErrors, ResourceBundle errorKeys) {
		this.maxErrors = maxErrors;
		this.errorKeys = errorKeys;
		this.errors = new ArrayList<BaseError>();
		this.warnings = new ArrayList<BaseError>();
	}

	public void setWarningsToErrorsConversion() {
		this.warnings = new ArrayList<BaseError>() {
			@Override
			public boolean add(BaseError e) {
				e.setLevel(ErrorLevel.ERROR);
				ErrorReporter.this.addError(e);
				return true;
			}
		};
	}

	public void setNoWarnings() {
		this.warnings = new ArrayList<BaseError>() {
			@Override
			public boolean add(BaseError e) { return false; }
		};
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
		boolean shouldExplain = false;
		StringBuilder sb = new StringBuilder();
		Iterator<BaseError> it = coll.iterator();
		while (it.hasNext()) {
			BaseError err = it.next();
			if (err.hasErrorCode())
				shouldExplain = true;
			err.setErrorKeys(this.errorKeys);
			sb.append(err + "\n");
			if (it.hasNext()) sb.append("\n");
		}

		if (shouldExplain) {
			sb.append("\n");
			String message;
			if (coll.size() == 1)
				message = "Use `hogwash --explain " + coll.get(0).getMessage().getErrorKind().getErrorCode() + "`";
			else
				message = "Use `hogwash --explain CODE` with one of the given error code";

			message = message + " to see a detailed explanation.";
			sb.append(message);
			sb.append("\n");
		}

		return sb.toString();
	}


	public void setMaxErrors(int maxErrors) {
		this.maxErrors = maxErrors;
	}

	public int getMaxErrors() {
		return this.maxErrors;
	}

}
