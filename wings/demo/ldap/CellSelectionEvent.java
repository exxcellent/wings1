
package ldap;

import java.util.EventObject;

public class CellSelectionEvent extends EventObject
{
    private int x;
    private int y;

    public CellSelectionEvent(Object source, int x, int y)
    {
        super(source);
        this.x = x;
        this.y = y;
    }

    public int getXPosition()
    {
        return(x);
    }

    public int getYPosition()
    {
        return(y);
    }


}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
