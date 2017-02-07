/*
 * Created on Oct 11, 2004
 */
package se.alanif.alanide.editors.formatters;

import java.util.NoSuchElementException;
import java.util.Scanner;

import org.eclipse.jface.text.formatter.IFormattingStrategy;


public class AlanFormattingStrategy implements IFormattingStrategy
{
	protected static final String lineSeparator = System.getProperty("line.separator");
	private static final String[] INDENTING_KEYWORDS =
	                                    {"THEN", "ELSE", "VERB", "START", "ADD", "THE", "EVERY", "OPTIONS",
	                                     "CHECK", "DOES", "OPTION", "SYNONYMS", "SYNTAX", "MESSAGE", "WHERE",
	                                     "DO", "DEPENDING", "CONTAINER"};
	private static final String[] OUTDENTING_KEYWORDS =
	                                    {"ELSE", "ELSIF", "DOES", "END"};
	private static final String[] TOPLEVEL_OPEN_CLAUSE_INITIALISER_KEYWORDS =
	                                    {"OPTIONS", "OPTION", "SYNONYMS", "MESSAGE", "SYNTAX", "START", "THE",
	                                     "EVERY", "MESSAGE", "WHEN", "VERB", "EVENT"};
	private static final String[] TOPLEVEL_FOLLOWER_KEYWORDS =
	                                    {"OPTIONS", "OPTION", "SYNONYMS", "MESSAGE", "SYNTAX", "START", "ADD",
	                                     "THE", "EVERY", "MESSAGE", "VERB"};
    private static final String[] PROPERTIES_OPENING_KEYWORDS =
                                        {"NAME", "DESCRIPTION", "ENTERED", "INITIALIZE",
                                         "MEMTIONED", "DEFINITE", "INDEFINITE", "NEGATIVE", "ARTICEL",
                                         "FORM", "SCRIPT", "PRONOUN", "WITH", "OPAQUE", "CONTAINER"};
    private static final String[] PROPERTIES_FOLLOWER_KEYWORDS =
                                        {"NAME", "DESCRIPTION", "ENTERED", "INITIALIZE",
                                         "MENTIONED", "DEFINITE", "INDEFINITE", "NEGATIVE", "ARTICEL",
                                         "FORM", "SCRIPT", "PRONOUN", "WITH", "OPAQUE", "CONTAINER", "END"};
    private static final String[] EXTRA_OUTDENT_ON_END_INITIALISER_KEYWORDS =
                                        {"DEPENDING", "DOES"};

	
	private String baseIndent = "";
	private boolean withinString;
	private boolean openTopLevelClause;
	private boolean outdentExtraOnEnd;
    private boolean openPropertiesClause;
	private static final int INDENT = 2;

	public AlanFormattingStrategy() {
		super();
	}

	public void formatterStarts(String initialIndentation) {
	    baseIndent = initialIndentation;
	    withinString = false;
	    openTopLevelClause = false;
		outdentExtraOnEnd = false;
		openPropertiesClause = false;
	}

	public String format(String content, boolean containsBeginningOfLine, String indentation, int[] positions) {
		int[] originalPositions = arrayCopy(positions);
		String result = "";
		int currentPosition = 0;
		String rest = content;
		int currentIndent = 0;
		int endOfLine = findEndOfLine(rest);
		while (endOfLine != -1) {
			final int leadingSpaces = countLeadingSpaces(rest);
			String line = extractLine(rest, endOfLine);
			currentIndent = calculatePreIndentation(line, currentIndent);
			result += reindent(line, currentIndent, containsBeginningOfLine);
			containsBeginningOfLine = true;
			adjustPositions(originalPositions, currentPosition, baseIndent.length()-leadingSpaces, positions);
			currentPosition += line.length() + lineSeparator.length();
			currentIndent = calculatePostIndentation(line, currentIndent);
			if (currentIndent < 0)
			    currentIndent = 0;
			rest = content.substring(currentPosition);
			endOfLine = findEndOfLine(rest);
		}
		if (rest.length() > 0) {
			currentIndent = calculatePreIndentation(rest, currentIndent);
			result += indent(rest, currentIndent);
		}
		return result;
	}

    private int calculatePreIndentation(final String line, final int currentIndent) {
		int indent = currentIndent;
		Scanner scanner = new Scanner(line);
		while (scanner.hasNext()) {
			String token = scanner.next();
			if (isComment(token))
				return indent;
			if (isOutdentingKeyword(token)) {
				indent -= INDENT;
			}
            if (openTopLevelClause && isTopLevelFollower(token)) {
                indent -= INDENT;
                openTopLevelClause = false;
            }
            if (openPropertiesClause && isPropertiesFollower(token)) {
                indent -= INDENT;
                openPropertiesClause = false;
            }
			if (token.equalsIgnoreCase("END")) {
				if (outdentExtraOnEnd) {
					indent -= INDENT;
					outdentExtraOnEnd = false;
				}
			}

		}
		return indent;
	}

    private int calculatePostIndentation(String line, int currentIndentation) {
        Scanner scanner = new Scanner(line);
        while (scanner.hasNext()) {
            String token = scanner.next();
			if (isComment(token))
				return currentIndentation;
			else if (hasSingleQuote(token))
                currentIndentation = toggleStringIndentation(currentIndentation);
            else if (isIndentingKeyword(token))
                currentIndentation += INDENT;
            
            if (isTopLevelOpenClauseInitialiser(token))
                openTopLevelClause = true;

            if (isPropertiesOpenClauseInitialiser(token))
                openPropertiesClause = true;
            
            if (isKeywordOpeningClauseRequiringExtraOutdentOnEnd(token))
				outdentExtraOnEnd = true;
            
            skipExtraKeywords(scanner, token);
        }
        return currentIndentation;
    }

    private boolean isComment(String token) {
		return token.equals("--");
	}

	private void skipExtraKeywords(Scanner scanner, String token) {
		if (token.equalsIgnoreCase("END"))
			scanner.next();
		if (token.equalsIgnoreCase("ADD")) {
			scanner.next();
			try {
				scanner.next();
			} catch (NoSuchElementException e) {
				// Ignore!
			}
		}
	}

	private int toggleStringIndentation(int currentIndentation) {
		if (withinString)
			currentIndentation -= 1;
		else
			currentIndentation += 1;
		withinString = !withinString;
		return currentIndentation;
	}

	private boolean hasSingleQuote(String token) {
	    return hasOddNumberOfQuotes(token);
	}

	public static int occurrences(String string, char ch) {
		int count = 0;
		int index = 0;
		while ((index = string.indexOf(ch, index)) != -1) {
			++index;
			++count;
		}
		return count;
	}

	private boolean hasOddNumberOfQuotes(String token) {
        return countOccurences("\"", token) % 2 == 1;
    }

    private int countOccurences(final String occurs, final String within) {
        int count = 0;
        int index;
        String rest = within;
        while ((index = rest.indexOf(occurs)) != -1) {
            count++;
            rest = rest.substring(index+1);
        }
        return count;
    }

    private boolean isIndentingKeyword(String token) {
        return memberOf(token, INDENTING_KEYWORDS);
    }

    private boolean isOutdentingKeyword(String token) {
        return memberOf(token, OUTDENTING_KEYWORDS);
    }

    private boolean isKeywordOpeningClauseRequiringExtraOutdentOnEnd(String token) {
        return memberOf(token, EXTRA_OUTDENT_ON_END_INITIALISER_KEYWORDS);
    }


    private boolean isTopLevelOpenClauseInitialiser(String token) {
        return memberOf(token, TOPLEVEL_OPEN_CLAUSE_INITIALISER_KEYWORDS);
    }

    private boolean isTopLevelFollower(String token) {
        return memberOf(token, TOPLEVEL_FOLLOWER_KEYWORDS);
    }

    private boolean isPropertiesOpenClauseInitialiser(String token) {
        return memberOf(token, PROPERTIES_OPENING_KEYWORDS);
    }

    private boolean isPropertiesFollower(String token) {
        return memberOf(token, PROPERTIES_FOLLOWER_KEYWORDS);
    }

	private boolean memberOf(String token, final String[] keywords) {
		for (String keyword : keywords)
            if (token.equalsIgnoreCase(keyword))
                return true;
        return false;
	}

	private String reindent(String line, int currentIndent, boolean containsBeginningOfLine) {
		if (containsBeginningOfLine)
			return indent(line, currentIndent) + lineSeparator;
		else
			return line + lineSeparator;
	}

	private String indent(String line, int currentIndent) {
		String indentation = repeat(" ", currentIndent);
		return baseIndent + indentation + ltrim(line);
	}

	private String repeat(String string, int count) {
		String result = "";
		for (int i=0; i<count; i++)
			result += string;
		return result;
	}

	private String extractLine(String rest, int endOfLine) {
		return rest.substring(0, endOfLine);
	}

	private int[] arrayCopy(int[] original) {
		if (original != null) {
			int[] copy = new int[original.length];
			for (int i=0; i<original.length; i++)
				copy[i] = original[i];
			return copy;
		} else
			return null;
	}

	private int findEndOfLine(String substring) {
		return substring.indexOf(lineSeparator);
	}

	private int countLeadingSpaces(String content) {
		int count = 0;
		while (isSpace(content.charAt(count)))
			count++;
		return count;
	}

	private boolean isSpace(final char charAt) {
		return " \t".indexOf(charAt) != -1;
	}

	private void adjustPositions(int[] originalPositions, int currentPosition, int delta, int[] positions) {
		if (positions != null)
			for (int p=0; p<positions.length; p++)
				if (originalPositions[p] >= currentPosition)
					positions[p] += delta;
	}

	private String ltrim(String source) {
		return source.replaceAll("^\\s+", "");
	}

	public void formatterStops() {
	}

}
