package fr.loganbraga.hogwash;

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

	@Parameter(description = "<files>")
	public List<String> files;

	@Parameter(names = "--quick-fail", description = "If set, exit after first error.")
	public boolean quickFail;

	@Parameter(names = "--help", description = "Show this help and exit.", help = true)
	public boolean help;


	public Parameters(String programName, String version, ErrorReporter er) {
		this.files = new ArrayList<String>();
		this.quickFail = false;
		this.help = false;

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
			BaseError error = new BaseError(e.getMessage());
			this.er.addError(error);
		}
	}

	public void printHelp() {
		this.printHelp(this.programName + " v" + this.version);
	}

	public void printHelp(String preMessage) {
		System.out.println(preMessage);
		System.out.println();
		this.parser.usage();
	}

}
