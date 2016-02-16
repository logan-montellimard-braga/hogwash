package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;
import org.antlr.v4.runtime.Token;

public abstract class Symbol {
	protected String name;
	protected Type type;
	protected Scope scope;
	protected Token token;
	protected boolean used;

	public Symbol(String name) {
		this.name = name;
		this.used = false;
		this.token = null;
	}

	public Symbol(String name, Type type) {
		this(name);
		this.type = type;
		this.used = false;
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

	public Token getToken() {
		return this.token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public boolean isUsed() {
		return this.used;
	}

	public void setIsUsed(boolean isUsed) {
		this.used = isUsed;
	}

	public String toString() {
		return this.name;
	}
}
