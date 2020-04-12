package tomoaki.courseClasses;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class Course {
	//first cell
	private String courseCRN;
	private String subject;
	//second cell
	private String title;
	private boolean isLabCourse;
	private String courseDescription;
	//fourth cell
	private String faculty;
	//sixth cell
	private String room;
	//seventh cell
	private double credit;
	//eighth cell
	private List<String> cores;
	//fifth cell
	private EnumMap<DayOfWeek, List<Hours>> hoursOfDay; // use this to detect the overlap
	private String timeContent;
	
	public Course() {
		cores = new ArrayList();
		hoursOfDay = new EnumMap<DayOfWeek, List<Hours>>(DayOfWeek.class);
	}
	
	public Course(String courseCRN, String title, String faculty, String room, double credit, List<String> cores, EnumMap<DayOfWeek,List<Hours>> hoursOfDay, String timeContent) {
		this.courseCRN = courseCRN;
		this.title = title;
		this.faculty = faculty;
		this.room = room;
		this.credit = credit;
		this.cores = cores;
		this.hoursOfDay = hoursOfDay;
		this.timeContent = timeContent;
	}
	
	public Course(String courseCRN, String title, String faculty, String room, double credit, String timeContent) {
		this.courseCRN = courseCRN;
		this.title = title;
		this.faculty = faculty;
		this.room = room;
		this.credit = credit;
		this.cores = new ArrayList<String>();
		this.hoursOfDay = new EnumMap<DayOfWeek, List<Hours>>(DayOfWeek.class);
		this.timeContent = timeContent;
	}
	
	public String getCourseDescription() {
		return courseDescription;
	}
	
	public void setCourseDescription(String courseDescription) {
		this.courseDescription = courseDescription;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public boolean getIsLabCourse() {
		return isLabCourse;
	}
	
	public void serIsLabCourse(boolean isLabCourse) {
		this.isLabCourse = isLabCourse;
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
	
	public EnumMap<DayOfWeek,List<Hours>> getHoursOfDay() {
		return hoursOfDay;
	}
	
	public List<Hours> getHoursFromDay(DayOfWeek day) {
		return hoursOfDay.get(day);
	}
	
	public void putHoursOfDay(DayOfWeek dayOfWeek, List<Hours> hours) {
		hoursOfDay.put(dayOfWeek, hours);
	}
	
	public void setHoursOfDay(EnumMap<DayOfWeek, List<Hours>> hoursOfDay) {
		this.hoursOfDay = hoursOfDay;
	}
	
	public String getTimeContent() {
		return timeContent;
	}
	
	public void setTimeContent(String timeContent) {
		this.timeContent = timeContent;
	}
	
	@Override
	public String toString() {
		return "Course{" +
			"courseCRN='" + courseCRN + '\'' +
			", subject='" + subject + '\'' +
			", isLabCourse=" + isLabCourse +
			", courseDescription='" + courseDescription + '\'' +
			", title='" + title + '\'' +
			", faculty='" + faculty + '\'' +
			", room='" + room + '\'' +
			", credit=" + credit +
			", cores=" + cores +
			", hoursOfDay=" + hoursOfDay +
			", timeContent='" + timeContent + '\'' +
			"}";
	}
}