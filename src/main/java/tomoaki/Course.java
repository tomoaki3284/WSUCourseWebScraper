package tomoaki;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class Course {
	private String courseCRN;
	private String courseCategory;
	private String title;
	private String faculty;
	private String room;
	private double credit;
	private int courseLevel;
	private List<String> cores;
	private EnumMap<DayOfWeek, Hours> hoursOfDay;
	
	public Course(String courseCRN, String classCategory, String title, String faculty, String room, double credit, int courseLevel) {
		this.courseCRN = courseCRN;
		this.courseCategory = classCategory;
		this.title = title;
		this.faculty = faculty;
		this.room = room;
		this.credit = credit;
		this.courseLevel = courseLevel;
		this.cores = new ArrayList<String>();
		this.hoursOfDay = new EnumMap<DayOfWeek, Hours>(DayOfWeek.class);
	}
	
	public String getCourseCRN() {
		return courseCRN;
	}
	
	public void setCourseCRN(String courseCRN) {
		this.courseCRN = courseCRN;
	}
	
	public String getCourseCategory() {
		return courseCategory;
	}
	
	public void setCourseCategory(String courseCategory) {
		this.courseCategory = courseCategory;
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
	
	public int getCourseLevel() {
		return courseLevel;
	}
	
	public void setCourseLevel(int courseLevel) {
		this.courseLevel = courseLevel;
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
}
