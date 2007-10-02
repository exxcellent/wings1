/*
 * @(#)SToolTip.java	03.10.2004
 *

 */


package org.wings;


/**
 * Used to display a ToolTip info for a Component.
 * This is the defautl davanced version against the normal tooltip and will result in
 * the generation of a JavaScript entry and onMouseOver on MouseOut usage
 * for Detailled Tootlip information @see JToolTip
 */
public class SToolTip extends SComponent {
    /**
     * @see #getUIClassID
     * @see #readObject
     */

    
    String tipText;
    SComponent component;

    /** Creates a tool tip. */
    public SToolTip() {
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return the string "ToolTipUI"
     * @see SComponent#getUIClassID
     * @see UIDefaults#getUI
     */
 /*   public String getUIClassID() {
        return uiClassID;
    }
*/

    /**
     * Sets the text to show when the tool tip is displayed.
     * The string <code>tipText</code> may be <code>null</code>.
     *
     * @param tipText the <code>String</code> to display
     * @beaninfo
     *    preferred: true
     *        bound: true
     *  description: Sets the text of the tooltip
     */
    public void setTipText(String tipText) {
        this.tipText = tipText;
        reload(ReloadManager.RELOAD_CODE);
    }

    /**
     * Returns the text that is shown when the tool tip is displayed.
     * The returned value may be <code>null</code>.
     *
     * @return the <code>String</code> that is displayed
     */
    public String getTipText() {
        return tipText;
    }

    /**
     * Specifies the component that the tooltip describes.
     * The component <code>c</code> may be <code>null</code>
     * and will have no effect.
     * <p>
     * This is a bound property.
     *
     * @param c the <code>SComponent</code> being described
     * @see SComponent#createToolTip
     */
    public void setComponent(SComponent c) {
        component = c;
        reload(ReloadManager.RELOAD_CODE);
    }

    /**
     * Returns the component the tooltip applies to.
     * The returned value may be <code>null</code>.
     *
     * @return the component that the tooltip describes
     *
     * @see SComponent#createToolTip
     */
    public SComponent getComponent() {
        return component;
    }

    protected void finalize() throws java.lang.Throwable {
      ToolTipManager.sharedInstance().unregisterComponent(this.getComponent()); /* To be sure to get the Map clean */
      super.finalize();
    }
    
}
