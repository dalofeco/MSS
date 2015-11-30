// Daniel Lopez - dql5295@psu.edu
// Meeting Scheduilng System [Time] - 11-7-2012
package mss;
import java.io.Serializable;

// enum to keep track of TIME as business hours and their index equivalents
public enum Time implements Serializable {
    NINEAM(0), TENAM(1), ELEVENAM(2), TWELVEPM(3), ONEPM(4), TWOPM(5), THREEPM(6), FOURPM(7), FIVEPM(8);
    private final int index;    
    Time(int index) {
        this.index = index;
    }
    
    public static Time indexToTime(int index) {
        if (index == 0)
            return NINEAM;
        else if (index == 1)
            return TENAM;
        else if (index == 2)
            return ELEVENAM;
        else if (index == 3)
            return TWELVEPM;
        else if (index == 4)
            return ONEPM;
        else if (index == 5)
            return TWOPM;
        else if (index == 6)
            return THREEPM;
        else if (index == 7)
            return FOURPM;
        else if (index == 8)
            return FIVEPM;
        else
            throw new IllegalArgumentException("Invalid index on Time.indexToTime() function call");
    }
    
    public int getIndex() {
        return index;
    }
    
    public static int getSize() {
        int size = 0;
        for (Time t : Time.values())
            size++;
        return size;
    }
    
    @Override
    public String toString() {
        String message;
        if (index == 0)
            message = "9 AM";
        else if (index == 1)
            message = "10 AM";
        else if (index == 2)
            message = "11 AM";
        else if (index == 3)
            message = "12 PM";
        else if (index == 4)
            message = "1 PM";
        else if (index == 5)
            message = "2 PM";
        else if (index == 6)
            message = "3 PM";
        else if (index == 7)
            message = "4 PM";
        else if (index == 8)
            message = "5 PM";
        else
            message = "ERROR IN INDEX FOR TOSTRING FOR TIME";
        
        return message;
    }
    
        // fixed values for time for easier access in 
    //private final int NINEAM = 0, TENAM = 1, ELEVENAM = 2, 
    //                TWELVEAM = 3, ONEPM = 4, TWOPM = 5, 
    //                THREEPM = 6, FOURPM = 7, FIVEPM = 8;
    
}


