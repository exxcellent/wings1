package ide;

import java.awt.event.*;
import java.util.*;
import java.util.logging.*;

import org.wingx.beans.beaneditor.*;

import org.wings.*;
import org.wings.event.*;


public class LogPanel
    extends SForm
{
    STextArea log;
    private Logger logger = Logger.getLogger("ide.LogPanel");

    public LogPanel() {
	log = new STextArea();
	log.setColumns(80);
	log.setRows(6);
	log.setEditable(false);
	log.setLineWrap(2);
        add(log);
    }

    public void appendText(String text) {
	log.setText(log.getText() + text);

	SInternalFrame frame = getFrame();
	if (frame != null) {
	    frame.setClosed(false);
	    frame.setIconified(false);
	}
	else
	    logger.warning("no reference to frame");
    }

    public void clearText() {
	log.setText("");
	SInternalFrame frame = getFrame();
	if (frame != null) {
	    frame.setIconified(true);
	}
	else
	    logger.warning("no reference to frame");
    }

    SInternalFrame frame;
    SInternalFrame getFrame() {
	if (frame == null) {
	    SComponent component = getParent();
	    while (!(component instanceof SInternalFrame))
		component = component.getParent();
	    frame = (SInternalFrame)component;
	}
	return frame;
    }
}
