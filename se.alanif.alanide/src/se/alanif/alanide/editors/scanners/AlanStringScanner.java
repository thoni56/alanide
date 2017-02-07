package se.alanif.alanide.editors.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import se.alanif.alanide.editors.AlanColorManager;
import se.alanif.alanide.preferences.IAlanPreferences;

public class AlanStringScanner extends AbstractAlanScanner {

	public AlanStringScanner(AlanColorManager manager) {
        colorManager = manager;
        final RGB preferenceColor = AbstractAlanScanner.preferenceColor(IAlanPreferences.STRING_COLOR_PREFERENCE);
        Color color = colorManager.getColor(preferenceColor);
        final IToken stringToken = new Token(new TextAttribute(color));
        setDefaultReturnToken(stringToken);
        coloringTokens.put(IAlanPreferences.STRING_COLOR_PREFERENCE, stringToken);

		List<PatternRule> rules = new ArrayList<PatternRule>();

		// Add rule for double quoted strings
		rules.add(new MultiLineRule("\"", "\"", stringToken));

		IRule[] theRules = new IRule[rules.size()];
		rules.toArray(theRules);
		setRules(theRules);
	}

}
