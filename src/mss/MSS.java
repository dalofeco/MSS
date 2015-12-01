// Daniel Lopez - dql5295@psu.edu
// Meeting Scheduling System - 11-5-2015
package mss;
import java.io.Serializable;

import java.util.ArrayList;

public class MSS implements Serializable {
    public ArrayList<Room> rooms;
    public ArrayList<Person> people;
    
    MSS() {
        final int size = Time.values().length;  // number of time options in Time enum
        rooms = new ArrayList();
        people = new ArrayList();
    }
    
    MSS(int numberOfRooms) { // creates new MSS with numberOfRooms 
        this();
        for (int i = 1; i <= numberOfRooms; i++) 
            rooms.add(new Room(i)); // adds a new room with roomNumber of i
    }
    
    public ArrayList<Person> getPeople() {
        return people;
    }
    
    public boolean deletePerson(Person personToDelete) {
        
        return people.remove(personToDelete);
    }
    public boolean addPerson(Person p) {
        boolean pass = true;
        for (Person person : people)
            if (person.getFirstName().equals(p.getFirstName()))
                if (person.getLastName().equals(p.getLastName()))
                    pass = false;
        if (pass)
            people.add(p); // outside of for loop to assure single execution
        
        return pass;
    }
    
    public boolean updatePerson(Person originalP, Person newP) { // for when editing a person, updates entries in rooms
        boolean pass = false;
    
        Meeting []meetings = findMeetingsForPerson(originalP);  // gets all the meetings the original person was in
        for (int i = 0; i < meetings.length; i++) {
            meetings[i].removePerson(originalP);
            meetings[i].addPerson(newP);
            if (i == meetings.length-1)
                pass = true;
        }
        
        return pass;
    }
    
    public Person findPersonNamed(String firstName, String lastName) {
        Person subject = null; // person to be returned, null in case not found
        for (Person p : people) {
            if (firstName.equals(p.getFirstName()))
                if (lastName.equals(p.getLastName()))
                    subject = p;
        }
        return subject;
    }
    
    public void printAllRooms() {
        for (Room r : rooms) {
            System.out.println(r.getDetailedRoomString());
        }
    }
    
    public boolean createRoom(int roomNumber) {
        boolean success = false;
        if (roomNumber >= 0) { // checks that roomNumber is positive
            boolean pass = true;
            for (Room room : rooms) { // check every room to compare room number
                if (roomNumber == room.getRoomNumber()) // to avoid same numbers
                    pass = false;
            }
            if (pass) { // out of for to ensure single execution
                rooms.add(new Room(roomNumber)); // // roomNumber is unique, so create room
                success = true;
            }
            else
                System.out.println("The room number is already taken or is negative!");
        }
        
        return success;
    }
    public boolean deleteRoom(int roomNumber) {
        boolean success = false;
        if (roomNumber >= 0) {
            if (rooms.size() > 1)
                for (Room room : rooms) {
                    if (roomNumber == room.getRoomNumber()) {
                        success = rooms.remove(room); // removes room if found
                        break;
                    }
                }
            else if (rooms.size() == 1) // if only one left, just delete the only one
                success = rooms.remove(rooms.get(0)); // in there and avoid a arraylist iteration concurrent modification exception
        }
        return success;
    }

    public Room findRoom(int roomNumber) throws RoomNumberNotFoundException {
        Room theRoom = null;
        boolean pass = false;
        for (Room room : rooms) {       // for every room added so far
            if (room.getRoomNumber() == roomNumber) { // compares room numbers 
                theRoom = room;   
                pass = true;
                break;
            }
        }
        if (!pass) {
            
            throw new RoomNumberNotFoundException(String.format("Room %d not found!", roomNumber));
             // for compiler not to complain on possible empty return type
        }
        
        return theRoom;  // returns the room for the corresponding room number
    }
    
    // Function returns an array of Time with the times in which the person has a meeting in the room
    public ArrayList<Time> findPersonInRoom(String firstName, String lastName, int roomNumber) throws RoomNumberNotFoundException {
        ArrayList<Time> appearances = new ArrayList();
        Room theRoom = null;
        boolean pass = false;
        try {
            theRoom = findRoom(roomNumber);
            pass = true;
        } catch (RoomNumberNotFoundException ex) {
            System.out.println(String.format("%s findPersonInRoom()", ex));
        }
        
        if (pass)
            for (int i = 0; i < Time.values().length; i++) // for every time possibility
                for (Person person : theRoom.getMeetings().get(i).getMembers()) // for every person in theRoom at meeting at time i
                    if (person.getFirstName().equals(firstName))
                        if (person.getLastName().equals(lastName))
                            appearances.add(Time.indexToTime(i)); // if the name is the same, add time to return array
        
        return appearances;
    }
    
    public boolean addMeeting(Room room, Time time, boolean overwrite) {
        boolean success = false;
        Meeting meeting;
        if (verifyRoomNumberExists(room.getRoomNumber())) {
            meeting = new Meeting(time, room.getRoomNumber());
            if (room.addMeeting(meeting, overwrite))            
                success = true;
            else
                success = false;
        }
        return success;
    }
    
    public boolean addMeeting(Meeting meetingToAdd) {
        return addMeeting(meetingToAdd, false); // add meeting with no overwrite
    }
    
    public boolean addMeeting(Meeting meetingToAdd, boolean overwrite) {
        Time time = meetingToAdd.getTime();
        boolean pass = true;
        try {
            Room meetingRoom = findRoom(meetingToAdd.getRoomNumber());
            for (Meeting meeting : meetingRoom.getMeetings()) // for each meeting at the time passed in as parameter
                if (meeting.isNull())
                    ; // do nothing if meeting is null
                else if (meeting.getMeetingID() == meetingToAdd.getMeetingID())// if an equal already exists, dont add
                    pass = false;
                else if (meeting.getTime() == meetingToAdd.getTime()) // if the meetings in the same room are in same time
                    if (!overwrite) // and overwrite is not true
                        pass = false;  // then do not add the meeting
            
            if (pass) { // unique new meeting
                if (verifyRoomNumberExists(meetingToAdd.getRoomNumber())) { // make sure meetingToAdd has a real room number
                    meetingRoom.getMeetings().remove(time.getIndex());
                    meetingRoom.getMeetings().add(time.getIndex(), meetingToAdd); // adds new meeting to the corresponding list for the time

                } else // else if room number doesn't exist
                    throw new IllegalArgumentException("meetingToAdd has a non existant room number in MSS.addMeeting()");
            }
        } catch (RoomNumberNotFoundException ex) {
            pass = false;
            System.out.println(ex);
        }
        return pass;
    }
    
    public boolean removeMeeting(Meeting meetingToRemove) {
        boolean pass = false;
        for (Meeting m : getNonNullMeetingsArray()) {
            if (m.getMeetingID() == meetingToRemove.getMeetingID()) {  // compare meetings by meetingID which is always unique
                try {
                    findRoom(m.getRoomNumber()).removeMeeting(m);
                    pass = true;
                } catch (RoomNumberNotFoundException ex) {
                    System.out.println("Error. This should never happen." + ex);
                }
            }
        }
        
        return pass;
    }
    
    public boolean verifyRoomNumberExists(int roomNumber) {
        boolean found;
        try {
            findRoom(roomNumber);
            found = true;
        } catch (RoomNumberNotFoundException ex) {
            found = false;
        }
        return found;
    }
    
    public void changeMeetingRoom(Meeting original, Room newRoom) {
        Room oldRoom;
        Time time = original.getTime(); // gets the time of the meeting
        int timeIndex = time.getIndex(); // index equivalent of the meeting time
        try {
            oldRoom = findRoom(original.getRoomNumber()); // gets the original room for the meeting
            original.setRoomNumber(newRoom.getRoomNumber());
            oldRoom.getMeetings().remove(original);
            if (newRoom.getMeetings().get(timeIndex).isNull()) {// no meeting exists
                newRoom.getMeetings().remove(timeIndex);
                newRoom.getMeetings().add(timeIndex, original);
            } else {
                System.out.println("Meeting already exists at that time in new room! Overwriting!");
                newRoom.getMeetings().remove(timeIndex); // deletes old meeting
                newRoom.getMeetings().add(timeIndex, original); // to add new meeting
                // prompt for confirmation of overwriting existing meeting in room at time
            }   
        } catch (RoomNumberNotFoundException ex) {
            System.out.println(String.format("%s ; CRITICAL ERROR check MSS.changeMeetingRoom()", ex));
            // NOTE: THIS SHOULD NEVER EXECUTE
        }
         
    }
    
    public Meeting[] getMeetingsArray() {
        ArrayList<Meeting> allMeetings = new ArrayList();
        for (Room r : rooms)
            for (Meeting m : r.getMeetings()) // get all meetings in each room
                allMeetings.add((Meeting)m); // and add to ArrayList
        Meeting []array = new Meeting[allMeetings.size()];
        int i = 0; // counter index variable
        for (Meeting m : allMeetings) {
            array[i] = m; // copy the ArrayList to a Meeting[]
            i++;
        }
        
        return array;
    }
    
    public Meeting[] getNonNullMeetingsArray() {
        ArrayList<Meeting> nonNullMeetings = new ArrayList();
        for (Meeting m : getMeetingsArray())
            if (!m.isNull())  // if m is not a null meeting
                nonNullMeetings.add(m);  // add to the array
        
        Meeting[] meetings = new Meeting[nonNullMeetings.size()];
        int i = 0; // counter
        for (Meeting m : nonNullMeetings) {
            meetings[i] = m;
            i++;
        }
        
        // System.out.println(nonNullMeetings);
        // System.out.println(nonNullMeetings.size());
        
        return meetings;
    }
    
    public Meeting findMeeting(int meetingID) {
        Meeting[] meetings = getNonNullMeetingsArray();
        for (Meeting m : meetings)
            if (meetingID == m.getMeetingID())
                return m;
        return null;
    }
    
    public int countMeetings() {
        int count = 0;
            for (Meeting m : getNonNullMeetingsArray()) // and every meeting within every room
                    count++; // then add one to the count
        
        return count;
    }
    
    public Room[] getRoomsArray() {
        Room[] allRooms = new Room[rooms.size()];
        int i = 0; // counter for loop
        for (Room r : rooms) {
            allRooms[i] = r;
            i++;
        }
        
        return allRooms;
    }
    
    public Person[] getPeopleArray() {
        Person[] people = new Person[getPeople().size()]; // make normal array of size of arraylist with people
        int i = 0; // index for for loop
        for (Person p : getPeople()) {
            people[i] = getPeople().get(i); // convert arraylist of people to People[]
            i++;        
        }
        
        return people;
    }
    
    public Meeting[] findMeetingsForPerson(Person person) {
        ArrayList<Meeting> meetings = new ArrayList();
        Meeting [] allMeetings = getNonNullMeetingsArray(); // get all meetings (non null)
        
        for (Meeting m : allMeetings) { // for every meeting that is scheduled
            for (Person p : m.getMembers()) { // for every person in the meeting
                if (p.equals(person))  // if the person is in the meeting
                    meetings.add(m); // add to the meetings array
            }
        }
        
    
        
        Meeting []personMeetings = new Meeting[meetings.size()]; // create a new meeting[]
        for (int i = 0; i < meetings.size(); i++) // and copy over all the elements from
            personMeetings[i] = meetings.get(i);  // the meetings ArrayList
        
        return personMeetings;  // returns array of the meetings 
    }
    
    public static void main(String[] args) {
        GUINewMSSFrame newFrame = new GUINewMSSFrame();
        newFrame.setVisible(true);
    }
    
    class RoomNumberNotFoundException extends Exception {
        RoomNumberNotFoundException(String message) {
            super(message);
        }
    }
    
}
