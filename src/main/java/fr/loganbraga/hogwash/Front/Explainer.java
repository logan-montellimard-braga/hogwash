package fr.loganbraga.hogwash.Front;

import fr.loganbraga.hogwash.Error.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Explainer {

	ErrorReporter er;

	public Explainer(ErrorReporter er) {
		this.er = er;
	}

	public String explain(String code) {
		if (!this.codeIsValid(code)) {
			ErrorMessage em = new ErrorMessage(ErrorKind.EXPLAIN_BAD_CODE, code);
			this.er.addError(new BaseError(em));
			return null;
		}
		String rPath = "/errors/details/" + code.toUpperCase() + ".md";
		String explanation = this.getErrorFile(rPath);
		if (explanation == null) {
			ErrorMessage em = new ErrorMessage(ErrorKind.EXPLAIN_NOT_FOUND, code);
			this.er.addError(new BaseError(em));
			return null;
		}
		return explanation;
	}

	protected String getErrorFile(String rPath) {
		InputStream is = Explainer.class.getResourceAsStream(rPath);
		if (is == null) return null;

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String input;
		StringBuilder sb = new StringBuilder();
		try {
			while ((input = br.readLine()) != null) sb.append(input + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	protected boolean codeIsValid(String code) {
		Pattern pattern = Pattern.compile("[eE]\\d{3}");
		Matcher matcher = pattern.matcher(code);
		return matcher.matches();
	}

}
