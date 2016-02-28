package fr.loganbraga.hogwash.Error;

public enum ErrorKind {
	BASE_ERROR(null),
	EXCEPTIONAL_ERROR(null),
	EXPLAIN_BAD_CODE(null),
	EXPLAIN_NOT_FOUND(null),
	BUILTINS_BOOTSTRAP("E001"),
	VAR_NOT_FOUND("E100"),
	VAR_FORWARD_REF("E101"),
	VAR_ALREADY_DEF("E102"),
	VAR_CALLED("E103"),
	VAR_NEVER_USED("E104"),
	VAR_NEVER_SET("E105"),
	VAR_MUT_EXPORT("E106"),
	CONST_NOT_SET("E107"),
	CONST_SET("E108"),
	EXT_VAR_DEF("E109"),
	VAR_DEF_FUNC("E110"),
	FUNC_NOT_FOUND("E200"),
	FUNC_AS_VAR("E201"),
	FUNC_ALREADY_DEF("E202"),
	PARAM_NAME_TAKEN("E203"),
	EXT_FUNC_DEF("E204"),
	FUNC_NEVER_CALLED("E205"),
	FUNC_DEF_VAR("E206"),
	FUNC_WRONG_ARITY("E207"),
	FUNC_WRONG_ARITY_F("E207"),
	OPT_ARG_NOT_LAST("E208"),
	MODULE_NOT_FOUND("E300"),
	REDUNDANT_IMPORT("E301"),
	LOOP_ST_NO_LOOP("E900");

	private final String errorCode;

	private ErrorKind(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return this.errorCode;
	}
}
