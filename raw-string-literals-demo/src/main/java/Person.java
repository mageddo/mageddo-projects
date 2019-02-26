import com.mageddo.rawstringliterals.RawString;
import com.mageddo.rawstringliterals.Rsl;

import static com.mageddo.rawstringliterals.RawStrings.lateInit;

@Rsl
public class Person {

	public void sayHello() {

		/**
		 * Hello World!!!
		 */
		@RawString
		final String word = lateInit();
		System.out.println(word);

	}

	public static void main(String[] args) {
		new Person().sayHello();
	}
}
