package org.shujito.cartonbox.util;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

// sources:
// http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
// http://stackoverflow.com/questions/3859288/how-to-calculate-time-ago-in-java

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
	
	public static String humanReadableTimeElapsed(long duration, TimeUnit max, TimeUnit min)
	{
		StringBuilder sb = new StringBuilder();
		
		TimeUnit[] timeUnits = TimeUnit.values();
		TimeUnit current = max;
		
		while(duration > 0)
		{
			long temp = current.convert(duration, TimeUnit.MILLISECONDS);
			
			if(temp > 0)
			{
				duration -= current.toMillis(temp);
				sb.append(temp);
				sb.append(" ");
				sb.append(current.name().toLowerCase(Locale.US));
				if(temp < 2)
				{
					sb.deleteCharAt(sb.length() - 1);
				}
				sb.append(", ");
			}
			
			if(current == min) break;
			
			current = timeUnits[current.ordinal() - 1];
		}
		
		if(sb.lastIndexOf(", ") < 0)
		{
			return String.format("0 %s", min.name().toLowerCase(Locale.US));
		}

		sb.deleteCharAt(sb.length() - 1);
		sb.deleteCharAt(sb.length() - 1);
		int i = sb.lastIndexOf(", ");
		if(i > 0)
		{
			sb.deleteCharAt(i);
			sb.insert(i, " and");
		}
		
		return sb.toString();
	}
}
