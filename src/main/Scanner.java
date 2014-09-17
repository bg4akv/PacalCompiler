package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;

import main.Dictionary.Tuple;
import main.Token.TokenType;
import main.Token.TokenValue;


public class Scanner {
	private enum State {
		START,
		END_OF_FILE,
		IDENTIFIER,
		NUMBER,
		STRING,
		OPERATION,
		TERMINAL
	};

	private enum NumberState {
		INTERGER,
		FRACTION,
		EXPONENT,
		DONE
	};

	private FileInputStream fileInput = null;
	private FileChannel fileChannel = null;
	private InputStreamReader fileReader = null;
	private boolean eof = false;

	private final String fileName;
	private int line;
	private int column;

	private final Dictionary dictionary = new Dictionary();
	private Token token;

	private String buffer = "";
	private char currentChar = 0;
	private boolean errorFlag;


	public Scanner(String fileName)
	{
		this.fileName = fileName;
		line = 1;
		column = 0;
		currentChar = 0;
		errorFlag = false;

		open(fileName);
		if (fileReader == null) {
			errorToken("When trying to open file " + fileName + ", occurred error.");
			errorFlag = true;
		}
	}

	private void open(String fileName)
	{
		if (fileName == null || fileName.length() == 0) {
			return;
		}
		fileReader = openFile(fileName);
	}

	private void close()
	{
		closeFile(fileReader);
		eof = false;
	}

	private InputStreamReader openFile(String fileName)
	{
		File file = new File(fileName);
		if (file.exists() == false) {
			System.out.println("\"" + fileName + "\"" + " not found!");
			return null;
		}

		try {
			fileInput = new FileInputStream(file);
			fileChannel = fileInput.getChannel();
			return new InputStreamReader(fileInput, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void closeFile(InputStreamReader fileReader)
	{
		if (fileReader == null) {
			return;
		}

		try {
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean readChar(InputStreamReader fileReader, char[] ch, int len, boolean peek)
	{
		if (fileReader == null || eof) {
			return false;
		}

		try {
			long pos = 0;
			if (peek) {
				pos = fileChannel.position();
			}
			int n = fileReader.read(ch, 0, len);
			if (n == -1) {
				eof = true;
				return false;
			}
			if (peek) {
				fileChannel.position(pos);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		//System.out.print(new String(ch));
		return true;
	}

	private boolean isXDigit(char ch)
	{
		return Character.isDigit(ch)
				|| (ch >= 'a' && ch <= 'f')
				|| (ch >= 'A' && ch <= 'F');
	}

	private boolean isSpace(char ch)
	{
		return (ch == ' ') || (ch == '\n') || (ch == '\r')
			|| (ch == '\t') || (ch == '\f') || (ch == '\u000b');
	}

	private void getNextChar()
	{
		char[] ch = new char[] {0};
		if (readChar(fileReader, ch, 1, false) == false) {
			currentChar = 0;
			return;
		}

		currentChar = ch[0];
		if (currentChar == '\n') {
			line++;
			column = 0;
		} else {
			column++;
		}
	}

	private char peekChar()
	{
		char[] ch = new char[] {0};
		if (readChar(fileReader, ch, 1, true) == false) {
			return 0;
		}

		return ch[0];
	}

	private void addToBuffer(char c)
	{
		buffer += c;
	}

	private void reduceBuffer()
	{
		buffer = buffer.substring(0, buffer.length() - 1);
	}

	private Token makeToken(TokenType tt, TokenValue tv, TokenLocation loc, String name, int symbolPrecedence)
	{
		Token token = new Token(tt, tv, loc, buffer, symbolPrecedence);
		buffer = "";

		return token;
	}

	private Token makeToken(TokenType tt, TokenValue tv, TokenLocation loc, long intValue, String name)
	{
		Token token = new Token(tt, tv, loc, intValue, buffer);
		buffer = "";

		return token;
	}

	private Token makeToken(TokenType tt, TokenValue tv, TokenLocation loc, double realValue, String name)
	{
		Token token = new Token(tt, tv, loc, realValue, buffer);
		buffer = "";

		return token;
	}

	private void preprocess()
	{
		do {
			while (isSpace(currentChar)) {
				getNextChar();
			}

			handleLineComment();
			handleBlockComment();
		} while (isSpace(currentChar));
	}

	private void handleLineComment()
	{
		TokenLocation loc = new TokenLocation(fileName, line, column);
		if (currentChar == '(' && peekChar() == '*') {
			// eat *
			getNextChar();
			// update currentChar_
			getNextChar();
			while (!(currentChar == '*' && peekChar() == ')')) {
				// skip comment content
				getNextChar();
				// accident EOF
				if (eof) {
					errorToken(new TokenLocation(fileName, line, column).toString() + "end of file happended in comment, *) is expected!, but find " + currentChar);
					errorFlag = true;
					break;
				}
			}
			if (!eof) {
				// eat *
				getNextChar();
				// eat ) and update currentChar_
				getNextChar();
			}
		}
	}

	private void handleBlockComment()
	{
		TokenLocation loc = new TokenLocation(fileName, line, column);
		if (currentChar == '{') {
			do
			{
				getNextChar();
				if (eof) {
					errorToken(new TokenLocation(fileName, line, column).toString() + "end of file happended in comment, } is expected!, but find " + currentChar);
					errorFlag = true;
					break;
				}
			} while (currentChar != '}');
			if (!eof) {
				// eat } and update currentChar_
				getNextChar();
			}
		}
	}

	private Token handleEOFState()
	{
		TokenLocation loc = new TokenLocation(fileName, line, column);
		Token token = makeToken(TokenType.END_OF_FILE, TokenValue.UNRESERVED, loc, "END_OF_FILE", -1);
		// close the file
		close();
		
		return token;
	}

	private Token handleNumberState()
	{
		boolean matched = false;
		boolean isFloat = false;
		int numberBase = 10;

		TokenLocation loc = new TokenLocation(fileName, line, column);
		if (currentChar == '$') {
			numberBase = 16;
			// eat $ and update currentChar_
			getNextChar();
		}

		NumberState numberState = NumberState.INTERGER;
		do {
			switch (numberState) {
			case INTERGER:
				if (numberBase == 10) {
					handleDigit();
				} else if (numberBase == 16) {
					handleXDigit();
				}
				// maybe want to support octal...
				break;
			case FRACTION:
				handleFraction();
				isFloat = true;
				break;
			case EXPONENT:
				handleExponent();
				isFloat = true;
				break;
			case DONE:
				break;
			default:
				errorToken("Match number state error.");
				errorFlag = true;
				break;
			}
			// change number state
			if (currentChar == '.') {
				numberState = NumberState.FRACTION;
			} else if (currentChar == 'E' || currentChar == 'e') {
				numberState = NumberState.EXPONENT;
			} else {
				numberState = NumberState.DONE;
			}
		} while (numberState != NumberState.DONE);

		if (isFloat) {
			return makeToken(TokenType.REAL, TokenValue.UNRESERVED, loc, new BigDecimal(buffer).doubleValue(), buffer);
		} else {
			return makeToken(TokenType.INTEGER, TokenValue.UNRESERVED, loc, new BigDecimal(buffer).longValue(), buffer);
		}
	}

	private Token handleStringState()
	{
		TokenLocation loc = new TokenLocation(fileName, line, column);
		// eat ' and NOT update currentChar_
		// because we don't want ' (single quote).
		getNextChar();
		while (true) {
			if (currentChar == '\'') {
				// '''' condition
				// see pascal standard section 6.1.7
				if (peekChar() == '\'') {
					getNextChar();
				} else { // otherwise, we have handle string literal completely.
					break;
				}
			}
			addToBuffer(currentChar);
			getNextChar();
		}
		// eat end ' and update currentChar_ .
		getNextChar();
		// just one char
		if (buffer.length() == 1) {
			// makeToken(TokenType.CHAR, TokenValue.UNRESERVED, loc_,
			//    static_cast<long>(buffer_.at(0)), buffer_);
			return makeToken(TokenType.CHAR, TokenValue.UNRESERVED, loc, buffer, -1);
		} else {
			return makeToken(TokenType.STRING_LITERAL, TokenValue.UNRESERVED, loc, buffer, -1);
		}
	}

	private Token handleIdentifierState()
	{
		TokenLocation loc = new TokenLocation(fileName, line, column);
		// add first char
		addToBuffer(currentChar);
		getNextChar();
		while (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
			addToBuffer(currentChar);
			getNextChar();
		}
		// end while. currentChar_ is not alpha, number and _.
		// keyword or not
		// because Pascal is not case sensitive
		// we should transform it to lower case
		buffer = buffer.toLowerCase();
		// use dictionary to judge it is keyword or not
		Tuple tokenMeta = dictionary.lookup(buffer);
		return makeToken(tokenMeta.tokenType, tokenMeta.tokenValue, loc, buffer, tokenMeta.precedence);
	}

	private Token handleOperationState()
	{
		TokenLocation loc = new TokenLocation(fileName, line, column);
		//boolean matched = false;

		// add current symbol char
		addToBuffer(currentChar);
		// add next one symbol char
		addToBuffer(peekChar());
		if (dictionary.haveToken(buffer)) {
			//matched = true;
			getNextChar();
		} else {
			reduceBuffer();
		}
		Tuple tokenMeta = dictionary.lookup(buffer);
		// token type, token value, name, symbol precedence
		Token token = makeToken(tokenMeta.tokenType, tokenMeta.tokenValue, loc, buffer, tokenMeta.precedence);
		// update currentChar_
		getNextChar();
		
		return token;
	}

	private void handleDigit()
	{
		// add first number of integer
		addToBuffer(currentChar);
		getNextChar();
		while (Character.isDigit(currentChar)) {
			addToBuffer(currentChar);
			getNextChar();
		}
		// end while. currentChar_ is not digit.
		// notice maybe currentChar_ is .(dot) or E/e,
		// so the NumberState can be changed into
		// NumberState.Fraction or NumberState.Exponent.
	}
	
	private void handleXDigit()
	{
		// notice: we have eat $ and update currentChar_
		// in the handleNumber function. so we need not
		// eat currentChar_ like handleDigit function.
		// only have $ or not
		boolean readFlag = false;
		while (isXDigit(currentChar)) {
			readFlag = true;
			addToBuffer(currentChar);
			getNextChar();
		}

		if (!readFlag) {
			errorToken(new TokenLocation(fileName, line, column).toString() + "Hexadecimal number format error.");
			errorFlag = true;
		}
	}

	private void handleFraction()
	{
		// currentChar_ is . (dot)
		// if we have number 4..12. just simple error condition.
		// our compiler has one big difference compared with
		// commercial compiler, that is about error conditions.
		if (peekChar() == '.') {
			errorToken(new TokenLocation(fileName, line, column).toString() + "Fraction number can not have dot after dot");
			errorFlag = true;
		}

		// eat .
		addToBuffer(currentChar);
		getNextChar();
		while (Character.isDigit(currentChar)) {
			addToBuffer(currentChar);
			getNextChar();
		}
	}

	private void handleExponent()
	{
		// eat E/e
		addToBuffer(currentChar);
		getNextChar();

		// if number has +/-
		if (currentChar == '+' || currentChar == '-') {
			addToBuffer(currentChar);
			getNextChar();
		}

		while (Character.isDigit(currentChar)) {
			addToBuffer(currentChar);
			getNextChar();
		}
	}

	public Token getCurrentToken()
	{
		return token;
	}

	public Token getNextToken()
	{
		token = null;
		State state = State.START;

		while (state != State.TERMINAL) {
			switch (state) {
			case START:
				getNextChar();

				preprocess();
				if (eof) {
					state = State.END_OF_FILE;
		        	} else {
		        		if (Character.isLetter(currentChar)) {
		        			state = State.IDENTIFIER;
		        		} else if (Character.isDigit(currentChar)
		        				|| (currentChar == '$' && isXDigit(peekChar()))) { // if it is digit or xdigit
		        			state = State.NUMBER;
		        		} else if (currentChar == '\'') {
		        			state = State.STRING;
		        		} else {
		        			state = State.OPERATION;
		        		}
		        	}
				break;

			case END_OF_FILE:
				token = handleEOFState();
				state = State.TERMINAL;
				break;

			case IDENTIFIER:
				token = handleIdentifierState();
				state = State.TERMINAL;
				break;

			case NUMBER:
				token = handleNumberState();
				state = State.TERMINAL;
				break;

			case STRING:
				token = handleStringState();
				state = State.TERMINAL;
				break;

			case OPERATION:
				token = handleOperationState();
				state = State.TERMINAL;
				break;

			default:
				errorToken("Match token state error.");
				errorFlag = true;
				state = State.TERMINAL;
				break;
			}
		}
		
		return token;
	}

	public boolean getErrorFlag()
	{
		return errorFlag;
	}

	public void errorToken(String msg)
	{
		System.out.println("Token Error:" + msg);
	}
}
