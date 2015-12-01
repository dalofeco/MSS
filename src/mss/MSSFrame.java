// Daniel Lopez - dql5295@psu.edu
// Meeting Scheduling System [MSSFrame] - 11-9-15
package mss;

import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import javax.swing.JFileChooser;

public class MSSFrame extends JFrame implements ActionListener, ListSelectionListener, MouseListener, ItemListener {
    
    private final MSS scheduler;
    
    private Room selectedRoom;
    private Meeting selectedMeeting;
    private Person selectedPerson;
    
    private final JList<Room> roomsList;
    private final JList<Person> peopleList;
    private final JList<Meeting> meetingsList;
    
    private final JButton plusMeeting;
    private final JButton minusMeeting;
    private final JButton plusPerson;
    private final JButton minusPerson;
    private final JButton plusRoom;
    private final JButton minusRoom;
    
    private final JTextArea outputTextArea;
    
    private final JRadioButton printRButton;
    private final JRadioButton editRButton;
    
    private final JMenuBar menuBar;
    private final JMenuItem openMenuItem;
    private final JMenuItem saveMenuItem;
    
    private boolean print = true;           // true when double click prints, false when double click edits
    
    private boolean valueChangedRunning = false; // boolean to keep track of when valueChanged is running 
                                                // to assure single execution of code
    private boolean itemStateRunning = false; // boolean to keep track of when itemStateChanged is running
                                             // to assure single execution of code
    public MSS getScheduler() {
        return scheduler;
    }
    
    private void createMenuBar() { // creates the menu bar
        JMenu file = new JMenu("File");
        
        saveMenuItem.setToolTipText("Save to a file..");
        openMenuItem.setToolTipText("Open a scheduler file..");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setToolTipText("Exit MSS");
        
        saveMenuItem.addActionListener(this);
        openMenuItem.addActionListener(this);
        
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });
        
        file.add(saveMenuItem);
        file.add(openMenuItem);
        file.add(exitMenuItem);
        
        menuBar.add(file);
    }
    
    MSSFrame(MSS mss) {
        super("Meeting Scheduling System");
        getContentPane().setLayout(new GridLayout(2,1)); // 2 rows, 1 column main layout 
        
        menuBar = new JMenuBar();        
        saveMenuItem = new JMenuItem("Save");
        openMenuItem = new JMenuItem("Open");

        createMenuBar();
        
        setJMenuBar(menuBar);
        
        scheduler = mss;
        
        Container topRow = new Container();
        topRow.setLayout(new GridLayout(2,2)); // 4 by 4 grid layout for top row
        
        final ImageIcon plusIcon = new ImageIcon("images/plus-icon.png");  // get plus icon for buttons
        final ImageIcon minusIcon = new ImageIcon("images/minus-icon.png"); // get minus icon for buttons
        
        // ROOMS CONTAINER
        Container roomsContainer = new Container();
        roomsContainer.setLayout(new BorderLayout());
        
        JLabel roomsLabel = new JLabel("Rooms:");
        roomsContainer.add(roomsLabel, BorderLayout.NORTH);
        
        Room []roomsArray = scheduler.getRoomsArray();
        if (roomsArray.length > 0) // if at least one element in array
            roomsList = new JList(scheduler.getRoomsArray()); // create it with the elements
        else                    // if not
            roomsList = new JList(); // create an empty JLIST
        
        roomsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // only allow one object to be selected at a time
        roomsList.addListSelectionListener(this); // adds this as the listener for list selection events
        roomsList.addMouseListener(this);
        
        roomsContainer.add(new JScrollPane(roomsList), BorderLayout.CENTER); // adds scroll pane with LIST object to scroll through list
        
        plusRoom = new JButton(plusIcon);
        minusRoom = new JButton(minusIcon);
        plusRoom.addActionListener(this);
        minusRoom.addActionListener(this);
        
        Container roomButtons = new Container();
        roomButtons.setLayout(new GridLayout(2,1));
        roomButtons.add(plusRoom);
        roomButtons.add(minusRoom);
        
        roomsContainer.add(roomButtons, BorderLayout.EAST); // add buttons to right side
        
        selectedRoom = roomsList.getSelectedValue(); // initialize to initial selected room
        // END ROOMS CONTAINER
        
        // MEETINGS CONTAINER
        Container meetingsContainer = new Container();
        meetingsContainer.setLayout(new BorderLayout());
        
        JLabel meetingsLabel = new JLabel("Meetings:");
        meetingsContainer.add(meetingsLabel, BorderLayout.NORTH);
        
        Meeting[] meetingsArray = scheduler.getNonNullMeetingsArray();
        Meeting[] empty = new Meeting[1];
        empty[0] = new Meeting(true);
        
        
        if (scheduler.countMeetings() > 0) // if at least one meeting that is not null
            meetingsList = new JList(meetingsArray); // create it with the elements
        else {                   // if not
            meetingsList = new JList(empty); // create an empty jlist
        }
        
        meetingsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // only allow one selection at a time
        meetingsList.addListSelectionListener(this);
        meetingsList.addMouseListener(this);
        
        meetingsContainer.add(new JScrollPane(meetingsList), BorderLayout.CENTER);
        
        plusMeeting = new JButton(plusIcon);
        minusMeeting = new JButton(minusIcon);
        plusMeeting.addActionListener(this);
        minusMeeting.addActionListener(this);
        
        Container meetingButtons = new Container();
        meetingButtons.setLayout(new GridLayout(2, 1));
        meetingButtons.add(plusMeeting);
        meetingButtons.add(minusMeeting);
        
        meetingsContainer.add(meetingButtons, BorderLayout.EAST); // add buttons to right side
        
        selectedMeeting = meetingsList.getSelectedValue(); // initialize to initial selected meeting
        // END MEETINGS CONTAINER
        
        // PEOPLE CONTAINER
        Container peopleContainer = new Container();
        peopleContainer.setLayout(new BorderLayout());
        
        JLabel peopleLabel = new JLabel("People:");
        peopleContainer.add(peopleLabel, BorderLayout.NORTH);
        
        Person[] peopleArray = scheduler.getPeopleArray();
        if (peopleArray.length > 0)                 // if at least one entry is available
            peopleList = new JList(peopleArray); // create list from people in scheduler
        else                        // if not,
            peopleList = new JList(); // create an empty jlist
        peopleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        peopleList.addListSelectionListener(this);
        peopleList.addMouseListener(this);
        
        peopleContainer.add(new JScrollPane(peopleList), BorderLayout.CENTER);
        
        plusPerson = new JButton(plusIcon);
        minusPerson = new JButton(minusIcon);
        plusPerson.addActionListener(this);
        minusPerson.addActionListener(this);
        
        Container peopleButtons = new Container();
        peopleButtons.setLayout(new GridLayout(2,1)); // set vertically alligned 2 buttons
        peopleButtons.add(plusPerson);
        peopleButtons.add(minusPerson);
        
        peopleContainer.add(peopleButtons, BorderLayout.EAST);  // add buttons to right side
        
        selectedPerson = peopleList.getSelectedValue();
        // END PEOPLE CONTAINER
        
        // OPTIONS CONTAINER
        Container lastContainer = new Container();
        lastContainer.setLayout(new BorderLayout());
        
        JLabel optionsTitleLabel = new JLabel("Options:");
        lastContainer.add(optionsTitleLabel, BorderLayout.NORTH);
        
        Container optionsContainer = new Container();
        optionsContainer.setLayout(new GridLayout(2, 1)); // 3 row to col grid layout
        
        printRButton = new JRadioButton("Print", true); // create a new selected JRadioButton labeled Print
        editRButton = new JRadioButton("Edit"); // create a new unselected JRadioButton labeled Edit
        
        editRButton.addItemListener(this); // adds MSSFrame as item listener
        printRButton.addItemListener(this); // for these buttons to monitor state
        
        JLabel doubleClickLabel = new JLabel(" on double click");
        doubleClickLabel.setHorizontalAlignment(JLabel.CENTER);
        
        Container editContainer = new Container();
        editContainer.setLayout(new BorderLayout());
        editContainer.add(editRButton, BorderLayout.WEST);
        
        Container printContainer = new Container();
        printContainer.setLayout(new BorderLayout());
        printContainer.add(printRButton, BorderLayout.EAST);
        
        Container buttonsContainer = new Container();
        buttonsContainer.setLayout(new GridLayout(1, 2));
        
        buttonsContainer.add(printContainer);
        buttonsContainer.add(editContainer);
        
        Container labelContainer = new Container();
        labelContainer.setLayout(new BorderLayout());
        labelContainer.add(doubleClickLabel, BorderLayout.NORTH);
        
        
        optionsContainer.add(buttonsContainer);
        optionsContainer.add(labelContainer); // adds label
                
        lastContainer.add(optionsContainer, BorderLayout.CENTER);
        // END OPTIONS CONTAINER
        
        
        // TOP ROW CONTAINER
        topRow.add(roomsContainer);
        topRow.add(peopleContainer);
        topRow.add(meetingsContainer);
        topRow.add(lastContainer);
        
        // BOTTOM ROW CONTAINER (TEXT VIEW)
        JPanel bottomRow = new JPanel();
        bottomRow.setLayout(new BorderLayout());
        
        Border padding = BorderFactory.createEmptyBorder(15, 15, 15, 15);
        bottomRow.setBorder(padding);
        
        JLabel textTitle = new JLabel("Output: ");
        
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        bottomRow.add(textTitle, BorderLayout.NORTH);
        bottomRow.add(outputTextArea, BorderLayout.CENTER);
        
        // CREATING DEFAULT TEST VALUES FOR PEOPLE
        //scheduler.addPerson(new Person("Bill", "Cosby", "8145655525"));
        //scheduler.addPerson(new Person("Obama", "Gates", "221-911-1199"));
        //scheduler.addPerson(new Person("Hi", "Lopez", "1123554454"));
        //scheduler.addPerson(new Person("Ne", "Rerta", "1123553354"));
        //scheduler.addPerson(new Person("Nejo", "El Conejo", "3423554474"));
        //scheduler.addPerson(new Person("Robert", "Matinez", "1123554760"));
        //scheduler.addPerson(new Person("Rob", "Lorenzo", "1198554454"));
        //scheduler.addPerson(new Person("Eric", "Propardo", "1123444454")); 

        // ************ \\
        // GUI ELEMENTS \\
        // ************ \\
        refreshLists(); // refreshes all the values in the lists
        
        setSize(800, 800);
        getContentPane().add(topRow);
        getContentPane().add(bottomRow);
        
    }
    
    void refreshLists() {
        Person[] people = scheduler.getPeopleArray();
        peopleList.removeAll();
        peopleList.setListData(people);
        
        Meeting[] meetings = scheduler.getNonNullMeetingsArray();
        meetingsList.removeAll();
        meetingsList.setListData(meetings);
        
        Room[] allRooms = scheduler.getRoomsArray();
        roomsList.removeAll();
        roomsList.setListData(allRooms);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == plusMeeting) {
            GUIMeetingPopup mpopup = new GUIMeetingPopup(this);
            mpopup.show();
            refreshLists();
        } else if (event.getSource() == plusPerson) {
            GUIPersonPopup ppopup = new GUIPersonPopup(this);
            ppopup.show();
            refreshLists();
        } else if (event.getSource() == plusRoom) {
            GUIRoomPopup rpopup = new GUIRoomPopup(this);
            rpopup.show();
            refreshLists();
        } else if (event.getSource() == minusMeeting) {
            if (!meetingsList.isSelectionEmpty()) { // make sure it doesn't run if nothing is selected
                Meeting meetingToDelete = meetingsList.getSelectedValue();
                int select = JOptionPane.showConfirmDialog(this, String.format("Are you sure you want to delete meeting # %d?", meetingToDelete.getMeetingID()));
                // bring up confirmation dialog to prevent accidental deletion
                // select value 0 is YES
                // select value 1 is NO
                // select value 2 is CANCEL            
                if (select == 0) { // user selected YES
                    scheduler.removeMeeting(meetingToDelete);
                    refreshLists();
                }
            }
        } else if (event.getSource() == minusPerson) {
            if (!peopleList.isSelectionEmpty()) { // make sure it doesn't run if nothing is selected
                Person personToDelete = peopleList.getSelectedValue();
                int select = JOptionPane.showConfirmDialog(this, String.format("Are you sure you want to delete %s %s?", personToDelete.getFirstName(), personToDelete.getLastName())); 
                // bring up confirmation dialog to prevent accidental deletion
                if (select == 0) { // user selected YES
                    deletePerson(personToDelete);
                    refreshLists();
                }
            }
        } else if (event.getSource() == minusRoom) {
            if (!roomsList.isSelectionEmpty()) { // make sure it doesn't run if nothing is selected)
                Room roomToDelete = roomsList.getSelectedValue();
                int select = JOptionPane.showConfirmDialog(this, String.format("Are you sure you want to delete room %d?", roomToDelete.getRoomNumber()));
                // bring up confirmation dialog to prevent accidental deletion
                if (select == 0) {// user selected YES
                    deleteRoom(roomToDelete);
                    refreshLists();
                }
            }
        } else if (event.getSource() == saveMenuItem) {
            saveMSS();
        } else if (event.getSource() == openMenuItem) {
            openMSS();
        }

    }
    
    private void updateTextArea(String message) {
        outputTextArea.setText(message);
    }
    
    private void outputPerson(int index) {
        Person personToPrint = scheduler.getPeopleArray()[index];  // get the person who is going to be printed 
        Meeting[] meetings = scheduler.findMeetingsForPerson(personToPrint);  // get all meetings that personToPrint is in
        String message = String.format("%s%n", personToPrint); // add the person string to the message
        for (Meeting m : meetings) // for every meeting, add a descriptive string to the message
            message = message.concat(String.format("\tAttending meeting #%s in room #%d at %s%n", m.getMeetingID(), m.getRoomNumber(), m.getTime()));
        
        updateTextArea(message);  // updates the jtextarea field with message
    }
    
    private void outputMeeting(int index) {
        Meeting meetingToPrint = scheduler.getNonNullMeetingsArray()[index]; 
        updateTextArea(meetingToPrint.getDetailedString());
    }
    
    private void editMeeting(Meeting m) {
        GUIMeetingPopup editing = new GUIMeetingPopup(this, m);
        editing.show();
        refreshLists();
    }
    
    private void editPerson(Person p) {
        GUIPersonPopup editing = new GUIPersonPopup(this, p);
        editing.show();
        refreshLists();
    }
    
    private void outputRoom(int index) {
        Room roomToPrint = scheduler.getRoomsArray()[index];
        Meeting[] meetings = roomToPrint.getNonNullMeetings();
        String message = String.format("%s%n", roomToPrint); // adds roomToPrint details to message
        for (Meeting m : meetings) 
            message = message.concat(String.format("\t%s", m)); // adds detail of every meeting in the room
        
        updateTextArea(message);
    }
    
    @Override // function updates instance variables that keep track of selected values
    public void valueChanged(ListSelectionEvent lse) {
        if (!lse.getValueIsAdjusting() && !valueChangedRunning) {// if the change is final and mouse has been released and that function isn't calling itself recursively by clearing selection
            valueChangedRunning = true;
            if (lse.getSource() == roomsList) {
                selectedRoom = roomsList.getSelectedValue();
                peopleList.clearSelection(); // only allow one list item to be selected
                meetingsList.clearSelection();  // at a time, so deselect all others
                // System.out.println("RoomsList");
            }
            else if (lse.getSource() == peopleList) {
                selectedPerson = peopleList.getSelectedValue();
                roomsList.clearSelection(); // only allow one list item to be selected
                meetingsList.clearSelection();  // at a time, so deselect all others
                // System.out.println("PeopleList");
            }
            else if (lse.getSource() == meetingsList) {
                selectedMeeting = meetingsList.getSelectedValue();
                peopleList.clearSelection(); // only allow one list item to be selected
                roomsList.clearSelection(); // at a time, so deselect all others
                // System.out.println("MeetingsList"); 

            }
        }
        valueChangedRunning = false; // done running, so can set to false again for next run
    }

    // ****** MOUSE INTERFACE METHODS ******
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource().getClass() == JList.class) { // only interested if a JLIST object was clicked
            if (e.getClickCount() == 2) { // if clicked twice (double click)
                JList list = (JList)e.getSource(); // get the list that was double clicked on
                int index = list.locationToIndex(e.getPoint()); // gets the index of the value clicked in the list
                if (index >= 0) { // verify the index is at least zero, to avoid ArrayOutOfBoundsException if JList is empty
                    if (list == meetingsList) // if the clicking came from the meetings list
                        if (print)
                            outputMeeting(index); // output that meeting
                        else {
                            Meeting[] m = scheduler.getNonNullMeetingsArray(); // get the meetings array
                            editMeeting(m[index]);  // and send the selected meeting to edit
                        }
                            
                    else if (list == peopleList) // or the people list
                        if (print)
                            outputPerson(index);
                        else {
                            Person[] people = scheduler.getPeopleArray();
                            editPerson(people[index]);
                        }
                    else if (list == roomsList) // or the room list
                        if (print)
                            outputRoom(index);
                        else
                            JOptionPane.showMessageDialog(this, "Rooms cannot be edited.");
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ; // no use for this function, yet must override
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        ; // no use for this function, yet must override
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        ; // no use for this function, yet must override
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ; // no use for this function, yet must override
    }
    
    // ***** END MOUSE INTERFACE METHODS ******
    // ***** START ITEMLISTENER METHODS *******
    @Override
    public void itemStateChanged(ItemEvent ie) {
        if (!itemStateRunning) { // only run if not currently running (boolean is false)
            itemStateRunning = true;
            if (ie.getSource() == printRButton) {
                if (printRButton.isSelected()) {
                    editRButton.setSelected(false); // deselect the edit if print was selected
                    print = true; // printing mode
                }
                else 
                    printRButton.setSelected(true); // if print button was selected when being selected, reselects after deselection
            } else if (ie.getSource() == editRButton) {
                if (editRButton.isSelected()) {
                    printRButton.setSelected(false); // deselect the print if edit was selected
                    print = false; // editing mode on, printing mode off
                }
                else
                    editRButton.setSelected(true); // if edit button was selected when being selected, reselects after deselection
            }
            itemStateRunning = false;
        }
    }
    
    // ***** END ITEMLISTENER METHODS ********
    
    private void openMSS() {
        File file;
        
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        
        if (returnValue == fileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            
            try {
            FileInputStream fileReader = new FileInputStream(file);
            ObjectInputStream objectReader = new ObjectInputStream(fileReader);

            MSS newScheduler = (MSS)objectReader.readObject();

            objectReader.close();
            fileReader.close();

            MSSFrame newFrame = new MSSFrame(newScheduler);
            newFrame.setVisible(true);

            dispose();

            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public boolean saveMSS() {
        File file;
        boolean success = true;
        
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(this);
        
        if (returnValue == fileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            
            try {
                FileOutputStream fout = new FileOutputStream(file);
                ObjectOutputStream oOut = new ObjectOutputStream(fout);
                oOut.writeObject(this.getScheduler());
                oOut.close();
                fout.close();

            } catch (IOException ex) {  // FileNotFoundException may be thrown, but is subclass of 
                success = false;         // IOException
                ex.printStackTrace();
            }
        } else 
            success = false;
        
        return success;
    }
    
    private void deletePerson(Person personToDelete) {
        Meeting []meetings = scheduler.findMeetingsForPerson(personToDelete);
        if (meetings.length > 0) {
            JOptionPane.showMessageDialog(this, "Unable to delete, the person is already in a meeting.");
        } else
            scheduler.deletePerson(personToDelete);
    }
    
    private void deleteRoom(Room roomToDelete) {
        if (roomToDelete.getNonNullMeetings().length > 0) // if it contains a single non-null meeting
            JOptionPane.showMessageDialog(this, String.format("Room %d could not be deleted because it contains %d meetings in it.", 
                    roomToDelete.getRoomNumber(), roomToDelete.getNonNullMeetings().length)); // do not delete it, pop up a message
        else
            scheduler.deleteRoom(roomToDelete.getRoomNumber()); // delete the room
    }
   
}
