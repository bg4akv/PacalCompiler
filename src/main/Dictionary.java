package main;

import java.util.HashMap;
import java.util.Map;

import main.Token.TokenType;
import main.Token.TokenValue;


public class Dictionary {
	public class Tuple {
		public TokenType tokenType;
		public TokenValue tokenValue;
		public int precedence;

		public Tuple(TokenValue tokenValue, TokenType tokenType, int precedence)
		{
			this.tokenType = tokenType;
			this.tokenValue = tokenValue;
			this.precedence = precedence;
		}
	}

	private final Map<String, Tuple> dictMap = new HashMap<String, Tuple>();


	public Dictionary()
	{
		addToken(":=",        new Tuple(TokenValue.ASSIGN,           TokenType.OPERATORS,  0));
		addToken("=",         new Tuple(TokenValue.EQUAL,            TokenType.OPERATORS,  1));
		addToken("<>",        new Tuple(TokenValue.NOT_EQUAL,        TokenType.OPERATORS,  2));
		addToken(">=",        new Tuple(TokenValue.GREATER_OR_EQUAL, TokenType.OPERATORS,  2));
		addToken(">",         new Tuple(TokenValue.GREATER_THAN,     TokenType.OPERATORS,  2));
		addToken("<=",        new Tuple(TokenValue.LESS_OR_EQUAL,    TokenType.OPERATORS,  2));
		addToken("<",         new Tuple(TokenValue.LESS_THAN,        TokenType.OPERATORS,  2));
		addToken("+",         new Tuple(TokenValue.PLUS,             TokenType.OPERATORS, 10));
		addToken("-",         new Tuple(TokenValue.MINUS,            TokenType.OPERATORS, 10));
		addToken("*",         new Tuple(TokenValue.MULTIPLY,         TokenType.OPERATORS, 20));
		addToken("/",         new Tuple(TokenValue.DIVIDE,           TokenType.OPERATORS, 20));
		addToken(":",         new Tuple(TokenValue.COLON,            TokenType.DELIMITER, -1));
		addToken(",",         new Tuple(TokenValue.COMMA,            TokenType.DELIMITER, -1));
		addToken("..",        new Tuple(TokenValue.DOT_DOT,          TokenType.DELIMITER, -1));
		addToken("(",         new Tuple(TokenValue.LEFT_PAREN,       TokenType.DELIMITER, -1));
		addToken("[",         new Tuple(TokenValue.LEFT_SQUARE,      TokenType.DELIMITER, -1));
		addToken(".",         new Tuple(TokenValue.PERIOD,           TokenType.DELIMITER, -1));
		addToken(")",         new Tuple(TokenValue.RIGHT_PAREN,      TokenType.DELIMITER, -1));
		addToken("]",         new Tuple(TokenValue.RIGHT_SQUARE,     TokenType.DELIMITER, -1));
		addToken(";",         new Tuple(TokenValue.SEMICOLON,        TokenType.DELIMITER, -1));
		addToken("^",         new Tuple(TokenValue.UPARROW,          TokenType.DELIMITER, -1));
		addToken("and",       new Tuple(TokenValue.AND,              TokenType.KEYWORDS,  -1));
		addToken("array",     new Tuple(TokenValue.ARRAY,            TokenType.KEYWORDS,  -1));
		addToken("begin",     new Tuple(TokenValue.BEGIN,            TokenType.KEYWORDS,  -1));
		addToken("case",      new Tuple(TokenValue.CASE,             TokenType.KEYWORDS,  -1));
		addToken("const",     new Tuple(TokenValue.CONST,            TokenType.KEYWORDS,  -1));
		addToken("do",        new Tuple(TokenValue.DO,               TokenType.KEYWORDS,  -1));
		addToken("downto",    new Tuple(TokenValue.DOWNTO,           TokenType.KEYWORDS,  -1));
		addToken("else",      new Tuple(TokenValue.ELSE,             TokenType.KEYWORDS,  -1));
		addToken("end",       new Tuple(TokenValue.END,              TokenType.KEYWORDS,  -1));
		addToken("file",      new Tuple(TokenValue.FILE,             TokenType.KEYWORDS,  -1));
		addToken("for",       new Tuple(TokenValue.FOR,              TokenType.KEYWORDS,  -1));
		addToken("forward",   new Tuple(TokenValue.FORWARD,          TokenType.KEYWORDS,  -1));
		addToken("function",  new Tuple(TokenValue.FUNCTION,         TokenType.KEYWORDS,  -1));
		addToken("if",        new Tuple(TokenValue.IF,               TokenType.KEYWORDS,  -1));
		addToken("goto",      new Tuple(TokenValue.GOTO,             TokenType.KEYWORDS,  -1));
		addToken("of",        new Tuple(TokenValue.OF,               TokenType.KEYWORDS,  -1));
		addToken("otherwise", new Tuple(TokenValue.OTHERWISE,        TokenType.KEYWORDS,  -1));
		addToken("packed",    new Tuple(TokenValue.PACKED,           TokenType.KEYWORDS,  -1));
		addToken("procedure", new Tuple(TokenValue.PROCEDURE,        TokenType.KEYWORDS,  -1));
		addToken("program",   new Tuple(TokenValue.PROGRAM,          TokenType.KEYWORDS,  -1));
		addToken("read",      new Tuple(TokenValue.READ,             TokenType.KEYWORDS,  -1));
		addToken("readln",    new Tuple(TokenValue.READLN,           TokenType.KEYWORDS,  -1));
		addToken("record",    new Tuple(TokenValue.RECORD,           TokenType.KEYWORDS,  -1));
		addToken("repeat",    new Tuple(TokenValue.REPEAT,           TokenType.KEYWORDS,  -1));
		addToken("set",       new Tuple(TokenValue.SET,              TokenType.KEYWORDS,  -1));
		addToken("string",    new Tuple(TokenValue.STRING,           TokenType.KEYWORDS,  -1));
		addToken("then",      new Tuple(TokenValue.THEN,             TokenType.KEYWORDS,  -1));
		addToken("to",        new Tuple(TokenValue.TO,               TokenType.KEYWORDS,  -1));
		addToken("type",      new Tuple(TokenValue.TYPE,             TokenType.KEYWORDS,  -1));
		addToken("until",     new Tuple(TokenValue.UNTIL,            TokenType.KEYWORDS,  -1));
		addToken("var",       new Tuple(TokenValue.VAR,              TokenType.KEYWORDS,  -1));
		addToken("while",     new Tuple(TokenValue.WHILE,            TokenType.KEYWORDS,  -1));
		addToken("write",     new Tuple(TokenValue.WRITE,            TokenType.KEYWORDS,  -1));
		addToken("writeln",   new Tuple(TokenValue.WRITELN,          TokenType.KEYWORDS,  -1));
		addToken("in",        new Tuple(TokenValue.IN,               TokenType.KEYWORDS,   2));
		addToken("or",        new Tuple(TokenValue.OR,               TokenType.KEYWORDS,  10));
		addToken("xor",       new Tuple(TokenValue.XOR,              TokenType.KEYWORDS,  10));
		addToken("div",       new Tuple(TokenValue.DIV,              TokenType.KEYWORDS,  20));
		addToken("shl",       new Tuple(TokenValue.SHL,              TokenType.KEYWORDS,  20));
		addToken("shr",       new Tuple(TokenValue.SHR,              TokenType.KEYWORDS,  20));
		addToken("mod",       new Tuple(TokenValue.MOD,              TokenType.KEYWORDS,  20));
		addToken("not",       new Tuple(TokenValue.NOT,              TokenType.KEYWORDS,  40));
	}

	private void addToken(String name, Tuple tokenMeta)
	{
		dictMap.put(name, tokenMeta);
	}

	// if we can find it in the dictionary, we change the token type
	public Tuple lookup(String name)
	{
		TokenValue tokenValue = TokenValue.UNRESERVED;
		TokenType tokenType = TokenType.IDENTIFIER;
		int precedence = -1;

		Tuple tuple = dictMap.get(name);
		if (tuple != null) {
			tokenType = tuple.tokenType;
			tokenValue = tuple.tokenValue;
			precedence = tuple.precedence;
		}

		return new Tuple(tokenValue, tokenType, precedence);
	}

	public boolean haveToken(String name)
	{
		return dictMap.containsKey(name);
	}
}
