import com.mageddo.rawstringliterals.RawString;

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
		final String word = "Hey";
		return word;
	}

}
