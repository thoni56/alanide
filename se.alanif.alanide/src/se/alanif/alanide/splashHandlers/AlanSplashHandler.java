
package se.alanif.alanide.splashHandlers;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.branding.IProductConstants;
import org.eclipse.ui.splash.BasicSplashHandler;

public class AlanSplashHandler extends BasicSplashHandler {

	private PaintListener painter = new PaintListener() {
		public void paintControl(PaintEvent e) {
			e.gc.setForeground(getForeground());
			Device display = e.gc.getDevice();
			Font font = new Font(display, "Arial", 14, SWT.BOLD | SWT.ITALIC); 
			e.gc.setFont(font);
			e.gc.drawString("v"+Platform.getBundle("se.alanif.alanide").getHeaders().get("Bundle-Version"), 10, 10, true);
			font.dispose();
		}
	};

	public void init(Shell splash) {
		super.init(splash);
		String progressRectString = null;
		String messageRectString = null;
		String foregroundColorString = null;
		IProduct product = Platform.getProduct();

		if (product != null) {
			progressRectString = product.getProperty(IProductConstants.STARTUP_PROGRESS_RECT);
			messageRectString = product.getProperty(IProductConstants.STARTUP_MESSAGE_RECT);
			foregroundColorString = product.getProperty(IProductConstants.STARTUP_FOREGROUND_COLOR);
		}
		
		createProgressRect(progressRectString);
		createMessageRect(messageRectString);
		setForegroundColor(foregroundColorString);

		setVersionPainter(painter);

		getContent(); // ensure creation of the progress
	}

	private void setVersionPainter(PaintListener painter) {
		getContent().addPaintListener(painter);
	}

	private void setForegroundColor(String foregroundColorString) {
		int foregroundColorInteger;
		try {
			foregroundColorInteger = Integer.parseInt(foregroundColorString, 16);
		} catch (Exception ex) {
			foregroundColorInteger = 0xD2D7FF; // off white
		}
		setForeground(new RGB((foregroundColorInteger & 0xFF0000) >> 16, (foregroundColorInteger & 0xFF00) >> 8, foregroundColorInteger & 0xFF));
	}

	private void createMessageRect(String messageRectString) {
		Rectangle messageRect = StringConverter.asRectangle(messageRectString, new Rectangle(10, 35, 300, 15));
		setMessageRect(messageRect);
	}

	private void createProgressRect(String progressRectString) {
		Rectangle progressRect = StringConverter.asRectangle(progressRectString, new Rectangle(10, 10, 300, 15));
		setProgressRect(progressRect);
	}
}
