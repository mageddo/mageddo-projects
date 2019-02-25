import com.mageddo.rawstringliterals.RawString;
import com.mageddo.rawstringliterals.RawStrings;

import static com.mageddo.rawstringliterals.RawStrings.lateInit;

@RawString("type")
public class TestClass {

	/** Hello There */
	@RawString("field")
	private static String name;

	public String sayHello(){

		/*
			SELECT
				NAME, AGE
			FROM CUSTOMER
		*/
		@RawString("local variable")
		final String word = lateInit();
		return word;
	}

}
