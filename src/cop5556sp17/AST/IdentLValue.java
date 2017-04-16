package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;

public class IdentLValue extends ASTNode {
	
	public Dec typedec;
	public IdentLValue(Token firstToken) {
		super(firstToken);
		typeName = null;
	}
	TypeName typeName;
	public TypeName getTypeName(){
		//return typedec.getTypeName();
		return this.typeName;
		
	}
	
	public void setTypeName(TypeName rarg){
		//typedec.setTypeName(rarg);
		this.typeName = rarg;
	}
	
	public void setindentLDec(Dec incoming){
		typedec = incoming;
	}
	@Override
	public String toString() {
		return "IdentLValue [firstToken=" + firstToken + "]";
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentLValue(this,arg);
	}

	public String getText() {
		return firstToken.getText();
	}

}
