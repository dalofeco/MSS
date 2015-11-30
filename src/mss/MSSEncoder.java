// Daniel Lopez - dql5295@psu.edu
// Meeting Scheduling System [MSSEncoder] - 11-8-15
package mss;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

public class MSSEncoder {
    public final static char separator = '|';
    public final static char attrIndicator = '<';
    public final static char attrEndIndicator = '>';
    public final static char classIndicator = '{';
    public final static char classEndIndicator = '}';
    public final static char personsIndicator = '[';
    public final static char personsEndIndicator = ']';
    
    public static String encodePerson(Person p) {
        String encodedPerson = "";
        encodedPerson = encodedPerson.concat(String.format("%c%s%c", classIndicator, 
                                             p.getClass().getName(), separator));
        
        encodedPerson = encodedPerson.concat(String.format("%c%s%c", attrIndicator, 
                                             p.getFirstName(), attrEndIndicator));
        
        encodedPerson = encodedPerson.concat(String.format("%c%s%c", attrIndicator,
                                                    p.getLastName(), attrEndIndicator));
        
        encodedPerson = encodedPerson.concat(String.format("%c%s%c", attrIndicator,
                                                    p.getPhoneNumber(), attrEndIndicator));
        
        encodedPerson = encodedPerson.concat(String.format("%c", classEndIndicator));
        
        return encodedPerson;
    }   
    public static Person decodePerson(String m) {
        Person person;
        String firstName = "", lastName = "", phoneNumber = "";
        int attrCount = 0;

        for (int i = 0; i < m.length(); i++) {
            if (i == 0) {
                if (m.charAt(0) != classIndicator)
                    throw new IllegalArgumentException("Invalid format in string provided! decodePerson()");
            } else if (i == 1) {
                String className = Person.class.getName();
                String readClassName = m.substring(1, 1+className.length());
                
                if (className.equals(readClassName))
                    i+= className.length();
                
            } else if (m.charAt(i) == separator) {
                // do nothing
            } 
            
            else if (m.charAt(i) == attrIndicator) {
                attrCount++; // increase attrCount to know which attr is being read
                int ii = i;
                while (ii < m.length() && m.charAt(ii) != attrEndIndicator) { // count how long the attr is
                    ii++;   // 
                }
                String attr = m.substring(i+1, ii); // get the attribute using substring
                
                if (attrCount == 1)
                    firstName = attr;
                else if (attrCount == 2)
                    lastName = attr;
                else if (attrCount == 3)
                    phoneNumber = attr;
                
            }   
        }
        if (!firstName.equals("") && !lastName.equals("") && !phoneNumber.equals("")) // ensure everything was read properly
            person = new Person(firstName, lastName, phoneNumber); // create person object
        else
            throw new IllegalArgumentException("Couldn't properly read from input! Error.");
        return person;
    }
    public static String encodeMeeting(Meeting m) {
        String encodedMeeting = "";
        ArrayList<Person> members = m.getMembers();
        
        if (!m.isNull()) { // verify m is not a placeholder or a null meeting
            encodedMeeting = encodedMeeting.concat(String.format("%c%s%c", classIndicator, 
                                                   m.getClass().getName(), separator)); // adds classname
            encodedMeeting = encodedMeeting.concat(String.format("%c%d%c", attrIndicator, 
                                                         m.getMeetingID(), attrEndIndicator)); // adds meeting ID as attr
            encodedMeeting = encodedMeeting.concat(String.format("%c", personsIndicator));
            for (Person p : members) 
                encodedMeeting = encodedMeeting.concat(encodePerson(p));
            encodedMeeting = encodedMeeting.concat(String.format("%c", personsEndIndicator));
            
            encodedMeeting = encodedMeeting.concat(String.format("%c", classEndIndicator));
        }
       
        return encodedMeeting;
    }    
    public static String encodeRoom(Room room) {
        String encodedRoom;
        ArrayList<Meeting> meetings = room.getMeetings();
        
        
        encodedRoom = String.format("%c%s%c", classIndicator, // add class name to string
                       room.getClass().getName(), separator); // surrounded by indicator chars
        encodedRoom = encodedRoom.concat(String.format("%c%d%c", attrIndicator, // add room number to String
                                           room.getRoomNumber(), attrEndIndicator));
                
        for (Meeting m : meetings) {
            if (!m.isNull()) { // true if m is not a placeholder and is a real meeting
                encodedRoom = encodedRoom.concat(encodeMeeting(m));
            }
        }
        
        encodedRoom = encodedRoom.concat(String.format("%c", classEndIndicator)); // adds a separator character
        
        return encodedRoom;
    }
    public static String encodeMSS(MSS mss) {
        String encodedMSS = "";
        encodedMSS = encodedMSS.concat(String.format("%c%s%c", classIndicator, // adds class name
                                    mss.getClass().getName(), separator));
        encodedMSS = encodedMSS.concat(String.format("%c", attrIndicator)); // adds attr indicator to store rooms
        for (Room room : mss.rooms) {                                       // as MSS's attr
            encodedMSS = encodedMSS.concat(encodeRoom(room)); // adds each encoded room
        }
        encodedMSS = encodedMSS.concat(String.format("%c%c", attrEndIndicator, attrIndicator));
        for (Person p : mss.people)
            encodedMSS = encodedMSS.concat(encodePerson(p));
        
        encodedMSS = encodedMSS.concat(String.format("%c%c", attrEndIndicator, classEndIndicator));
        
        return encodedMSS;
    }
    
    public static boolean writeToFile(Object obj) {
        boolean pass;
        String fileName = "hello";
        try {        
            
            String buffer = "";
            if (obj.getClass() == Room.class) {
                buffer = encodeRoom((Room)obj);
                fileName = fileName.concat(".ROOM");
            } else if (obj.getClass() == Person.class) {
                buffer = encodePerson((Person)obj);
                fileName = fileName.concat(".PRS");
            } else if (obj.getClass() == Meeting.class) {
                buffer = encodeMeeting((Meeting)obj);
                fileName = fileName.concat(".MEET");
            } else if (obj.getClass() == MSS.class) {
                buffer = encodeMSS((MSS)obj);
                fileName = fileName.concat(".MSS");
            }
            File file = new File(fileName);
            if (!file.exists())  // if the file doesn't exist, create it
                file.createNewFile();
            
            FileWriter fw = new FileWriter(file); 
            BufferedWriter bw = new BufferedWriter(fw);
            
            bw.write(buffer); // write the buffer to the file with BufferedWriter bw
            bw.close();
            
            pass = true;  // if got to here, successfully wrote
        } catch (IOException ex) {
            System.out.println(ex);
            pass = false;
        }
        return pass;
    }
    
    
}
