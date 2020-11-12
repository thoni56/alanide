package se.alanif.alanide.editors.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

import se.alanif.alanide.editors.AlanColorManager;
import se.alanif.alanide.editors.AlanKeywordRule;
import se.alanif.alanide.editors.AlanWhitespaceDetector;
import se.alanif.alanide.editors.AlanWordDetector;
import se.alanif.alanide.preferences.IAlanPreferences;

public class AlanCodeScanner extends AbstractAlanScanner {

	public static final String keywords[] = {
        "actor",
        "add",
        "after",
        "an",
        "and",
        "are",
        "article",
        "at",
        "attributes",
        "before",
        "between",
        "by",
        "can",
        "cancel",
        "character",
        "characters",
        "check",
        "container",
        "contains",
        "count",
        "current",
        "decrease",
        "definite",
        "depend",
        "depending",
        "describe",
        "description",
        "directly",
        "do",
        "does",
        "each",
        "else",
        "elsif",
        "empty",
        "end",
        "entered",
        "event",
        "every",
        "exclude",
        "exit",
        "extract",
        "first",
        "for",
        "form",
        "from",
        "has",
        "header",
        "here",
        "if",
        "import",
        "in",
        "include",
        "increase",
        "indefinite",
        "indirectly",
        "initialize",
        "into",
        "is",
        "isa",
        "it",
        "last",
        "limits",
        "list",
        "locate",
        "location",
        "look",
        "make",
        "max",
        "mentioned",
        "message",
        "meta",
        "min",
        "name",
        "near",
        "nearby",
        "negative",
        "no",
        "not",
        "of",
        "off",
        "on",
        "only",
        "opaque",
        "option",
        "options",
        "or",
        "play",
        "prompt",
        "pronoun",
        "quit",
        "random",
        "restart",
        "restore",
        "save",
        "say",
        "schedule",
        "score",
        "script",
        "set",
        "show",
        "start",
        "step",
        "stop",
        "strip",
        "style",
        "sum",
        "synonyms",
        "syntax",
        "system",
        "taking",
        "the",
        "then",
        "this",
        "to",
        "transcript",
        "transitively",
        "until",
        "use",
        "verb",
        "visits",
        "wait",
        "when",
        "where",
        "with",
        "word",
        "words"
	};

    public AlanCodeScanner(AlanColorManager manager) {
        colorManager = manager;
        
        final RGB stringColor = preferenceColor(IAlanPreferences.STRING_COLOR_PREFERENCE);
        final RGB keywordColor = preferenceColor(IAlanPreferences.KEYWORD_COLOR_PREFERENCE);
        final RGB commentColor = preferenceColor(IAlanPreferences.COMMENT_COLOR_PREFERENCE);
        final RGB idColor = preferenceColor(IAlanPreferences.ID_COLOR_PREFERENCE);
        final RGB defaultColor = preferenceColor(IAlanPreferences.DEFAULT_COLOR_PREFERENCE);

        final IToken stringToken = new Token(new TextAttribute(colorManager.getColor(stringColor)));
        coloringTokens.put(IAlanPreferences.STRING_COLOR_PREFERENCE, stringToken);

        final IToken keywordToken = new Token(new TextAttribute(colorManager.getColor(keywordColor), null, SWT.BOLD));
        coloringTokens.put(IAlanPreferences.KEYWORD_COLOR_PREFERENCE, keywordToken);

        final IToken commentToken = new Token(new TextAttribute(colorManager.getColor(commentColor)));
        coloringTokens.put(IAlanPreferences.COMMENT_COLOR_PREFERENCE, commentToken);

        final IToken idToken = new Token(new TextAttribute(colorManager.getColor(idColor)));
        coloringTokens.put(IAlanPreferences.ID_COLOR_PREFERENCE, idToken);

        final Token defaultToken = new Token(new TextAttribute(colorManager.getColor(defaultColor)));
        coloringTokens.put(IAlanPreferences.DEFAULT_COLOR_PREFERENCE, defaultToken);
        setDefaultReturnToken(defaultToken);

		List<IRule> rules = new ArrayList<IRule>();

        // Add rule for comments
        rules.add(new EndOfLineRule("--", commentToken));
        
        // Add rule for double quoted strings
        rules.add(new MultiLineRule("\"", "\"", stringToken));

        WordRule keywordRule = new AlanKeywordRule(new AlanWordDetector(), idToken);        
        for (int i = 0; i < keywords.length; i++) {
            keywordRule.addWord(keywords[i], keywordToken);
        }
		rules.add(keywordRule);
		
        // Add a rule for single quoted id's
		rules.add(new SingleLineRule("'", "'", idToken));

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new AlanWhitespaceDetector()));

		IRule[] theRules = new IRule[rules.size()];
		rules.toArray(theRules);
		setRules(theRules);
	}
}
