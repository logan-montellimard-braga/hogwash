package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;

public class VariableSymbol extends Symbol {
	protected boolean exportable;
	protected boolean mutable;

	protected boolean set;

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

	public boolean isSet() {
		return this.set;
	}

	public void setIsSet(boolean isSet) {
		this.set = isSet;
	}

	public boolean isExportable() {
		return this.exportable;
	}

	public boolean isMutable() {
		return this.mutable;
	}

	public String toString() {
		return (this.exportable ? "ext " : "") + (this.mutable ? "mut " : "") + this.name + ": " + this.type.getName();
	}
}
