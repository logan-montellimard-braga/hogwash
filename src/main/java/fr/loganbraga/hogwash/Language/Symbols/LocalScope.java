package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;

public class LocalScope extends BaseScope {
	public LocalScope(Scope enclosingScope) {
		super(enclosingScope);
	}

	public String getScopeName() {
		return "local";
	}
}
