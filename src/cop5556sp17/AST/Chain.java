package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;


public abstract class Chain extends Statement {
	
	private TypeName nodetype;
	
	public TypeName getTypeName(){
		return nodetype;
	}
	
	public void setTypeName(TypeName rarg){
		nodetype = rarg;
	}
	
	public Chain(Token firstToken) {
		super(firstToken);
		nodetype = null;
	}

}
