package se.alanif.alanide.editors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ide.ResourceUtil;

import se.alanif.alanide.AlanIDEPlugin;
import se.alanif.alanide.ui.OverlayImageDescriptor;

/**
 * This was inspired by the JDT solution found in JavaEditorErrorTickUpdater
 */
public class AlanEditorLabelUpdater implements IResourceChangeListener {

	private static final String EDITOR_TITLE_ICON = "alan_doc";
	private AlanEditor editor;

	public AlanEditorLabelUpdater(AlanEditor editor) {
		Assert.isNotNull(editor);
		this.editor = editor;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_BUILD);
	}

	public void resourceChanged(IResourceChangeEvent event) {
		if (isMarkerChangeForResource(event, editor)) {
			/* Changes in markers on this resource, so re-decorate title image */
			decorate();
		}
	}

	private boolean isMarkerChangeForResource(IResourceChangeEvent event, AlanEditor editor) {
		boolean isMarkerChangeForThisResource;
		final IResource resource = ResourceUtil.getResource(editor.getEditorInput());
		final IPath path = resource.getFullPath();
		IResourceDelta delta = event.getDelta().findMember(path);
		isMarkerChangeForThisResource = (delta != null) && ((delta.getFlags() & IResourceDelta.MARKERS) != 0);
		return isMarkerChangeForThisResource;
	}

	public void decorate() {
		Shell shell = editor.getEditorSite().getShell();
		if (shell != null && !shell.isDisposed()) {
			shell.getDisplay().syncExec(new Runnable() {
				public void run() {
					Image decoratedImage = decorateImage(editor.getTitleImage(), getSeverity());
					editor.updatedTitleImage(decoratedImage);
				}
			});
		}
	}

	private Image decorateImage(Image titleImage, int severity) {
		final ImageRegistry registry = AlanIDEPlugin.getDefault().getImageRegistry();
		String key = createKey(severity);
		ImageDescriptor descriptor = AlanIDEPlugin.getImageDescriptor(key);
		if (descriptor != null)
			return descriptor.createImage();

		OverlayImageDescriptor overlayImageDescriptor = buildDecoratedImage(severity, key);
		registry.put(key, overlayImageDescriptor);
		return overlayImageDescriptor.createImage();
	}

	private String createKey(int severity) {
		String key;
		switch (severity) {
		case IMarker.SEVERITY_ERROR: key = EDITOR_TITLE_ICON + ".error"; break;
		case IMarker.SEVERITY_WARNING: key = EDITOR_TITLE_ICON + ".warning"; break;
		default: key = EDITOR_TITLE_ICON; break;
		}
		return key;
	}

	private OverlayImageDescriptor buildDecoratedImage(int severity, String key) {
		ImageDescriptor overlay = null;
		if (severity >= IMarker.SEVERITY_ERROR)
			overlay = AlanIDEPlugin.getImageDescriptor("ovr16.error_ovr");
		else if (severity == IMarker.SEVERITY_WARNING)
			overlay = AlanIDEPlugin.getImageDescriptor("ovr16.warning_ovr");
		ImageDescriptor baseImage = AlanIDEPlugin.getImageDescriptor(EDITOR_TITLE_ICON);
		OverlayImageDescriptor overlayIcon = new OverlayImageDescriptor(baseImage);

		if (overlay != null)
			overlayIcon.addOverlay(overlay, IDecoration.BOTTOM_LEFT);
		return overlayIcon;
	}

	private int getSeverity() {
		int severity = 0;
		try {
			final IResource resource = ResourceUtil.getResource(editor.getEditorInput());
			severity = resource.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			// Might be a project that is not open
		}
		return severity;
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		this.editor = null;
	}

}



