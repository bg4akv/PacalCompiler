package main;

public class TokenLocation {
	private final String fileName;
	private final int line;
	private final int column;

	public TokenLocation()
	{
		fileName = "";
		line = 1;
		column = 0;
	}

	public TokenLocation(String fileName, int line, int column)
	{
		this.fileName = fileName;
		this.line = line;
		this.column = column;
	}

	// this method is very similar with toString method in Java.
	@Override
	public String toString()
	{
		//return fileName + ":" + String.valueOf(line) + ":" + String.valueOf(column) + ":";
		return String.format("%s", fileName);
	}
}
