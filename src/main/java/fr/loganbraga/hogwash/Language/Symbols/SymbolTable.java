package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;

public class SymbolTable {
	protected GlobalScope globalScope = new GlobalScope();

	public SymbolTable() {
		this.initTypeSystem();
	}

	protected void initTypeSystem() {
		for (PrimitiveType pType : PrimitiveType.values())
			this.globalScope.define(new PrimitiveTypeSymbol(pType.name().toLowerCase()));
	}
}
