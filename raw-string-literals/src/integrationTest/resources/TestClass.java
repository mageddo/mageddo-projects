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

	public String sayHello2(){
		try {
			/*
				UPDATE TABLE SET NAME='MATEUS' WHERE ID = 5
			*/
			@RawString
			final String sql = lateInit();
			return sql;
		} catch (RuntimeException e){
			throw e;
		}
	}

	public String sayHello3(){
		try {
			try {
				/*
				SAY_HELLO_3
				*/
				@RawString
				final String msg = lateInit();
				return msg;
			} finally {

			}
		} catch (RuntimeException e){
			throw e;
		}
	}

}
