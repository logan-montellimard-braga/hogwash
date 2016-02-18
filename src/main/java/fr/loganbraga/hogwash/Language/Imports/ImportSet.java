package fr.loganbraga.hogwash.Language.Imports;

import java.util.*;
import java.io.File;
import java.io.IOException;

public class ImportSet {

	protected Set<File> imports;

	public ImportSet() {
		this.imports = new HashSet<File>();
	}

	public void addImport(File file) throws ModuleAlreadyImportedException {
		try {
			if (this.imports.contains(file.getCanonicalFile()))
				throw new ModuleAlreadyImportedException(file);

			this.imports.add(file.getCanonicalFile());
		} catch (IOException e) { e.printStackTrace(); }
	}

}
