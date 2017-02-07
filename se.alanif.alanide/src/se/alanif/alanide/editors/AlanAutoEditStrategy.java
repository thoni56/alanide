package se.alanif.alanide.editors;

import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;

public class AlanAutoEditStrategy implements IAutoEditStrategy {

	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		if (command.text.equals("\"")) {
			command.text = "\"\"";
			placeCaretBetweenQuotes(command);
		} else if (command.text.equals("'")) {
			command.text = "''";
			placeCaretBetweenQuotes(command);
		}
	}

	private void placeCaretBetweenQuotes(DocumentCommand command) {
		command.caretOffset = command.offset + 1;
		command.shiftsCaret = false;
	}

}
