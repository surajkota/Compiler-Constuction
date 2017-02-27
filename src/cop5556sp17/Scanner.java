package cop5556sp17;

import java.util.ArrayList;
import java.util.Collections;

//import cop5556sp17.Scanner.Kind;

//import cop5556sp17.Scanner.Kind;

public class Scanner {
	/**
	 * Kind enum
	 */
	
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}
	

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}
		

	

	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;  

		//returns the text of this Token
		public String getText() {
			//TODO IMPLEMENT THIS
			if(this.length>0){
			return chars.substring(this.pos, this.pos+this.length);
			}
			else if(this.length == 0){
				return "EOF";
			}
			return null;
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			//TODO IMPLEMENT THIS
			int line_num = Collections.binarySearch(line_numbers, this.pos);
			if(line_num<0){
				line_num=Math.abs(line_num);
				line_num-=2;
			}
			return new LinePos(line_num, this.pos-line_numbers.get(line_num));
			//return null;
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 * @throws IllegalNumberException 
		 */
		//public int intVal() throws NumberFormatException, IllegalNumberException{
		public int intVal() throws NumberFormatException, IllegalNumberException {
			//TODO IMPLEMENT THIS
			int tokenvalue = 0;
			if(this.kind==Kind.INT_LIT){
			
				try{
					tokenvalue = Integer.parseInt(chars.substring(this.pos, this.pos+this.length));
					
				}catch(Exception E){
					//throw E;
					throw new IllegalNumberException("Number: "+chars.substring(this.pos, this.pos+this.length)+" out of int bounds at pos "+this.pos);
				}
			}
			return tokenvalue;
		}

		public boolean isKind(Kind k) {
			// TODO Auto-generated method stub
			if(this.kind == k){
				return true;
			}
			else{
				return false;				
			}
		}
		
		public boolean isKind(Kind... kinds) {
			// TODO Auto-generated method stub
			for(Kind k : kinds){
				if (this.kind == k) {
					return true;
				}
			}
			return false;				
		}
		@Override
		  public int hashCode() {
		   final int prime = 31;
		   int result = 1;
		   result = prime * result + getOuterType().hashCode();
		   result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		   result = prime * result + length;
		   result = prime * result + pos;
		   return result;
		  }

		  @Override
		  public boolean equals(Object obj) {
		   if (this == obj) {
		    return true;
		   }
		   if (obj == null) {
		    return false;
		   }
		   if (!(obj instanceof Token)) {
		    return false;
		   }
		   Token other = (Token) obj;
		   if (!getOuterType().equals(other.getOuterType())) {
		    return false;
		   }
		   if (kind != other.kind) {
		    return false;
		   }
		   if (length != other.length) {
		    return false;
		   }
		   if (pos != other.pos) {
		    return false;
		   }
		   return true;
		  }

		 

		  private Scanner getOuterType() {
		   return Scanner.this;
		  }
	}

	 


	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
		line_numbers = new ArrayList<>();
		line_numbers.add(0);
		tokenNum=0;
		pos=0;

	}

	public static enum State {
		START,IN_DIGIT,IN_IDENT,AFTER_EQ,AFTER_NOT,AFTER_MINUS,AFTER_GT,
		AFTER_OR,AFTER_LT,AFTER_DIV,COMMENT_START;
	}

	public int skipWhiteSpace(){
		int tch;
		int tlength=chars.length();
		while(pos<tlength && Character.isWhitespace(tch = chars.charAt(pos))){
			if(tch=='\n'){
				line_numbers.add(pos+1);//Change- Not End of line(\n) position but where the new line starts
			}
			pos++;
		}
		return pos;
	}
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		pos=0; 
		//TODO IMPLEMENT THIS!!!!
		int length=chars.length();
		int startPos=0;
		int ch=-10;
		State state=State.START;
		
		while(pos<=length){
			ch = pos < length ? chars.charAt(pos) : -1;
        switch (state) {
            case START: {
            	pos=skipWhiteSpace();
            	ch = pos < length ? chars.charAt(pos) : -1;
            	startPos=pos;
            	switch(ch){
            	case -1: {tokens.add(new Token(Kind.EOF, pos, 0)); pos++;}  break;
                case '+': {tokens.add(new Token(Kind.PLUS, startPos, 1));pos++;} break;
                case '*': {tokens.add(new Token(Kind.TIMES, startPos, 1));pos++;} break;
                case '%': {tokens.add(new Token(Kind.MOD, startPos, 1));pos++;} break;
                case '&': {tokens.add(new Token(Kind.AND, startPos, 1));pos++;} break;
                case ';': {tokens.add(new Token(Kind.SEMI,startPos, 1));pos++;}break;
                case ',': {tokens.add(new Token(Kind.COMMA,startPos, 1));pos++;}break;
                case '(': {tokens.add(new Token(Kind.LPAREN,startPos, 1));pos++;}break;
                case ')': {tokens.add(new Token(Kind.RPAREN,startPos, 1));pos++;}break;
                case '{': {tokens.add(new Token(Kind.LBRACE,startPos, 1));pos++;}break;
                case '}': {tokens.add(new Token(Kind.RBRACE,startPos, 1));pos++;}break;
                case '0': {tokens.add(new Token(Kind.INT_LIT,startPos, 1));pos++;}break;
                
                case '=': {state = State.AFTER_EQ;pos++;}break;
                case '!': {state = State.AFTER_NOT;pos++;}break;
                case '-': {state = State.AFTER_MINUS;pos++;}break;
                case '>': {state = State.AFTER_GT;pos++;}break;
                case '|': {state = State.AFTER_OR;pos++;}break;
                case '<': {state = State.AFTER_LT;pos++;}break;
                case '/': {state = State.AFTER_DIV;pos++;}break;
                
                default: {
                    if (Character.isDigit(ch)) {state = State.IN_DIGIT;pos++;} 
                    else if (Character.isJavaIdentifierStart(ch)) {
                         state = State.IN_IDENT;pos++;
                     } 
                     else {throw new IllegalCharException(
                                "illegal char " +ch+" at pos "+pos);
                     }
                  }
            	}
            }  break;
            case IN_DIGIT: {
            	ch = pos < length ? chars.charAt(pos) : -1;
            	if (Character.isDigit(ch)) {
            		pos++;
            	}
            	else{
            		
            		try{
            			Integer.parseInt(chars.substring(startPos, pos));
            		}catch(Exception E){
            			throw new IllegalNumberException("Number: "+chars.substring(startPos, pos)+" out of int bounds at pos"+pos);
            		}
            		
            		tokens.add(new Token(Kind.INT_LIT,startPos, pos-startPos));
            		state = State.START;
            	}
            }  break;
            case IN_IDENT: {
            	
            	ch = pos < length ? chars.charAt(pos) : -1;
                if (Character.isJavaIdentifierPart(ch)) {
                    pos++;
              } else {
            	  boolean isnotkeyword = true;
            	  String test_thisident=chars.substring(startPos, pos);
            	  for(Kind type: Kind.values()){
            		  if(type.getText().equals(test_thisident)){
            			  tokens.add(new Token(type, startPos, pos - startPos));
            			  isnotkeyword=false;
            			  break;
            		  }
            	  }
            	  if(isnotkeyword){
            		  tokens.add(new Token(Kind.IDENT, startPos, pos - startPos));
            	  }
            	  state = State.START;
              }

            }  break;
            case AFTER_EQ: {
            	
            	ch = pos < length ? chars.charAt(pos) : -1;
            	if(ch=='='){
            		tokens.add(new Token(Kind.EQUAL, startPos, 2));
            		pos++;
            		state = State.START;
            	}
            	else{
            		throw new IllegalCharException("illegal char " +ch+" at pos "+pos);
            	}
            }  break;
            case AFTER_NOT: {
            	
            	ch = pos < length ? chars.charAt(pos) : -1;
            	if(ch=='='){
            		tokens.add(new Token(Kind.NOTEQUAL, startPos, 2));
            		pos++;
            		state = State.START;
            	}
            	else{
            		tokens.add(new Token(Kind.NOT, startPos, 1));
            		state=State.START;
            	}
            }  break;
            case AFTER_MINUS: {
            	
            	ch = pos < length ? chars.charAt(pos) : -1;
            	if(ch=='>'){
            		tokens.add(new Token(Kind.ARROW, startPos, 2));
            		pos++;
            		state = State.START;
            	}
            	else{
            		tokens.add(new Token(Kind.MINUS, startPos, 1));
            		state=State.START;
            	}
            }  break;
            case AFTER_GT: {
            	
            	ch = pos < length ? chars.charAt(pos) : -1;
            	if(ch=='='){
            		tokens.add(new Token(Kind.GE, startPos, 2));
            		pos++;
            		state = State.START;
            	}
            	else{
            		tokens.add(new Token(Kind.GT, startPos, 1));
            		state=State.START;
            	}
            }  break;
            case AFTER_OR: {
            	
            	ch = pos < length ? chars.charAt(pos) : -1;
            	if(ch=='-'){
            		pos++;
            		
                	ch = pos < length ? chars.charAt(pos) : -1;
                	if(ch == '>'){
                		tokens.add(new Token(Kind.BARARROW, startPos, 3));
                		pos++;
                		state = State.START;
                	}
                	else{
                		tokens.add(new Token(Kind.OR, startPos, 1));
                		tokens.add(new Token(Kind.MINUS, startPos+1, 1));
                		state = State.START;
                		//throw new IllegalCharException("illegal char " +ch+" at pos "+pos);
                	}
            	}
            	else{
            		tokens.add(new Token(Kind.OR, startPos, 1));
            		state=State.START;
            	}
            }  break;
            case AFTER_LT: {
            	
            	ch = pos < length ? chars.charAt(pos) : -1;
            	if(ch == '-'){
            		tokens.add(new Token(Kind.ASSIGN, startPos, 2));
            		pos++;
            		state = State.START;
            	}
            	else if(ch == '='){
            		tokens.add(new Token(Kind.LE, startPos, 2));
            		pos++;
            		state=State.START;
            	}
            	else{
            		tokens.add(new Token(Kind.LT, startPos, 1));
            		state=State.START;
            	}
            }  break;
            case AFTER_DIV: {
            	
            	ch = pos < length ? chars.charAt(pos) : -1;
            	if(ch=='*'){
            		pos++;
            		state = State.COMMENT_START;
            	}
            	else{
            		tokens.add(new Token(Kind.DIV, startPos, 1));
            		state=State.START;
            	}
            }  break;
            case COMMENT_START: {
            	pos=skipWhiteSpace();
            	ch = pos < length ? chars.charAt(pos) : -1;
            	if(ch=='*'){
            		pos++;            		
                	ch = pos < length ? chars.charAt(pos) : -1;
                	if(ch=='/'){
                		pos++;
                		state = State.START;
                	}
                	else if(ch == -1) {									/*required or not*/
                		//TODO throw exception
                		throw new IllegalCharException("unclosed comment at EOF  " +ch+" at pos "+pos);
                		//tokens.add(new Token(Kind.EOF, pos, 0)); pos++;
                		
                	}
                	else{
                		//pos++;		//Skipping comment char
                	}
            	}
            	else if(ch == -1) {
            		//TODO throw exception
            		throw new IllegalCharException("unclosed comment at EOF " +ch+" at pos "+pos);
            		//tokens.add(new Token(Kind.EOF, pos, 0)); pos++;
            	}
            	else{
            		pos++;			//Skipping comment char
            	}
            }  break;
            default:  assert false;
        }// switch(state)
    } // while
    //return this;
		if(ch!=-1)
		{tokens.add(new Token(Kind.EOF,pos,0));}
		return this;  
	}
/*
*/
	final ArrayList<Token> tokens;
	final ArrayList<Integer> line_numbers;
	final String chars;
	int tokenNum;
	int pos;
	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	
	/*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */public Token peek() {
	    if (tokenNum >= tokens.size())
	        return null;
	    return tokens.get(tokenNum);
	}
	/*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 
	public Token peek(){
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum+1);		
	}*/

	

	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		//TODO IMPLEMENT THIS
		return t.getLinePos();
		//return null;
	}
	
	public void printall(){
		for(int i=0;i<tokens.size();i++){
			Token t = tokens.get(i);
			System.out.println("Kind: " + t.kind.getText() + " token: "+t.getText() + " pos: "+t.pos + " len: "+t.length);
			System.out.println(t.getLinePos().toString());
			//System.out.println(" Line: "+t.getLinePos().line+" posinLine: "+t.getLinePos().posInLine);
		}
	}


}
