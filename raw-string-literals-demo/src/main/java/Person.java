import com.mageddo.rawstringliterals.RawString;
import com.mageddo.rawstringliterals.Rsl;

import static com.mageddo.rawstringliterals.RawStrings.lateInit;

@Rsl
public class Person {

	public String sayHello() {

		/**
		 * Hello World!!!
		 */
		@RawString
		final String word = lateInit();
		return word;

	}

	public static void main(String[] args) {
		new Person().sayHello();
	}
}
