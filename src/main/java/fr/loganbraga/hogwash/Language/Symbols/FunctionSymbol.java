package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;

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

	public void define(Symbol symbol) {
		try {
			this.arguments.addEntry(symbol);
		} catch (SymbolAlreadyExistsException e) {

		}
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
}
