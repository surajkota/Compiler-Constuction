package cop5556sp17;

import static cop5556sp17.Scanner.Kind.SEMI;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.Program;
import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Token;

public class ScannerTest {

	@Rule
    public ExpectedException thrown = ExpectedException.none();


	
	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
	}

	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = ";;;";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(SEMI, token1.kind);
		assertEquals(1, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
	
	
	/**
	 * This test illustrates how to check that the Scanner detects errors properly. 
	 * In this test, the input contains an int literal with a value that exceeds the range of an int.
	 * The scanner should detect this and throw and IllegalNumberException.
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		//String input = "99999999999999999";
		String input = "99999";
		Scanner scanner = new Scanner(input);
		//thrown.expect(IllegalNumberException.class);
		scanner.scan();		
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(Scanner.Kind.INT_LIT, token1.kind);
		assertEquals(0, token1.pos);
		String text = token1.getText();
		assertEquals(text.length(), token1.length);
		assertEquals(99999, token1.intVal());
		Scanner.LinePos l1 = token1.getLinePos();
		assertEquals(0,l1.line);
		assertEquals(0,l1.posInLine);
	}

//TODO  more tests
	@Test
	public void testWhiteSpace() throws IllegalCharException, IllegalNumberException{
		String input = "sleep\n   def";
		Scanner scanner = new Scanner(input);
//		thrown.expect(IllegalNumberException.class);
		scanner.scan();		
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.OP_SLEEP, token.kind);
		assertEquals(0, token.pos);
		String text = Scanner.Kind.OP_SLEEP.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		Scanner.LinePos l1 = token.getLinePos();
		assertEquals(0,l1.line);
		assertEquals(0,l1.posInLine);
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(Scanner.Kind.IDENT, token1.kind);
		assertEquals(9, token1.pos);
		text = token1.getText();
		assertEquals(text.length(), token1.length);
		assertEquals("def", token1.getText());
		l1 = token1.getLinePos();
		assertEquals(1,l1.line);
		assertEquals(3,l1.posInLine);
		/*
		 * Scanner.Token token1 = scanner.nextToken();
		assertEquals(Scanner.Kind.KW_IF, token1.kind);
		assertEquals(6, token1.pos);
		text = Scanner.Kind.KW_IF.getText();
		assertEquals(text.length(), token1.length);
		assertEquals("if", token1.getText());
		*/
		
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}


@Test
public void testwithprining() throws IllegalCharException, IllegalNumberException{
	//String input = "/**? ]*/ */ hello\n   world+()&%! xloc-yloc|->";
	//String input = "height /**/ abcdef /*abcd*/jkl/*";
	//String input = "integer /**/ boolean integer | boolean | image | url | file | frame | while | if | sleep | screenheight | screenwidthgray | convolve | blur | scale gray /*abcd*/jkl";
	//String input = "|->gray | convolve | blur | scale width | height xloc | yloc | hide | show | move true | false |  | &  |  ==  | !=  | < |  > | <= | >= | +  |  -  |  *   |  /   |  % | !  | -> |  |-> | <- | ;  | ,  |  (  |  )  | { | }";
	//String input = "abc\tdk";
	//String input = "this is a string literal\non two lines";
	//String input = "00123integer abc\n123 789 99 hello if{a==b;}/**/";
	//String input = "==!=";
	//String input = "/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
	String input = "|;|--->->-|->lu";
	//System.out.println(input.length()+ " " + input);
	Scanner scanner = new Scanner(input);
	//thrown.expect(IllegalCharException.class);
	scanner.scan();		
	//scanner.printall();
	/*
	 * Scanner.Token token1 = scanner.nextToken();
	assertEquals(Scanner.Kind.KW_IF, token1.kind);
	assertEquals(6, token1.pos);
	text = Scanner.Kind.KW_IF.getText();
	assertEquals(text.length(), token1.length);
	assertEquals("if", token1.getText());
	*/
	
	//check that the scanner has inserted an EOF token at the end
	//Scanner.Token token3 = scanner.nextToken();
	//assertEquals(Scanner.Kind.EOF,token3.kind);
}
}
