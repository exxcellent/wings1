package ldap;

import java.util.EventListener;

public interface CellSelectionListener extends EventListener {
    void cellSelected(CellSelectionEvent e);

    /** @link dependency 
     * @stereotype receive*/
    /*#CellSelectionEvent lnkCellSelectionEvent;*/
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
