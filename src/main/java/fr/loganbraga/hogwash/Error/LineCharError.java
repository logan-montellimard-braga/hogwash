package fr.loganbraga.hogwash.Error;

import fr.loganbraga.hogwash.Error.BaseError;
import static org.fusesource.jansi.AnsiRenderer.*;

public class LineCharError extends BaseError {
	protected String input;
	protected String inputName;
	protected int line;
	protected int charPos;
	protected int errorCharStartPos;
	protected int errorCharStopPos;

	public LineCharError(String message, String inputName, String input, int line, int charPos) {
		this(message, inputName, input, line, charPos, -1, -1);
	}

	public LineCharError(String message, String inputName, String input,
			int line, int charPos, int errorCharStartPos, int errorCharStopPos) {
		super(message);
		this.inputName = inputName;
		this.input = input;
		this.line = line;
		this.charPos = charPos;
		this.errorCharStartPos = errorCharStartPos;
		this.errorCharStopPos = errorCharStopPos;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.inputName + ":" + this.line + ":" + (this.charPos + 1) + " ");
		sb.append(super.toString());
		sb.append("\n");

		String pre = this.inputName + ":" + this.line + " ";
		sb.append(pre);
		sb.append(this.underlineError(pre.length()));

		return sb.toString();
	}

	protected String underlineError(int preLength) {
		StringBuilder sb = new StringBuilder();

		String errorLine = this.input.split("\n")[this.line - 1];

		int offset = errorLine.length() - errorLine.replace("\t", "").length();
		errorLine = errorLine.replace("\t", "  ");
		String trimedLine = errorLine.replaceFirst("^\\s+", "");
		offset -= errorLine.length() - trimedLine.length();

		sb.append(trimedLine);
		sb.append("\n");

		for (int i = 0; i < this.charPos + preLength + offset; i++) sb.append(" ");

		if (this.errorCharStartPos >= 0 && this.errorCharStopPos >= 0)
			for (int i = this.errorCharStartPos; i <= this.errorCharStopPos; i++)
				sb.append(render("@|" + this.getColorByLevel(this.level) + " " +
							(i == this.errorCharStartPos ? "^" : "~") + "|@"));

		return sb.toString();
	}
}
