package interview;

public class TheFinally {

	public static void main(String[] args) {
		if (a()) {
			System.out.println("Do something.");
		}
	}

	public static Boolean a() {
		try {
			return true;
		} catch (Exception e) {

		} finally {
			System.out.println("Finally");
		}
		return false;
	}

}
