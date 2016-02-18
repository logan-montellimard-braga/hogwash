package fr.loganbraga.hogwash.Language.Parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import java.io.InputStream;
import java.io.IOException;

public class NamedInputStream extends ANTLRInputStream {

	protected String name;

	public NamedInputStream(InputStream is, String name) throws IOException {
		super(is);
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
