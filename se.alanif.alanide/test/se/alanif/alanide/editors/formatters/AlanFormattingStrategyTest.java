/*
 * Created on 21 jan 2009
 *
 */
package se.alanif.alanide.editors.formatters;

import se.alanif.alanide.editors.formatters.AlanFormattingStrategy;
import junit.framework.TestCase;

public class AlanFormattingStrategyTest extends TestCase {

    private static final String STRING_QUOTED_WORD = "\"word\"";
	private static final String THREE_SPACES = "   ";
	private static final String FOURTEEN_SPACES_AND_TWO_TABS = "     \t   \t      ";
	private static final String TWO_SPACES = "  ";
	private static final int OFFSET = 2;
	private static final int LAST = 1;
	private static final int FIRST = 0;
	private static final String BASE_INDENT = randomIndent();
    private static final String LINE1 = "line1";
    private static final String LINE2 = "line2";
    private static final String nl = System.getProperty("line.separator");
	private static final String LINE3 = "line3";
	private static final String LINE4 = "line4";
    private static final String INDENT = "  ";
    
    private static final String BASIC_LINE = "Make it right." + nl;

    // Indenting lines
	private static final String START_AT_LINE = "Start At x."+nl;
    private static final String IF_LINE = "If x Then" + nl;
    private static final String ELSIF_LINE = "Elsif something Then"+nl;
	private static final String ELSE_LINE = "Else"+nl;
	private static final String ELSE_STRING_LINE = "Else \"Some message\""+nl;
	private static final String VERB_LINE = "Verb v"+nl;
	private static final String CHECK_LINE = "Check a = b"+nl;
	private static final String DOES_LINE = "Does"+nl;

	// Outdenting lines
    private static final String END_IF_LINE = "End If ."+nl;
    private static final String END_VERB_LINE = "End Verb ."+nl;
	private static final String COMMENT_LINE = "-- Does this get indented? It should start at the beginning"+nl;
    private static final String OPTIONS_LINE = "Options"+nl;
    private static final String SYNONYMS_LINE = "Synonyms"+nl;
    private static final String ATTRIBUTES_LINE = "Is"+nl;
    private static final String END_ADD_LINE = "End Add"+nl;
    private static final String ADD_LINE = "Add To Every object"+nl;
    private static final String DEPENDING_LINE = "Depending On x"+nl;
    private static final String THEN_LINE = "=74 Then"+nl;
    private static final String END_DEPEND_LINE = "End Depend"+nl;
    private static final String THE_LINE = "The x"+nl;
    private static final String CONTAINER_LINE = "Container"+nl;
    private static final String END_THE_LINE = "End The x"+nl;

    private AlanFormattingStrategy formatter;

    protected void setUp() {
        formatter = new AlanFormattingStrategy();
    }
    
    private static String randomIndent() {
    	int length = (int) Math.round(10.0*Math.random());
    	String spaces = "";
    	for (int i=0; i<length; i++)
    		spaces += " ";
		return spaces;
	}

	public void testEmptyContentIsNotChanged() {
        formatter.formatterStarts("");
        assertEquals("", formatter.format("", true, "", null));
	}

    public void testAnyTextNotBeginningOnNewLineShouldNotIndent() throws Exception {
    	int[] positions = createPositions(3);
        formatter.formatterStarts("");
        assertEquals("bla", formatter.format("bla", false, BASE_INDENT, positions));
        verifyPositionOffsets(positions, 0, 2, 0);
    }

	public void testOneLineIsIndentedWithTheInitialIndent() throws Exception {
		int[] positions = createPositions(LINE1.length());
        String expected = BASE_INDENT+LINE1+nl;
        
        formatter.formatterStarts(BASE_INDENT);
        assertEquals(expected, formatter.format(LINE1+nl, true, BASE_INDENT, positions));

        verifyPositionOffsets(positions, 0, LINE1.length()-1, BASE_INDENT.length());
    }

    public void testTwoUnindentedLinesAreIndentedWithTheInitialIndent() throws Exception {
        final String content = LINE1+nl+
        					   LINE2+nl;
        final String expected = BASE_INDENT+LINE1+nl+
        						BASE_INDENT+LINE2+nl;
        
        int [] positions = createPositions(content.length());
        int [][] offsets = new int[2][3];
        int segment = 0;
        offsets[segment][FIRST] = 0;
        offsets[segment][LAST]  = LINE1.length()+nl.length()-1;
        offsets[segment][OFFSET] = BASE_INDENT.length();
        segment++;
        offsets[segment][FIRST] = LINE1.length()+nl.length();
        offsets[segment][LAST] = LINE1.length()+nl.length() + LINE2.length()+nl.length()-1;
        offsets[segment][OFFSET] = 2*BASE_INDENT.length();

        formatter.formatterStarts(BASE_INDENT);
		assertEquals(expected, formatter.format(content, true, BASE_INDENT, positions));

        for (int[] offset : offsets)
        	verifyPositionOffsets(positions, offset[FIRST], offset[LAST], offset[OFFSET]);
    }

    public void testAdjustPositionsForThreeIndentedLinesWhichAreReindentedWithTheInitialIndent() throws Exception {
        final String FIRST_LINE_INDENT = TWO_SPACES;
		final String FIRST_LINE = FIRST_LINE_INDENT+LINE1+nl;
		final String SECOND_LINE_INDENT = FOURTEEN_SPACES_AND_TWO_TABS;
		final String SECOND_LINE = SECOND_LINE_INDENT + LINE2 + nl;
		final String THIRD_LINE_INDENT = THREE_SPACES;
		final String THIRD_LINE = THIRD_LINE_INDENT + LINE3 + nl;

		final String content = FIRST_LINE +
							   SECOND_LINE +
							   THIRD_LINE;
		final String expected = BASE_INDENT+LINE1+nl+
								BASE_INDENT+LINE2+nl+
								BASE_INDENT+LINE3+nl;

		int [] positions = createPositions(content.length());
        int [][] offsets = new int[3][3];
        int segment = 0;
        offsets[segment][FIRST] = FIRST_LINE_INDENT.length();
        offsets[segment][LAST]  = FIRST_LINE.length()-1;
        offsets[segment][OFFSET] = BASE_INDENT.length()-FIRST_LINE_INDENT.length();
        segment++;
        offsets[segment][FIRST] = FIRST_LINE.length() + SECOND_LINE_INDENT.length();
        offsets[segment][LAST] = FIRST_LINE.length() + SECOND_LINE.length()-1;
        offsets[segment][OFFSET] = 2*BASE_INDENT.length() - FIRST_LINE_INDENT.length() - SECOND_LINE_INDENT.length();
        segment++;
        offsets[segment][FIRST] = FIRST_LINE.length() + SECOND_LINE.length() + THIRD_LINE_INDENT.length();
        offsets[segment][LAST] = FIRST_LINE.length() + SECOND_LINE.length() + THIRD_LINE.length()-1;
        offsets[segment][OFFSET] = 3*BASE_INDENT.length() - FIRST_LINE_INDENT.length() - SECOND_LINE_INDENT.length() - THIRD_LINE_INDENT.length();

        formatter.formatterStarts(BASE_INDENT);
		assertEquals(expected, formatter.format(content, true, BASE_INDENT, positions));

        for (int[] offset : offsets)
        	verifyPositionOffsets(positions, offset[FIRST], offset[LAST], offset[OFFSET]);
    }
    
    public void testIsNotLineStartInhibitsIndentingFirstLine() throws Exception {
		final String content = LINE1+nl+
							   FOURTEEN_SPACES_AND_TWO_TABS+LINE2+nl;
        final String expected = LINE1+nl+
                     			BASE_INDENT+LINE2+nl;

        formatter.formatterStarts(BASE_INDENT);
		assertEquals(expected, formatter.format(content, false, BASE_INDENT, null));
	}
    
    public void testNoLineSeparatorAtEndStillIndentsLastLine() throws Exception {
		final String content = TWO_SPACES+LINE1+nl+
							   FOURTEEN_SPACES_AND_TWO_TABS+LINE2+nl+
							   THREE_SPACES+LINE3;
        final String expected = BASE_INDENT+LINE1+nl+
        						BASE_INDENT+LINE2+nl+
        						BASE_INDENT+LINE3;

        formatter.formatterStarts(BASE_INDENT);
		assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
	}

    public void testStringQuotedWordDoNotAffectIndentation() throws Exception {
        formatter.formatterStarts(BASE_INDENT);
		final String content = STRING_QUOTED_WORD + "." + nl +
							   BASIC_LINE;
		final String expected = BASE_INDENT+STRING_QUOTED_WORD + "." + nl +
								BASE_INDENT+BASIC_LINE;

        formatter.formatterStarts(BASE_INDENT);
		assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
    }

    public void testStringSpanningMultipleLinesShouldIndentFollowingLinesWithAnExtraSingleSpace() throws Exception {
    	final String content = TWO_SPACES+"\""+LINE1+nl+
        					   FOURTEEN_SPACES_AND_TWO_TABS+LINE2+nl+
        					   THREE_SPACES+LINE3+"\""+nl+
        					   TWO_SPACES+LINE4;
        final String expected = BASE_INDENT+"\""+LINE1+nl+
        						BASE_INDENT+" "+LINE2+nl+
        						BASE_INDENT+" "+LINE3+"\""+nl+
        						BASE_INDENT+LINE4;

        formatter.formatterStarts(BASE_INDENT);
		assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
	}

    public void testIfStatementIsIndented() throws Exception {
        final String content = IF_LINE+
        					   BASIC_LINE+
        					   END_IF_LINE+
        					   BASIC_LINE;
        final String expected = BASE_INDENT+IF_LINE+
        						BASE_INDENT+INDENT+BASIC_LINE+
        						BASE_INDENT+END_IF_LINE+
        						BASE_INDENT+BASIC_LINE;
        
        formatter.formatterStarts(BASE_INDENT);
		assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
    }

    public void testIfElsifEndStatementIsIndented() throws Exception {
        final String content = IF_LINE +
        					   BASIC_LINE +
        					   ELSIF_LINE +
        					   BASIC_LINE +
        					   END_IF_LINE +
        					   BASIC_LINE;
        final String expected = BASE_INDENT+IF_LINE+
        						BASE_INDENT+INDENT+BASIC_LINE+
        						BASE_INDENT+ELSIF_LINE+
        						BASE_INDENT+INDENT+BASIC_LINE+
        						BASE_INDENT+END_IF_LINE+
        						BASE_INDENT+BASIC_LINE;
        
        formatter.formatterStarts(BASE_INDENT);
		assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
    }

    public void testStartIndentsRestOfSource() throws Exception {
    	final String content = START_AT_LINE+
    						   BASIC_LINE+
    						   BASIC_LINE+
    						   BASIC_LINE;
        final String expected = BASE_INDENT+START_AT_LINE+
        						BASE_INDENT+INDENT+BASIC_LINE+
        						BASE_INDENT+INDENT+BASIC_LINE+
        						BASE_INDENT+INDENT+BASIC_LINE;

    	formatter.formatterStarts(BASE_INDENT);
		assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
	}
    
    public void testBasicVerbIsIndented() throws Exception {
    	final String content = VERB_LINE+
    						   BASIC_LINE+
    						   BASIC_LINE+
    						   END_VERB_LINE+
    						   BASIC_LINE;
        final String expected = BASE_INDENT+VERB_LINE+
        						BASE_INDENT+INDENT+BASIC_LINE+
        						BASE_INDENT+INDENT+BASIC_LINE+
        						BASE_INDENT+END_VERB_LINE+
        						BASE_INDENT+BASIC_LINE;

        formatter.formatterStarts(BASE_INDENT);
		assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
	}
    
    public void testVerbCheckIsIndented() throws Exception {
    	final String content = VERB_LINE+
    						   CHECK_LINE+
    						   BASIC_LINE+
    						   END_VERB_LINE;
        final String expected = BASE_INDENT+VERB_LINE+
        						BASE_INDENT+INDENT+CHECK_LINE+
        						BASE_INDENT+INDENT+INDENT+BASIC_LINE+
        						BASE_INDENT+END_VERB_LINE;

        formatter.formatterStarts(BASE_INDENT);
		assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
	}

    public void testVerbCheckElseIsIndented() throws Exception {
    	final String content = VERB_LINE+
    						   CHECK_LINE+
    						   ELSE_LINE+
    						   BASIC_LINE+
    						   END_VERB_LINE;
        final String expected = BASE_INDENT+VERB_LINE+
        						BASE_INDENT+INDENT+CHECK_LINE+
        						BASE_INDENT+INDENT+ELSE_LINE+
        						BASE_INDENT+INDENT+INDENT+BASIC_LINE+
        						BASE_INDENT+END_VERB_LINE;

        formatter.formatterStarts(BASE_INDENT);
		assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
	}

    public void testVerbCheckElseDoesIsIndented() throws Exception {
    	final String content = VERB_LINE+
    						   CHECK_LINE+
    						   ELSE_LINE+
    						   BASIC_LINE+
    						   DOES_LINE+
    						   BASIC_LINE+
    						   END_VERB_LINE;
        final String expected = BASE_INDENT+VERB_LINE+
        						BASE_INDENT+INDENT+CHECK_LINE+
        						BASE_INDENT+INDENT+ELSE_LINE+
        						BASE_INDENT+INDENT+INDENT+BASIC_LINE+
        						BASE_INDENT+INDENT+DOES_LINE+
        						BASE_INDENT+INDENT+INDENT+BASIC_LINE+
        						BASE_INDENT+END_VERB_LINE;

        formatter.formatterStarts(BASE_INDENT);
		assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
	}
    
    public void testVerbCheckIsIndentedUptoNextEndEvenWhenCheckAndElseAreOneLiners() throws Exception {
    	final String content = VERB_LINE+
    						   CHECK_LINE+
    						   ELSE_STRING_LINE+
    						   END_VERB_LINE;
        final String expected = BASE_INDENT+VERB_LINE+
        						BASE_INDENT+INDENT+CHECK_LINE+
        						BASE_INDENT+INDENT+ELSE_STRING_LINE+
        						BASE_INDENT+END_VERB_LINE;

        formatter.formatterStarts(BASE_INDENT);
		assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
	}
    
    public void testCommentsAreIndentedButNotConsidered() throws Exception {
    	final String content = COMMENT_LINE+
    						   COMMENT_LINE+
    						   BASIC_LINE;
    	final String expected = BASE_INDENT+COMMENT_LINE+
    							BASE_INDENT+COMMENT_LINE+
    							BASE_INDENT+BASIC_LINE;

    	formatter.formatterStarts(BASE_INDENT);
    	assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
		
	}

    public void testOptionsIsIndented() throws Exception {
        final String content = OPTIONS_LINE+
                               BASIC_LINE+
                               BASIC_LINE+
                               START_AT_LINE;
        final String expected = BASE_INDENT+OPTIONS_LINE+
                                BASE_INDENT+INDENT+BASIC_LINE+
                                BASE_INDENT+INDENT+BASIC_LINE+
                                BASE_INDENT+START_AT_LINE;

        formatter.formatterStarts(BASE_INDENT);
        assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
    }

    public void testSynonymsIsIndented() throws Exception {
        final String content = BASIC_LINE+
                               SYNONYMS_LINE+
                               BASIC_LINE+
                               BASIC_LINE+
                               OPTIONS_LINE;
        final String expected = BASE_INDENT+BASIC_LINE+
                                BASE_INDENT+SYNONYMS_LINE+
                                BASE_INDENT+INDENT+BASIC_LINE+
                                BASE_INDENT+INDENT+BASIC_LINE+
                                BASE_INDENT+OPTIONS_LINE;

        formatter.formatterStarts(BASE_INDENT);
        assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
    }

    public void testTopLevelOpenClauseIndentationIsTerminiatedByVerb() throws Exception {
        final String content = BASIC_LINE+
                               SYNONYMS_LINE+
                               BASIC_LINE+
                               BASIC_LINE+
                               VERB_LINE;
        final String expected = BASE_INDENT+BASIC_LINE+
                                BASE_INDENT+SYNONYMS_LINE+
                                BASE_INDENT+INDENT+BASIC_LINE+
                                BASE_INDENT+INDENT+BASIC_LINE+
                                BASE_INDENT+VERB_LINE;

        formatter.formatterStarts(BASE_INDENT);
        assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
    }

    public void testPropertiesAreIndentedAndOutdentedByStartOfNewProperty() throws Exception {
        final String content = BASIC_LINE+
                               ATTRIBUTES_LINE+
                               BASIC_LINE+
                               BASIC_LINE+
                               ATTRIBUTES_LINE+
                               BASIC_LINE;
        final String expected = BASE_INDENT+BASIC_LINE+
                                BASE_INDENT+ATTRIBUTES_LINE+
                                BASE_INDENT+INDENT+BASIC_LINE+
                                BASE_INDENT+INDENT+BASIC_LINE+
                                BASE_INDENT+ATTRIBUTES_LINE+
                                BASE_INDENT+INDENT+BASIC_LINE;
        formatter.formatterStarts(BASE_INDENT);
        assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
    }
    
    public void testPropertiesAreIndentedAndOutdentedByEnd() throws Exception {
        final String content = ADD_LINE+
                               ATTRIBUTES_LINE+
                               BASIC_LINE+
                               BASIC_LINE+
                               END_ADD_LINE+
                               BASIC_LINE;
        final String expected = BASE_INDENT+ADD_LINE+
                                BASE_INDENT+INDENT+ATTRIBUTES_LINE+
                                BASE_INDENT+INDENT+INDENT+BASIC_LINE+
                                BASE_INDENT+INDENT+INDENT+BASIC_LINE+
                                BASE_INDENT+END_ADD_LINE+
                                BASE_INDENT+BASIC_LINE;
        formatter.formatterStarts(BASE_INDENT);
        assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
    }
    
    public void testDependingStatementIsIndented() throws Exception {
        final String content = DEPENDING_LINE+
                               THEN_LINE+
                               BASIC_LINE+
                               ELSE_LINE+
                               BASIC_LINE+
                               END_DEPEND_LINE+
                               BASIC_LINE;
        final String expected = BASE_INDENT+DEPENDING_LINE+
                                BASE_INDENT+INDENT+THEN_LINE+
                                BASE_INDENT+INDENT+INDENT+BASIC_LINE+
                                BASE_INDENT+INDENT+ELSE_LINE+
                                BASE_INDENT+INDENT+INDENT+BASIC_LINE+
                                BASE_INDENT+END_DEPEND_LINE+
                                BASE_INDENT+BASIC_LINE;
        formatter.formatterStarts(BASE_INDENT);
        assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
    }
    
    public void testContainerPropertiesIsIndentedAndTerminatedByEnd() throws Exception {
        final String content = THE_LINE+
                               CONTAINER_LINE+
                               BASIC_LINE+
                               END_THE_LINE+
                               BASIC_LINE;
        final String expected = BASE_INDENT+THE_LINE+
                                BASE_INDENT+INDENT+CONTAINER_LINE+
                                BASE_INDENT+INDENT+INDENT+BASIC_LINE+
                                BASE_INDENT+END_THE_LINE+
                                BASE_INDENT+BASIC_LINE;
        formatter.formatterStarts(BASE_INDENT);
        assertEquals(expected, formatter.format(content, true, BASE_INDENT, null));
    }
    
    private void verifyPositionOffsets(int[] positions, int first, int last, int offset) {
		for (int i=first; i<=last; i++)
			assertEquals(i+offset, positions[i]);
	}
    
    private int[] createPositions(int numberOfPositions) {
    	int[] positions = new int[numberOfPositions];
    	for (int i=0; i<numberOfPositions; i++)
    		positions[i] = i;
    	return positions;
	}

}
