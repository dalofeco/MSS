// Daniel Lopez - dql5295@psu.edu
// Meeting Scheduling System [Person] - 11-5-2015

package mss;
import java.io.Serializable;

public class Person implements Serializable {
    private final String name;
    private final String lastName;
    private final String phoneNumber;
    
    Person(String name, String lastName, String phone) {
        this.phoneNumber = formatPhone(phone);
        this.name = fixUpperCase(name); // fix uppercase capitalizes 
        this.lastName = fixUpperCase(lastName); // the first letter

    }
    
    public String getFirstName() {
        return name;
    }    
    public String getLastName() {
        return lastName;
    }   
    public String getPhoneNumber() {
        return phoneNumber;
    }
    private String formatPhone(String phone) {
        String fixedPhone;
        if (phone.length() == 10) {
            fixedPhone = String.format("%s%c%s%c%s", phone.substring(0,3), 
                                                '-', phone.substring(3, 6), 
                                                '-', phone.substring(6,10));
            // adds hyphens to look like: (123-456-7895)
        }
        else
            fixedPhone = phone;
        
        return fixedPhone;
    }  
    private String fixUpperCase(String name) {
        String fixedName;
        if (Character.toUpperCase(name.charAt(0)) == name.charAt(0))
            fixedName = name; // return the same, first character is already uppercase
        else
            fixedName = String.format("%c%s", 
                                    Character.toUpperCase(name.charAt(0)), 
                                    name.substring(1, name.length()));
        
        return fixedName;
        
    }
    @Override
    public String toString() {
        return String.format("%s %s, Phone: %s", name, lastName, phoneNumber);
    }
    
    public boolean equals(Person person2) {
        boolean pass = false;
        if (name.equals(person2.getFirstName()))
            if (lastName.equals(person2.getLastName()))
                if (phoneNumber.equals(person2.getPhoneNumber()))
                    pass = true;
        
        return pass;
    }
}
