package Scanner;

/**
 * 
 * @author benjamin
 *Pass in characters to check if they belong to certain literals, key words, or are just normal variables
 *
 */

public class Dictionary {
	
	private static char[] INT_ARRAY = {'i', 'n','t'};
	private static char[] BOOLEAN_ARRAY = {'b','o','o','l','e','a','n'};
	private static char[] FLOAT_ARRAY = {'f','l','o','a','t'};
	private static char[] VOID_ARRAY = {'v','o','i','d'};
	private static char[] IF_ARRAY =  {'i','f'};
	private static char[] ELSE_ARRAY = {'e','l','s','e'};
	private static char[] FOR_ARRAY = {'f','o','r'};
	private static char[] WHILE_ARRAY = {'w','h','i','l','e'};
	private static char[] BREAK_ARRAY = {'b','r','e','a','k'};
	private static char[] CONTINUE_ARRAY = {'c','o','n','t','i','n','u','e'};
	private static char[] RETURN_ARRAY = {'r','e','t','u','r','n'};
	private int state;
	private boolean keyWordState;
	private static char prevChar;
	enum literalState{
		noChar,
		integerState,
		floatState,
		exponentState,
		keyword,
		id
	}
	enum wordId{
		
	}
	
	public Dictionary(){

	}
	
	boolean checkForKeyWord(){
		
		return false;
	}
	
	boolean checkForLiteral(char c){
		boolean isLiteral = false;
		// 1 or more digits
		if(c >= '0' && c <= '9'){
			isLiteral = true;
		}
		prevChar = c;
		return isLiteral;
	}
}
