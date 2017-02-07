/*
 * Created on 2005-jan-14
 *
 */
package se.alanif.alanide.views;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author Thomas Nilsson
 *
 */
public class AlanPerspectiveFactory implements IPerspectiveFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		// Get the editor area.
		String editorArea = layout.getEditorArea();

		// Top left: Resource Navigator view and Bookmarks view placeholder
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
		topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
		topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		
		// Bottom left: Outline view and Property Sheet view
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.50f, "topLeft");
		bottomLeft.addView(IPageLayout.ID_OUTLINE);
		bottomLeft.addView(IPageLayout.ID_PROP_SHEET);

		// Bottom right: Problem/Task List folder
		IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.8f, editorArea);
		bottomRight.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottomRight.addView(IPageLayout.ID_TASK_LIST);
		bottomRight.addView(IPageLayout.ID_PROGRESS_VIEW);
	}

}
