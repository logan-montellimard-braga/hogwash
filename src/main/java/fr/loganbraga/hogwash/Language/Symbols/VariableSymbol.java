package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;

public class VariableSymbol extends Symbol {
	protected boolean exportable;
	protected boolean mutable;

	public VariableSymbol(String name, Type type) {
		this(name, type, false, false);
	}

	public VariableSymbol(String name, Type type, boolean mutable) {
		this(name, type, mutable, false);
	}

	public VariableSymbol(String name, Type type, boolean mutable, boolean exportable) {
		super(name, type);
		this.mutable = mutable;
		this.exportable = exportable;
	}

	public boolean isExportable() {
		return this.exportable;
	}

	public boolean isMutable() {
		return this.mutable;
	}
}
