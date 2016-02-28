package fr.loganbraga.hogwash.Language.Analyzer;

import fr.loganbraga.hogwash.Language.Symbols.*;
import fr.loganbraga.hogwash.Language.Imports.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.ParserRuleContext;
import java.util.*;
import java.io.File;

public class SymbolTable {

	protected GlobalScope globalScope;
	protected Map<ParseTree, Scope> scopes;
	protected ImportSet importSet;

	public SymbolTable(List<String> builtins) {
		this.globalScope = new GlobalScope();
		this.scopes = new IdentityHashMap<ParseTree, Scope>();
		this.importSet = new ImportSet();

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
				FunctionSymbol f = new FunctionSymbol(
						it.next(), t, this.globalScope, FunctionVisibility.PUBLIC);
				f.setIsBuiltin(true);
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

	public void addImport(File imp) throws ModuleAlreadyImportedException {
		this.importSet.addImport(imp);
	}

	public void reset(boolean resetGlobalScope) {
		this.scopes.clear();
		if (resetGlobalScope) this.globalScope = new GlobalScope();
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
