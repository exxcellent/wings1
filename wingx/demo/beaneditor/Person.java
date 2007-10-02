package beaneditor;

import java.beans.*;
import java.lang.reflect.*;

public class Person
{
    public Person() {}

    private String firstName;
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getFirstName() { return firstName; }

    private String lastName;
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getLastName() { return lastName; }

    private int age;
    public void setAge(int age) {
        this.age = age;
    }
    public int getAge() { return age; }

    private float weight;
    public void setWeight(float weight) {
        this.weight = weight;
    }
    public float getWeight() { return weight; }

    private boolean selfEmployed;
    public void setSelfEmployed(boolean selfEmployed) {
        this.selfEmployed = selfEmployed;
    }
    public boolean getSelfEmployed() { return selfEmployed; }

    public String toString() {
        StringBuffer buffer = new StringBuffer(getClass().getName());
        buffer.append("[");

        try {
            BeanInfo info = Introspector.getBeanInfo(getClass());
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();

            boolean first=true;
            for (int i=0; i < descriptors.length; i++) {
                try {
                    Method getter = descriptors[i].getReadMethod();
                    if (getter == null)
                        continue;
                    Object value = getter.invoke(this, null);
                    if (first)
                        first = false;
                    else
                        buffer.append(",");
                    buffer.append(descriptors[i].getName() + "=" + value);
                }
                catch (Exception e) {}
            }
        }
        catch (Exception e) {}

        buffer.append("]");
        return buffer.toString();
    }
}
