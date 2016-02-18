package fr.loganbraga.hogwash.Language.Imports;

import fr.loganbraga.hogwash.Language.Imports.ImportDirective;

public class ModuleNotFoundException extends Exception {
	public ModuleNotFoundException(ImportDirective id) {
		super("Module '" + id.getPath() + "' was not found.");
	}
}
