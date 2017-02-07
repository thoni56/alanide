package se.alanif.alanide.views;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class AlanLabelProblemDecorator extends LabelProvider implements ILightweightLabelDecorator {


	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof IResource) {
			try {
				int severity = ((IResource) element).findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
				if (severity >= IMarker.SEVERITY_ERROR)
					decoration.addOverlay(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui", "icons/full/ovr16/error_ovr.gif"), IDecoration.BOTTOM_LEFT);
				else if (severity == IMarker.SEVERITY_WARNING)
					decoration.addOverlay(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui", "icons/full/ovr16/warning_ovr.gif"), IDecoration.BOTTOM_LEFT);
			} catch (CoreException e) {
				// Might be a project that is not open
			}
		}
	}

}