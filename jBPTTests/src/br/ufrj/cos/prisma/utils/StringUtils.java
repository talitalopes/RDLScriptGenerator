package br.ufrj.cos.prisma.utils;

public class StringUtils {

	public static String repeat(String str, int count) {
		int i = 0;
		String finalStr = "";
		while (i < count) {
			finalStr = String.format("%s%s", finalStr, str);
			i++;
		}
		return finalStr;	
	}
}
