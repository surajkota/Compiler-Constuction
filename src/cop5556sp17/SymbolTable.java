package cop5556sp17;



import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import cop5556sp17.AST.Dec;


public class SymbolTable {
	
	
	//TODO  add fields
	int currentscope, nextscope;
	public class category{
		int scopenum;
		Dec details;
		public category(int passscope, Dec passdetails){
			scopenum = passscope;
			details = passdetails;
		}
	}
	
	HashMap<String, LinkedList<category>> table;
	Stack<Integer> scope_stack;
	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		//TODO:  IMPLEMENT THIS
		currentscope = nextscope++;
		scope_stack.push(currentscope);
	}
	
	
	/**
	 * leaves scope
	 */
	public void leaveScope(){
		//TODO:  IMPLEMENT THIS
		scope_stack.pop();
		currentscope = scope_stack.peek();
	}
	
	public boolean insert(String ident, Dec dec){
		//TODO:  IMPLEMENT THIS
		if(table.containsKey(ident)){
			LinkedList<category> identfound = table.get(ident);
			boolean inornot = true;
			int listsize = identfound.size(), testscope=-1;
			for(int i=0;i<listsize;i++){
				testscope = identfound.get(i).scopenum;
				if(testscope == currentscope){
					inornot = false;
					break;
				}else if(testscope<currentscope){
					break;
				}
			}
			if(inornot){
				table.get(ident).add(new category(currentscope, dec));
			}
			return inornot;
		}else{
			LinkedList<category> identnotfound = new LinkedList<category>();
			identnotfound.add(new category(currentscope, dec));
			table.put(ident, identnotfound);
			return true;
		}
	}
	
	public Dec lookup(String ident){
		//TODO:  IMPLEMENT THIS
		Dec retd = null;
		if(table.containsKey(ident)){
			LinkedList<category> identfound = table.get(ident);
			
			int listsize = identfound.size(), testscope=-1;
			for(int i=0;i<listsize;i++){
				testscope = identfound.get(i).scopenum;
				if(scope_stack.contains(testscope)){
					retd = identfound.get(i).details;
					//break;
				}
			}
		}
		return retd;
	}
		
	public SymbolTable() {
		//TODO:  IMPLEMENT THIS
		nextscope = 1;
		currentscope = 0;
		scope_stack = new Stack<Integer>();
		scope_stack.push(0);
		table = new HashMap<String, LinkedList<category>>();
		
	}


	@Override
	public String toString() {
		//TODO:  IMPLEMENT THIS
		return table.toString();
	}
	
	


}
