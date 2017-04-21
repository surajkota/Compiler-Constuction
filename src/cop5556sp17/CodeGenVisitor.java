package cop5556sp17;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import static cop5556sp17.AST.Type.TypeName.FRAME;
import static cop5556sp17.AST.Type.TypeName.IMAGE;
import static cop5556sp17.AST.Type.TypeName.URL;
import static cop5556sp17.Scanner.Kind.*;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
		argslot = 0;
		slot_num = 1;
		localdecs = new ArrayList<decvariables>();
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction
	int argslot;
	int slot_num;

	public class decvariables{
		public decvariables(Dec consd, Label consslabel, Label conselabel, int consslot){
			this.d = consd;
			this.startlabel = consslabel;
			this.endlabel = conselabel;
			this.decslot = consslot;
		}
		Dec d;
		Label startlabel, endlabel;
		int decslot;
	}
	
	ArrayList<decvariables> localdecs;
	FieldVisitor fv;   

	
	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec dec : params)
			dec.visit(this, mv);
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
		//TODO  visit the local variables
		for(decvariables dec: localdecs) {
			//TODO this is wrong
			mv.visitLocalVariable(dec.d.getIdent().getText(), dec.d.getTypeName().getJVMTypeDesc(), null, dec.startlabel, dec.endlabel, dec.decslot);
		}

		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method
		
		
		cw.visitEnd();//end of class
		
		//generate classfile and return it
		return cw.toByteArray();
	}



	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getType());
		assignStatement.getVar().visit(this, arg);
		return null;
	}
	
	public class chaininfoexchange{
		Kind nodekind;
		String lhsrhs;
	}
	
	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		chaininfoexchange exl = new chaininfoexchange();
		Chain Lchain = binaryChain.getE0();
		exl.lhsrhs = "left";
		exl.nodekind = binaryChain.getArrow().kind;
		//TODO visit before or after checking conditions
		Lchain.visit(this, exl);
		
		if(Lchain.getTypeName().equals(TypeName.URL)){
			//mv.visitInsn(ACONST_NULL);
			//mv.visitInsn(SWAP);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
			
		}else if(Lchain.getTypeName().equals(TypeName.FILE)){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
		}
		
		chaininfoexchange exr = new chaininfoexchange();
		Chain Rchain = binaryChain.getE1();
		exr.lhsrhs = "right";
		exr.nodekind = binaryChain.getArrow().kind;
		//TODO visit before or after checking conditions
		Rchain.visit(this, exr);
		/*
		if (Rchain.getTypeName().equals(TypeName.URL)){
            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
        } else if (Rchain.getTypeName().equals(TypeName.FILE)) {
            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
        } else {
        	//TODO nothing comes here
        }*/
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
      //TODO  Implement this
		binaryExpression.getE0().visit(this, arg);//pushes E1,E0 on top of stack
		binaryExpression.getE1().visit(this, arg);
		Label conditionyes=new Label();
		Label endexpr=new Label();
		if(binaryExpression.getOp().isKind(DIV)){
			if(binaryExpression.getType().equals(TypeName.IMAGE)){
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig, false);
			}else{
				mv.visitInsn(IDIV);
			}
		}else if(binaryExpression.getOp().isKind(TIMES)){
			if(binaryExpression.getType().equals(TypeName.IMAGE)){
				//TODO handle commutativity
				if(binaryExpression.getE0().getType().equals(TypeName.INTEGER)){
					mv.visitInsn(SWAP);
				}
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
			}else{
				mv.visitInsn(IMUL);
			}
		}else if(binaryExpression.getOp().isKind(PLUS)){
			if(binaryExpression.getType().equals(TypeName.IMAGE)){
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", PLPRuntimeImageOps.addSig, false);
			}else{
				mv.visitInsn(IADD);
			}
		}else if(binaryExpression.getOp().isKind(MINUS)){
			if(binaryExpression.getType().equals(TypeName.IMAGE)){
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", PLPRuntimeImageOps.subSig, false);
			}else{
				mv.visitInsn(ISUB);
			}
		}else if(binaryExpression.getOp().isKind(MOD)){
			if(binaryExpression.getType().equals(TypeName.IMAGE)){
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig, false);
			}else{
				mv.visitInsn(IREM);
			}
		}else if(binaryExpression.getOp().isKind(GE)){
			mv.visitJumpInsn(IF_ICMPGE,conditionyes);
			mv.visitInsn(ICONST_0);
		}else if(binaryExpression.getOp().isKind(GT)){
			mv.visitJumpInsn(IF_ICMPGT,conditionyes);
			mv.visitInsn(ICONST_0);
		}else if(binaryExpression.getOp().isKind(LT)){
			mv.visitJumpInsn(IF_ICMPLT,conditionyes);
			mv.visitInsn(ICONST_0);
		}else if(binaryExpression.getOp().isKind(LE)){
			mv.visitJumpInsn(IF_ICMPLE,conditionyes);
			mv.visitInsn(ICONST_0);
		}else if(binaryExpression.getOp().isKind(EQUAL)){
			mv.visitJumpInsn(IF_ICMPEQ,conditionyes);
			mv.visitInsn(ICONST_0);
		}else if(binaryExpression.getOp().isKind(NOTEQUAL)){
			mv.visitJumpInsn(IF_ICMPNE,conditionyes);
   		 	mv.visitInsn(ICONST_0);
   		}else if(binaryExpression.getOp().isKind(OR)){
			mv.visitInsn(IOR);
		}else if(binaryExpression.getOp().isKind(AND)){
			mv.visitInsn(IAND);
		}
		mv.visitJumpInsn(GOTO,endexpr);
		mv.visitLabel(conditionyes);
		mv.visitInsn(ICONST_1);
		mv.visitLabel(endexpr);
		
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		//TODO  Implement this
		Label startscope = new Label();
		mv.visitLabel(startscope);
		Label endscope = new Label();
		for(Dec dec : block.getDecs()){
			dec.visit(this, arg);
			localdecs.add(new decvariables(dec, startscope, endscope, dec.getslot()));
		}
		
		for(Statement stmt : block.getStatements()){
			//TODO check
			if(stmt instanceof AssignmentStatement){
				if(((AssignmentStatement) stmt).getVar().typedec instanceof ParamDec)
					mv.visitVarInsn(ALOAD, 0);
			}
			stmt.visit(this, arg);
			if (stmt instanceof Chain || stmt instanceof BinaryChain){
		  		mv.visitInsn(POP);
		  	} 
			
		}
		
		
		
		mv.visitLabel(endscope);
		/*for(Dec dec: block.getDecs()) {
			//TODO this is wrong
		//	mv.visitLocalVariable(dec.getIdent().getText(), dec.getTypeName().getJVMTypeDesc(), null, startscope, endscope, dec.getslot());
		}*/
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		//TODO Implement this
		mv.visitLdcInsn(booleanLitExpression.getValue() );
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		//assert false : "not yet implemented";
		if(constantExpression.getFirstToken().isKind(Kind.KW_SCREENWIDTH)){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth", PLPRuntimeFrame.getScreenWidthSig, false);
		}else if(constantExpression.getFirstToken().isKind(Kind.KW_SCREENHEIGHT)){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight", PLPRuntimeFrame.getScreenHeightSig, false);
		}
		
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		//TODO Implement this
		declaration.setslot(slot_num++);
		if(declaration.getTypeName().isType(TypeName.IMAGE, TypeName.FRAME)){
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, declaration.getslot());
		}
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		String toinvoke="";
		if(filterOpChain.getFirstToken().kind == Kind.OP_BLUR){
			toinvoke = "blurOp";
		}
		else if(filterOpChain.getFirstToken().kind == Kind.OP_GRAY){
			toinvoke = "grayOp";
		}
		else if(filterOpChain.getFirstToken().kind == Kind.OP_CONVOLVE){
			toinvoke = "convolveOp";
		}
		//TODO Random from discussion, how to invoke correctly ?
		chaininfoexchange ex = (chaininfoexchange) arg;
		
		if(filterOpChain.getFirstToken().isKind(OP_GRAY) && ex.lhsrhs.equals("right")){
			mv.visitInsn(DUP);
		}else{
			mv.visitInsn(ACONST_NULL);
		}
		//mv.visitInsn(ACONST_NULL); 
		mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, toinvoke, PLPRuntimeFilterOps.opSig, false);
		//mv.visitInsn(DUP);
		//TODO verify
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		String toinvoke = "";
		String methoddesc = "";
		if (frameOpChain.getFirstToken().kind == Kind.KW_SHOW){
			toinvoke = "showImage";
			methoddesc = PLPRuntimeFrame.showImageDesc;
		}else if (frameOpChain.getFirstToken().kind == Kind.KW_HIDE){
			toinvoke = "hideImage";
			methoddesc = PLPRuntimeFrame.hideImageDesc;
		}else if (frameOpChain.getFirstToken().kind == Kind.KW_XLOC){
			toinvoke = "getXVal";
			methoddesc = PLPRuntimeFrame.getXValDesc;
		}else if (frameOpChain.getFirstToken().kind == Kind.KW_YLOC){
			toinvoke = "getYVal";
			methoddesc = PLPRuntimeFrame.getYValDesc;
		}else if (frameOpChain.getFirstToken().kind == Kind.KW_MOVE){
			frameOpChain.getArg().visit(this, arg);
			toinvoke = "moveFrame";
			methoddesc = PLPRuntimeFrame.moveFrameDesc;
		}
		mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, toinvoke, methoddesc, false);
		//mv.visitInsn(DUP); 
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		//TODO check for getfield
		chaininfoexchange ex = (chaininfoexchange) arg;
		if(ex.lhsrhs.equals("left")){
				if(identChain.typedec instanceof ParamDec){
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, identChain.getFirstToken().getText(), identChain.typedec.getTypeName().getJVMTypeDesc());
				}else if(identChain.typedec.getTypeName().equals(TypeName.BOOLEAN) || identChain.typedec.getTypeName().equals(TypeName.INTEGER)){
					mv.visitVarInsn(ILOAD, identChain.typedec.getslot());
				}else{
					mv.visitVarInsn(ALOAD, identChain.typedec.getslot());					
				}
/*			}else if (identChain.typedec.getTypeName().equals(TypeName.IMAGE) || identChain.typedec.getTypeName().equals(TypeName.FRAME)){
				
			}else if (identChain.typedec.getTypeName().equals(TypeName.FILE) || identChain.typedec.getTypeName().equals(TypeName.URL)){
				mv.visitVarInsn(ALOAD, 0);

				mv.visitFieldInsn(GETFIELD, className, identChain.getFirstToken().getText(), identChain.typedec.getTypeName().getJVMTypeDesc());			
			}*/
		}else{
			/*if(identChain.typedec.getTypeName().equals(TypeName.IMAGE) || identChain.typedec.getTypeName().equals(TypeName.INTEGER)){
				if(identChain.typedec.getTypeName().isType(TypeName.IMAGE)){
					//mv.visitVarInsn(ALOAD, identChain.typedec.getslot());
                    //mv.visitInsn(SWAP);
					//TODO handle image paramdec
                    mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "copyImage", PLPRuntimeImageOps.copyImageSig, false);
                    mv.visitVarInsn(ASTORE, identChain.typedec.getslot());
                    mv.visitVarInsn(ALOAD, identChain.typedec.getslot());
				}else{
					if(identChain.typedec instanceof ParamDec){
						mv.visitVarInsn(ALOAD, 0);
						mv.visitInsn(SWAP);
						mv.visitFieldInsn(PUTFIELD, className, identChain.getFirstToken().getText(), identChain.typedec.getTypeName().getJVMTypeDesc());
					}else{
						if(identChain.typedec.getTypeName().isType(TypeName.BOOLEAN, TypeName.INTEGER)){
	                        mv.visitVarInsn(ISTORE, identChain.typedec.getslot());
	                    }else{
	                        mv.visitVarInsn(ASTORE, identChain.typedec.getslot());
	                    }
						//mv.visitVarInsn(ISTORE, identChain.typedec.getslot());
					}
				}*/
			if(identChain.typedec.getTypeName().isType(TypeName.INTEGER, TypeName.BOOLEAN)){
				mv.visitInsn(DUP);
				//TODO Check if swap is needed
				if(identChain.typedec instanceof ParamDec){
					mv.visitVarInsn(ALOAD, 0);
					//TODO check if swap is required
					//mv.visitInsn(SWAP);
					mv.visitFieldInsn(PUTFIELD, className, identChain.getFirstToken().getText(), identChain.typedec.getTypeName().getJVMTypeDesc());
                 }else{
                     mv.visitVarInsn(ISTORE, identChain.typedec.getslot());
                     //mv.visitVarInsn(ALOAD, identChain.typedec.getslot());
                 }
					//mv.visitVarInsn(ISTORE, identChain.typedec.getslot());
			}else if(identChain.typedec.getTypeName().equals(TypeName.IMAGE)){
				//TODO commected copy check
				//mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "copyImage", PLPRuntimeImageOps.copyImageSig, false);
                mv.visitVarInsn(ASTORE, identChain.typedec.getslot());
				mv.visitVarInsn(ALOAD, identChain.typedec.getslot());
			}else if(identChain.typedec.getTypeName().equals(TypeName.FILE)){
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, identChain.getFirstToken().getText(),identChain.typedec.getTypeName().getJVMTypeDesc());
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write", PLPRuntimeImageIO.writeImageDesc, false);
				//TODO check if lines required here
			}else if(identChain.typedec.getTypeName().equals(TypeName.FRAME)){
				//mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, identChain.typedec.getslot());
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
				mv.visitVarInsn(ASTORE, identChain.typedec.getslot());
				mv.visitVarInsn(ALOAD, identChain.typedec.getslot());
			}
		}
		//mv.visitInsn(DUP);
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		//TODO Implement this
		if(identExpression.typedec instanceof ParamDec){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, identExpression.getFirstToken().getText(), identExpression.getType().getJVMTypeDesc());
		}else if(identExpression.typedec.getTypeName().isType(TypeName.INTEGER, TypeName.BOOLEAN)){
			mv.visitVarInsn(ILOAD, identExpression.typedec.getslot());
		}else{
			mv.visitVarInsn(ALOAD, identExpression.typedec.getslot());
		}
					
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		//TODO Implement this
		if(identX.typedec.getTypeName().isType(TypeName.IMAGE)){
			   mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
			   //TODO changed ???
			   mv.visitVarInsn(ASTORE, identX.typedec.getslot());
		}else{
			if(identX.typedec instanceof ParamDec){
			//TODO Is it correct?
				//mv.visitVarInsn(ALOAD, 0);
				//mv.visitInsn(SWAP);
				mv.visitFieldInsn(PUTFIELD, className, identX.getText(), identX.typedec.getTypeName().getJVMTypeDesc());
			}else if (identX.typedec.getTypeName().isType(TypeName.INTEGER, TypeName.BOOLEAN)){
				mv.visitVarInsn(ISTORE, identX.typedec.getslot());	
				}else{
					mv.visitVarInsn(ASTORE, identX.typedec.getslot());
				}
		}
		return null;

	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		//TODO Implement this
		ifStatement.getE().visit(this, arg);
		Label conditioniseq = new Label();
		mv.visitJumpInsn(IFEQ, conditioniseq);
		Label startlb = new Label();
		mv.visitLabel(startlb);
		ifStatement.getB().visit(this, arg);
		mv.visitLabel(conditioniseq);
		Label endlb = new Label();
		mv.visitLabel(endlb);
		
		/*for(Dec dec: ifStatement.getB().getDecs()) {
			//TODO this is wrong
			mv.visitLocalVariable(dec.getIdent().getText(), dec.getTypeName().getJVMTypeDesc(), null, startlb, endlb, dec.getslot());
		}*/
		
		/*
		 Label l1 = new Label();
         Label l2 = new Label();
         mv.visitLabel(l1);
         ifStatement.getE().visit(this, arg);
         mv.visitJumpInsn(IFEQ, l2);
         mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
         visitBlock(ifStatement.getB(), arg);
         mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
         mv.visitLabel(l2);      
 
         for (int i = 0; i < ifStatement.getB().getDecs().size(); i++) {
                 Dec dec = ifStatement.getB().getDecs().get(i);
                 mv.visitLocalVariable(dec.getIdent().getText(), dec.getTypeName().getJVMTypeDesc(), null, l1, l2, dec.getslot());
         }*/

		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		
		if(imageOpChain.getFirstToken().isKind(KW_SCALE)){
			imageOpChain.getArg().visit(this, arg);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", PLPRuntimeImageOps.scaleSig, false);
		}else if(imageOpChain.getFirstToken().isKind(OP_WIDTH)){
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getWidth", PLPRuntimeImageOps.getWidthSig, false);
		}else if(imageOpChain.getFirstToken().isKind(OP_HEIGHT)){
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getHeight", PLPRuntimeImageOps.getHeightSig, false);
		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		//TODO Implement this
		mv.visitLdcInsn(intLitExpression.value);//BIPUSH?????
		return null;
	}


	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		//TODO Implement this
		//For assignment 5, only needs to handle integers and booleans
		//paramDec.getIdent().getText();
		//paramDec.getTypeName().getJVMTypeDesc();
		FieldVisitor localfv = cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), paramDec.getTypeName().getJVMTypeDesc(), null, null);
		localfv.visitEnd();
		
		//mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
		//mv.visitFieldInsn(PUTFIELD, "Name", "x", "I");
		if(paramDec.getTypeName().equals(TypeName.INTEGER)){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(argslot++);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false); 
			//fv = cw.visitField( ACC_PUBLIC, paramDec.getIdent().getText(), "I", null, null);
			mv.visitFieldInsn(PUTFIELD,className, paramDec.getIdent().getText(), "I");
		}else if(paramDec.getTypeName().equals(TypeName.BOOLEAN)){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(argslot++);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitFieldInsn(PUTFIELD,className, paramDec.getIdent().getText(), "Z");
		}else if(paramDec.getTypeName().equals(TypeName.FILE)){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitTypeInsn(NEW, "java/io/File");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(argslot++);
			mv.visitInsn(AALOAD);
			
			mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), paramDec.getTypeName().getJVMTypeDesc());
		}else if(paramDec.getTypeName().equals(TypeName.URL)){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(argslot++);
			//mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig,false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), paramDec.getTypeName().getJVMTypeDesc());
			
		}
		//localfv.visitEnd(); //double visitend no
		//TODO confirm this
		return null;

	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		//assert false : "not yet implemented";
		sleepStatement.getE().visit(this, arg);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		//assert false : "not yet implemented";
		List<Expression> tuplelist  = tuple.getExprList();
		for(Expression expr : tuplelist){
			expr.visit(this, arg);
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		//TODO Implement this
		
		Label startlb = new Label();
		mv.visitLabel(startlb);
		Label guard = new Label();
		Label body = new Label();
		mv.visitJumpInsn(GOTO, guard);
		
		mv.visitLabel(body);
		
		whileStatement.getB().visit(this, arg);
		mv.visitLabel(guard);
		whileStatement.getE().visit(this, arg);
		mv.visitJumpInsn(IFNE, body);
		Label endlb = new Label();
		mv.visitLabel(endlb);
		/*for(Dec dec: whileStatement.getB().getDecs()) {
			//TODO this is wrong
			mv.visitLocalVariable(dec.getIdent().getText(), dec.getTypeName().getJVMTypeDesc(), null, startlb, endlb, dec.getslot());
		}*/
		return null;
	}

}
