package se.alanif.alanide.ui;

import java.util.Vector;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

public class OverlayImageDescriptor extends CompositeImageDescriptor {

	private class Overlay {
		protected int location;
		protected ImageDescriptor descriptor;

		public Overlay(ImageDescriptor overlayDescriptor, int location) {
			this.descriptor = overlayDescriptor;
			this.location = location;
		}
	}
	
	private Image baseImage;

	private Point sizeOfImage;

	private Vector<Overlay> overlays = new Vector<Overlay>();

	/**
	 * Constructor for overlayImageIcon.
	 */
	public OverlayImageDescriptor(ImageDescriptor descriptor) {
		// Base image of the object
		this.baseImage = descriptor.createImage();
		sizeOfImage = new Point(baseImage.getBounds().width, 
				baseImage.getBounds().height);
	}


	public void addOverlay(ImageDescriptor overlayDescriptor, int location) {
		overlays.add(new Overlay(overlayDescriptor, location));
	}


	/**
	 * @see org.eclipse.jface.resource.CompositeImageDescriptor#drawCompositeImage(int, int)
	 * DrawCompositeImage is called to draw the composite image.
	 * 
	 */
	protected void drawCompositeImage(int arg0, int arg1) {
		// Draw the base image
		drawImage(baseImage.getImageData(), 0, 0); 
		for (int i=0; i < overlays.size(); i++) {
			Overlay overlay = overlays.get(i);
			ImageData imageData = overlay.descriptor.getImageData();
			switch(overlay.location) {
			case IDecoration.TOP_LEFT:
				drawImage(imageData, 0, 0);
				break;

			case IDecoration.TOP_RIGHT:
				drawImage(imageData, sizeOfImage.x - imageData.width, 0);
				break;

			case IDecoration.BOTTOM_LEFT:
				drawImage(imageData, 0, sizeOfImage.y - imageData.height);
				break;

			case IDecoration.BOTTOM_RIGHT:
				drawImage(imageData, sizeOfImage.x - imageData.width,
						sizeOfImage.y - imageData.height);
				break;

			}
		}

	}

	/**
	 * @see org.eclipse.jface.resource.CompositeImageDescriptor#getSize()
	 * get the size of the object
	 */
	protected Point getSize() {
		return sizeOfImage;
	}

	/**
	 * Get the image formed by overlaying different images on the base image
	 * 
	 * @return composite image
	 */ 
	public Image getImage() {
		return createImage();
	}

}
