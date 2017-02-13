package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;

import cop5556sp17.Scanner.Token;

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
	void parse() throws SyntaxException {
		program();
		matchEOF();
		return;
	}

	void expression() throws SyntaxException {
		//TODO
		term();
		while(t.isKind(relOp)){
			consume();
			term();
		}
		
		//throw new UnimplementedFeatureException();
	}

	void term() throws SyntaxException {
		//TODO
		elem();
		while(t.isKind(weakOp)){
			consume();
			elem();
		}
		//throw new UnimplementedFeatureException();
	}

	void elem() throws SyntaxException {
		//TODO
		factor();
		while(t.isKind(strongOp)){
			consume();
			factor();
		}
		//throw new UnimplementedFeatureException();
	}

	void factor() throws SyntaxException {
		Kind kind = t.kind;
		switch (kind) {
		case IDENT: {
			consume();
		}
			break;
		case INT_LIT: {
			consume();
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			consume();
		}
			break;
		case LPAREN: {
			consume();
			expression();
			match(RPAREN);
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal factor");
		}
	}

	void block() throws SyntaxException {
		//TODO
		match(LBRACE);
		while(t.isKind(FIRST_dec) || t.isKind(FIRST_statement)){
			if(t.isKind(FIRST_dec)){
				dec();
			}else{
				statement();
			}
		}
		match(RBRACE);
		//throw new UnimplementedFeatureException();
	}

	void program() throws SyntaxException {
		//TODO
		match(IDENT);
		if(t.isKind(LBRACE)){
			block();
		}else if(t.isKind(FIRST_paramDec)){
			paramDec();
			while(t.isKind(COMMA)){
				consume();
				paramDec();
			}
			block();
		}else{
			throw new SyntaxException("illegal factor");
		}
		//throw new UnimplementedFeatureException();
	}

	void paramDec() throws SyntaxException {
		//TODO
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
			throw new SyntaxException("illegal paramDec");
		}
		match(IDENT);
		//throw new UnimplementedFeatureException();
	}

	void dec() throws SyntaxException {
		//TODO
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
			throw new SyntaxException("illegal dec");
		}
		match(IDENT);
		//throw new UnimplementedFeatureException();
	}

	void statement() throws SyntaxException {
		//TODO
		Kind kind = t.kind;
		if(kind == OP_SLEEP){
			consume();
			expression();
			match(SEMI);
		}else if(kind == KW_WHILE || kind == KW_IF){
			consume();
			match(LPAREN);
			expression();
			match(RPAREN);
			block();
		}else if(t.isKind(FIRST_chain)){
			if(t.isKind(IDENT)){
				Token next_t = scanner.peek();
				if(next_t.isKind(ASSIGN)){
					consume();
					match(ASSIGN);
					expression();
				}
				else{
					chain();
				}
			}else{
				chain();
			}
			match(SEMI);
		}else{
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal statement");
		}
		//throw new UnimplementedFeatureException();
	}

	void chain() throws SyntaxException {
		//TODO
		chainElem();match(arrowOp);chainElem();
		while(t.isKind(arrowOp)){
			consume();
			chainElem();
		}
		//throw new UnimplementedFeatureException();
	}

	void chainElem() throws SyntaxException {
		//TODO
		if(t.isKind(IDENT)){
			consume();
		}else if(t.isKind(filterOp) || t.isKind(frameOp) || t.isKind(imageOp)){
			consume();
			arg();
		}else{
			throw new SyntaxException("illegal chain element");
		}
		//throw new UnimplementedFeatureException();
	}

	void arg() throws SyntaxException {
		//TODO
		if(t.isKind(LPAREN)){
			consume();
			expression();
			while(t.isKind(COMMA)){
				consume();
				expression();
			}
			match(RPAREN);
		}
		else{
			return;
		}
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
		throw new SyntaxException("saw " + t.kind + "expected " + kind);
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
		throw new SyntaxException("saw " + t.kind + "expected " );
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
