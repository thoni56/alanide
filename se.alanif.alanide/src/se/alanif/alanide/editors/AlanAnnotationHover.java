/*
 * Created on 2005-mar-12
 *
 */
package se.alanif.alanide.editors;

import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.MarkerUtilities;

/**
 * @author Thomas Nilsson
 *
 */
public class AlanAnnotationHover implements IAnnotationHover {

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.source.IAnnotationHover#getHoverInfo(org.eclipse.jface.text.source.ISourceViewer, int)
     */
    public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
        String text = null;
        Iterator< ? > annotations = sourceViewer.getAnnotationModel().getAnnotationIterator();
        while (annotations.hasNext()) {
            Annotation annotation = (Annotation)annotations.next();
            String type = annotation.getType();
            if (annotation instanceof MarkerAnnotation) {
                MarkerAnnotation markerAnnotation = (MarkerAnnotation)annotation;
                IMarker marker = markerAnnotation.getMarker();
                if (type.startsWith("org.eclipse.ui.workbench.texteditor")) {
                    try {
                        final int markerLineNumber = MarkerUtilities.getLineNumber(marker);
                        if (markerLineNumber == lineNumber+1)
                            if (text == null)
                                text = (String)marker.getAttribute(IMarker.MESSAGE);
                            else
                                text = text + "\n" + (String)marker.getAttribute(IMarker.MESSAGE);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
        return text;
    }

}
