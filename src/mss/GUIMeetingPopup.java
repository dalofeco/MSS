// Daniel Lopez - dql5295@psu.edu
// Meeting Scheduling System [GUIMeetingPopup] - 11-11-2015
package mss;
import java.util.ArrayList;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;

// THIS CONSTRUCTOR SHOULD NEVER BE CALLED IF THE OWNER'S PEOPLE IS EMPTY, MANAGE IN MEETING CREATOR FUNCTION MSSFRAME
public class GUIMeetingPopup extends JDialog implements ActionListener, ItemListener {
    private final Container participantsContainer;
    private final JComboBox[] participantsComboBoxes;
    private final ArrayList<Person> valuesInParticipantsCB;

    private Time selectedTime;
    private int selectedRoom;
    private int availablePeople;
    
    private int shown; // keeps track of how many participants are being shown
    
    private final JComboBox<Time> timesComboBox;
    private final JComboBox<String> roomComboBox;
    
    private final JButton plusButton;
    private final JButton minusButton;
    private final JButton createButton;
    private final JButton cancelButton;
    
    private final int numOfPeople;
    private final MSS scheduler;
    
    private int meetingID;       // only used when editing, to keep track of which meeting is being edited
    private boolean privateChanges = false;  // keeps track of when class is changing the combo boxes to avoid
                                                   // having itemStateChanged() function executing code 
    private boolean editing = false;  // if true, this popup is being used for editing 
                                     // rather than creating a new meeting

    
        GUIMeetingPopup(MSSFrame owner) {
            this(owner, "New Meeting");
        }
    
        GUIMeetingPopup(MSSFrame owner, String title) {
            super(owner, title, true); // invokes superclass constructor with MODALITY (window is only active one)
            this.setSize(500, 500);
            
            scheduler = owner.getScheduler();
            availablePeople = -1; // -1 to allow for the first addPerson(), then after that refreshParticipantsComboBoxes() 
                                   // takes care of updating its value
                       
            setLayout(new BorderLayout());  // main layout
            
            // TIME CONTAINER
            FlowLayout layout1 = new FlowLayout();
            layout1.setAlignment(FlowLayout.CENTER);
            Container timeContainer = new Container();  // first container
            timeContainer.setLayout(layout1);  
            
            Time[] times = new Time[Time.getSize()];
            int index = 0;
            
            for (Time t : Time.values()) { // for every time option there is, 
                times[index] = t; // add the Time value to an array
                index++;  
            }
            
            timesComboBox = new JComboBox(times);
            timesComboBox.addItemListener(this);  // for refreshing of people on change
            
            selectedTime = times[0]; // sets the selectedTime variable to the first possibility
            
                        
            timeContainer.add(new JLabel("Time:"));
            timeContainer.add(timesComboBox);
            
            // END TIME CONTAINER
            // ROOM NUMBER CONTAINER
            final int NUM_OF_BLOCKS = 8;  // final int for number of visual blocks for Participans 
            
            Container roomNumberContainer = new Container();
            roomNumberContainer.setLayout(layout1);
                        
            index = 0; // reset the same index variable
            String[] roomNumbers = new String[owner.getScheduler().rooms.size()]; // size of however many rooms there are
            for (Room r : owner.getScheduler().rooms) {
                roomNumbers[index] = String.format("%d", r.getRoomNumber());
                index++;
            }
            
            roomComboBox = new JComboBox(roomNumbers); 
            roomComboBox.addItemListener(this); // to refresh people on change
            
            selectedRoom = Integer.parseInt(roomNumbers[0]); // sets the selectedRoom variable to the first possible room
            
            roomNumberContainer.add(new JLabel("Room Number: "));
            roomNumberContainer.add(roomComboBox);
            // EMD ROOM NUMBER CONTAINER
            // PARTICIPANTS CONTAINER
            participantsComboBoxes = new JComboBox[NUM_OF_BLOCKS];
            participantsContainer = new Container();
            numOfPeople = owner.getScheduler().people.size(); // get the number of people added to the MSS system
            
            final int participantBlockRows = (NUM_OF_BLOCKS / 2)+1; // 2 columns || and add one extra row for the title
            
            participantsContainer.setLayout(new GridLayout(participantBlockRows, 3));
            participantsContainer.add(new JLabel("Participants: "));
            participantsContainer.add(new Container());
            
            ArrayList<Person> participants = owner.getScheduler().getPeople(); // gets people available -- SHOULD NEVER BE EMPTY ARRAY
            
            int loopLimit;                      // have a dynamic loop limit to avoid creating blocks 
            if (numOfPeople < NUM_OF_BLOCKS)  // if there is not even enough
                loopLimit = numOfPeople;  // people to fill them
            else
                loopLimit = NUM_OF_BLOCKS;
            
            for (int i = 0; i < loopLimit; i++) {
                
                ArrayList<Person> newParticipants = new ArrayList(participants); // creates new arraylist based on participants array
                
                for (int ii = 0; ii < participants.size(); ii++)
                    if (isPersonBusyAtTimeInRoom(participants.get(ii), selectedTime, selectedRoom)) { // if person is busy
                        newParticipants.remove(participants.get(ii)); // then remove him from the participants to add to combo box
                    }
                
                participantsComboBoxes[i] = new JComboBox(newParticipants.toArray());
                
                participantsComboBoxes[i].addItemListener(this);

                if (i != 0)
                    participantsComboBoxes[i].setVisible(false);  // set all but the first invisible

                participantsContainer.add(participantsComboBoxes[i]);  // creates a new container with each participant
            }
            
            valuesInParticipantsCB = participants; // store as instance variable for use in refreshing the combo boxes
 
            String[] emptyArray = new String[1];
            emptyArray[0] = "";
            
            for (int i = loopLimit; i < NUM_OF_BLOCKS; i++) { // create empty placeholder combo boxes to keep the structure intact
                participantsComboBoxes[i] = new JComboBox(emptyArray);
                
                if (i != 0)
                    participantsComboBoxes[i].setVisible(false);  // set all but the first invisible
                participantsContainer.add(participantsComboBoxes[i]);
            }
            
            shown = 1; // sets shown to one since only one participant ComboBox is visible
            
            // END PARTICIPANTS CONTAINER
            // ADD PLUS AND MINUS PARTICIPANT BUTTONS
            Container buttonsContainer = new Container();
            
            FlowLayout buttonsLayout = new FlowLayout(); // create new flow layout with center allignment
            buttonsLayout.setAlignment(FlowLayout.CENTER);
            buttonsLayout.setHgap(60);
            buttonsContainer.setLayout(buttonsLayout);
            
            // create two buttons, one with plus and other with minus icons
            ImageIcon plusIcon = new ImageIcon(getClass().getClassLoader().getResource("plus-icon.png"));
            plusButton = new JButton(plusIcon);
            plusButton.setSize(125, 125);
            plusButton.setToolTipText("Add a person");
            plusButton.addActionListener(this);
            
            ImageIcon minusIcon = new ImageIcon(getClass().getClassLoader().getResource("minus-icon.png"));
            minusButton = new JButton(minusIcon);
            minusButton.setSize(125, 125);
            minusButton.setToolTipText("Delete a person");
            minusButton.addActionListener(this);
            
            buttonsContainer.add(plusButton);
            buttonsContainer.add(minusButton);
            // END BUTTONS CONTAINER
            
            // CREATE OR CANCEL BUTTONS
            Container finalButtonsContainer = new Container();
            
            // buttonsLayout is the same layout used for the buttons container
            FlowLayout finalButtonsLayout = new FlowLayout();
            finalButtonsLayout.setAlignment(FlowLayout.CENTER);
            finalButtonsLayout.setHgap(100);
            finalButtonsContainer.setLayout(finalButtonsLayout);
            
            createButton = new JButton("Create");
            createButton.addActionListener(this);
            
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(this);
            
            finalButtonsContainer.add(createButton);
            finalButtonsContainer.add(cancelButton);
           // END CREATE OR CANCEL (FINAL BUTTONS) CONTAINER
            
            Container topContainer = new Container();
            topContainer.setLayout(new GridLayout(2, 1));
            topContainer.add(timeContainer);
            topContainer.add(roomNumberContainer);
            
            Container bottomContainer = new Container();
            bottomContainer.setLayout(new BorderLayout());
            bottomContainer.add(buttonsContainer, BorderLayout.CENTER);
            bottomContainer.add(finalButtonsContainer, BorderLayout.SOUTH);
            
            add(topContainer, BorderLayout.NORTH);
            add(participantsContainer, BorderLayout.CENTER);
            add(bottomContainer, BorderLayout.SOUTH);
        }
        
        GUIMeetingPopup(MSSFrame owner, Meeting m) { // constructor to create popup for editing the meeting
            this(owner, String.format("Editing Meeting %d", m.getMeetingID()));
            editing = true;
            meetingID = m.getMeetingID();
            
            ArrayList<Person> members = m.getMembers();
            if (members.size() > 1)
                for (int i = 0; i < members.size(); i++) {
                    if (i != 0) 
                        addPerson(); // add a new field for a person
                    participantsComboBoxes[i].setSelectedItem(members.get(i)); // sets the participant boxes to the meeting selection
                }
            else if (members.size() == 1)
                participantsComboBoxes[0].setSelectedItem(members.get(0));
            else 
                System.err.println("members.size() is 0; GUIMeetingPopup(MSSFrame owner, Meeting m)");
            
            timesComboBox.setSelectedItem(m.getTime()); // select the appropriate time
            
            roomComboBox.setSelectedItem(String.format("%d", m.getRoomNumber())); // select the appropriate room
            
            createButton.setText("Save"); // change the label from "Create" to "Save"
            
        }
        
        
        private boolean isPersonBusyAtTimeInRoom(Person p, Time time, int roomNumber) {
            boolean busy = false;
            for (Room r : scheduler.rooms)  { // checks every room
                if (r.getRoomNumber() != roomNumber) { // avoid checking the room you selected since you can overwrite
                    Meeting meetingAtTime = r.getMeetings().get(time.getIndex());  // gets the meeting in the room at Time argument
                    if (!meetingAtTime.isNull())
                        for (Person member : meetingAtTime.getMembers()) // checks every member
                            if (p.equals(member)) // if any match, then assume the person is busy at that time
                                busy = true;
                    }
            }
            
            return busy;
        }

        
        private void refresh() {
            for (int i = 0; i < shown; i++)
                removePerson();
            
        }
        
        
    //    @SuppressWarnings("empty-statement") // suppress warnings for empty while loop body below
        // function refreshes the combo boxes' fields, not allowing repetitions or illegal values
    @SuppressWarnings("empty-statement")
        private void refreshParticipantsComboBoxes() {
             
            ArrayList<Person> elementsToAdd = new ArrayList();
            for (Person p : valuesInParticipantsCB)
                if (!isPersonBusyAtTimeInRoom(p, selectedTime, selectedRoom)) // assure person has no other meeting
                    elementsToAdd.add(p);
            
            availablePeople = elementsToAdd.size();

            if (shown == 1) { // if only one is shown, just add all options to the first
                Person rememberedSelection = (Person)participantsComboBoxes[0].getSelectedItem();
                participantsComboBoxes[0].removeAllItems();
                
                for (Person p : elementsToAdd)
                    participantsComboBoxes[0].addItem(p);
                
                if (elementsToAdd.contains(rememberedSelection)) // if the remembered selection is still available
                    participantsComboBoxes[0].setSelectedItem(rememberedSelection); // re-select it
                else
                    participantsComboBoxes[0].setSelectedIndex(0);
            }
            else {
                
                ArrayList<Person> taken = new ArrayList(); // stores what element index is selected
                ArrayList<Integer> takenBy = new ArrayList(); // stores what element selected the corresponding element in taken
                
                
                for (int i = 0; i < shown-1; i++) { // NOTE: the last box shown will be skipped to be dealt with later (since it has no selection yet)
                    Person newPerson = (Person)participantsComboBoxes[i].getSelectedItem();
                    if (!taken.contains(newPerson)) {  // only add as taken if it is the first instance
                        taken.add(newPerson); // store the Person for the selection
                        takenBy.add(i);
                    }
                }
                int count = shown-1;
                if (count >= elementsToAdd.size()) { // this means a whole refresh is necessary
                    refresh(); // refresh the people panels
                    return; 
                }
                while (taken.contains(elementsToAdd.get(count))) { // find a person that has not been taken
                    count--;  // save its index in count
                }
                
                taken.add(elementsToAdd.get(count));  // add the person found not to be taken
                takenBy.add(shown-1); // taken by the last box shown
                    

                assert shown == taken.size();   

                for (int i = 0; i < shown; i++) {
                    ArrayList<Person> newInfo = (ArrayList<Person>)elementsToAdd.clone(); // make a copy of the people to add, set as newInfo
                    for (int ii = 0; ii < taken.size(); ii++) {   // for every value that is selected 
                        if (i != takenBy.get(ii)) { // if not equal, means ComboBox at i did not take this value
                            int indexToDelete = newInfo.indexOf(taken.get(ii)); // store the index of the taken value to delete
                            if (indexToDelete > -1) // avoid negative numbers
                                newInfo.set(indexToDelete, null); // set each entry that should be deleted to null
                        }         // don't delete right away since deleting will change indexes for other entries and cause bugs
                    }
                    while(newInfo.remove((Person)null)); // removes every entry that is already selected by another combo box

                    Person rememberedSelection = (Person)participantsComboBoxes[i].getSelectedItem(); // gets the selected item to remember it
                   
                    //
                    participantsComboBoxes[i].removeAllItems();
                    
                    for (Person p : newInfo) {
                        participantsComboBoxes[i].addItem(p);
                    }

                    boolean pass = false;
                        if (taken.contains(rememberedSelection)) { // check if the original selection is now taken
                            if (takenBy.get(taken.indexOf(rememberedSelection)) == i) // if it is, check if they are owners
                                pass = true; // set flag pass
                        } else // if not taken, no problem reselecting it
                            pass = true;
                    
                    if (pass) 
                        if (newInfo.contains(rememberedSelection)) // if the remembered item is still in new possible selections
                            participantsComboBoxes[i].setSelectedItem(rememberedSelection); // resets the previously selected item
                    else
                        // if (participantsComboBoxes[i].getItemCount() > i) // to prevent out of bounds selections
                        ;//participantsComboBoxes[i].setSelectedIndex(i);
                    //
                }
            }
            participantsContainer.validate();
        }
        
        // BUTTON PRESS METHODS        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == plusButton) 
                addPerson();
            else if (e.getSource() == minusButton) 
                removePerson();
            else if (e.getSource() == cancelButton) 
                cancel();
            else if (e.getSource() == createButton)
                if (editing)
                    createMeeting(meetingID); // edit the meeting
                else
                    createMeeting(); // create the meeting
        }
        
        private void addPerson() {
            privateChanges = true;
            if (shown < availablePeople) {
                shown++;
                refreshParticipantsComboBoxes(); // must go after shown is updated (shown++)
                participantsComboBoxes[shown-1].setVisible(true); // set first invisible visible
                
            } else if (availablePeople == -1) {
                availablePeople = scheduler.getPeople().size();
                refreshParticipantsComboBoxes();
                addPerson();
            } else
                JOptionPane.showMessageDialog(this, "No more people!");
            privateChanges = false;
        }
        private void removePerson() {
            privateChanges = true;
            if (shown != 1) { // dont remove the very last one
                shown--;
                participantsComboBoxes[shown].setVisible(false); // set last visible invisible
                
                refreshParticipantsComboBoxes();
            }
            privateChanges = false;
        }
        private void createMeeting() {
            createMeeting(-1); // updates meeting, runs in "editing" mode
        }
        
        private void createMeeting(int ID) {
            boolean nullValue = false;
            String roomNumberString = (String)roomComboBox.getSelectedItem();  // gets string value of room number from combo box
            int roomNumber = Integer.parseInt(roomNumberString);
            
            Time time = (Time)timesComboBox.getSelectedItem(); // gets the time value from the combo box
            
            ArrayList<Person> members = new ArrayList();
            for (int i = 0; i < shown; i++) // only choose shown JComboBoxes' values
                members.add((Person)participantsComboBoxes[i].getSelectedItem()); // add all selected persons to array
            ArrayList<Meeting> currentMeetings = null;
            
            try {
                currentMeetings = scheduler.findRoom(roomNumber).getMeetings();
            } catch (MSS.RoomNumberNotFoundException ex) {
                System.err.println("ERROR! THIS SHOULD NEVER HAPPEN! GUIMeetingPopup.createMeeting()");
            }
                      
            if (currentMeetings != null) {
                Meeting m = currentMeetings.get(time.getIndex()); // get the meeting that will be replaced
                nullValue = m.isNull();
                
                
                boolean update = false;
                
                Meeting oldMeeting = null;
                
                if (editing && ID != -1) {// if in editing mode and (ID is only -1 if not editing)
                    oldMeeting = scheduler.findMeeting(ID); // update m to be the one being replaced
                    if (m.getMeetingID() == ID) 
                        update = true; // this indicates that we will be updating the meeting at the same place, rather than creating a new one
                }
                
                int select = 0;
                if (!nullValue && !update) // if the value being replaced is not null, and we are not in updating (overwriting) the same meeting 
                    select = JOptionPane.showConfirmDialog(this, String.format("There is already a meeting at room %d at %s. Would you like to overwrite it?", roomNumber, time));
                    
                if (select == 0 && update) { // USER SELECTED YES            
                    scheduler.addMeeting(new Meeting(members, time, roomNumber), true); // add the meeting to the schedule, with overwrite as TRUE
                    dispose();
                } else if (select == 0 && !update) { // if it is unique, then just add the meeting
                    if (editing)
                        scheduler.removeMeeting(oldMeeting); // if not an update, remove the original to then create a new one
                    scheduler.addMeeting(new Meeting(members, time, roomNumber)); // add the new meeting to the scheduler
                    dispose(); // after creating the meeting, the dialog window is closed
                } else if (select == 1)
                    ; // if no overwrite, then do nothing, just close dialog
                else if (select == 2)
                    dispose();
            }
        }
        
        private void cancel() {
            dispose(); // closes the dialog window
        }
        
        private void resetParticipantsBoxes() {
            for (int i = 0; i < shown; i++) {
                participantsComboBoxes[i].removeAllItems();
                for (Person p : valuesInParticipantsCB)
                    participantsComboBoxes[i].addItem(p);
            }
            refreshParticipantsComboBoxes(); // updates all combo boxes' to real values
        }
        
        // ITEM LISTENER METHODS
        @Override
        public void itemStateChanged(ItemEvent e) {
            if ((e.getSource() == timesComboBox || e.getSource() == roomComboBox) && e.getStateChange() == ItemEvent.SELECTED) { 
                if (!privateChanges) {
                    selectedTime = (Time)timesComboBox.getSelectedItem();  // gets selected time
                    selectedRoom = Integer.parseInt((String)roomComboBox.getSelectedItem()); //gets integer value of selected room number
                    privateChanges = true;
                    refreshParticipantsComboBoxes(); // perform twice, first time gets rid of all unable
                    refreshParticipantsComboBoxes(); // perform twice, second time ensures no duplicate selections
                    privateChanges = false;
                }
            } else if (!privateChanges) { // boolean keeps track of when function is called to prevent infinite recursive calls of refreshParticipantsComboBoxes() triggering itemStateChanged(), and itself again
                if (e.getStateChange() != ItemEvent.SELECTED && !privateChanges) { // this function triggers twice per value change, one time it equals one (also, private changes are not taking place)-+
                    privateChanges = true;
                    refreshParticipantsComboBoxes();
                    privateChanges = false;
                }
            }
            
        }


}

