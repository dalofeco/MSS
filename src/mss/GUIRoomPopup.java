// Daniel Lopez - dql5295@psu.edu
// Meeting Scheduling System [GUIRoomPopup] - 11-11-2015
package mss;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUIRoomPopup extends JDialog implements ActionListener {
    private final JButton createButton;
    private final JButton cancelButton;
    private final JTextField roomNumberField;
    
    private final MSSFrame owner;
    
    GUIRoomPopup(MSSFrame owner) {
        super(owner, "New Room");
        setLayout(new GridLayout(3,1));
        setSize(300, 180);
        add(new Container()); // add empty placeholder container to first row
        
        this.owner = owner;
        final int TEXT_FIELD_WIDTH = 15;
        
        
        // ROOM NUMBER FIELD AND LABEL UI ELEMENTS
        JLabel roomNumberLabel = new JLabel("Room Number: ");
        roomNumberField = new JTextField(TEXT_FIELD_WIDTH);
        
        Container roomNumberCont = new Container();
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.CENTER);
        roomNumberCont.setLayout(layout);
        
        roomNumberCont.add(roomNumberLabel);
        roomNumberCont.add(roomNumberField);
        // END ROOM NUMBER FIELD AND LABEL
        
        // BUTTONS
        createButton = new JButton("Create");
        createButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        
        Container buttonsCont = new Container();
        buttonsCont.setLayout(layout);
        buttonsCont.add(createButton);
        buttonsCont.add(cancelButton);
        // END BUTTONS
        
        add(roomNumberCont);
        add(buttonsCont);
    }
    
    private boolean createRoom(String roomNumber) {
        boolean pass = true;
        int roomNum;
        for (char c : roomNumber.toCharArray()) // check every character
            if (!Character.isDigit(c)) // if any character is not a digit
                pass = false;  // do not parse into int
        if (pass) {
            roomNum = Integer.parseInt(roomNumber);
            pass = false; // set to false for next check
            try {
                owner.getScheduler().findRoom(roomNum); // find the room for given room number
            } catch (MSS.RoomNumberNotFoundException ex) { // if not found, exception thrown
                pass = true;  // set true to indicate to create room
            }
            if (pass)
                owner.getScheduler().createRoom(roomNum);
        }
        
        return pass;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == createButton)
            if (createRoom(roomNumberField.getText())) {
                owner.refreshLists(); // refresh lists in main frame
                dispose(); // get rid of current popup
            } else
                JOptionPane.showMessageDialog(this, "Invalid input or room number already taken!");
        else if (ae.getSource() == cancelButton)
            dispose();
    }
}
