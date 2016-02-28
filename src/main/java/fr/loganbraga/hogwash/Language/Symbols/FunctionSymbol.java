package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;
import java.util.Collection;
import java.util.ArrayList;

public class FunctionSymbol extends Symbol implements Scope {
	protected SymbolDictionary arguments;
	protected Scope enclosingScope;
	protected FunctionVisibility visibility;
	protected boolean builtin;

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

	public void setIsBuiltin(boolean builtin) {
		this.builtin = builtin;
	}

	public boolean isBuiltin() {
		return this.builtin;
	}

	public int getMinArity() {
		int arity = 0;
		for (Symbol sym : this.arguments.getAllSymbols()) {
			ArgumentSymbol arg = (ArgumentSymbol) sym;
			if (!arg.hasDefault()) arity++;
		}
		return arity;
	}

	public int getMaxArity() {
		return this.arguments.size();
	}

	public FunctionVisibility getVisibility() {
		return this.visibility;
	}

	public Collection<Symbol> getAllSymbols() {
		// return new ArrayList<Symbol>();
		return this.arguments.getAllSymbols();
	}

	public String toString() {
		return this.getScopeName() + ": " + this.arguments.toString() + " -> " + this.type;
	}
}
