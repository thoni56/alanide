/*
 * Created on 2005-mar-13
 *
 */
package se.alanif.alanide.editors;

import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.texteditor.MarkerAnnotation;

/**
 * @author Thomas Nilsson
 *
 */
public class AlanTextHover implements ITextHover {

    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        IAnnotationModel model = ((SourceViewer)textViewer).getAnnotationModel();
        Iterator<?> annotations = model.getAnnotationIterator();
        while (annotations.hasNext()) {
            Annotation annotation = (Annotation)annotations.next();
            String type = annotation.getType();
            if (annotation instanceof MarkerAnnotation) {
                MarkerAnnotation markerAnnotation = (MarkerAnnotation)annotation;
                IMarker marker = markerAnnotation.getMarker();
                if (type.startsWith("org.eclipse.ui.workbench.texteditor")) {
                    int markerStart = marker.getAttribute(IMarker.CHAR_START, 0);
                    int markerEnd = marker.getAttribute(IMarker.CHAR_END, 0);
                    if (markerStart == hoverRegion.getOffset()
                            && markerEnd == hoverRegion.getOffset()+ hoverRegion.getLength() - 1)
                        return (String)marker.getAttribute(IMarker.MESSAGE, null);
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
     */
    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        IAnnotationModel model = ((SourceViewer)textViewer).getAnnotationModel();
        Iterator<?> annotations = model.getAnnotationIterator();
        while (annotations.hasNext()) {
            Annotation annotation = (Annotation)annotations.next();
            String type = annotation.getType();
            if (annotation instanceof MarkerAnnotation) {
                MarkerAnnotation markerAnnotation = (MarkerAnnotation)annotation;
                IMarker marker = markerAnnotation.getMarker();
                if (type.startsWith("org.eclipse.ui.workbench.texteditor")) {
                    int markerStart = marker.getAttribute(IMarker.CHAR_START, 0);
                    int markerEnd = marker.getAttribute(IMarker.CHAR_END, 0);
                    if (markerStart <= offset && markerEnd >= offset)
                        return new Region(markerStart, markerEnd-markerStart+1);
                }
            }
        }
        return null;
    }

}
