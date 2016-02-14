package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;

public class GlobalScope extends BaseScope {
	public GlobalScope() {
		super(null);
	}

	public String getScopeName() {
		return "global";
	}
}
