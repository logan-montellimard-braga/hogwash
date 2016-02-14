package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;

public abstract class BaseScope implements Scope {
	protected Scope enclosingScope;
	protected SymbolDictionary symbols;

	public BaseScope(Scope enclosingScope) {
		this.symbols = new SymbolDictionary();
		this.enclosingScope = enclosingScope;
	}

	public Symbol resolve(String name) {
		Symbol sym = this.symbols.getEntry(name);
		if (sym != null)
			return sym;

		if (this.enclosingScope != null)
			return this.enclosingScope.resolve(name);

		return null;
	}

	public void define(Symbol symbol) {
		try {
			this.symbols.addEntry(symbol);
		} catch (SymbolAlreadyExistsException e) {

		}
		symbol.setScope(this);
	}

	public Scope getEnclosingScope() {
		return this.enclosingScope;
	}
}
