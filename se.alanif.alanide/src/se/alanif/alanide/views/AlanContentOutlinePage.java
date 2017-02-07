/*
 * Created on 2005-jun-04
 *
 */
package se.alanif.alanide.views;

import java.io.InputStream;
import java.io.StringBufferInputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import se.alanif.alanide.editors.AlanEditor;
import se.alanif.alanide.model.AlanModel;
import se.alanif.alanide.model.AlanModelLabelProvider;

public class AlanContentOutlinePage extends ContentOutlinePage implements IDocumentListener {

    IDocumentProvider documentProvider;
    Object input = null;
    InputStream stream = null;
    AlanEditor editor = null;
    
    public AlanContentOutlinePage(IDocumentProvider documentProvider, AlanEditor editor) {
        this.documentProvider = documentProvider;
        this.editor = editor;
    }

    public void createControl(Composite parent) {
        super.createControl(parent);
        TreeViewer viewer = getTreeViewer();
        viewer.setLabelProvider(new AlanModelLabelProvider());
        if (stream != null)
            viewer.setContentProvider(new AlanModel(stream));
        if (input != null)
            viewer.setInput(input);
        viewer.addSelectionChangedListener(this);
    }

    public void setInput(Object input) {
        this.input = input;
        if (input instanceof FileEditorInput) {
            IDocument document = documentProvider.getDocument(input);
            document.addDocumentListener(this);
            try {
                stream = ((FileEditorInput)input).getStorage().getContents();
            } catch (CoreException e) {
            	System.out.println("Could not getContents()");
                e.printStackTrace();
            }
        }
   }

    public void documentAboutToBeChanged(DocumentEvent event) {
    }

    public void documentChanged(DocumentEvent event) {
        /* TODO This should not create a new model,
         * just update the section that has been changed */
        String content = event.getDocument().get();
        /* TODO To get away from deprecated StringBufferInputStream we
         * will have to change CoCo scanner to take a Reader instead
         * of a Stream
         * Duh, or create an Adapter!
         */
        stream = new StringBufferInputStream(content);
        getTreeViewer().setContentProvider(new AlanModel(stream));
    }

    public void selectionChanged(SelectionChangedEvent event) {

        super.selectionChanged(event);

        ISelection selection= event.getSelection();
        if (selection.isEmpty())
            editor.resetHighlightRange();
        else {
            if (selection instanceof IStructuredSelection)
                try {
                    IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                    Object element = structuredSelection.getFirstElement();
                    if (element instanceof AlanModel) {
                        AlanModel modelElement = (AlanModel) element;
                        editor.setHighlightRange(modelElement.start, modelElement.length, true);
                    }
                } catch (IllegalArgumentException x) {
                    editor.resetHighlightRange();
                }
        }
    }
   
    /**
     * Updates the outline page.
     */
    public void update() {
        TreeViewer viewer= getTreeViewer();

        if (viewer != null) {
            Control control= viewer.getControl();
            if (control != null && !control.isDisposed()) {
                control.setRedraw(false);
                viewer.setInput(input);
                viewer.expandAll();
                control.setRedraw(true);
            }
        }
    }

}
