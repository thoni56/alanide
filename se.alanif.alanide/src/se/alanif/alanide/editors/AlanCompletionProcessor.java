/*
 * Created on 2006-jan-10
 *
 */
package se.alanif.alanide.editors;

import java.util.ArrayList;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import se.alanif.alanide.editors.scanners.AlanCodeScanner;

public class AlanCompletionProcessor implements IContentAssistProcessor {

    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
            int documentOffset) {
        String keywords[] = AlanCodeScanner.keywords;
        ArrayList<CompletionProposal> results = new ArrayList<CompletionProposal>();
        String startString = startOfWord(viewer, documentOffset);
        for (int k= 0; k < keywords.length; k++) {
            if (startString.length()<=keywords[k].length()
                    && keywords[k].substring(0, startString.length()).compareToIgnoreCase(startString) == 0) {
                final CompletionProposal completionProposal = new CompletionProposal(capitalize(keywords[k])+" ", documentOffset-startString.length(),
                        startString.length(), keywords[k].length()+1);
                results.add(completionProposal);
            }
        }
        ICompletionProposal[] r = new ICompletionProposal[results.size()];
        for (int p = 0; p < results.size(); p++)
            r[p] = results.get(p);
        return r;
    }

    private String capitalize(String keyword) {
        return Character.toUpperCase(keyword.charAt(0)) + keyword. substring(1, keyword.length());
    }

    private String startOfWord(ITextViewer viewer, int documentOffset) {
        IDocument document = viewer.getDocument();
        int startOffset = documentOffset-1;
        try {
            while (Character.isJavaIdentifierPart(document.getChar(startOffset)))
                startOffset--;
            startOffset++;
        } catch (BadLocationException e) {
            startOffset = 0;
        }
        try {
            return document.get(startOffset, documentOffset-startOffset);
        } catch (BadLocationException e) {
            return "";
        }
    }

    public IContextInformation[] computeContextInformation(ITextViewer viewer,
            int offset) {
        // TODO Auto-generated method stub
        return null;
    }

    public char[] getCompletionProposalAutoActivationCharacters() {
        return new char[] {' '};
    }

    public char[] getContextInformationAutoActivationCharacters() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getErrorMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    public IContextInformationValidator getContextInformationValidator() {
        // TODO Auto-generated method stub
        return null;
    }

}
