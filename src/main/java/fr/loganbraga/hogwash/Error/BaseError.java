package fr.loganbraga.hogwash.Error;

import fr.loganbraga.hogwash.Error.ErrorLevel;
import static org.fusesource.jansi.AnsiRenderer.*;

public class BaseError {
	protected static final String ERROR_COLOR = "red";
	protected String message;
	protected ErrorLevel level;

	public BaseError() {
		this("Unknown error.", ErrorLevel.ERROR);
	}

	public BaseError(String message) {
		this(message, ErrorLevel.ERROR);
	}

	public BaseError(String message, ErrorLevel level) {
		this.message = message;
		this.level = level;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ErrorLevel getLevel() {
		return this.level;
	}

	public void setLevel(ErrorLevel level) {
		this.level = level;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("@|" + ERROR_COLOR + " ");
		sb.append(this.level.name().toLowerCase() + ":|@ ");
		sb.append("@|bold " + this.message + "|@");
		return render(sb.toString());
	}
}
