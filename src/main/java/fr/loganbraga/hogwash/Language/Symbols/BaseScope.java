package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;
import java.util.Collection;

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

	public void define(Symbol symbol) throws SymbolAlreadyExistsException {
		this.symbols.addEntry(symbol);
		symbol.setScope(this);
	}

	public Scope getEnclosingScope() {
		return this.enclosingScope;
	}

	public Collection<Symbol> getAllSymbols() {
		return this.symbols.getAllSymbols();
	}

	public String toString() {
		return this.getScopeName() + ": " + this.symbols.toString();
	}
}
