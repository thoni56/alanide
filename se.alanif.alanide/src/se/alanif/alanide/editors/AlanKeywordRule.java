/*
 * Created on 2004-okt-09
 *
 */
package se.alanif.alanide.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

public class AlanKeywordRule extends WordRule {

	private StringBuffer fBuffer= new StringBuffer();
	
	public AlanKeywordRule(IWordDetector detector) {
		super(detector);
	}

    public AlanKeywordRule(IWordDetector detector, IToken defaultToken) {
        super(detector, defaultToken);
    }

	public IToken evaluate(ICharacterScanner scanner) {
		int c= scanner.read();
		if (fDetector.isWordStart((char) c)) {
			if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1)) {
				
				fBuffer.setLength(0);
				do {
					fBuffer.append(Character.toLowerCase((char) c));
					c= scanner.read();
				} while (c != ICharacterScanner.EOF && fDetector.isWordPart((char) c));
				scanner.unread();
				
				IToken token= (IToken) fWords.get(fBuffer.toString());
				if (token != null)
					return token;
					
				if (fDefaultToken.isUndefined())
					unreadBuffer(scanner);
					
				return fDefaultToken;
			}
		}
		
		scanner.unread();
		return Token.UNDEFINED;
	}
	
}
