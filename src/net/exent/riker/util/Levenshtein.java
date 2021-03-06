/*
 *  The MIT License
 * 
 *  Copyright 2010 Vidar Wahlberg <canidae@exent.net>.
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package net.exent.riker.util;

/**
 * Utility class to calculate Levenshtein distance between two strings.
 */
public final class Levenshtein {
	/**
	 * Matrix used for calculating the Levenshtein distance.
	 */
	/**
	 * Private constructor to prevent instantiation.
	 */
	private Levenshtein() {
	}

	/**
	 * Calculate the similarity between two strings, value returned goes from 0.0 to 1.0 where lowest value means complete mismatch and highest value means the strings are identical.
	 * This is done by dividing the Levenshtein distance between the two strings with the length of the largest input string.
	 * The comparison is case insensitive.
	 * If either of the strings are empty then 0.0 is returned.
	 * @param string1 the first input string
	 * @param string2 the second input string
	 * @return the similarity of the strings, value from 0.0 to 1.0
	 */
	public static double similarity(String string1, String string2) {
		/* check that both strings contain data */
		if (string1 == null || string2 == null || string1.length() == 0 || string2.length() == 0)
			return 0.0;
		/* set matrix size */
		int maxLength = Math.max(string1.length(), string2.length());
		int[][] matrix = new int[maxLength + 1][maxLength + 1];
		for (int a = 0; a < matrix.length; ++a) {
			matrix[a][0] = a;
			matrix[0][a] = a;
		}
		/* lowercase strings */
		string1 = string1.toLowerCase();
		string2 = string2.toLowerCase();
		/* compare the strings */
		for (int a = 1; a <= string1.length(); ++a) {
			for (int b = 1; b <= string2.length(); ++b) {
				int cost = string1.charAt(a - 1) == string2.charAt(b - 1) ? 0 : 1;
				int above = matrix[a - 1][b];
				int left = matrix[a][b - 1];
				int diag = matrix[a - 1][b - 1];
				int cell = Math.min(above + 1, Math.min(left + 1, diag + cost));
				if (a > 2 && b > 2) {
					int trans = matrix[a - 2][b - 2] + 1;
					if (string1.charAt(a - 2) != string2.charAt(b - 1))
						++trans;
					if (string1.charAt(a - 1) != string2.charAt(b - 2))
						++trans;
					if (cell > trans)
						cell = trans;
				}
				matrix[a][b] = cell;
			}
		}
		return 1.0 - (double) matrix[string1.length()][string2.length()] / (double) maxLength;
	}
}
