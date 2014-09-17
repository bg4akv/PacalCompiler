package main;

import main.Token.TokenType;


public class Main {

	public static void main(String[] args)
	{
		Scanner scanner = new Scanner("scanner_test.pas");
		if (scanner.getErrorFlag()) {
			return;
		}

		Token token = null;
		do {
			token = scanner.getNextToken();
			if (token != null) {
				System.out.println(token.dump());
			}
		} while (token != null
			&& token.getTokenType() != TokenType.END_OF_FILE);
		
		System.out.println("--- program exit ---");
	}
}
