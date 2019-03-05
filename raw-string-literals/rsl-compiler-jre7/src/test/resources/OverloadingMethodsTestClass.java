import com.mageddo.rawstringliterals.RawString;
import com.mageddo.rawstringliterals.RawStrings;
import com.mageddo.rawstringliterals.Rsl;

import java.util.Date;

import static com.mageddo.rawstringliterals.RawStrings.lateInit;

@Rsl
public class OverloadingMethodsTestClass {

	public String findCustomers(){
		return "foo";
	}

	public String findCustomers(Date from){
		/*
			SELECT
				NAME, AGE
			FROM CUSTOMER
			WHERE CREATE > :from
		*/
		@RawString
		final String word = lateInit();
		return word;
	}


	public String findCustomers(String name){
		try {
			/*
			SELECT
				NAME, AGE
			FROM CUSTOMER
			WHERE NAME = :name
			*/
			@RawString
			final String sql = lateInit();
			return sql;
		} catch (RuntimeException e){
			throw e;
		}
	}

}
