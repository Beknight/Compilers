package Scanner;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author benjamin Pass in characters to check if they belong to certain
 *         literals, key words, or are just normal variables
 * 
 */

public class Dictionary {

	private static char[] TRUE_ARRAY  = {'t','r','u','e'};
	private static char[] FALSE_ARRAY = {'f','a','l','s','e'};
	private WordState dictState;
	private KeyWordSearch keyWordState;
	private static char prevChar;
	private LinkedList<Point2D> rowCol;
	private LinkedList<char[]> keyWordList;
	private LinkedList<Integer> matchList;

	public enum WordState {
		noChar, integerState, floatState, exponentState, keyword, variable, error, end
	}

	public enum SearchState {
		found, whiteSpace, notFound
	}

	public enum KeyWordSearch {
		set, started, found, none
	}

	public Dictionary() {
		// initialise the linked lists
		rowCol = new LinkedList<Point2D>();
		keyWordList = new LinkedList<char[]>();
		matchList = new LinkedList<Integer>();
		// setup the rest of the fields
		resetDictionary();
		addWordsToList();
	}

	boolean checkForKeyWord(char c) {
		//
		if (keyWordState == KeyWordSearch.set) {
			// search through all of the lists for the first char
			for (int i = 0; i < keyWordList.size(); i++) {
				// for every first letter word, we add a new row col
				char curChar = keyWordList.get(i)[0];
				if (curChar == c) {
					// add the row and col into the next letter
					rowCol.add(new Point2D.Float(i, 0));
					keyWordState = KeyWordSearch.started;
				}
			}
		} else if (keyWordState == KeyWordSearch.started) {
			// for all the row cols, check the next letter
			for (int i = 0; i < rowCol.size(); i++) {
				int wordIndex = (int) rowCol.get(i).getX();
				int letterIndex = (int) rowCol.get(i).getY();
				char[] curWord = keyWordList.get(wordIndex);
		
				if (letterIndex + 1 < curWord.length
						&& c == curWord[letterIndex + 1]) {
//					System.out.println("key letter found: " + letterIndex + " "
//							+ new String(keyWordList.get(wordIndex)));
					rowCol.get(i).setLocation(wordIndex, letterIndex + 1);
					// if match found, add to matchList
					if ((letterIndex + 1) == (curWord.length - 1)) {
						matchList.add(wordIndex);
						keyWordState = KeyWordSearch.found;
					}
				} else {
					rowCol.remove(i);
					// check if in match list, if so remove it.
					if (matchList.contains(wordIndex)) {
						matchList.remove(i);
						keyWordState = KeyWordSearch.started;
					}
				}
			}
		}
		return false;
	}

	public void resetDictionary() {
		setState(WordState.noChar);
		keyWordState = KeyWordSearch.set;
		rowCol.clear();
		matchList.clear();
	}

	boolean checkCar(char c) {
		boolean isLiteral = false;
		// 1 or more digits
		if (c >= '0' && c <= '9') {
			isLiteral = updateStateIntFound();
		} else if (c == '.') {
			isLiteral = updateStateDotFound();
		} else if (c == ' ') {
			isLiteral = updateStateWhiteSpaceFound();
		} else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '_')) {
			if((c == 'E' || c == 'e' )){
				isLiteral = updateEFound();
			}if(!isLiteral){
				isLiteral = updateStateAlphaFound();
			}
		} else if(c == '+' || c == '-'){
			isLiteral = updateSignFound();
		}
		if(isLiteral){
			checkForKeyWord(c);
		}else {
			// do an error check here
		}
			keyWordState = KeyWordSearch.started;
		prevChar = c;

		return isLiteral;
	}
	
	boolean updateSignFound(){
		boolean isAccepted = false;
		if(curState() == WordState.exponentState){
			isAccepted = true;
		}
		return isAccepted;
	}
	
	boolean updateEFound(){
		boolean isAccepted = false;
		// this is 
		if (curState() == WordState.floatState){
			isAccepted = true;
			setState(WordState.exponentState);
		}
		return isAccepted;
	}

	boolean updateStateIntFound() {
		boolean isAccepted = false;
		// first character found is an integer, so if no error has to be start
		// of an int/float/expo
		if ((curState() == WordState.noChar
				|| curState() == WordState.integerState || curState() == WordState.floatState || curState() == WordState.exponentState)) {
			if(curState() == WordState.noChar){
				setState(WordState.integerState);
			}else{
				setState(curState());
			}
			isAccepted = true;
		} else if (curState() == WordState.variable) {
			setState(WordState.variable);
			isAccepted = true;
		} else {
			System.out.println("error");
			isAccepted = false;
			setState(WordState.error);
		}
		return isAccepted;
	}

	boolean updateStateDotFound() {
		boolean isAccepted = false;
		if (curState() == WordState.noChar
				|| curState() == WordState.integerState) {
			isAccepted = true;
			setState(WordState.floatState);
		} else {
			isAccepted = false;
			setState(WordState.error);
		}
		return isAccepted;
	}

	boolean updateStateWhiteSpaceFound() {
		boolean isAccepted = false;
		return isAccepted;
	}

	boolean updateStateAlphaFound() {
		boolean isAccepted = false;
		if (curState() == WordState.noChar || curState() == WordState.variable) {
			isAccepted = true;
			setState(WordState.variable);
		}else{
			isAccepted = false;
			setState(WordState.error);
		}
		return isAccepted;
	}

	WordState curState() {
		return dictState;
	}

	private void setState(WordState state) {
		dictState = state;
	}

	public int convertToToken() {
		int curToken = 0;
		if (curState() == WordState.integerState) {
			curToken = Token.INTLITERAL;
		} else if (curState() == WordState.floatState || curState() == WordState.exponentState) {
			curToken = Token.FLOATLITERAL;
		} else if (curState() == WordState.variable) {
			curToken = Token.ID;
			if(matchList.size() == 1){
				curToken = Token.BOOLEANLITERAL;
			}
		}else if(curState() == WordState.error){
			curToken = Token.ERROR;
		}
	
		return curToken;
	}

	private void addWordsToList() {
		keyWordList.add(TRUE_ARRAY);
		keyWordList.add(FALSE_ARRAY);
	}
	
	boolean checkFloat(String floatString){
		char[] floatLiteral = floatString.toCharArray();
		boolean isGood = false;
		int eCount = 0;
		int opCount = 0;
		for(int i = 0; i < floatLiteral.length; i++){
			char curC = floatLiteral[i];
			if(curC == 'e' || curC == 'E'){
				eCount++;
			}else if(curC == '+' || curC == '-'){
				opCount++;
			}
		}
		if(eCount <= 1 && opCount <=1){
			isGood = true;
		}else {
			isGood = false;
		}
		if(floatLiteral[floatLiteral.length - 1] == 'e'){
			isGood = false;
		}
		
		return isGood;
	}
}
