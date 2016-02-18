package fr.loganbraga.hogwash.Language.Imports;

import java.io.File;

public class ModuleAlreadyImportedException extends Exception {
	public ModuleAlreadyImportedException(File file) {
		super("Module '" + file.getPath() + "' was already imported.");
	}
}
