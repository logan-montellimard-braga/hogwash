package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;
import java.util.Collection;
import java.util.ArrayList;

public class FunctionSymbol extends Symbol implements Scope {
	protected SymbolDictionary arguments;
	protected Scope enclosingScope;
	protected FunctionVisibility visibility;

	public FunctionSymbol(String name, Type returnType, Scope enclosingScope) {
		this(name, returnType, enclosingScope, FunctionVisibility.PRIVATE);
	}

	public FunctionSymbol(String name, Type returnType, Scope enclosingScope, FunctionVisibility visibility) {
		super(name, returnType);
		this.arguments = new SymbolDictionary();
		this.enclosingScope = enclosingScope;
		this.visibility = visibility;
	}

	public Symbol resolve(String name) {
		Symbol sym = this.arguments.getEntry(name);
		if (sym != null)
			return sym;

		if (this.enclosingScope != null)
			return this.enclosingScope.resolve(name);

		return null;
	}

	public void define(Symbol symbol) throws SymbolAlreadyExistsException {
		this.arguments.addEntry(symbol);
		symbol.setScope(this);
	}

	public Scope getEnclosingScope() {
		return this.enclosingScope;
	}

	public String getScopeName() {
		return this.name;
	}

	public FunctionVisibility getVisibility() {
		return this.visibility;
	}

	public Collection<Symbol> getAllSymbols() {
		return new ArrayList<Symbol>();
	}

	public String toString() {
		return this.getScopeName() + ": " + this.arguments.toString() + " -> " + this.type;
	}
}
