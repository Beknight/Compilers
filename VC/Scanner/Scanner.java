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
			accept();
			return Token.LPAREN;
		case '.':
			// attempting to recognise a float

		case '|':
			accept();
			if (currentChar == '|') {
				accept();
				return Token.OROR;
			} else {
				return Token.ERROR;
			}
		case ')':
			accept();
			return Token.RPAREN;
		case '+':
			accept();
			return Token.PLUS;
		case '-':
			accept();
			return Token.MINUS;
		case ';':
			accept();
			return Token.SEMICOLON;
			// ....
		case SourceFile.eof:
			currentSpelling.append(Token.spell(Token.EOF));
			return Token.EOF;
		default:
			checkForLiteral();
			break;
		}
		accept();
		return Token.ERROR;
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

		// You must record the position of the current token somehow

		kind = nextToken();

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
			System.out.print("ES ||");
		}
		if (commentFound) {
			// skip eveyrthing until either EOF or * then /
			while(currentChar != sourceFile.eof && !checkForCommentEnd(isStar)){
				// check to make sure that it is not end of comment
				System.out.print(currentChar);
				accept();
			}
			System.out.println(" || EOC FOUND");
		}
		return commentFound;
	}

	private boolean ignoreWhiteSpaces() {
		boolean whiteSpaces = false;
		while (currentChar == ' ') {
			// check the current char is white
			accept();
			whiteSpaces = true;
			System.out.println("white space accepted");
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
				accept();
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
	private void checkForLiteral(){
		boolean tokenContinue = true;
		while(tokenContinue){
			//throw the current char into the dictionary 
			tokenContinue = dict.checkCar(currentChar);
			// if true, add to spelling
			if(tokenContinue){
				accept();	
				currentSpelling.append(currentChar);
			}
				
		}
		System.out.println(currentSpelling);
		// else check dictionary for token?
		dict.resetDictionary();
	}
	
	private int dictToToken(){
		int foundToken = 0;
		// convert the dictionary token to the proper token
		return foundToken;
		
	}
}
