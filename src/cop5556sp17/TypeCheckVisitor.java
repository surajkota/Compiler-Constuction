package cop5556sp17;

import cop5556sp17.AST.*;


import java.util.ArrayList;
import java.util.List;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;
import static cop5556sp17.AST.Type.*;

import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Chain lchain = binaryChain.getE0();
		ChainElem rchain = binaryChain.getE1();
		Token betop = binaryChain.getArrow();
		lchain.visit(this, null);
		rchain.visit(this, null);
		TypeName Lchaintype = lchain.getTypeName();
		TypeName Rchaintype = rchain.getTypeName();
		if(betop.isKind(Kind.ARROW)){
			if(Lchaintype.isType(TypeName.URL) || Lchaintype.isType(TypeName.FILE)){
				if(Rchaintype.isType(TypeName.IMAGE)){
					binaryChain.setTypeName(TypeName.IMAGE);
				}else{
					throw new TypeCheckException("Binary chain mismatch :" + lchain.getFirstToken().getText() + " at " + lchain.getFirstToken().getLinePos());
				}
			}else if(Lchaintype.isType(TypeName.FRAME)){
				if(rchain instanceof FrameOpChain && rchain.getFirstToken().isKind(KW_XLOC, KW_YLOC)){
					binaryChain.setTypeName(TypeName.INTEGER);
				}else if(rchain instanceof FrameOpChain && rchain.getFirstToken().isKind(KW_SHOW, KW_HIDE, KW_MOVE)){
					binaryChain.setTypeName(TypeName.FRAME);
				}else{
					throw new TypeCheckException("Binary chain mismatch :" + lchain.getFirstToken().getText() + " at " + lchain.getFirstToken().getLinePos());
				}
			}else if(Lchaintype.isType(TypeName.IMAGE)){
				if(Rchaintype.isType(TypeName.FRAME)){
					binaryChain.setTypeName(TypeName.FRAME);
				}else if(Rchaintype.isType(TypeName.FILE)){
					binaryChain.setTypeName(TypeName.NONE);
				}else if(rchain instanceof IdentChain && rchain.getTypeName().equals(TypeName.IMAGE)){
					binaryChain.setTypeName(TypeName.IMAGE);
				}else if(rchain instanceof ImageOpChain && rchain.getFirstToken().isKind(OP_WIDTH, OP_HEIGHT)){
					binaryChain.setTypeName(TypeName.INTEGER);
				}else if(rchain instanceof FilterOpChain && rchain.getFirstToken().isKind(OP_GRAY, OP_BLUR, OP_CONVOLVE)){
					binaryChain.setTypeName(TypeName.IMAGE);
				}else if(rchain instanceof ImageOpChain && rchain.getFirstToken().isKind(KW_SCALE)){
					binaryChain.setTypeName(TypeName.IMAGE);
				}else{
					throw new TypeCheckException("Binary chain mismatch :" + lchain.getFirstToken().getText() + " at " + lchain.getFirstToken().getLinePos());
				}
			}else if(Lchaintype.isType(TypeName.INTEGER)){
				if(rchain instanceof IdentChain && rchain.getTypeName().equals(TypeName.INTEGER)){
					binaryChain.setTypeName(TypeName.FRAME);
				}
			}else{
				throw new TypeCheckException("Binary not match condition :" + lchain.getFirstToken().getText() + " at " + lchain.getFirstToken().getLinePos());
			}
		}else if(betop.isKind(Kind.BARARROW)){
			if(Lchaintype.isType(TypeName.IMAGE) && rchain instanceof FilterOpChain && rchain.getFirstToken().isKind(OP_GRAY, OP_BLUR, OP_CONVOLVE)){
				binaryChain.setTypeName(TypeName.IMAGE);
			}else{
				throw new TypeCheckException("Binary mismatch :" + lchain.getFirstToken().getText() + " at " + lchain.getFirstToken().getLinePos());
			}
		}
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e0 = binaryExpression.getE0();
		Expression e1 = binaryExpression.getE1();
		Token beto = binaryExpression.getOp();
		e0.visit(this, null);
		e1.visit(this, null);
		TypeName e0type = e0.getType();
		TypeName e1type = e1.getType();
		if(beto.isKind(EQUAL, NOTEQUAL)){
			if(e0type.isType(e1type)){
				binaryExpression.setType(TypeName.BOOLEAN);
			}else{
				throw new TypeCheckException("Illegal operator for expression :" + e0.firstToken);
			}
		}else if(beto.isKind(LT, LE, GT, GE) ){
			if(e0type.isType(TypeName.BOOLEAN) && e1type.isType(TypeName.BOOLEAN)){
				binaryExpression.setType(TypeName.BOOLEAN);
			}else if(e0type.isType(TypeName.INTEGER) && e1type.isType(TypeName.INTEGER)){
				binaryExpression.setType(TypeName.BOOLEAN);
			}else{
				throw new TypeCheckException("Illegal operator for expression :" + e0.firstToken);
			}
		}else if(beto.isKind(PLUS, MINUS)){
			
			if(e0type.isType(TypeName.INTEGER) && e1type.isType(TypeName.INTEGER)){
				binaryExpression.setType(TypeName.INTEGER);
			}else if (e0type.isType(TypeName.IMAGE) && e1type.isType(TypeName.IMAGE)){
				binaryExpression.setType(TypeName.IMAGE);
			}else{
				throw new TypeCheckException("Illegal operator for expression :" + e0.firstToken);
			}
		}else if(beto.isKind(TIMES)){
			
			if(e0type.isType(TypeName.INTEGER) && e1type.isType(TypeName.INTEGER)){
				binaryExpression.setType(TypeName.INTEGER);
			}else if (e0type.isType(TypeName.INTEGER, TypeName.IMAGE) && e1type.isType(TypeName.INTEGER, TypeName.IMAGE)){
				binaryExpression.setType(TypeName.IMAGE);
			}else{
				throw new TypeCheckException("Illegal operator for expression :" + e0.firstToken);
			}
		}else if(beto.isKind(DIV)){
			
			if(e0type.isType(TypeName.INTEGER) && e1type.isType(TypeName.INTEGER)){
				binaryExpression.setType(TypeName.INTEGER);
			}else if(e0type.isType(TypeName.IMAGE) && e1type.isType(TypeName.INTEGER)){
				binaryExpression.setType(TypeName.IMAGE);
			}else{
				throw new TypeCheckException("Illegal operator for expression :" + e0.firstToken);
			}
		}else if(beto.isKind(OR, AND)){
			if(e0type.isType(TypeName.BOOLEAN) && e1type.isType(TypeName.BOOLEAN)){
				binaryExpression.setType(TypeName.BOOLEAN);
			}else{
				throw new TypeCheckException("Illegal operator for expression :" + e0.firstToken);
			}
		}else if(beto.isKind(MOD)){
			if(e0type.isType(TypeName.INTEGER) && e1type.isType(TypeName.INTEGER)){
				binaryExpression.setType(TypeName.INTEGER);
			}else if(e0type.isType(TypeName.IMAGE) && e1type.isType(TypeName.INTEGER)){
				binaryExpression.setType(TypeName.IMAGE);
			}else{
				throw new TypeCheckException("Illegal operator for expression :" + e0.firstToken);
			}
		}
		else{
			throw new TypeCheckException("Illegal operator for expression :" + e0.firstToken);
		}
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO Auto-generated method stub
		symtab.enterScope();
		int listsize = block.getDecs().size();
		
		if(listsize>0){
			ArrayList<Dec> declist = block.getDecs();
			for(int i=0;i<listsize;i++){
				declist.get(i).visit(this, null);
			}
		}
		listsize = block.getStatements().size();
		if(listsize>0){
			ArrayList<Statement> statementlist = block.getStatements();
			for(int i=0;i<listsize;i++){
				statementlist.get(i).visit(this, null);
			}
		}
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub`
		booleanLitExpression.setType(TypeName.BOOLEAN);
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(filterOpChain.getArg().getExprList().size()==0){
			filterOpChain.setTypeName(TypeName.IMAGE);			
		}else{
			throw new TypeCheckException("FilterOp Tuple not zero size: " + filterOpChain.firstToken.getText() + " at " + filterOpChain.firstToken.getLinePos());
		}
		
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(frameOpChain.firstToken.isKind(KW_SHOW, KW_HIDE)){
			if(frameOpChain.getArg().getExprList().size()==0){
				frameOpChain.setTypeName(TypeName.NONE);			
			}else{
				throw new TypeCheckException("FramerOp Tuple not zero size: " + frameOpChain.firstToken.getText() + " at " + frameOpChain.firstToken.getLinePos());
			}
		}else if(frameOpChain.firstToken.isKind(KW_XLOC, KW_YLOC)){
			if(frameOpChain.getArg().getExprList().size()==0){
				frameOpChain.setTypeName(TypeName.INTEGER);			
			}else{
				throw new TypeCheckException("FramerOp Tuple not zero size: " + frameOpChain.firstToken.getText() + " at " + frameOpChain.firstToken.getLinePos());
			}
		}else if(frameOpChain.firstToken.isKind(KW_MOVE)){
			if(frameOpChain.getArg().getExprList().size()==2){
				frameOpChain.getArg().visit(this, null);
				frameOpChain.setTypeName(TypeName.NONE);			
			}else{
				throw new TypeCheckException("FramerOp Tuple not size = 2: " + frameOpChain.firstToken.getText() + " at " + frameOpChain.firstToken.getLinePos());
			}
		}//TODO else part
		/*else{
			//TODO what to do
			throw new SyntaxException("Not frame op");
		}*/
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec decofident = symtab.lookup(identChain.firstToken.getText());
		if(decofident != null){
			identChain.setTypeName(Type.getTypeName(decofident.getFirstToken()));
			identChain.typedec = decofident;
		}else{
			throw new TypeCheckException("Idetifier declaration missing or not visible in current scope: " + identChain.firstToken.getText() + " at " + identChain.firstToken.getLinePos());
		}
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec decofident = symtab.lookup(identExpression.firstToken.getText());
		identExpression.typedec=decofident;
		if(decofident != null){
			identExpression.setType(Type.getTypeName(decofident.getFirstToken()));
		}else{
			throw new TypeCheckException("Idetifier declaration missing or not visible in current scope: " + identExpression.firstToken.getText() + " at " + identExpression.firstToken.getLinePos());
		}
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		ifStatement.getE().visit(this, null);
		if(ifStatement.getE().getType().isType(TypeName.BOOLEAN)){
			boolean nouse = false;
		}else{
			throw new TypeCheckException("If statemnt expression not boolean: " + ifStatement.firstToken.getLinePos() + " pos: " + ifStatement.firstToken.pos);
		}
		ifStatement.getB().visit(this, null);
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		intLitExpression.setType(TypeName.INTEGER);
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		sleepStatement.getE().visit(this, null);
		if(sleepStatement.getE().getType().isType(TypeName.INTEGER)){
			boolean nouse = false;
		}else{
			throw new TypeCheckException("Sleep statemnt rValue not integer: " + sleepStatement.firstToken.getLinePos() + " pos: " + sleepStatement.firstToken.pos);
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		whileStatement.getE().visit(this, null);
		if(whileStatement.getE().getType().isType(TypeName.BOOLEAN)){
			boolean nouse = false;
		}else{
			throw new TypeCheckException("While statemnt expression not boolean: " + whileStatement.firstToken.getLinePos() + " pos: " + whileStatement.firstToken.pos);
		}
		whileStatement.getB().visit(this, null);
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		boolean insertion = false;
		insertion = symtab.insert(declaration.getIdent().getText(), declaration);
		if(insertion == false){
			throw new TypeCheckException("Variable redeclaration not permitted: " + declaration.getIdent().getText());
		}else{
			declaration.setTypeName(Type.getTypeName(declaration.firstToken));
		}
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO Auto-generated method stub
		int listsize = program.getParams().size();
		if(listsize>0){
			ArrayList<ParamDec> paramlist = program.getParams();
			for(int i=0;i<listsize;i++){
				paramlist.get(i).visit(this, null);
			}
		}
		program.getB().visit(this, null);
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		assignStatement.getVar().visit(this, null);
		assignStatement.getE().visit(this, null);
		if(assignStatement.getVar().getTypeName().isType(assignStatement.getE().getType())){
			boolean nouse = false;
		}else{
			throw new TypeCheckException("LValue and RValue type mismatch in assignment statement: " + assignStatement.getVar().getText());
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		Dec decofident = symtab.lookup(identX.getText());
		
		if(decofident != null){
			//identX.setindentLDec(decofident);
			identX.typedec=decofident;
			identX.setTypeName(Type.getTypeName(decofident.getFirstToken()));
		}else{
			throw new TypeCheckException("Idetifier declaration missing or not visible in current scope: " + identX.firstToken.getText());
		}
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(symtab.insert(paramDec.getIdent().getText(), paramDec)==false){
			throw new TypeCheckException("Variable redeclaration not permitted: " + paramDec.getIdent().getText());
		}else{
			paramDec.setTypeName(Type.getTypeName(paramDec.firstToken));
		}
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		// TODO Auto-generated method stub
		constantExpression.setType(TypeName.INTEGER); 
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(imageOpChain.firstToken.isKind(OP_WIDTH, OP_HEIGHT)){
			if(imageOpChain.getArg().getExprList().size()==0){
				imageOpChain.setTypeName(TypeName.INTEGER);			
			}else{
				throw new TypeCheckException("ImageOp Tuple not zero size: " + imageOpChain.firstToken.getText() + " at " + imageOpChain.firstToken.getLinePos());
			}
		}else if(imageOpChain.firstToken.isKind(KW_SCALE)){
			if(imageOpChain.getArg().getExprList().size()==1){
				imageOpChain.getArg().visit(this, null);
				imageOpChain.setTypeName(TypeName.IMAGE);			
			}else{
				throw new TypeCheckException("FramerOp Tuple not size = 2: " + imageOpChain.firstToken.getText() + " at " + imageOpChain.firstToken.getLinePos());
			}
		}
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		// TODO Auto-generated method stub
		int listsize = tuple.getExprList().size();
		if(listsize>0){
			List<Expression> exprlist = tuple.getExprList();
			for(int i=0;i<listsize;i++){
				exprlist.get(i).visit(this, null);
				if(exprlist.get(i).getType().isType(TypeName.INTEGER)){
					boolean nouse = false;
				}else{
					throw new TypeCheckException("Tuple Expression type should be integer: " + exprlist.get(i).getFirstToken().getText() + " at " + exprlist.get(i).getFirstToken().getLinePos());
				}
			}
		}

		return null;
	}


}
