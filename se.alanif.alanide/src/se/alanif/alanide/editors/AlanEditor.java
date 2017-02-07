package se.alanif.alanide.editors;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import se.alanif.alanide.views.AlanContentOutlinePage;

public class AlanEditor extends TextEditor {

	private AlanColorManager colorManager;
    private AlanContentOutlinePage outlinePage;
	private AlanEditorLabelUpdater labelUpdater;
    
	public AlanEditor() {
		super();
		colorManager = new AlanColorManager();
		setSourceViewerConfiguration(new AlanViewerConfiguration(colorManager, this));
        setDocumentProvider(new AlanDocumentProvider());
        labelUpdater = new AlanEditorLabelUpdater(this);
	}

	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		super.init(site, input);
		labelUpdater.decorate();
	}
	
	
    /*
     * Extend Actions with Alan specific (non-Javadoc)
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#createActions()
     */
    protected void createActions() {
        super.createActions();
        final ResourceBundle resourceBundle = AlanEditorMessages.getResourceBundle();
        IAction contentAssistAction= new TextOperationAction(resourceBundle, "ContentAssistProposal.", this, ISourceViewer.CONTENTASSIST_PROPOSALS); //$NON-NLS-1$
        contentAssistAction.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
        setAction("ContentAssistProposal", contentAssistAction); //$NON-NLS-1$
        
        IAction formatAction= new TextOperationAction(resourceBundle, "Format.", this, ISourceViewer.FORMAT); //$NON-NLS-1$
        //formatAction.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
        setAction("Format", formatAction); //$NON-NLS-1$
    }
    
    public ISourceViewer getTheSourceViewer() {
        return getSourceViewer();
    }

    /*
     * @see org.eclipse.ui.texteditor.ExtendedTextEditor#editorContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
     */
    protected void editorContextMenuAboutToShow(IMenuManager menu) {
        super.editorContextMenuAboutToShow(menu);
        menu.add(new Separator());
        addAction(menu, "ContentAssistProposal"); //$NON-NLS-1$
        addAction(menu, "Format");
    }
    
    public void dispose() {
        colorManager.dispose();
        labelUpdater.dispose();
        super.dispose();
    }
    
    /*
     * Return adapters for Alan source code, currently only Outline
     */
    public Object getAdapter(@SuppressWarnings("rawtypes") Class requiredClass) {
    	if (IContentOutlinePage.class.equals(requiredClass)) {
    		if (outlinePage == null) {
    			outlinePage = new AlanContentOutlinePage(getDocumentProvider(), this);
    			outlinePage.setInput(getEditorInput());
    		}
    		return outlinePage;
    	}
    	return super.getAdapter(requiredClass);
    }

	public void updatedTitleImage(Image image) {
		setTitleImage(image);
	}

}
