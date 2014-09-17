package main;

public class TokenLocation {
	private final String fileName;
	private final int line;
	private final int column;

	public TokenLocation()
	{
		fileName = "";
		line = 0;
		column = 0;
	}

	public TokenLocation(String fileName, int line, int column)
	{
		this.fileName = fileName;
		this.line = line;
		this.column = column;
	}

	@Override
	public String toString()
	{
		return fileName + ":" + String.valueOf(line) + ":" + String.valueOf(column) + ":";
	}
}
