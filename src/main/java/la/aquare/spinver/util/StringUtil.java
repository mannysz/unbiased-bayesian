package la.aquare.spinver.util;

import java.util.Arrays;

public final class StringUtil {
	
	public static String join(String[] array, String delimiter) {
		StringBuilder strBuilder = new StringBuilder();
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				strBuilder.append(array[i]);
				if (i < array.length -1) {
					strBuilder.append(delimiter);
				}
			}
		}
		return strBuilder.toString();
	}
	
	public static String join(String[] array, int start, int end, String delimiter) {
		String result = "";
		try {
			result = join(Arrays.copyOfRange(array, start, end), delimiter);	
		} catch (ArrayIndexOutOfBoundsException ex) {
			//empty
		} catch (IllegalArgumentException ex) {
			//empty
		} catch (NullPointerException ex) {
			//empty
		}
		return result;
	}
	
	public static String join(String[] array, int start, String delimiter) {
		return join(array, start, array.length, delimiter);
	}
}
