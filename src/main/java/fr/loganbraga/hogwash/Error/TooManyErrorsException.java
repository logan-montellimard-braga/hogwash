package fr.loganbraga.hogwash.Error;

public class TooManyErrorsException extends RuntimeException {

	ErrorReporter er;
	
	public TooManyErrorsException(ErrorReporter er) {
		super("Too many errors");
		this.er = er;
	}

	public ErrorReporter getErrorReporter() {
		return this.er;
	}

}
