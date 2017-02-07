/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package se.alanif.alanide.editors;


import org.eclipse.jface.action.*;
import org.eclipse.ui.*;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.texteditor.*;

/**
 * Contributes interesting Alan actions to the desktop's Edit menu and the toolbar.
 */
public class AlanEditorActionContributor extends TextEditorActionContributor {

	protected RetargetTextEditorAction contentAssistProposalAction;
 
	/**
	 * Default constructor.
	 */
	public AlanEditorActionContributor() {
		super();
		contentAssistProposalAction= new RetargetTextEditorAction(AlanEditorMessages.getResourceBundle(), "ContentAssistProposal."); //$NON-NLS-1$
		contentAssistProposalAction.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS); 
	}
	
	/*
	 * @see IEditorActionBarContributor#init(IActionBars)
	 */
	public void init(IActionBars bars) {
		super.init(bars);
		
		IMenuManager menuManager= bars.getMenuManager();
		IMenuManager editMenu= menuManager.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
		if (editMenu != null) {
			editMenu.add(new Separator());
			editMenu.add(contentAssistProposalAction);
		}	
	}
	
	private void doSetActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);

		ITextEditor editor= null;
		if (part instanceof ITextEditor)
			editor= (ITextEditor) part;

		contentAssistProposalAction.setAction(getAction(editor, "ContentAssistProposal")); //$NON-NLS-1$
	}
	
	/*
	 * @see IEditorActionBarContributor#setActiveEditor(IEditorPart)
	 */
	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);
		doSetActiveEditor(part);
	}
	
	/*
	 * @see IEditorActionBarContributor#dispose()
	 */
	public void dispose() {
		doSetActiveEditor(null);
		super.dispose();
	}
}
