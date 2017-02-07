/*
 * Created on 2005-jun-05
 *
 */
package se.alanif.alanide.model;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


import se.alanif.alanide.AlanIDEPlugin;

public class AlanModelLabelProvider extends LabelProvider {

    public Image getImage(Object element) {
        ImageRegistry registry = AlanIDEPlugin.getDefault().getImageRegistry();
        if (element instanceof AlanModel) {
            AlanModel alanElement = (AlanModel) element;
            String icon = AlanIDEPlugin.ICON_PREFIX+"outline."+alanElement.getKind();
            return registry.get(icon);
        }        
        return null;
    }

    public String getText(Object element) {
        if (element instanceof AlanModel)
            return ((AlanModel)element).getName();
        return null;
    }

}
