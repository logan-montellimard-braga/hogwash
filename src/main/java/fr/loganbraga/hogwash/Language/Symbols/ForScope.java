package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.LocalScope;

public class ForScope extends LocalScope {
	 
	public ForScope(Scope enclosingScope) {
		super(enclosingScope);
	}

	public String getScopeName() {
		return "for loop scope";
	}

}
