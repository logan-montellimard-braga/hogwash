package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;

public class ArgumentSymbol extends VariableSymbol {

	protected boolean hasDefault;

	public ArgumentSymbol(String name, Type type) {
		super(name, type, true, false);
	}

	public void setHasDefault(boolean hasDefault) {
		this.hasDefault = hasDefault;
	}

	public boolean hasDefault() {
		return this.hasDefault;
	}

}
