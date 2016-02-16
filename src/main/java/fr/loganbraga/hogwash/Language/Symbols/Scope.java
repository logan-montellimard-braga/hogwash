package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;
import java.util.Collection;

public interface Scope {
	public String getScopeName();

	public Scope getEnclosingScope();

	public void define(Symbol sym)
		throws SymbolAlreadyExistsException;

	public Symbol resolve(String name);

	public Collection<Symbol> getAllSymbols();
}
