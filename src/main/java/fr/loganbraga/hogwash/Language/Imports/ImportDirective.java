package fr.loganbraga.hogwash.Language.Imports;

import java.io.File;
import org.antlr.v4.runtime.Token;

public class ImportDirective {

	protected static final String DEFAULT_EXTENSION = "hogwash";
	protected File path;
	protected Token token;
	
	public ImportDirective(String path) {
		this.path = this.generatePath(path);
		this.token = null;
	}

	protected File generatePath(String path) {
		File file = new File(path);
		String name = file.getName();

		int dot = name.lastIndexOf('.');
		String ext = dot == -1 ? null : name.substring(dot + 1);
		if (ext == null || !ext.equals(DEFAULT_EXTENSION))
			file = new File(path + "." + DEFAULT_EXTENSION);

		return file;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public Token getToken() {
		return this.token;
	}

	public File getPath() {
		return this.path;
	}

	public String toString() {
		return this.path.toString();
	}

}
