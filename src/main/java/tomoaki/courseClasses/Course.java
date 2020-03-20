package tomoaki.courseClasses;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class Course {
	//first cell
	private String courseCRN;
	//second cell
	private String title;
	//fourth cell
	private String faculty;
	//sixth cell
	private String room;
	//seventh cell
	private double credit;
	//eighth cell
	private List<String> cores;
	//fifth cell
	private EnumMap<DayOfWeek, Hours> hoursOfDay;
	
	public Course() {
		cores = new ArrayList();
		hoursOfDay = new EnumMap<DayOfWeek, Hours>(DayOfWeek.class);
	}
	
	public Course(String courseCRN, String title, String faculty, String room, double credit, List<String> cores, EnumMap<DayOfWeek,Hours> hoursOfDay) {
		this.courseCRN = courseCRN;
		this.title = title;
		this.faculty = faculty;
		this.room = room;
		this.credit = credit;
		this.cores = cores;
		this.hoursOfDay = hoursOfDay;
	}
	
	public Course(String courseCRN, String title, String faculty, String room, double credit) {
		this.courseCRN = courseCRN;
		this.title = title;
		this.faculty = faculty;
		this.room = room;
		this.credit = credit;
		this.cores = new ArrayList<String>();
		this.hoursOfDay = new EnumMap<DayOfWeek, Hours>(DayOfWeek.class);
	}
	
	public String getCourseCRN() {
		return courseCRN;
	}
	
	public void setCourseCRN(String courseCRN) {
		this.courseCRN = courseCRN;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getFaculty() {
		return faculty;
	}
	
	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
	
	public String getRoom() {
		return room;
	}
	
	public void setRoom(String room) {
		this.room = room;
	}
	
	public double getCredit() {
		return credit;
	}
	
	public void setCredit(double credit) {
		this.credit = credit;
	}
	
	public List<String> getCores() {
		return cores;
	}
	
	public void addCore(String core) {
		cores.add(core);
	}
	
	public EnumMap<DayOfWeek,Hours> getHoursOfDay() {
		return hoursOfDay;
	}
	
	public Hours getHoursFromDay(DayOfWeek day) {
		return hoursOfDay.get(day);
	}
	
	public void putHoursOfDay(DayOfWeek dayOfWeek, Hours hours) {
		hoursOfDay.put(dayOfWeek, hours);
	}
	
	public void setHoursOfDay(EnumMap<DayOfWeek, Hours> hoursOfDay) {
		this.hoursOfDay = hoursOfDay;
	}
	
	@Override
	public String toString() {
		return "Course{" +
			"courseCRN='" + courseCRN + '\'' +
			", title='" + title + '\'' +
			", faculty='" + faculty + '\'' +
			", room='" + room + '\'' +
			", credit=" + credit +
			", cores=" + cores +
			", hoursOfDay=" + hoursOfDay +
			"}";
	}
}
