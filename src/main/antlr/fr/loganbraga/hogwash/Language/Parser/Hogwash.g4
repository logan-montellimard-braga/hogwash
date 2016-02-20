grammar Hogwash;

@header {
    package fr.loganbraga.hogwash.Language.Parser;
}

compilationUnit
    : /*moduleDecl?*/ importDecl* sourceElements? EOF;

/* moduleDecl */
/*     : MODULE moduleName SEMI */
/*     ; */

importDecl
    : IMPORT string SEMI
    ;

/* moduleName */
/*     : ModuleName */
/*     ; */

sourceElements
    : sourceElement+
    ;

sourceElement
    : statement
    | functionDecl
    ;

statements
    : statement+
    ;

statement
    : block
    | variableStatement SEMI
    | ifStatement
    | loopingStatement
    | continueStatement SEMI
    | breakStatement SEMI
    | returnStatement SEMI
    | switchStatement
    | emptyStatement
    | expression SEMI
    ;

loopingStatement
    : loopStatement
    | whileStatement
    | doWhileStatement SEMI
    | forStatement
    | forInStatement
    ;

block
    : LBRACE statement* RBRACE
    ;

functionDecl
    : PUB? FUNC name LPAREN formalParameterList? RPAREN
        functionReturnType? block
    ;

formalParameterList
    : formalParameter (COMMA formalParameter)*
    ;

formalParameter
    : name formalParameterType? formalParameterDefaultValue?
    ;

formalParameterType
    : COLON typeDecl
    ;

functionReturnType
    : LARROW funcTypeDecl
    ;

formalParameterDefaultValue
    : EQUAL expression
    ;

functionBody
    : statements?
    ;

expression
    : expression LBRACK expression RBRACK               # IndexExpression
    | ExtIdentifier arguments                           # ExtFuncCallExpression
    | Identifier arguments                              # FuncCallExpression
    | expression op=(INC | DEC)                         # PostOpExpression
    | DELETE expression                                 # DeleteExpression
    | op=(INC | DEC | ADD | SUB) expression             # PreOpExpression
    | BANG expression                                   # NotExpression
    | <assoc=right>
        expression POW expression                       # PowExpression
    | expression REGEX_MATCH expression                 # RegexMatchExpression
    | expression op=(MUL | DIV | MOD) expression        # MultExpression
    | expression op=(ADD | SUB) expression              # AddExpression
    | expression op=(LT | GT | LTE | GTE) expression    # CompExpression
    | expression op=(D_EQUAL | NOT_EQUAL) expression    # EqExpression
    | expression AND expression                         # AndExpression
    | expression OR expression                          # OrExpression
    | expression QUESTION expression COLON expression   # TernaryIfExpression
    | <assoc=right>
        lhs assignmentOperator expression               # AssignExpression
    | ExtIdentifier                                     # ExtIdentifierExpression
    | name                                              # IdentifierExpression
    | literal                                           # LiteralExpression
    | LPAREN expression RPAREN                          # ParenExpression
    ;

parExpression
    : LPAREN expression RPAREN
    ;

expressions
    : expression (COMMA expression)*
    ;

arguments
    : LPAREN argumentsList? RPAREN
    ;

argumentsList
    : expression (COMMA expression)*
    ;

ifStatement
    : IF parExpression statement elseStatement?
    ;

elseStatement
    : ELSE statement
    ;

loopStatement
    : LOOP statement
    ;

whileStatement
    : WHILE parExpression statement
    ;

doWhileStatement
    : DO statement WHILE parExpression
    ;

forStatement
    : FOR LPAREN variableStatement? SEMI expressions? SEMI expressions? RPAREN
        statement
    ;

forInStatement
    : FOR LPAREN forVariableDecl IN forInSource RPAREN statement
    ;

forInSource
    : range
    | name
    | string
    ;

range
    : Range
    ;

returnStatement
    : RETURN expression?
    ;

breakStatement
    : BREAK
    ;

continueStatement
    : CONTINUE
    ;

switchStatement
    : SWITCH parExpression caseBlock
    ;

emptyStatement
    : SEMI
    ;

caseBlock
    : LBRACE caseClauses? (defaultClause caseClauses?)? RBRACE
    ;

caseClauses
    : caseClause+
    ;

caseClause
    : CASE expression COLON statement*
    ;

defaultClause
    : DEFAULT COLON statement*
    ;

variableStatement
    : LET variableDeclList
    ;

variableDeclList
    : variableDecl (COMMA variableDecl)*
    ;

variableDecl
    : PUB? MUT? name (COLON typeDecl)? (EQUAL variableInit)?
    ;

forVariableDecl
    : LET variableDecl
    ;

variableInit
    : expression
    ;

lhs
    : name
    ;

assignmentOperator
    : EQUAL
    | MUL_ASSIGN
    | DIV_ASSIGN
    | MOD_ASSIGN
    | ADD_ASSIGN
    | SUB_ASSIGN
    ;

typeDecl
    : T_TYPE typeDeclSuffix?
    ;

typeDeclSuffix
    : LBRACK RBRACK
    | LBRACE RBRACE
    ;

funcTypeDecl
    : funcType=(T_TYPE | T_VOID)
    ;


literal
    : integer
    | floating
    | codeInsert
    | regex
    | rawString
    | string
    | array
    | hashMap
    | bool
    ;


integer
    : DecIntegerLit
    | HexIntegerLit
    | OctIntegerLit
    | BinIntegerLit
    ;

floating
    : FloatLit
    ;

codeInsert
    : CodeInsertLit
    ;

rawString
    : RawString
    ;

string
    : StringLit
    ;

array
    : LBRACK integer (COMMA integer)* RBRACK
    ;

hashMap
    : LBRACE hashMapValues? RBRACE
    ;

hashMapValues
    : hashMapValue (COMMA hashMapValue)* COMMA?
    ;

hashMapValue
    : string EQUAL expression
    ;

bool
    : TRUE
    | FALSE
    ;

regex
    : RegexLit
    ;

name
    : Identifier
    ;


///
/// LEXER ///
///

// Keywords
LET            : 'let';
MUT            : 'mut';
PUB            : 'pub';
IF             : 'if';
ELSE           : 'else';
FOR            : 'for';
IN             : 'in';
DO             : 'do';
WHILE          : 'while';
LOOP           : 'loop';
BREAK          : 'break';
CONTINUE       : 'continue';
RETURN         : 'return';
IMPORT         : 'import';
AS             : 'as';
MODULE         : 'module';
SWITCH         : 'switch';
CASE           : 'case';
DEFAULT        : 'default';
FUNC           : 'fn';
DELETE         : 'delete';
TRUE           : 'true';
FALSE          : 'false';

// Type Keywords
T_TYPE         : 'any' | 'number' | 'string' | 'regex';
T_VOID         : 'void';


// IDENTIFIERS
ExtIdentifier
    : AT Identifier
    ;

Identifier
    : [a-zA-Z_] [a-zA-Z0-9_]*
    ;

/* ModuleName */
/*     : [a-zA-Z] [a-zA-Z_\.]* */
/*     ; */


// STRINGS
CodeInsertLit
    : TILDE StringLit TILDE
    ;

RawString
    : [rR] StringLit
    ;

RegexLit
    : POUND StringLit
    ;

StringLit
    : '\'' (ESC | ~[\\\r\n'])* '\''
    | '"' (ESC | ~[\\\r\n"])* '"'
    ;


// NUMBERS
Range
    : Digits D_DOT Digits (D_DOT Sign? Digits)?
    ;


FloatLit
    : Digits '.' Digits? Exponent?
    | '.' Digits Exponent?
    ;

DecIntegerLit
    : Digits Exponent?
    ;

HexIntegerLit
    : HEX_PREFIX HexDigits HexExponent?
    ;

OctIntegerLit
    : OCT_PREFIX OctDigits
    ;

BinIntegerLit
    : BIN_PREFIX BinDigits
    ;


fragment
Digits
    : Digit ((Digit | UNDERSCORE)* Digit)?
    ;

fragment
Digit
    : [0-9]
    ;

fragment
HexDigits
    : HexDigit ((HexDigit | UNDERSCORE)* HexDigit)?
    ;

fragment
HexDigit
    : [0-9a-fA-F]
    ;

fragment
OctDigits
    : OctDigit ((OctDigit | UNDERSCORE)* OctDigit)?
    ;

fragment
OctDigit
    : [0-7]
    ;

fragment
BinDigits
    : BinDigit ((BinDigit | UNDERSCORE)* BinDigit)?
    ;

fragment
BinDigit
    : [01]
    ;

fragment
HEX_PREFIX
    : '0' [xX]
    ;

fragment
OCT_PREFIX
    : '0' [oO]
    ;

fragment
BIN_PREFIX
    : '0' [bB]
    ;

fragment
UNDERSCORE
    : '_'
    ;

fragment
Exponent
    : [eE] Sign? Digits
    ;

fragment
HexExponent
    : [pP] Sign? Digits
    ;

fragment
Sign
    : [+-]
    ;


// Separators
LPAREN         : '(';
RPAREN         : ')';
LBRACE         : '{';
RBRACE         : '}';
LBRACK         : '[';
RBRACK         : ']';
COMMA          : ',';
SEMI           : ';';

// Operators
EQUAL          : '=';
ADD_ASSIGN     : '+=';
SUB_ASSIGN     : '-=';
MUL_ASSIGN     : '*=';
DIV_ASSIGN     : '/=';
MOD_ASSIGN     : '%=';
REGEX_MATCH    : '=~';
GT             : '>';
LT             : '<';
BANG           : '!';
QUESTION       : '?';
LARROW         : '->';
COLON          : ':';
D_DOT          : '..';
POUND          : '#';
TILDE          : '~';
AT             : '@';
D_EQUAL        : '==';
LTE            : '<=';
GTE            : '>=';
NOT_EQUAL      : '!=';
AND            : '&&';
OR             : '||';
POW            : '**';
INC            : '++';
DEC            : '--';
ADD            : '+';
SUB            : '-';
MUL            : '*';
DIV            : '/';
MOD            : '%';


// GENERAL
ESC
    : '\\'.
    ;

SKIPS
    : (COMMENTS | WHITESPACE) -> skip
    ;

COMMENTS
    : COMMENT
    | LINE_COMMENT
    ;

COMMENT
    : '/*' .*? '*/'
    ;

LINE_COMMENT
    : '//' ~[\r\n]*
    ;

WHITESPACE
    : [ \t\r\n]+
    ;

ErrorChar
    : .
    ;
