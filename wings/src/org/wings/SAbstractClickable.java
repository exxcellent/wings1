package org.wings;

import org.wings.plaf.ClickableCG;

/**
 * 
 * @author armin
 * created at 24.02.2004 13:07:02
 */
public abstract class SAbstractClickable
    extends SAbstractIconTextCompound
    implements ClickableRenderComponent {

    private static final String cgClassID = "ClickableCG";

    /**
     * Creates a new <code>SClickable</code> instance with the specified text
     * (left alligned) and no icon.
     *
     * @param text The text to be displayed by the label.
     */
    public SAbstractClickable(String text) {
        this(text, null, LEFT);
    }

    /**
     * Creates a new <code>SClickable</code> instance with no text and no icon.
     */
    public SAbstractClickable() {
        this((String) null);
    }

    /**
     * Creates a new <code>SClickable</code> instance with the specified icon
     * (left alligned) and no text.
     *
     * @param icon The image to be displayed by the label.
     */
    public SAbstractClickable(SIcon icon) {
        this(icon, LEFT);
    }

    /**
     * Creates a new <code>SClickable</code> instance with the specified icon
     * (alligned as specified) and no text.
     *
     * @param icon The image to be displayed by the clickable.
     * @param horizontalAlignment One of the following constants defined in
     *        <code>SConstants</code>:
     *        <code>LEFT</code>, <code>CENTER</code>, <code>RIGHT</code>.
     * @see SConstants
     */
    public SAbstractClickable(SIcon icon, int horizontalAlignment) {
        this(null, icon, horizontalAlignment);
    }

    /**
     * Creates a new <code>SClickable</code> instance with the specified icon
     * and the specified text (left alligned).
     *
     * @param text The text to be displayed by the SClickable.
     * @param icon The image to be displayed by the SClickable.
     */
    public SAbstractClickable(String text, SIcon icon) {
        setText(text);
        setIcon(icon);
        setHorizontalAlignment(LEFT);
    }

    /**
     * Creates a new <code>SClickable</code> instance with the specified icon
     * and the specified text (alligned as specified).
     *
     * @param text The text to be displayed by the SClickable.
     * @param icon The image to be displayed by the SClickable.
     * @param horizontalAlignment One of the following constants defined in
     *        <code>SConstants</code>:
     *        <code>LEFT</code>, <code>CENTER</code>, <code>RIGHT</code>.
     * @see SConstants
     */
    public SAbstractClickable(String text, SIcon icon, int horizontalAlignment) {
        setText(text);
        setIcon(icon);
        setHorizontalAlignment(horizontalAlignment);
    }

    /**
     * Creates a new <code>SClickable</code> instance with the specified text
     * (alligned as specified) and no icon.
     *
     * @param text The text to be displayed by the SClickable.
     * @param horizontalAlignment One of the following constants defined in
     *        <code>SConstants</code>:
     *        <code>LEFT</code>, <code>CENTER</code>, <code>RIGHT</code>.
     * @see SConstants
     */
    public SAbstractClickable(String text, int horizontalAlignment) {
        this(text, null, horizontalAlignment);
    }

    public abstract boolean isEpochChecking();

    public abstract SimpleURL getURL();

    public String getCGClassID() {
        return cgClassID;
    }

    public void setCG(ClickableCG cg) {
        super.setCG(cg);
    }
}
