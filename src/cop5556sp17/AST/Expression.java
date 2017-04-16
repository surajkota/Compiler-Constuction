package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

public abstract class Expression extends ASTNode {
	
	private TypeName nodetype; 
	
	protected Expression(Token firstToken) {
		super(firstToken);
		nodetype = null;
	}
	
	public TypeName getType(){
		return this.nodetype;
	}
	
	public void setType(TypeName rarg){
		this.nodetype = rarg;
	}
	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;

}
