package fr.loganbraga.hogwash.Error;

import fr.loganbraga.hogwash.Error.*;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

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

	public String reportErrors(Comparator<BaseError> sorter) {
		return this.report(this.errors, sorter);
	}

	public String reportWarnings(Comparator<BaseError> sorter) {
		return this.report(this.warnings, sorter);
	}

	protected String report(List<BaseError> coll, Comparator<BaseError> sorter) {
		Set<String> errorCodes = new HashSet<String>();
		if (sorter != null) Collections.sort(coll, sorter);
		StringBuilder sb = new StringBuilder();
		Iterator<BaseError> it = coll.iterator();
		while (it.hasNext()) {
			BaseError err = it.next();
			if (err.hasErrorCode())
				errorCodes.add(err.getErrorCode());
			err.setErrorKeys(this.errorKeys);
			sb.append(err + "\n");
			if (it.hasNext()) sb.append("\n");
		}

		if (coll.size() > 0) {
			sb.append("\n");
			sb.append("Reported ");
			sb.append(coll.size());
			sb.append(" " + (coll.size() == 1 ? "notice" : "notices") + ".");
		}

		if (errorCodes.size() > 0) {
			sb.append("\n");
			String message;
			if (errorCodes.size() == 1)
				message = "Use `hogwash --explain " + errorCodes.iterator().next() + "` to see a detailed explanation";
			else {
				message = "Use `hogwash --explain CODE` with one of the given error codes to see a detailed explanation";
				String codes = errorCodes.toString();
				codes = codes.substring(1, codes.length() - 1);
				message += "\nRelevant codes: " + codes;
			}

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

	public static class CodeErrorSorter implements Comparator<BaseError> {
		public int compare(BaseError e1, BaseError e2) {
			return e1.getErrorCode().compareTo(e2.getErrorCode());
		}

		public String toString() {
			return "code";
		}
	}

	public static class LineErrorSorter implements Comparator<BaseError> {
		public int compare(BaseError e1, BaseError e2) {
			String e1Name = e1.getInputName();
			String e2Name = e2.getInputName();
			if (!e1Name.equals(e2Name))
				return e1Name.compareTo(e2Name);

			if (e1 instanceof LineCharError && e2 instanceof LineCharError) {
				LineCharError lce1 = (LineCharError) e1;
				LineCharError lce2 = (LineCharError) e2;
				int e1Line = lce1.getLine();
				int e2Line = lce2.getLine();
				int cmp;
				if (e1Line == e2Line) {
					int e1Char = lce1.getCharPos();
					int e2Char = lce2.getCharPos();
					cmp = e1Char > e2Char ? 1 : 0;
					if (cmp == 0) cmp = e1Char == e2Char ? 0 : -1;
				} else {
					cmp = e1Line > e2Line ? 1 : -1;
				}
				return cmp;
			}
			return 0;
		}

		public String toString() {
			return "line";
		}
	}

}
