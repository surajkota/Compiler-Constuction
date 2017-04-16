package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.AST.*;

public class ASTTest {

	static final boolean doPrint = true;
	static void show(Object s){
		if(doPrint){System.out.println(s);}
	}
	

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IntLitExpression.class, ast.getClass());
	}



	@Test
	public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "1+abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
	}
	
	@Test
	public void testuserdefined() throws IllegalCharException, IllegalNumberException, SyntaxException {
		//String input = "abc / 123 * (2)+ true & screenheight != 2/3*4&xyz-8/4|(screenwidth<jkl) >2<3*10>=6<=9==def";//expr
		//String input = "a123{boolean abc convolve( xy2z *true + (t),helo) -> abc;}";//program//;(2+3)  * true + 2;}";//"A -> blur -> c";//arg epsilon//"prog0 {}";
		//String input = "3<5<7";//"A -> width (2+3,6,7,8,9/(80)) -> c";//expr//chain
		//String input  = "(abc / 123 * (2)+ true & screenheight != 2/3*4&xyz-8/4|(screenwidth<jkl) >2<3>=6<=9==def,abc / 123 * (2)+ true & screenheight != 2/3*4&xyz-8/4|(screenwidth<jkl) >2<3>=6<=9==def,abc / 123 * (2)+ true & screenheight != 2/3*4&xyz-8/4|(screenwidth<jkl) >2<3>=6<=9==def)";//arg
		//String input = "abc<-true; abc|->blur; if((3==4)){} sleep 9/hello; while(abc / 123 * (2)+ true & screenheight != 2/3*4&xyz-8/4|(screenwidth<jkl) >2<3>=6<=9==def){abc<-new<gjh;} ";//statement*4
		//String input = "abc file def, url int {frame htl boolean n while(ty/l+6>0){net<-6; hty->yloc;}}";//Program, block, dec, paramdec, statement ...all check
		//String input = "abc {} abc {image img} abc{ sleep 2/3;} def url intege{} abc file def, url int {frame htl boolean n while(ty/l+6>0){net<-6; hty->yloc;}}";
		//String input = "abc file def, url int {frame htl boolean n while(abc / 123 * (2)+ true & screenheight != 2/3*4&xyz-8/4|(screenwidth<jkl) >2<3*10>=6<=9==def){net<-6; hty->yloc;}}";
		//String input = "prog0 {}";
		String input = "p {\nboolean y \ny <- false;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.program();
		//assertEquals(Program.class, ast.getClass());
		//System.out.println(ast.toString());
		/*ast = parser.program();
		System.out.println(ast.toString());
		ast = parser.program();
		System.out.println(ast.toString());
		ast = parser.program();
		System.out.println(ast.toString());
		ast = parser.program();
		System.out.println(ast.toString());*/
		/*ast = parser.statement();
		System.out.println(ast.toString());
		ast = parser.statement();
		System.out.println(ast.toString());
		ast = parser.statement();
		System.out.println(ast.toString());
		ast = parser.statement();
		System.out.println(ast.toString());
		*/
		/*Program be = (Program) ast;
		Block b = be.getB();
		ArrayList<Statement> s = b.getStatements();
		BinaryChain bc = (BinaryChain) s.get(0);
		FilterOpChain ce = (FilterOpChain) bc.getE0();
		Tuple t = ce.getArg();
		List<Expression> el = t.getExprList();
		System.out.println(el.get(0));
		System.out.println(el.get(1));*/
		/*
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(EQUAL, be.getOp().kind);
		System.out.println(be.toString());
		be = (BinaryExpression) be.getE0();
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(LE, be.getOp().kind);
		System.out.println(be.toString());*/
		/*String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.factor();
		assertEquals(IdentExpression.class, ast.getClass());
		System.out.println(ast.getClass());
		 * BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);*/
	}
	
}
