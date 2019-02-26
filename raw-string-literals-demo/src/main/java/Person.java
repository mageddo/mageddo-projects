import com.mageddo.rawstringliterals.RawString;

import static com.mageddo.rawstringliterals.RawStrings.lateInit;

@RawString
public class Person {

	public void sayHello() {

		/**
		 * Hello World!!!
		 */
		@RawString final String word = lateInit();

		System.out.println(word);

	}

	public static void main(String[] args) {
		new Person().sayHello();
	}
}
