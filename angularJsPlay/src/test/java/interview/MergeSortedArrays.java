package interview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Merge two sorted integer array into one sorted array .
 * 
 * Example1
 * 
 * Input:
 * 
 * 1 3 5 7 9
 * 
 * 2 4 6 8 10
 * 
 * Ouput:
 * 
 * 1 2 3 4 5 6 7 8 9 10
 * 
 * 
 * 
 * 
 * 
 * Example2
 * 
 * Input:
 * 
 * 9 8 6 3 2
 * 
 * 7 6 3 2
 * 
 * Output:
 * 
 * 9 8 7 6 6 3 3 2 2
 * 
 * 
 * @author qinghai
 * 
 */
public class MergeSortedArrays {

	public static void main(String[] args) throws Exception {
		int[] arr1 = readArray();
		int[] arr2 = readArray();

		int[] pointer = { 0, 0 };
		int[][] arrays = new int[2][];
		arrays[0] = arr1;
		arrays[1] = arr2;

		boolean isAscend1 = checkAscend(arr1);
		boolean isAscend2 = checkAscend(arr2);
		if (isAscend1 != isAscend2) {
			throw new IllegalArgumentException(
					"Array 1 and array 2 are sorted by different rules.");
		}

		int[] result = new int[arr1.length + arr2.length];
		for (int i = 0; i < result.length; i++) {
			int num = 0;
			if (pointer[0] >= arrays[0].length) {
				num = 1;
			} else if (pointer[1] >= arrays[1].length) {
				num = 0;
			} else {
				if (isAscend1) {
					if (arrays[0][pointer[0]] > arrays[1][pointer[1]]) {
						num = 1;
					}
				} else {
					if (arrays[0][pointer[0]] < arrays[1][pointer[1]]) {
						num = 1;
					}
				}
			}

			result[i] = arrays[num][pointer[num]];
			pointer[num]++;
		}

		for (int r : result) {
			System.out.print(r + " ");
		}
	}

	public static boolean checkAscend(int[] arr) {
		if (arr.length >= 2) {
			for (int i = 0; i < arr.length - 1; i++) {
				if (arr[i] < arr[i + 1]) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}

	public static int[] readArray() throws IOException {
		System.out.println("Input the array:");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		String line = reader.readLine();
		String[] strArr = line.split(" ");
		int[] result = new int[strArr.length];
		for (int i = 0; i < strArr.length; i++) {
			result[i] = Integer.valueOf(strArr[i]);
		}
		return result;
	}

}
