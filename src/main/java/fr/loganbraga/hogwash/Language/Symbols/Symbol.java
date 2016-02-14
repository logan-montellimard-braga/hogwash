package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;

public abstract class Symbol {
	protected String name;
	protected Type type;
	protected Scope scope;

	public Symbol(String name) {
		this.name = name;
	}

	public Symbol(String name, Type type) {
		this(name);
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public Type getType() {
		return this.type;
	}

	public Scope getScope() {
		return this.scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
}
