// Daniel Lopez - dql5295@psu.edu
// Meeting Scheduling System [Room] - 11-5-2015

package mss;
import java.io.Serializable;
import java.util.ArrayList;



public class Room implements Serializable {
    private int participants = 0;
    private final int roomNumber;
    private ArrayList<Meeting> meetings; // arraylist of size of however many business hours 
                                            // to store meetings
    
    public Room(int room) {
        final int size = Time.values().length; // nine possible scheduling times
        meetings = new ArrayList<Meeting>(size);
        for (int i = 0; i < size; i++)
            meetings.add(new Meeting(true)); // create empty placeholder null meetings
                
        if (room >= 0)
            roomNumber = room;
        else
            throw new IllegalArgumentException("Room number cannot be negative!");
    }
    
    public int countParticipantsAt(Time time) {
        return meetings.get(time.getIndex()).countMembers(); // returns number of participants
    }
    public void addParticipant(Person participant, Time time) {
        ArrayList<Person> people = meetings.get(time.getIndex()).getMembers(); // gets the people attending at time
        if (!people.contains(participant)) { // checks participant isnt already signed up
            people.add(participant); // add person to room at specified time
            participants++; // adds one to total counter
        } else
            System.out.println(String.format("Participant already in room %d at time %s",
                                                    roomNumber, time)); // participants not found
    }   
    public boolean removeParticipant(Person participant, Time time) {
        boolean success = false;
        if(meetings.get(time.getIndex()).removePerson(participant)) {
            participants--;
            success = true;
        }
        else
            System.out.println(String.format("Participant not found in room %d", roomNumber)); // participants not found
        
        return success;
    }
    public int countMeetings() {
        int count = 0;
        for (Meeting meeting : meetings)
            if (!meeting.isNull()) // if meeting is not null (actual meeting in place, not placeholder)
                count++;
        return count;
    }
    
    public boolean addMeeting(Meeting m, boolean overwrite) {
        boolean pass = false;
        if (!m.isNull()) {
            if (meetings.get(m.getTime().getIndex()).isNull() || overwrite) {  // since only one meeting per hour, the meeting index is the index
                meetings.remove(m.getTime().getIndex());   // of the time, so remove the current placeholder
                meetings.add(m);                            // and add the new real value
                pass = true;
            }
        }
        
        return pass;
    }
    
    public void addNullMeeting(int index) {
        meetings.add(index, new Meeting(true)); // adds a null meeting at index
    }
    
    public boolean removeMeeting(Meeting m) {
        boolean pass = false;
        int index = 0;
        for (Meeting meeting : meetings) { // check every meeting for equivalency with the passed in
            if (m.getMeetingID() == meeting.getMeetingID()) { // if the parameter meeting is found, (compare by unique IDs)
                meetings.remove(m); // delete it from arraylist
                addNullMeeting(index); // adds a null placeholder meeting at same index
                pass = true;
                break;
            }
            index++;
        }
        return pass;
    }
    
    public void wipe() {
        //participants = 0;
        System.out.println("Wipe has not been properly implemented yet!");
    }
    public int getRoomNumber() {
        return roomNumber;
    }
    public ArrayList<Meeting> getMeetings() {
        return meetings;
    }
    
    public Meeting[] getNonNullMeetings() {
        ArrayList<Meeting> meetings = new ArrayList();
        for (Meeting m : getMeetings()) {
            if (!m.isNull()) // if the meeting is not a null placeholder
                meetings.add(m); // add it to the meetings arraylist
        }
        
        Meeting []nonNullMeetings = new Meeting[meetings.size()]; // create normal array with size of arraylist
        for (int i = 0; i < meetings.size(); i++)
            nonNullMeetings[i] = meetings.get(i); // copy elements from arraylist to array
        
        return nonNullMeetings;
    }
    
    public String getDetailedRoomString () { //returns a string with all meetings being held in room
        String message = "";
        message = this.toString(); // add the simple toString description of room
        message = message.concat("\n"); // add a newline 
        for (Meeting m : meetings) 
            if (!m.isNull())  // m is a real meeting and not an empty placeholder
                message = message.concat(String.format("%s", m)); // append meeting printed to message        
        
        return message;
    }
    
    @Override
    public String toString() {
        return String.format("Room %d - %d meetings are taking place here.", getRoomNumber(), countMeetings());
    }
}
