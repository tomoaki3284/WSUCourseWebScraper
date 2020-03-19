package tomoaki;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to represent the opening and closing hours of a given dining location
 */
public class Hours {

  // hours [0..23]/minute[0..59] in military (24h format)
  private int startHour, startMinute, endHour, endMinute;

  // match the open-close hours using regular expression pattern
  // Notes:
  //   - am/pm are required.
  //   - 12am == middnight
  //   - 12pm == noon
  //   - open and close times should NOT overlap
  //
  // Examples:
  //   8:00am-8:30pm
  //   6:30am-12:00pm
  //   7:00am-12:00am
  //   6:30am-01:00am
  //
  static private Pattern hoursPattern = Pattern
      .compile("(\\d+):(\\d+)(am|pm)\\s*-\\s*(\\d+):(\\d+)(am|pm)");

  /**
   * Class constructor, takes the opening/closing hours string as parameter Notes: - hours have to
   * be [0..12] - minutes have to be [0..59] - am/pm are required - 12am == middnight - 12pm == noon
   * - open and close times should NOT overlap Examples: 8:00am-8:30pm 6:30am-12:00pm 7:00am-12:00am
   * 6:30am-01:00am
   *
   * @param hoursString The opening-closing hours of the location, see examples above.
   * @throws Exception Exception will be thrown if the supplied hours do not meet the required
   *                   format.
   */
  public Hours(String hoursString) throws Exception {
    Matcher m = hoursPattern.matcher(hoursString);
    if (m.find()) {
      startHour = getHour(Integer.parseInt(m.group(1)), m.group(3));
      startMinute = Integer.parseInt(m.group(2));
      endHour = getHour(Integer.parseInt(m.group(4)), m.group(6));
      endMinute = Integer.parseInt(m.group(5));

      if (Integer.parseInt(m.group(1)) < 0 || Integer.parseInt(m.group(1)) > 12) {
        throw new Exception("Invalid start hour " + m.group(1));
      }
      if (Integer.parseInt(m.group(4)) < 0 || Integer.parseInt(m.group(4)) > 12) {
        throw new Exception("Invalid end hour " + m.group(4));
      }
      if (Integer.parseInt(m.group(2)) < 0 || Integer.parseInt(m.group(2)) > 59) {
        throw new Exception("Invalid start minute " + m.group(2));
      }
      if (Integer.parseInt(m.group(5)) < 0 || Integer.parseInt(m.group(5)) > 59) {
        throw new Exception("Invalid end minute " + m.group(5));
      }

      if (!m.group(3).equals("am") && !m.group(3).equals("pm")) {
        throw new Exception("Invalid am/pm for start time " + m.group(3));
      }
      if (!m.group(6).equals("am") && !m.group(6).equals("pm")) {
        throw new Exception("Invalid am/pm for end time " + m.group(6));
      }
    } else {
      throw new Exception("Invalid hours: " + hoursString);
    }
  }

  /**
   * Get the opening hour of the location, this will return a number 0-23
   *
   * @return the opening hour from 0 (midnight) to 23 (11pm at night)
   */
  public int getStartHour() {
    return startHour;
  }

  /**
   * Get the opening minute of the location, will return a number 0 to 59
   *
   * @return the opening minute from 0 to 59
   */
  public int getStartMinute() {
    return startMinute;
  }

  /**
   * Get the closing hour of the location, this will return a number 0-23
   *
   * @return the closing hour from 0 (midnight) to 23 (11pm at night)
   */
  public int getEndHour() {
    return endHour;
  }

  /**
   * Get the closing minute of the location, will return a number 0 to 59
   *
   * @return the closing minute from 0 to 59
   */
  public int getEndMinute() {
    return endMinute;
  }

  /**
   * Check if the location is open at the given hour/minute
   *
   * @param hour   the hour of the time that we're checking, [0..23]
   * @param minute the minute of the time that we're checking
   * @return true if the location is open, false if the location is closed
   */
  public boolean isOpen(int hour, int minute) {
    assert hour >= 0 && hour <= 23;
    assert minute >= 0 && minute <= 59;
    // hour 0..23
    // minute 0..59

    if (getEndHour() < getStartHour()) {
      // we allow this only when we think this will get covered in the next day
      if (hour < getEndHour()) {
        return true;
      }
      if (hour == getEndHour()) {
        return minute <= getEndMinute();
      }
    }

    int adjEndHour = (getEndHour() < getStartHour()) ? 24 : getEndHour();

    // we're in the same day
    if (hour == getStartHour() && minute >= getStartMinute()) {
      return true;
    }
    if (hour > getStartHour()) {
      if (hour == adjEndHour) {
        return minute <= getEndMinute();
      } else {
        return hour < adjEndHour;
      }
    }
    return false;
  }

  /**
   * Convert a 0-12 hour and am/pm to military (24h format)
   *
   * @param hour the hour (0 to 12)
   * @param amPm the string "am" or "pm"
   * @return The hour converted to 4h format (0 to 23)
   */
  public static int getHour(int hour, String amPm) {
    assert hour >= 0;
    assert hour <= 12;
    if (hour == 12) {
      if (amPm.equals("am")) {
        return 0;
      }
      return 12;
    }
    if (amPm.equals("am")) {
      return hour;
    } else {
      return (hour + 12) % 24;
    }
  }
}

