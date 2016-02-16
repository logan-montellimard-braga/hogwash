package fr.loganbraga.hogwash.Language.Parser;

import fr.loganbraga.hogwash.Error.*;
import org.antlr.v4.runtime.*;

public class FormatterErrorListener extends BaseErrorListener {

	protected ErrorReporter er;

	public FormatterErrorListener(ErrorReporter er) {
		this.er = er;
	}

	public void syntaxError(Recognizer recognizer, Object symbol,
			int line, int charPos, String msg, RecognitionException e) {

		CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
		String input = tokens.getTokenSource().getInputStream().toString();
		Token token = (Token) symbol;

		BaseError error = new LineCharError(msg, this.er.getInputName(), input,
				line, charPos, token.getStartIndex(), token.getStopIndex());

		this.er.addError(error);
	}
}
