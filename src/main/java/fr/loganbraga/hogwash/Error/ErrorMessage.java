package fr.loganbraga.hogwash.Error;

import fr.loganbraga.hogwash.Error.*;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.text.MessageFormat;

public class ErrorMessage {
	
	protected ErrorKind kind;
	protected Object[] args;

	public ErrorMessage(ErrorKind kind, Object... args) {
		this.kind = kind;
		this.args = args;
	}

	public String render(ResourceBundle rb) {
		String message = null;
		try {
			message = rb.getString(this.kind.name());
		} catch (MissingResourceException e) {
			message = "error message not found for " + this.kind.name();
		}
		message = MessageFormat.format(message, this.args);
		return message;
	}

}
