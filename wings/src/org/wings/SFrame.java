/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
 *
 * This file is part of wingS (http://j-wings.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

package org.wings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.event.InvalidLowLevelEvent;
import org.wings.event.SInvalidLowLevelEventListener;
import org.wings.event.SRenderListener;
import org.wings.plaf.FrameCG;
import org.wings.script.JavaScriptListener;
import org.wings.script.ScriptListener;
import org.wings.session.Session;
import org.wings.session.SessionManager;
import org.wings.style.StyleSheet;
import org.wings.util.ComponentVisitor;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The frame is the root component in every component hierarchie.
 * A SessionServlet requires an instance of SFrame to render the page.
 * SFrame consists of some header informaton (meta, link, script)
 * and a stack of components. The bottommost component of the stack is always
 * the contentPane. When dialogs are to be shown, they are stacked on top of
 * it.
 *
 * @author <a href="mailto:hengels@mercatis.de">Holger Engels</a>,
 *         <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SFrame
    extends SRootContainer
    implements PropertyChangeListener {
    private static final Log logger = LogFactory.getLog("org.wings");

    /**
     * @see #getCGClassID()
     */
    private static final String cgClassID = "FrameCG";

    /**
     *  The Title of the Frame.
     */
    protected String title;

    /**
     * @see #setBaseTarget(String)
     */    
    protected String baseTarget = null;

    /**
     * A Set containing additional tags for the html header.
     */
    protected List headers;

    /**
     * @see #getLinkColor()
     */
    private Color linkColor;

    /**
     * @see #getVLinkColor()
     */
    private Color vLinkColor;
    
    /**
     * @see #getALinkColor()
     */
    private Color aLinkColor;

    private RequestURL requestURL = null;
    
    private String targetResource;

    private HashMap dynamicResources;

    private SComponent focusComponent = null;                //Component which requests the focus

    private JavaScriptListener focus = null;          //Listener which sets the focus onload
    
    /** @see #setBackButton(SButton) */
    private SButton backButton;

    /** @see #fireDefaultBackButton() */
    private long defaultBackButtonLastPressedTime;
    
    /** the style sheet used in certain look and feels. */
    protected StyleSheet styleSheet;  // IMPORTANT: initialization with null causes errors;
    // These: all properties, that are installed by the plaf, are installed during the initialization of
    // SComponent. The null initializations happen afterwards and overwrite the plaf installed values.
    // However: null is the default initialization value, so this is not a problem!
    // The same applies to all descendants of SComponent!
    
    /**
     * @see #setNoCaching(boolean)
     */
    private boolean noCaching = true;
    
    /** 
     * For performance reasons.
     * @see #fireInvalidLowLevelEventListener
     */
    private boolean fireInvalidLowLevelEvents = false;        

    private boolean resizable = true;
    
    /**
     * Creates a new SFrame
     */
    public SFrame() {
        getSession().addPropertyChangeListener("lookAndFeel", this);
        getSession().addPropertyChangeListener("request.url", this);
    }

    /**
     * Creates a new SFrame
     *
     * @param title Title of this frame, rendered in browser window title
     */
    public SFrame(String title) {
        this();
        setTitle(title);
    }

    /**
     * Adds a dynamic ressoure.
     * @see #getDynamicResource(Class) 
     */
    public void addDynamicResource(DynamicResource d) {
        if (dynamicResources == null) {
            dynamicResources = new HashMap();
        }
        dynamicResources.put(d.getClass(), d);
    }

    /**
     * Adds a dynamic ressoure of given class.
     * @see #getDynamicResource(Class) 
     * @param dynamicResourceClass Class of dynamic ressource to remove
     */
    public void removeDynamicResource(Class dynamicResourceClass) {
        if (dynamicResources != null) {
            dynamicResources.remove(dynamicResourceClass);
        }
    }

    /**
     * Severeral Dynamic code Ressources are attached to a <code>SFrame</code>. 
     * <br>See <code>Frame.plaf</code> for details, but in general you wil find attached 
     * to every <code>SFrame</code> a
     * <ul><li>A {@link DynamicCodeResource} rendering the HTML-Code of all SComponents inside this frame.
     * <li>A {@link org.wings.script.DynamicScriptResource} rendering the attached (Java-)Scripts of all SComponents 
     * into an external file and including them by a link tag into the rendered frame.
     * <li>A {@link org.wings.style.DynamicStyleSheetResource} rendering the CSS attributes 
     * of all SComponents inside this frame into an external file with CSS classes.
     * </ul>  
     */
    public DynamicResource getDynamicResource(Class c) {
        if (dynamicResources == null) {
            dynamicResources = new HashMap();
        }
        return (DynamicResource) dynamicResources.get(c);
    }
    
    /**
     * Return <code>this</code>.
     *
     * @return this.
     */
    public SFrame getParentFrame() {
        return this;
    }

    /**
     * A String with the current epoch of this SFrame. Provided by the 
     * {@link DynamicCodeResource} rendering this frame.
     *
     * @return A String with current epoch. Increased on every invalidation.
     */
    public String getEventEpoch() {
        return getDynamicResource(DynamicCodeResource.class).getEpoch();
    }

    /**
     * Set server address.
     */
    public final void setRequestURL(RequestURL requestURL) {
        this.requestURL = requestURL;
    }

    /**
     * Returns the base URL for a request to the WingsServlet. This URL
     * is used to assemble an URL that trigger events. In order to be used
     * for this purpose, you've to add your parameters here.
     */
    public final RequestURL getRequestURL() {
        RequestURL result = null;
        // first time we are called, and we didn't get any change yet
        if (requestURL == null) {
            requestURL = (RequestURL) SessionManager.getSession().getProperty("request.url");
        }
        if (requestURL != null) {
            result = (RequestURL) requestURL.clone();
            result.setResource(getTargetResource());
        }
        return result;
    }

    /**
     * Set the target resource
     */
    public void setTargetResource(String targetResource) {
        this.targetResource = targetResource;
    }

    /**
     * Every externalized ressource has an id. A frame is a <code>DynamicCodeResource</code>.
     *
     * @return The id of this <code>DynamicCodeResource</code>
     */
    public String getTargetResource() {
        if (targetResource == null) {
            targetResource = getDynamicResource(DynamicCodeResource.class).getId();
        }
        return targetResource;
    }

    /**
     * Set the base target. This is the target of any link pressed.
     */
    public void setBaseTarget(String baseTarget) {
        this.baseTarget = baseTarget;
    }

    /**
     * Set the base target frame. This frame will receive all klicks
     * in this frame. Usually you want to use this for the ReloadManager
     * frame.
     */
    public void setBaseTarget(SFrame otherFrame) {
        /*
         * this knows, that the frames are usually named "frame" + ID
         */
        setBaseTarget("frame" + otherFrame.getComponentId());
    }

    public String getBaseTarget() {
        return baseTarget;
    }

    /**
     * Add an {@link Renderable}  into the header of the HTML page
     * @param m is typically a {@link org.wings.header.Link} or {@link DynamicResource}.
     * @see org.wings.header.Link
     * @see org.wings.script.DynamicScriptResource
     * @see DynamicCodeResource
     */
    public void addHeader(Object m) {
        if (!getHeaders().contains(m))
            headers.add(m);
    }

    /** @see #addHeader(Object) */
    public void removeHeader(Object m) {
        headers.remove(m);
    }

    /** 
     * Removes all headers. Be carful about what you do!
     * @see #addHeader(Object) 
     */
    public void clearHeaders() {
        getHeaders().clear();
    }

    /**
     * @see #addHeader(Object)
     */
    public List getHeaders() {
        if (headers == null)
            headers = new ArrayList(2);
        return headers;
    }

    /**
     * Sets the title of this HTML page. Typically shown in the browsers window title.
     * 
     * @param title The window title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Title of this HTML page. Typically shown in the browsers window title.
     *
     * @return Current page title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Default color for <b>unvisited</b> HTML Links (specified in HTML <code>BODY</code> tag).
     *
     * @param c The link color
     */
    public void setLinkColor(Color c) {
        linkColor = c;
    }

    /**
     * Default color for <b>unvisited</b> HTML Links (specified in HTML <code>BODY</code> tag).  
     *
     * @return Default color for HTML Links (specified in HTML <code>BODY</code> tag)
     */
    public Color getLinkColor() {
        return linkColor;
    }

    /**
     * Default color for <b>visited</b> HTML Links (specified in HTML <code>BODY</code> tag)  
     *
     * @param c Default color for <b>visited</b> HTML Links (specified in HTML <code>BODY</code> tag)
     */
    public void setVLinkColor(Color c) {
        vLinkColor = c;
    }

    /**
     * Default color for <b>visited</b> HTML Links (specified in HTML <code>BODY</code> tag)  
     *
     * @return Default color for <b>visited</b> HTML Links (specified in HTML <code>BODY</code> tag)
     */
    public Color getVLinkColor() {
        return vLinkColor;
    }

    /**
     * Default color for <b>active</b> HTML Links (specified in HTML <code>BODY</code> tag)  
     *
     * @param c Default color for <b>active</b> HTML Links (specified in HTML <code>BODY</code> tag)
     */
    public void setALinkColor(Color c) {
        aLinkColor = c;
    }

    /**
     * Default color for <b>active</b> HTML Links (specified in HTML <code>BODY</code> tag)  
     *
     * @return Default color for <b>active</b> HTML Links (specified in HTML <code>BODY</code> tag)
     */
    public Color getALinkColor() {
        return aLinkColor;
    }

    /**
     * Sets the style sheet used in certain look and feels.
     *
     * @param s the style sheet used in certain look and feels.
     */
    public void setStyleSheet(StyleSheet s) {
        styleSheet = s;
    }

    /**
     * Returns the style sheet used in certain look and feels.
     *
     * @return the style sheet used in certain look and feels.
     */
    public StyleSheet getStyleSheet() {
        return styleSheet;
    }

    /**
     * @return <code>true</code> if the generated HTML code of this frame/page should 
     * not be cached by browser, <code>false</code> if no according HTTP/HTML headers
     * should be rendered
     * @see #setNoCaching(boolean)
     */
    public boolean isNoCaching() {
        return noCaching;
    }

    /**
     * Typically you don't want any wings application to operate on old 'views' meaning
     * old pages. Hence all generated HTML pages (<code>SFrame</code> objects
     * rendered through {@link DynamicCodeResource} are marked as <b>do not cache</b>
     * inside the HTTP response header and the generated HTML frame code.
     * <p>If for any purpose (i.e. you a writing a read only application) you want
     * th user to be able to work on old views then set this to <code>false</code>
     * and Mark the according <code>SComponent</code>s to be not epoch checked 
     * (i.e. {@link SAbstractButton#setEpochChecking(boolean)})
     * 
     * @see LowLevelEventListener#isEpochChecking()
     * @see org.wings.session.LowLevelEventDispatcher
     * 
     * @param noCaching The noCaching to set.
     */
    public void setNoCaching(boolean noCaching) {
        this.noCaching = noCaching;
    }

    /** 
     * Shows this frame. This means it gets registered at the session.
     * @see Session#getFrames()
     */ 
    public void show() {
        setVisible(true);
    }

    /** 
     * Hides this frame. This means it gets removed at the session.
     * @see Session#getFrames() 
     */
    public void hide() {
        setVisible(false);
    }

    /** 
     * Shows or hide this frame. This means it gets (un)registered at the session.
     * @see Session#getFrames()
     */ 
    public void setVisible(boolean b) {
        if (b) {
            getSession().addFrame(this);
        } else {
            getSession().removeFrame(this);
        }
        super.setVisible(b);
    }
    
    public boolean isResizable() {
        return this.resizable;
    }
    
    public void setResizable ( boolean resizable ) {
        this.resizable = resizable;
    }    

    public void propertyChange(PropertyChangeEvent pe) {
        if ("lookAndFeel".equals(pe.getPropertyName())) {
            updateComponentTreeCG(getContentPane());
        }
        if ("request.url".equals(pe.getPropertyName())) {
            setRequestURL((RequestURL) pe.getNewValue());
        }
    }

    private void updateComponentTreeCG(SComponent c) {
        c.updateCG();
        if (c instanceof SContainer) {
            SComponent[] children = ((SContainer) c).getComponents();
            for (int i = 0; i < children.length; i++) {
                updateComponentTreeCG(children[i]);
            }
        }
        updateCG();
    }

    public String getCGClassID() {
        return cgClassID;
    }

    public void setCG(FrameCG cg) {
        super.setCG(cg);
    }

    public void invite(ComponentVisitor visitor)
        throws Exception {
        visitor.visit(this);
    }

    /**
     * Adds a scriptListener to the body-tag, which sets the focus to the Component
     * of the frame which requests the focus at last.
     */
    public void setFocus() {
        StringBuffer compId;
        String formName = null;
        
        if (focusComponent != null) {
            try {
                this.removeScriptListener(focus);
            } catch (Exception e) {
            }

            final SForm form = focusComponent.getParentForm();
            if (form != null)
                formName = form.getName();
            
            if (formName == null) {
                logger.warn("attempt to request focus on element that is within an SForm without a name. Call setName(String) on that SForm first.");
            }

            compId = new StringBuffer(focusComponent.getComponentId());

            focus = new JavaScriptListener("onload", "gainFocus('" + compId + "');");
            addScriptListener(focus);

            focusComponent = null;
        }

    }

    /**
     * This function is called by SComponent.requestFocus().
     * @param c the component which requests the focus.
     *
     */
    public void focusRequest(SComponent c) {
        focusComponent = c;
    }

    /**
     * Everytime a frame ist reloaded, this Method is called
     * by org.wings.plaf.css1.Util. Then setFocus must be called,
     * bacause the ids of the Components change at that time.
     */
    public ScriptListener[] getScriptListeners() {
        setFocus();
        return (ScriptListener[]) super.getListeners(ScriptListener.class);
    }
    
    /** 
     * Registers an {@link SInvalidLowLevelEventListener} in this frame.
     * @param l The listener to notify about outdated reqests
     * @see InvalidLowLevelEvent
     */
    public final void addInvalidLowLevelEventListener(SInvalidLowLevelEventListener l) {
        addEventListener(SInvalidLowLevelEventListener.class, l);
        fireInvalidLowLevelEvents = true;
    }

    /** 
     * Removes the passed {@link SInvalidLowLevelEventListener} from this frame.
     * @param l The listener to remove
     * @see InvalidLowLevelEvent
     */
    
    public final void removeDispatchListener(SInvalidLowLevelEventListener  l) {
        removeEventListener(SRenderListener.class, l);
    }  

    /**
     * Notify all {@link SInvalidLowLevelEventListener} about an outdated request 
     * on the passed component
     * @param source The <code>SComponent</code> received an outdated event
     * @see org.wings.session.LowLevelEventDispatcher
     */
    public final void fireInvalidLowLevelEventListener(LowLevelEventListener source) {
        if (fireInvalidLowLevelEvents) {
            Object[] listeners = getListenerList();
            InvalidLowLevelEvent e = null; 
            for (int i = listeners.length - 2; i >= 0; i -= 2) {                
                if (listeners[i] == SInvalidLowLevelEventListener.class) {
                    // Lazily create the event:
                    if (e == null) 
                        e = new InvalidLowLevelEvent(source);
                    ((SInvalidLowLevelEventListener)listeners[i + 1]).invalidLowLevelEvent(e);
                }
            }
        } 
        fireDefaultBackButton();
    }    
    
    
    
    /**
     * A button activated on detected browser back clicks.
     * @return Returns the backButton.
     * @see #setBackButton(SButton)
     */
    public SButton getBackButton() {
        return backButton;
    }

    /**
     * This button allows you to programattically react on Back buttons pressed in the browser itselfs.
     * This is a convenience method in contrast to {@link #addInvalidLowLevelEventListener(SInvalidLowLevelEventListener)}.
     * While the listener throws an event on every detected component receiving an invalid
     * request, this button is only activated if
     * <ul>
     * <li>Maximum once per request
     * <li>Only if some time passed by to avoid double-clicks to be recognized as back button clicks.
     * </ul>
     * <b>Note:</b> To work correctly you should set use GET posting 
     * {@link SForm#setPostMethod(boolean)} and use {@link SFrame#setNoCaching(boolean)} for
     * no caching. This will advise the browser to reload every back page.
     * @param defaultBackButton A button to trigger upon detected invalid epochs.
     */
    public void setBackButton(SButton defaultBackButton) {
        this.backButton = defaultBackButton;
    }
    
    /** Fire back button only once and if some time already passed by to avoid double clicks. */
    private void fireDefaultBackButton() {
        final int TIME_TO_ASSUME_DOUBLECLICKS_MS = 750;
        if (this.backButton != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - defaultBackButtonLastPressedTime > TIME_TO_ASSUME_DOUBLECLICKS_MS) {
                // Simulate a button press
                backButton.processLowLevelEvent(null, new String[] { "1" });
            }
            defaultBackButtonLastPressedTime = currentTime;
        }
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
