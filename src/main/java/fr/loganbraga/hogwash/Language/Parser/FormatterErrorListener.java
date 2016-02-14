package fr.loganbraga.hogwash.Language.Parser;

import fr.loganbraga.hogwash.Error.*;
import org.antlr.v4.runtime.*;

public class FormatterErrorListener extends BaseErrorListener {

	protected String inputName;
	protected ErrorReporter er;

	public FormatterErrorListener(String inputName, ErrorReporter er) {
		this.inputName = inputName;
		this.er = er;
	}

	public void syntaxError(Recognizer recognizer, Object symbol,
			int line, int charPos, String msg, RecognitionException e) {

		CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
		String input = tokens.getTokenSource().getInputStream().toString();
		Token token = (Token) symbol;

		BaseError error = new LineCharError(msg, this.inputName, input,
				line, charPos, token.getStartIndex(), token.getStopIndex());

		this.er.addError(error);
	}
}
