import com.mageddo.rawstringliterals.RawString;
import com.mageddo.rawstringliterals.RawStrings;
import com.mageddo.rawstringliterals.Rsl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Rsl
public class PersonTest {

	@Test
	public void sayHello() {
		assertEquals("\n\t\t * Hello World!!!\n\t\t ", new Person().sayHello());
	}

	@Test
	public void loremIpsum(){
		/*
		Lorem Ipsum is simply dummy text of
		the printing and typesetting industry
		 */
		@RawString
		final String text = RawStrings.lateInit();
		assertEquals(
			"\n\t\tLorem Ipsum is simply dummy text of\n\t\tthe printing and typesetting industry\n\t\t ",
			text
		);
	}
}
