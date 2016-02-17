package fr.loganbraga.hogwash.Error;

import fr.loganbraga.hogwash.Error.ErrorLevel;
import java.util.ResourceBundle;
import static org.fusesource.jansi.AnsiRenderer.*;

public class BaseError {
	protected static final String ERROR_COLOR = "red";
	protected static final String WARNING_COLOR = "yellow";
	protected ErrorMessage message;
	protected ErrorLevel level;
	protected ResourceBundle errorKeys;

	public BaseError(ErrorMessage message) {
		this(message, ErrorLevel.ERROR);
	}

	public BaseError(ErrorMessage message, ErrorLevel level) {
		this.message = message;
		this.level = level;
	}

	public ErrorMessage getMessage() {
		return this.message;
	}

	public void setMessage(ErrorMessage message) {
		this.message = message;
	}

	public ErrorLevel getLevel() {
		return this.level;
	}

	public void setLevel(ErrorLevel level) {
		this.level = level;
	}

	public void setErrorKeys(ResourceBundle errorKeys) {
		this.errorKeys = errorKeys;
	}

	protected String getColorByLevel(ErrorLevel level) {
		String color;
		switch(level) {
			case WARNING:
				color = WARNING_COLOR;
				break;
			default:
				color = ERROR_COLOR;
				break;
		}
		return color;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("@|" + this.getColorByLevel(this.level) + " ");
		sb.append(this.level.name().toLowerCase() + ":|@ ");
		sb.append("@|bold " + this.message.render(this.errorKeys) + "|@");
		return render(sb.toString());
	}
}
