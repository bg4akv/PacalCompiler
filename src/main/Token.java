package main;

public class Token {
	public enum TokenType {
		// see Pascal standard 6.4
		// in fact, we can put these 5 types to one category
		// named constant. but I want to make it more cleaner.
		INTEGER, // such as 3, 4 and so on
		REAL, // such as 3.14
		BOOLEAN, // true or false
		CHAR, // such as 'a','b'
		STRING_LITERAL, // such as 'hello world'
		IDENTIFIER, // such as abc
		KEYWORDS, // such as if
		OPERATORS, // such as  + - * /
		DELIMITER, // such as ,
		END_OF_FILE, // end of file
		UNKNOWN
	};

	public enum TokenValue {
		// see pascal standard 6.1.2
		AND,
		FOR,
		TO,
		DOWNTO,
		DO,
		IF,
		THEN,
		ELSE,
		WHILE,
		REPEAT,
		UNTIL,
		FUNCTION,
		PROCEDURE,
		BEGIN,
		END,
		PROGRAM,
		FORWARD,
		GOTO,
		OR,
		NOT,
		CASE,
		OTHERWISE,
		WITH,
		IN,

		// I/O routine
		WRITE,
		WRITELN,
		READ,
		READLN,

		// type/var/const
		TYPE,
		VAR,
		PACKED,
		ARRAY,
		OF,
		RECORD,
		CONST,
		FILE,
		SET,
		STRING,

		// symbols
		LEFT_PAREN, // (
		RIGHT_PAREN, // )
		LEFT_SQUARE, // [
		RIGHT_SQUARE, // ]
		PLUS, // +
		MINUS, // -
		MULTIPLY, // *
		DIVIDE, // /
		COMMA, // ,
		SEMICOLON, // ;
		COLON, // :
		ASSIGN, // :=
		PERIOD, // .
		DOT_DOT, // ..
		UPARROW, // ^
		DIV, // div
		MOD, // mod
		XOR, // xor
		SHR, // shr
		SHL, // shl

		// comparation symbols
		LESS_OR_EQUAL, // <=
		LESS_THAN, // <
		GREATER_OR_EQUAL, // >=
		GREATER_THAN, // >
		EQUAL, // =
		NOT_EQUAL, // <>

		UNRESERVED
	};

	private final TokenType type;
	private final TokenValue value;
	private final TokenLocation location;
	private final int symbolPrecedence;
	private final String name;

	// const values of token
	private final long intValue;
	private final double realValue;
	private final String strValue;

	public Token()
	{
		this(TokenType.UNKNOWN, TokenValue.UNRESERVED, new TokenLocation("", 0, 0), "", -1, "", 0, 0);
	}

	public Token(TokenType type, TokenValue value, TokenLocation location, String name, int symbolPrecedence)
	{
		this(type, value, location, name, symbolPrecedence, "", 0, 0);
	}

	public Token(TokenType type, TokenValue value, TokenLocation location, String strValue, String name)
	{
		this(type, value, location, name, -1, strValue, 0, 0);
	}

	public Token(TokenType type, TokenValue value, TokenLocation location, long intValue, String name)
	{
		this(type, value, location, name, -1, "", intValue, 0);
	}

	public Token(TokenType type, TokenValue value, TokenLocation location, double realValue, String name)
	{
		this(type, value, location, name, -1, "", 0, realValue);
	}

	public Token(TokenType type, TokenValue value, TokenLocation location,
			String name, int symbolPrecedence,
			String strValue, long intValue, double realValue)
	{
		this.type = type;
		this.value = value;
		this.location = location;
		this.name = name;
		this.symbolPrecedence = symbolPrecedence;

		this.strValue = new String(strValue);
		this.intValue = intValue;
		this.realValue = realValue;
		
	}

	public TokenType getTokenType()
	{
		return type;
	}

	public TokenValue getTokenValue()
	{
		return value;
	}

	public String getTokenName()
	{
		return name;
	}

	public TokenLocation getTokenLocation()
	{
		return location;
	}

	public long getIntValue()
	{
		return intValue;
	}

	public double getRealValue()
	{
		return realValue;
	}

	public String getStringValue()
	{
		return strValue;
	}

	public int getSymbolPrecedence()
	{
		return symbolPrecedence;
	}

	public String tokenTypeDescription()
	{
		String buffer = "";

		switch (type) {
		case INTEGER:
			buffer = "integer:        ";
			break;
		case REAL:
			buffer = "real:           ";
			break;
		case BOOLEAN:
			buffer = "boolean:        ";
			break;
		case CHAR:
			buffer = "char:           ";
			break;
		case STRING_LITERAL:
			buffer = "string literal: ";
			break;
		case IDENTIFIER:
			buffer = "identifier:     ";
			break;
		case KEYWORDS:
			buffer = "keywords:       ";
			break;
		case OPERATORS:
			buffer = "operators:      ";
			break;
		case DELIMITER:
			buffer = "delimiter:      ";
			break;
		case END_OF_FILE:
			buffer = "eof             ";
			break;
		case UNKNOWN:
			buffer = "unknown:        ";
			break;
		default:
			break;
		}

		return buffer;
	}

	public String dump()
	{
		return location.toString() + "\t" + tokenTypeDescription()
				+ "\t" + name + "\t\t" + getSymbolPrecedence() + "\n";
	}
}
