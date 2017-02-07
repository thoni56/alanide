package se.alanif.alanide.editors.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.RGB;

import se.alanif.alanide.editors.AlanColorManager;
import se.alanif.alanide.preferences.IAlanPreferences;

public class AlanCommentScanner extends AbstractAlanScanner {

	public AlanCommentScanner(AlanColorManager manager) {
        colorManager = manager;
        
        RGB commentColor = preferenceColor(IAlanPreferences.COMMENT_COLOR_PREFERENCE);
        final IToken commentToken = new Token(new TextAttribute(colorManager.getColor(commentColor)));
        coloringTokens.put(IAlanPreferences.COMMENT_COLOR_PREFERENCE, commentToken);

        List<PatternRule> rules = new ArrayList<PatternRule>();
		// Add rule for comments
		rules.add(new EndOfLineRule("--", commentToken));

		IRule[] theRules = new IRule[rules.size()];
		rules.toArray(theRules);
		setRules(theRules);
	}
}
