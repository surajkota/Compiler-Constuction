package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;


public abstract class Chain extends Statement {
	
	private TypeName nodetype;
	
	public TypeName getTypeName(){
		return this.nodetype;
	}
	
	public void setTypeName(TypeName rarg){
		this.nodetype = rarg;
	}
	
	public Chain(Token firstToken) {
		super(firstToken);
		nodetype = null;
	}

}
