package org.wingx.beans.beaneditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.wings.ResourceImageIcon;
import org.wings.SBorderLayout;
import org.wings.SButton;
import org.wings.SComponent;
import org.wings.SDialog;
import org.wings.SFlowLayout;
import org.wings.SFont;
import org.wings.SIcon;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.SResetButton;
import org.wings.SSeparator;
import org.wings.border.SBevelBorder;

/*
 * @author Holger Engels
 * @version $Revision$
 */

public class CustomEditorDialog
    extends SDialog
{
    private static final String TITLE_PREFIX = "Editor: ";

    private final SLabel titleLabel = new SLabel();
    private final SButton closeButton = new SButton("Close");
    private final SResetButton resetButton = new SResetButton("Reset");
    private SComponent editor;

    public CustomEditorDialog(SComponent editor) {
	this.editor = editor;
        setLayout(new SBorderLayout());
        setBorder(new SBevelBorder());

        SFont titleLabelFont = new SFont();
        titleLabelFont.setStyle(BOLD);
        titleLabelFont.setSize(2);
        titleLabel.setText(TITLE_PREFIX);
        titleLabel.setFont(titleLabelFont);

        SSeparator hrule = new SSeparator();
        //hrule.setShade(false);

        // SImageIcon spacer = new ResourceImageIcon(this.getClass(), "../../ui/resources/nothing.gif");
        SIcon spacer = new ResourceImageIcon("org/wings/icons/transdot.gif");
        //spacer.setWidth(600);
        //spacer.setHeight(1);
        //spacer.setStyle(null);

        SPanel headerPanel = new SPanel();
        headerPanel.add(titleLabel);
        headerPanel.add(hrule);
        headerPanel.add(new SLabel(spacer));

        closeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    hide();
		    fireActionPerformed("closed");
		}
	    });

        SPanel buttonPanel = new SPanel();
        buttonPanel.setLayout(new SFlowLayout());
        buttonPanel.setHorizontalAlignment(CENTER);
        buttonPanel.add(closeButton);
        // buttonPanel.add(new SSpacer(10));
        buttonPanel.add(resetButton);

        add(headerPanel, SBorderLayout.NORTH);
        add(editor, SBorderLayout.CENTER);
        add(buttonPanel, SBorderLayout.SOUTH);
    }

    public void setTitle(String title) {
	titleLabel.setText(TITLE_PREFIX + title);
    }
    public String getTitle() { return titleLabel.getText().substring(TITLE_PREFIX.length()); }
}
