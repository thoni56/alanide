/*
 * Created on 2004-okt-09
 *
 */
package se.alanif.alanide.editors;

import org.eclipse.jface.text.rules.IWordDetector;

public class AlanWordDetector implements IWordDetector {
	public boolean isWordStart(char c) {
		return Character.isLetter(c);
	}
	public boolean isWordPart(char c) {
		return Character.isJavaIdentifierPart(c);
	}
}
