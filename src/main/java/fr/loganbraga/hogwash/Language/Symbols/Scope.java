package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;

public interface Scope {
	public String getScopeName();

	public Scope getEnclosingScope();

	public void define(Symbol sym);

	public Symbol resolve(String name);
}
