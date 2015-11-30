// Daniel Lopez - dql5295@psu.edu
// Meeting Scheduling System [Meeting] - 11-7-2015
package mss;
import java.util.ArrayList;
import java.io.Serializable;

public class Meeting implements Serializable {
    private final boolean isNull;
    private ArrayList<Person> members;
    private Time time;
    private int meetingID;
    private static int generatorID = 0; // static variable to generate unique IDs for meetings
    private int roomNumber;
 
    public Time getTime() {
        return time;
    }
    public Person findPerson(String firstName, String lastName) { // finds person in meeting, returns null if not found
        for (Person person : members) 
            if (person.getFirstName().equals(firstName))
                if (person.getLastName().equals(lastName))
                    return person;
        return null;
    }
    
    Meeting(ArrayList<Person> members, Time time, int roomNumber) {
        this(time, roomNumber);
        for (Person person : members)
            this.members.add(person);
    }    
    Meeting(Person[] members, Time time, int roomNumber) {
        this(time, roomNumber);
        for (Person person : members) 
            this.members.add(person);
    }   
    Meeting(Time time, int roomNumber) {
        this.members = new ArrayList();
        this.time = time;
        this.meetingID = ++generatorID; // assures unique ID for each meeting generated
        this.roomNumber = roomNumber;
        this.isNull = false;
    }
    Meeting(boolean nullify) {
        this.isNull = true;
    } // creates a null Meeting (placeholder)
   
    public boolean isNull() {
        return this.isNull;
    }
    
    public int getMeetingID() {
        return meetingID;
    }
    public int getRoomNumber() {
        return roomNumber;
    }
    public int countMembers() {
        int size = 0;
        if (members != null) // if members is null, accessing will cause an error
            size = members.size();
        return size;
    }
    public ArrayList<Person> getMembers() {
        return members;
    }
    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
    
    public boolean addPerson(Person person) {
        for (Person guy : members)
            if (!person.getFirstName().equals(guy.getFirstName()) || !person.getLastName().equals(guy.getLastName())) {
                members.add(person);
                return true;
            }
        return false;
    }    
    public boolean removePerson(Person person) {
        for (Person guy : members)
            if (person.getFirstName().equals(guy.getFirstName()))
                if (person.getLastName().equals(guy.getLastName())) {
                    members.remove(person);
                    return true;
                }
        return false;
    }
    
    @Override 
    public String toString() {
        String message = ""; // initialize to nothing in case members are null and message is never set to anything
        if (!isNull()) { // 
            message = String.format("Meeting #%d at %s with %d people", meetingID, getTime(), countMembers());
        } else 
            message = "Null Meeting";
        return message;

    }
    
    public String getDetailedString() {
        String message = ""; // initialize to nothing in case members are null and message is never set to anything
        if (members != null) {
            message = String.format("Meeting #%d at %s with %d people at room #%d%n", meetingID, getTime(), countMembers(), roomNumber);
                    
            for (Person p : members) 
                message = message.concat(String.format("\t%s attending..%n", p));
        }
        return message;
}
}
