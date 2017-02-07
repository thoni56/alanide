package se.alanif.alanide.editors;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import se.alanif.alanide.AlanIDEPlugin;
import se.alanif.alanide.editors.formatters.AlanFormattingStrategy;
import se.alanif.alanide.editors.scanners.AbstractAlanScanner;
import se.alanif.alanide.editors.scanners.AlanCodeScanner;
import se.alanif.alanide.editors.scanners.AlanCommentScanner;
import se.alanif.alanide.editors.scanners.AlanPartitionScanner;
import se.alanif.alanide.editors.scanners.AlanStringScanner;

public class AlanViewerConfiguration extends SourceViewerConfiguration {
    
	private AlanDoubleClickStrategy doubleClickStrategy;
    private AbstractAlanScanner commentScanner;
    private AbstractAlanScanner stringScanner;
    private AbstractAlanScanner codeScanner;
	private AlanColorManager colorManager;
    private AlanAnnotationHover annotationHover;
    private AlanTextHover textHover;
    private AlanEditor editor;

    private IPropertyChangeListener listener = new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            codeScanner.adaptToColorChange(event);
            stringScanner.adaptToColorChange(event);
            editor.getTheSourceViewer().invalidateTextPresentation();
        }
    };

	public AlanViewerConfiguration(AlanColorManager colorManager, AlanEditor theEditor) {
		this.colorManager = colorManager;
        this.editor = theEditor;
        IPreferenceStore store = AlanIDEPlugin.getDefault().getPreferenceStore();
        store.addPropertyChangeListener(listener);
	}

    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
            AlanPartitionScanner.ALAN_STRING_CONTENT,
            AlanPartitionScanner.ALAN_CODE_CONTENT};
	}

    public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new AlanDoubleClickStrategy();
		return doubleClickStrategy;
	}

    protected AbstractAlanScanner getAlanCommentScanner() {
        if (commentScanner == null) {
            commentScanner = new AlanCommentScanner(colorManager);
        }
        return commentScanner;
    }

    protected AbstractAlanScanner getAlanStringScanner() {
        if (stringScanner == null) {
            stringScanner = new AlanStringScanner(colorManager);
        }
        return stringScanner;
    }

    protected AbstractAlanScanner getAlanCodeScanner() {
        if (codeScanner == null) {
            codeScanner = new AlanCodeScanner(colorManager);
         }
        return codeScanner;
    }

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer dr =
            new DefaultDamagerRepairer(getAlanCommentScanner());
        reconciler.setDamager(dr, AlanPartitionScanner.ALAN_COMMENT_CONTENT);
        reconciler.setRepairer(dr, AlanPartitionScanner.ALAN_COMMENT_CONTENT);

        dr = new DefaultDamagerRepairer(getAlanStringScanner());
        reconciler.setDamager(dr, AlanPartitionScanner.ALAN_STRING_CONTENT);
        reconciler.setRepairer(dr, AlanPartitionScanner.ALAN_STRING_CONTENT);

        dr = new DefaultDamagerRepairer(getAlanCodeScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		return reconciler;
	}
    
    public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
        if (annotationHover == null)
            annotationHover = new AlanAnnotationHover();
        return annotationHover;
    }

    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
        if (textHover == null)
            textHover = new AlanTextHover();
        return textHover;
    }

    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        ContentAssistant assistant= new ContentAssistant();
        assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
        assistant.setContentAssistProcessor(new AlanCompletionProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
        return assistant;
    }

    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, 
        String contentType) {
            IAutoEditStrategy strategy= (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType)
            		? new AlanAutoEditStrategy()
            		: new DefaultIndentLineAutoEditStrategy());
            return new IAutoEditStrategy[] { strategy };
        }
    
    public IContentFormatter getContentFormatter(ISourceViewer sourceViewer)
    {
        ContentFormatter formatter = new ContentFormatter();
        formatter.enablePartitionAwareFormatting(false);
        AlanFormattingStrategy defaultStrategy = new AlanFormattingStrategy();
        formatter.setFormattingStrategy(defaultStrategy, IDocument.DEFAULT_CONTENT_TYPE);

        return formatter;
    }

}