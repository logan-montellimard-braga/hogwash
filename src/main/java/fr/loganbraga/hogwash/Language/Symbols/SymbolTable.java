package fr.loganbraga.hogwash.Language.Symbols;

import fr.loganbraga.hogwash.Language.Symbols.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.ParserRuleContext;
import java.io.*;
import java.util.*;

public class SymbolTable {

	protected GlobalScope globalScope;
	protected Map<ParseTree, Scope> scopes;

	public SymbolTable(List<String> builtins) {
		this.globalScope = new GlobalScope();
		this.scopes = new IdentityHashMap<ParseTree, Scope>();

		this.initTypeSystem();
		this.bootstrapEnvironment(builtins);
	}

	protected void initTypeSystem() {
		for (PrimitiveType pType : PrimitiveType.values())
			try {
				this.globalScope.define(new PrimitiveTypeSymbol(pType.name().toLowerCase()));
			} catch (SymbolAlreadyExistsException e) {
				// This should never happen
				e.printStackTrace();
			}
	}

	protected void bootstrapEnvironment(List<String> builtins) {
		Iterator<String> it = builtins.iterator();
		try {
			while (it.hasNext()) {
				String noType = PrimitiveTypeSymbol.DEFAULT_TYPE.name().toLowerCase();
				PrimitiveTypeSymbol t = (PrimitiveTypeSymbol) this.globalScope.resolve(noType);
				FunctionSymbol f = new FunctionSymbol(it.next(), t, this.globalScope);
				f.setIsUsed(true);
				this.globalScope.define(f);
			}
		} catch (SymbolAlreadyExistsException e) {
			// This should never happen
			e.printStackTrace();
		}
	}

	public GlobalScope getGlobalScope() {
		return this.globalScope;
	}

	public void addScope(ParserRuleContext ctx, Scope scope) {
		this.scopes.put(ctx, scope);
	}

	public Scope getScope(ParserRuleContext ctx) {
		return this.scopes.get(ctx);
	}

	public List<Symbol> getAllSymbols() {
		List<Symbol> symbols = new ArrayList<Symbol>();

		Iterator<Scope> it = this.scopes.values().iterator();
		while (it.hasNext()) {
			Collection<Symbol> syms = it.next().getAllSymbols();
			symbols.addAll(syms);
		}

		symbols.addAll(this.globalScope.getAllSymbols());

		return symbols;
	}

}
