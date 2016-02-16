package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;

public class PrimitiveTypeSymbol extends Symbol implements Type {

	public static final PrimitiveType DEFAULT_TYPE = PrimitiveType.ANY;

	public PrimitiveTypeSymbol(String name) {
		super(name);
	}

}
