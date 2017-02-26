package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;

import java.util.ArrayList;
import java.util.List;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.*;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	/**
	 * Useful during development to ensure unimplemented routines are
	 * not accidentally called during development.  Delete it when 
	 * the Parser is finished.
	 *
	 */
	@SuppressWarnings("serial")	
	public static class UnimplementedFeatureException extends RuntimeException {
		public UnimplementedFeatureException() {
			super();
		}
	}

	Scanner scanner;
	Token t;
	final Kind[] strongOp = {TIMES , DIV , AND , MOD };
	final Kind[] weakOp = {PLUS , MINUS , OR};
	final Kind[] relOp = {LT , LE , GT , GE, EQUAL, NOTEQUAL};
	final Kind[] imageOp = {OP_WIDTH , OP_HEIGHT , KW_SCALE};
	final Kind[] frameOp = {KW_SHOW , KW_HIDE , KW_MOVE , KW_XLOC ,KW_YLOC};
	final Kind[] filterOp = {OP_BLUR ,OP_GRAY , OP_CONVOLVE};
	final Kind[] arrowOp = {ARROW,   BARARROW};
	final Kind[] FIRST_dec = {KW_INTEGER , KW_BOOLEAN , KW_IMAGE , KW_FRAME};
	final Kind[] FIRST_statement = {OP_SLEEP , KW_WHILE , KW_IF , IDENT, OP_BLUR ,OP_GRAY , OP_CONVOLVE, KW_SHOW , KW_HIDE , KW_MOVE , KW_XLOC ,KW_YLOC, OP_WIDTH , OP_HEIGHT , KW_SCALE};
	final Kind[] FIRST_paramDec = { KW_URL , KW_FILE , KW_INTEGER , KW_BOOLEAN};
	final Kind[] FIRST_chain = {IDENT, OP_BLUR ,OP_GRAY , OP_CONVOLVE, KW_SHOW , KW_HIDE , KW_MOVE , KW_XLOC ,KW_YLOC, OP_WIDTH , OP_HEIGHT , KW_SCALE};
	
	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	ASTNode parse() throws SyntaxException {
		ASTNode returnobj = program();
		matchEOF();
		return returnobj;
	}

	Expression expression() throws SyntaxException {
		//TODO
		Token ftok = t;
		Expression returnobj = term();
		while(t.isKind(relOp)){
			Token opr = t;
			consume();
			returnobj = new BinaryExpression(ftok, returnobj , opr, term());
		}
		return returnobj;
		//throw new UnimplementedFeatureException();
	}

	Expression term() throws SyntaxException {
		//TODO
		Token ftok = t;
		Expression returnobj = elem();
		while(t.isKind(weakOp)){
			Token opr = t;
			consume();
			returnobj = new BinaryExpression(ftok, returnobj , opr, elem());
		}
		return returnobj;
		//throw new UnimplementedFeatureException();
	}

	Expression elem() throws SyntaxException {
		//TODO
		Token ftok = t;
		Expression returnobj = factor();
		while(t.isKind(strongOp)){
			Token opr = t;
			consume();
			returnobj = new BinaryExpression(ftok, returnobj , opr, factor());
		}
		return returnobj;
		//throw new UnimplementedFeatureException();
	}

	Expression factor() throws SyntaxException {
		Token ftok = t;
		Expression returnobj = null;
		Kind kind = t.kind;
		switch (kind) {
		case IDENT: {
			consume();
			returnobj = new IdentExpression(ftok);
		}
			break;
		case INT_LIT: {
			consume();
			returnobj = new IntLitExpression(ftok);
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			consume();
			returnobj = new BooleanLitExpression(ftok);
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			consume();
			returnobj = new ConstantExpression(ftok);
		}
			break;
		case LPAREN: {
			consume();
			returnobj = expression();
			match(RPAREN);
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("Exception @factor" + "saw " + t.kind + "expected First-Factor"  + " tokenNum: " + scanner.tokenNum + " & token at pos: " + t.pos + " "+ t.getLinePos().toString() );
		}
		return returnobj;
	}

	Block block() throws SyntaxException {
		//TODO
		Token ftok = t;
		ArrayList<Dec> decs = new ArrayList<Dec>();
		ArrayList<Statement> statements = new ArrayList<Statement>();
		//TODO clarify the type to be used
		match(LBRACE);
		while(t.isKind(FIRST_dec) || t.isKind(FIRST_statement)){
			if(t.isKind(FIRST_dec)){
				decs.add(dec());
			}else{
				statements.add(statement());
			}
		}
		match(RBRACE);
		//throw new UnimplementedFeatureException();
		return new Block(ftok, decs, statements);
	}

	Program program() throws SyntaxException {
		//TODO
		Token ftok = t;
		match(IDENT);
		ArrayList<ParamDec> params = new ArrayList<ParamDec>();
		Block block_followparamdec;
		if(t.isKind(LBRACE)){
			block_followparamdec = block();
		}else if(t.isKind(FIRST_paramDec)){
			params.add(paramDec());
			while(t.isKind(COMMA)){
				consume();
				params.add(paramDec());
			}
			block_followparamdec = block();
		}else{
			throw new SyntaxException("Exception @program " + "saw " + t.kind + " expected LBRACE or First-paramDec"  + " tokenNum: " + scanner.tokenNum + " & token at pos: " + t.pos + " "+ t.getLinePos().toString() );
		}
		return new Program(ftok, params, block_followparamdec);
		//throw new UnimplementedFeatureException();
	}

	ParamDec paramDec() throws SyntaxException {
		//TODO
		Token ftok = t;
		Kind kind = t.kind;
		switch (kind) {
		case KW_URL:
		case KW_FILE: 
		case KW_INTEGER:
		case KW_BOOLEAN: {
			consume();
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("Exception @paramdec " + "saw " + t.kind + " expected First-paramDec"  + " tokenNum: " + scanner.tokenNum + " & token at pos: " + t.pos + " "+ t.getLinePos().toString());
		}
		Token identtoken = t;
		match(IDENT);
		return new ParamDec(ftok, identtoken);
		//throw new UnimplementedFeatureException();
	}

	Dec dec() throws SyntaxException {
		//TODO
		Token ftok = t;
		Kind kind = t.kind;
		switch (kind) {
		case KW_INTEGER:
		case KW_BOOLEAN: 
		case KW_IMAGE:
		case KW_FRAME: {
			consume();
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("Exception @dec " + "saw " + t.kind + " expected First-paramDec"  + " tokenNum: " + scanner.tokenNum + " & token at pos: " + t.pos + " "+ t.getLinePos().toString());
		}
		Token identtoken = t;
		match(IDENT);
		return new Dec(ftok, identtoken);
		//throw new UnimplementedFeatureException();
	}

	Statement statement() throws SyntaxException {
		//TODO
		Token ftok = t;
		Statement returnobj = null;
		Kind kind = t.kind;
		if(kind == OP_SLEEP){
			consume();
			Expression sleep_followexpression = expression();
			match(SEMI);
			returnobj = new SleepStatement(ftok, sleep_followexpression);
		}else if(kind == KW_WHILE){
			consume();
			match(LPAREN);
			Expression while_followexpression = expression();
			match(RPAREN);
			Block while_followblock = block();
			returnobj = new WhileStatement(ftok, while_followexpression, while_followblock); 
		}else if(kind == KW_IF){
			consume();
			match(LPAREN);
			Expression if_followexpression = expression();
			match(RPAREN);
			Block if_followblock = block();
			returnobj = new IfStatement(ftok, if_followexpression, if_followblock);
		}else if(t.isKind(FIRST_chain)){
			if(t.isKind(IDENT)){
				Token next_t = scanner.peek();
				if(next_t.isKind(ASSIGN)){
					consume();
					match(ASSIGN);
					Expression assign_exprfollow = expression();
					returnobj = new AssignmentStatement(ftok, new IdentLValue(ftok), assign_exprfollow);
				}
				else{
					returnobj = chain();
				}
			}else{
				returnobj = chain();
			}
			match(SEMI);
		}else{
			//you will want to provide a more useful error message
			throw new SyntaxException("Exception @statement " + "saw " + t.kind + " expected op_sleep or First-while/if/chain/assign"  + " tokenNum: " + scanner.tokenNum + " & token at pos: " + t.pos + " "+ t.getLinePos().toString());
		}
		return returnobj;
		//throw new UnimplementedFeatureException();
	}

	Chain chain() throws SyntaxException {
		//TODO
		Token ftok = t;
		Chain returnobj = chainElem();
		Token arrow = t;match(arrowOp);
		returnobj = new BinaryChain(ftok, returnobj, arrow, chainElem());
		while(t.isKind(arrowOp)){
			arrow = t;
			consume();
			returnobj = new BinaryChain(ftok, returnobj, arrow, chainElem());
		}
		return returnobj;
		//throw new UnimplementedFeatureException();
	}

	ChainElem chainElem() throws SyntaxException {
		//TODO
		ChainElem returnobj = null;
		Token ftok = t;
		if(t.isKind(IDENT)){
			consume();
			returnobj = new IdentChain(ftok);
		}else if(t.isKind(filterOp)){
			consume();
			returnobj = new FilterOpChain(ftok, arg());
		}else if(t.isKind(frameOp)){
			consume();
			returnobj = new FilterOpChain(ftok, arg());
		}else if(t.isKind(imageOp)){
			consume();
			returnobj = new FilterOpChain(ftok, arg());
		}else{
			throw new SyntaxException("Exception @chainelem " + "saw " + t.kind + " expected ident or First-Filterop/frameop/imageop"  + " tokenNum: " + scanner.tokenNum + " & token at pos: " + t.pos + " "+ t.getLinePos().toString() );
		}
		return returnobj;
		//throw new UnimplementedFeatureException();
	}

	Tuple arg() throws SyntaxException {
		//TODO
		Token ftok = t;
		List<Expression> exprList = new ArrayList<Expression>(); 
		if(t.isKind(LPAREN)){
			consume();
			expression();
			while(t.isKind(COMMA)){
				consume();
				exprList.add(expression());
			}
			match(RPAREN);
		}
		else{
			//do nothing now return;
		}
		return new Tuple(ftok, exprList);
		//throw new UnimplementedFeatureException();
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			return consume();
		}
		throw new SyntaxException("@match saw " + t.kind + " expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		if (t.isKind(kinds)) {
			return consume();
		}
		
		//TODO complete the error
		StringBuilder expected=new StringBuilder();
		for(Kind k: kinds){
			expected.append(k + " ");
		}
		throw new SyntaxException("@match2 saw " + t.kind + "expected one of: " + expected);
		//return false; //replace this statement
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		System.out.println(tmp.getText() + " ");
		return tmp;
	}

}
