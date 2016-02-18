package fr.loganbraga.hogwash.Error;

import fr.loganbraga.hogwash.Error.*;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class ErrorReporter {

	protected String inputName;
	protected int maxErrors;
	protected ResourceBundle errorKeys;

	protected List<BaseError> errors;
	protected List<BaseError> warnings;

	public ErrorReporter(String inputName, int maxErrors, ResourceBundle errorKeys) {
		this.inputName = inputName;
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
		StringBuilder sb = new StringBuilder();
		Iterator<BaseError> it = coll.iterator();
		while (it.hasNext()) {
			BaseError err = it.next();
			err.setErrorKeys(this.errorKeys);
			sb.append(err + "\n");
			if (it.hasNext()) sb.append("\n");
		}

		return sb.toString();
	}

	public String getInputName() {
		return this.inputName;
	}

	public void setMaxErrors(int maxErrors) {
		this.maxErrors = maxErrors;
	}

	public int getMaxErrors() {
		return this.maxErrors;
	}

	public void setInputName(String inputName) {
		this.inputName = inputName;
	}

}
