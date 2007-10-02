/*
 * SFormattedTextField.java
 *
 * Created on 9. September 2003, 09:05
 */

package org.wings;

import java.text.Format;
import java.text.NumberFormat;
import java.text.DateFormat;
import java.awt.event.*;

import javax.swing.text.*;
import javax.swing.JFormattedTextField;
import org.wings.STextField;
import org.wings.script.*;

import org.wings.text.*;


/*
 * 
 *
 * @author  theresia
 */
public class SFormattedTextField extends STextField{

    
    private SAbstractFormatter formatter = null;
    
    
    public SFormattedTextField() {  
    }
    
    public SFormattedTextField(SAbstractFormatter formatter){
        this.formatter = formatter;
        formatter.install(this);
    }
    
    public void setValue( Object object ) {
        String string = null;
        if (formatter != null) string = this.formatter.valueToString( object );
        super.setText( string );  
    }
    
    public Object getValue() {
        Object returnValue = this.formatter.stringToValue( this.getText() );
        return returnValue;
    }
    
    // brauch man das?? So wohl nicht...
    public SFormattedTextField(Object value){
        super(value.toString());
    }
    
    public SAbstractFormatter getFormatter(){
        return formatter;
    }
    
    public void setFormatter(SAbstractFormatter formatter){
        if(this.formatter != null){
            this.formatter.uninstall(this);
        }
        this.formatter = formatter;
        formatter.install(this);
    }
    
    /*
     * When an ActionListener is added, the Formatter must be updated.
     */
    public void addActionListener(ActionListener listener){
        super.addActionListener(listener);
        if(formatter == null){
            formatter = new SDefaultFormatter();
            formatter.install(this);
        }
        else{
            formatter.updateFormatter();
        }
    }
        
}