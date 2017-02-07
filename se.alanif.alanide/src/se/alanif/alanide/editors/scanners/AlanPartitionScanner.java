package se.alanif.alanide.editors.scanners;

import org.eclipse.jface.text.rules.*;

public class AlanPartitionScanner extends RuleBasedPartitionScanner {
    public static final String ALAN_COMMENT_CONTENT = "__alan_comment_content";
	public final static String ALAN_STRING_CONTENT = "__alan_string_content";
    public static final String ALAN_CODE_CONTENT = "__alan_code_content";
    public final static String ALAN_DEFAULT_CONTENT = "__alan_default_content";

	public AlanPartitionScanner() {
        IPredicateRule[] rules = new IPredicateRule[2];

        IToken alanCommentToken = new Token(ALAN_COMMENT_CONTENT);
        rules[0] = new EndOfLineRule("--", alanCommentToken);

		IToken alanStringToken = new Token(ALAN_STRING_CONTENT);
		rules[1] = new MultiLineRule("\"", "\"", alanStringToken);
		setPredicateRules(rules);
	}
}
