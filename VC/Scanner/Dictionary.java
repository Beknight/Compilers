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
	private WordState dictState;
	private static char prevChar;
	public enum WordState{
		noChar,
		integerState,
		floatState,
		exponentState,
		keyword,
		variable,
		error,
		end
	}

	public enum SearchState{
		found,
		whiteSpace,
		notFound
	}
	
	public Dictionary(){
		dictState = WordState.noChar;
	}
	
	boolean checkForKeyWord(){
		
		return false;
	}
	
	boolean checkIntLiteral(char c){
		boolean isLiteral = false;
		// 1 or more digits
		if(c >= '0' && c <= '9'){
			isLiteral = true;
			updateStateIntFound(SearchState.found);
		}else if(c == '.'){
			isLiteral = true;
		}else{
		}

		prevChar = c;
		
		return isLiteral;
	}

	boolean updateStateIntFound( SearchState foundState){
		boolean isAccepted = false;
		//first character found is an integer, so if no error has to be start of an int/float/expo
		if((curState() == WordState.noChar || curState() == WordState.integerState || curState() == WordState.floatState) && foundState == SearchState.found){
			setState(WordState.integerState);
			isAccepted = true;
		}
		return isAccepted;
	}
	
	boolean updateStateDotFound(){
		boolean isAccepted = false;
		if(curState() == WordState.noChar || curState() == WordState.integerState){
			isAccepted = true;
			setState(WordState.floatState);
		}
		return isAccepted;
	}
	
	WordState curState(){
		return dictState;
	}
	private void setState(WordState state){
		dictState = state;
	}
}
