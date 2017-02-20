package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;


public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}


	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}

	@Test
	public void usertest() throws IllegalCharException, IllegalNumberException, SyntaxException{
		//String input = "a123{boolean abc convolve( xy2z *true + (t),helo) -> abc;}";//;(2+3)  * true + 2;}";//"A -> blur -> c";//"prog0 {}";
		//String input = "abc / 123 * (2)+ true & screenheight != 2/3*4&xyz-8/4|(screenwidth<jkl) >2<3>=6<=9==def";//expression and down
		//String input = "(abc / 123 * (2)+ true & screenheight != 2/3*4&xyz-8/4|(screenwidth<jkl) >2<3>=6<=9==def,abc / 123 * (2)+ true & screenheight != 2/3*4&xyz-8/4|(screenwidth<jkl) >2<3>=6<=9==def,abc / 123 * (2)+ true & screenheight != 2/3*4&xyz-8/4|(screenwidth<jkl) >2<3>=6<=9==def)";
		//String input = "abc -> im -> gray (neural , 2+3) -> xloc ->scale(abc / 123 * (2)+ true & screenheight != 2/3*4&xyz-8/4|(screenwidth<jkl) >2<3>=6<=9==def, (jkg/3+4)";
		//thrown.expect(Parser.SyntaxException.class);
		//String input = "abc<-true; abc->blur; if((3==4)){} sleep 9/hello; while(abc / 123 * (2)+ true & screenheight != 2/3*4&xyz-8/4|(screenwidth<jkl) >2<3>=6<=9==def){abc<-new<gjh;} ";
		//String input = "abc {} abc {image img} abc{ sleep 2/3;} def url intege{} abc file def, url int {frame htl boolean n while(ty/l+6>0){net<-6; hty->yloc;}}";
		//String input = "3<5<7";
		String input = "iif->gray(8);";
		//String input = "abc file def, url int {frame htl boolean n while(ty/l+6>0){net<-6; hty->yloc;}}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
		//parser.statement();
		//parser.expression();
		//parser.program();parser.program();parser.program();parser.program();parser.program();
		//parser.statement();parser.statement();parser.statement();parser.statement();parser.statement();
		//parser.chain();
		//parser.arg();
		//parser.expression();
		//parser.term();
		//parser.elem();
		//parser.program();
		//parser.parse();
	}
}
