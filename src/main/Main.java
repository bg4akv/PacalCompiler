package main;

import main.Token.TokenType;


public class Main {

	public static void main(String[] args)
	{
		Scanner scanner = new Scanner("scanner_test.pas");
		if (scanner.getErrorFlag()) {
			return;
		}

		scanner.getNextToken();
		while (scanner.getToken().getTokenType() != TokenType.END_OF_FILE) {
			System.out.println(scanner.getToken().dump());
			scanner.getNextToken();
		}

		System.out.println("program exit");
	}
}
