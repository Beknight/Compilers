/**
 **	Scanner.java                        
 **/

package Scanner;

import VC.ErrorReporter;

public final class Scanner {

	private SourceFile sourceFile;
	private boolean debug;

	private ErrorReporter errorReporter;
	private StringBuffer currentSpelling;
	private char currentChar;
	private SourcePosition sourcePos;
	private Dictionary dict;
	private TextCount counter;
	// =========================================================

	public Scanner(SourceFile source, ErrorReporter reporter) {
		sourceFile = source;
		errorReporter = reporter;
		currentChar = sourceFile.getNextChar();
		debug = false;
		dict = new Dictionary();
		counter = new TextCount();
	}

	public void enableDebugging() {
		debug = true;
	}

	// accept gets the next character from the source program.

	private void accept() {
		if(currentChar == '\r' || currentChar == '\n'){
			counter.incrementLineCounter();
			counter.resetColumn();
		}else{
			counter.incrementColumnCounter();
		}
		currentChar = sourceFile.getNextChar();

		// you may save the lexeme of the current token incrementally here
		// you may also increment your line and column counters here
	}

	// inspectChar returns the n-th character after currentChar
	// in the input stream.
	//
	// If there are fewer than nthChar characters between currentChar
	// and the end of file marker, SourceFile.eof is returned.
	//
	// Both currentChar and the current position in the input stream
	// are *not* changed. Therefore, a subsequent call to accept()
	// will always return the next char after currentChar.

	private char inspectChar(int nthChar) {
		return sourceFile.inspectChar(nthChar);
	}

	private int nextToken() {
		// Tokens: separators, operators, literals, identifiers and keyworods
		switch (currentChar) {
		// separators
		case '(':
			addCharToString();
			accept();
			return Token.LPAREN;
		case '|':
			accept();
			if (currentChar == '|') {
				addCharToString();
				accept();
				return Token.OROR;
			} else {
				return Token.ERROR;
			}
		case ')':
			addCharToString();
			accept();
			return Token.RPAREN;
		case '*':
			addCharToString();
			accept();
			return Token.MULT;
		case '+':
			addCharToString();
			accept();
			return Token.PLUS;
		case '-':
			addCharToString();
			accept();
			return Token.MINUS;
		case ';':
			addCharToString();
			accept();
			return Token.SEMICOLON;
		case SourceFile.eof:
			currentSpelling.append(Token.spell(Token.EOF));
			accept();
//			counter.incrementColumnCounter();
			return Token.EOF;
		case ',':
			addCharToString();
			accept();
			return Token.COMMA;
		case '/':
			addCharToString();
			accept();
			return Token.DIV;
		case '<':
			addCharToString();
			accept();
			return Token.LT;
		case '>' :
			addCharToString();
			accept();
			return Token.GT;
		case '=':
			addCharToString();
			accept();
			return Token.EQ;
		default:
			int token = checkForLiteral();
//			accept();
			return token;
		}
	}

	private void addCharToString(){
		currentSpelling.append(currentChar);
	}
	
	void skipSpaceAndComments() {
		// needs to find the start of the next token
		boolean skippableFound = true;
		while(skippableFound){
			switch (currentChar) {
			case ' ':
				// check for space
				skippableFound = ignoreWhiteSpaces();
				break;
			// check for slash
			case '/':
				// inspect the next char
				skippableFound = checkForCommentChars();
				break;
			case '\n':
				skippableFound = true;
				accept();
				break;
			case '\r':
				skippableFound = true;
				accept();
				break;
			default:
				skippableFound = false;
					break;
			}
		}
	}

	public Token getToken() {
		Token tok;
		int kind;

		// skip white space and comments
		skipSpaceAndComments();

		currentSpelling = new StringBuffer("");

		sourcePos = new SourcePosition();
		sourcePos.lineStart = counter.getLineCount();
		sourcePos.charStart = counter.getColumnCount();
		// You must record the position of the current token somehow

		kind = nextToken();

		sourcePos.lineFinish = counter.getLineCount();
		sourcePos.charFinish = counter.getColumnCount() - 1;
		tok = new Token(kind, currentSpelling.toString(), sourcePos);

		// * do not remove these three lines
		if (debug)
			System.out.println(tok);
		return tok;
	}

	private boolean checkForCommentChars() {
		// check that the next char is etiher a star or a slash
		boolean commentFound = false;
		boolean isStar = false;
		boolean endOfComment = false;
		
		if (sourceFile.inspectChar(1) == '/') {
			commentFound = true;
		} else if (sourceFile.inspectChar(1) == '*') {
			commentFound = true;
			isStar = true;
		}
		if(commentFound){
			acceptMultiple(2);
		}
		if (commentFound) {
			// skip eveyrthing until either EOF or * then /
			while(currentChar != sourceFile.eof && !checkForCommentEnd(isStar)){
				// check to make sure that it is not end of comment
				accept();
			}
		}
		return commentFound;
	}

	private boolean ignoreWhiteSpaces() {
		boolean whiteSpaces = false;
		while (currentChar == ' ') {
			// check the current char is white
			accept();
			whiteSpaces = true;
		}
		return whiteSpaces;
	}

	private boolean checkForCommentEnd(boolean isStar) {
		char nextChar = ' ';
		boolean endCommentFound = false;
		if(isStar){
			nextChar = sourceFile.inspectChar(1);
			if (currentChar == '*' && nextChar == '/') {
				endCommentFound = true;
				acceptMultiple(2);
			}
		}else{
			if(currentChar == '\n' || currentChar == '\r'){
				endCommentFound = true;
				accept();
			}
		}
		return endCommentFound;
	}
	private void acceptMultiple(int n){
		int count = 0;
		while(count < n){
			accept();
			count++;
		}
	}
	private int checkForLiteral(){
		boolean tokenContinue = true;
		int token = -1;
		while(tokenContinue){
			//throw the current char into the dictionary 
			tokenContinue = dict.checkCar(currentChar);
			// if true, add to spelling
			if(tokenContinue){
				currentSpelling.append(currentChar);
				accept();	
			}
		}
		
		token = dictToToken();
		// else check dictionary for token?
		dict.resetDictionary();
		return token;
	}
	
	private int dictToToken(){
		int foundToken = 0;
		foundToken = dict.convertToToken();
		if(foundToken == Token.FLOATLITERAL){
			if(!dict.checkFloat(currentSpelling.toString())){
				foundToken = Token.ERROR;
			}
		}
		// get the token from the dictionary
		return foundToken;
		
	}
}
