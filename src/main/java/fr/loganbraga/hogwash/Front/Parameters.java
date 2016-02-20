package fr.loganbraga.hogwash.Front;

import fr.loganbraga.hogwash.Error.*;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.util.List;
import java.util.ArrayList;

public class Parameters {

	private JCommander parser;
	private String programName;
	private String version;
	private ErrorReporter er;

	@Parameter(description = "<file>")
	public List<String> files;

	@Parameter(names = "--quick-fail", description = "Exit after first error")
	public boolean quickFail;

	@Parameter(names = "--no-warnings", description = "Disable warnings")
	public boolean noWarnings;

	@Parameter(names = "--strict", description = "Treat all warnings as errors")
	public boolean strict;

	@Parameter(names = "--explain", description = "Show detailed message about given error identifier")
	public String explain;

	@Parameter(names = "--help", description = "Show this help and exit", help = true)
	public boolean help;


	public Parameters(String programName, String version, ErrorReporter er) {
		this.files = new ArrayList<String>();
		this.quickFail = false;
		this.noWarnings = false;
		this.strict = false;
		this.help = false;
		this.explain = null;

		this.programName = programName;
		this.version = version;
		this.er = er;

		this.parser = new JCommander(this);
		this.parser.setProgramName(this.programName.toLowerCase());
	}

	public void parse(String[] args) {
		try {
			this.parser.parse(args);
		} catch (ParameterException e) {
			BaseError error = new BaseError(new ErrorMessage(
						ErrorKind.BASE_ERROR, e.getMessage()));
			this.er.addError(error);
		}
	}

	public String printHelp() {
		return this.printHelp(this.programName + " v" + this.version);
	}

	public String printHelp(String preMessage) {
		StringBuilder sb = new StringBuilder();
		sb.append(preMessage);
		sb.append("\n\n");
		this.parser.usage(sb);
		return sb.toString();
	}

}
