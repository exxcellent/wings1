package ldap;

import java.util.Comparator;

class AttributeOrderComparator
    implements Comparator
{
    String string;

    public AttributeOrderComparator(String string) {
	this.string = string;
    }

    public int compare(Object o1, Object o2) {
	Row r1 = (Row)o1;
	Row r2 = (Row)o2;

	int pos1 = string.indexOf(r1.id);
	int pos2 = string.indexOf(r2.id);

	if (pos1 == -1 && pos2 != -1)
	    return 1;

	if (pos2 == -1 && pos1 != -1)
	    return -1;

	if (pos1 == -1 && pos2 == -1)
	    return (r1 == null) ? -1 : r1.id.compareTo(r2.id);

	return pos1 - pos2;
    }

    public boolean equals(Object obj) {
	if (!(obj instanceof AttributeOrderComparator))
	    return false;

	AttributeOrderComparator comp = (AttributeOrderComparator)obj;
	return string.equals(comp.string);
    }
}
