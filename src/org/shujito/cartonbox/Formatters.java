package org.shujito.cartonbox;

import java.util.Locale;

// source:
// http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java

public class Formatters
{
	public static String humanReadableByteCount(long bytes)
	{
		return humanReadableByteCount(bytes, false);
	}
	
	public static String humanReadableByteCount(long bytes, boolean si)
	{
		int unit = si ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
		return String.format(Locale.US, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
}
