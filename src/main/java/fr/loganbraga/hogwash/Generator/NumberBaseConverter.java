package fr.loganbraga.hogwash.Generator;

public class NumberBaseConverter {

	protected enum Base { 
		DEC(10), HEX(16), OCT(8), BIN(2);

		private int base;

		private Base(int base) { this.base = base; }
		public int getBase() { return this.base; }
	}

	protected static final String PREFIX_DEC = "";
	protected static final String PREFIX_HEX = "0x";
	protected static final String PREFIX_OCT = "0o";
	protected static final String PREFIX_BIN = "0b";
	
	public double toDecimalLong(String value) {
		value = value.replace("_", "");

		Base base = this.extractBase(value);
		String splitter = "[eE]";
		if (base == Base.HEX) splitter = "[pP]";

		String[] expParts = value.split(splitter);
		String integer = expParts[0];
		String exp = expParts.length > 1 ? expParts[1] : null;

		if (base != Base.DEC) { integer = integer.substring(2); }
		double longVal = (double) Long.parseLong(integer, base.getBase());
		double originalLong = longVal;

		if (exp != null) {
			int exponent = Integer.parseInt(exp, Base.DEC.getBase());
			longVal = longVal * Math.pow(base.getBase(), exponent);
		}

		return longVal;
	}

	public double toDecimalDouble(String value) {
		return 0.0;
	}

	protected Base extractBase(String value) {
		if (value.startsWith(PREFIX_HEX)) return Base.HEX;
		if (value.startsWith(PREFIX_OCT)) return Base.OCT;
		if (value.startsWith(PREFIX_BIN)) return Base.BIN;

		return Base.DEC;
	}

}
