import com.mageddo.rawstringliterals.RawString;
import com.mageddo.rawstringliterals.RawStrings;
import com.mageddo.rawstringliterals.Rsl;

import java.util.function.Supplier;

import static com.mageddo.rawstringliterals.RawStrings.lateInit;

@Rsl
public class ClassWithLambdaExpression {

	public String sayHello(){

		/*
			SELECT
				NAME, AGE
			FROM CUSTOMER
		*/
		@RawString
		String word = lateInit();

		processLambda(c -> {
			System.out.println("hi there" + word);
			return word;
		});

		return word;
	}

	String processLambda(MyConsumer s){
		return s.apply("hello!");
	}

	public static interface MyConsumer {
		String apply(String arg);
	}

}
