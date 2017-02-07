package se.alanif.alanide.editors.scanners;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import se.alanif.alanide.editors.AlanColorManager;

public abstract class AbstractAlanScanner extends RuleBasedScanner {

    protected AlanColorManager colorManager = null;
    protected Map<String, IToken> coloringTokens = new HashMap<String, IToken>();
    
    protected static RGB preferenceColor(String colorPreference) {
        IPreferencesService service = Platform.getPreferencesService();
        String preferenceValue = service.getString("se.alanif.alanide", colorPreference, null, null);
        return StringConverter.asRGB(preferenceValue);
    }
    
    public void adaptToColorChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (coloringTokens.containsKey(property)) {
            Token coloringToken = (Token) coloringTokens.get(property);
            RGB rgb = null;
            Object value = event.getNewValue();
            if (value instanceof RGB)
                rgb = (RGB) value;
            else if (value instanceof String)
                rgb = StringConverter.asRGB((String) value);
            if (rgb != null) {
                Color color = colorManager.getColor(property);
                if (color == null || !rgb.equals(color.getRGB())) {
                    colorManager.unbindColor(property);
                    colorManager.bindColor(property, rgb);
    
                    color= colorManager.getColor(property);
                }
                Object data = coloringToken.getData();
                if (data instanceof TextAttribute) {
                    TextAttribute oldAttr = (TextAttribute) data;
                    coloringToken.setData(new TextAttribute(color, oldAttr.getBackground(), oldAttr.getStyle()));
                }
            }
        }
    }

}
