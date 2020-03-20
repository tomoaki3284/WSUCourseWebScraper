package tomoaki.courseClasses;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class Course {
	//first cell
	private String category;
	private String crn;
	private String section;
	private int level;
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
	
	public Course(String classCategory, String courseCRN,  String courseSection, String title, String faculty, String room, double credit, int level) {
		this.crn = courseCRN;
		this.section = courseSection;
		this.category = classCategory;
		this.title = title;
		this.faculty = faculty;
		this.room = room;
		this.credit = credit;
		this.level = level;
		this.cores = new ArrayList<String>();
		this.hoursOfDay = new EnumMap<DayOfWeek, Hours>(DayOfWeek.class);
	}
	
	public String getCrn() {
		return crn;
	}
	
	public void setCrn(String courseCRN) {
		this.crn = courseCRN;
	}
	
	public String getSection() {
		return section;
	}
	
	public void setSection(String section) {
		this.section = section;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String courseCategory) {
		this.category = courseCategory;
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
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
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
			"category='" + category + '\'' +
			", crn='" + crn + '\'' +
			", section='" + section + '\'' +
			", level=" + level +
			", title='" + title + '\'' +
			", faculty='" + faculty + '\'' +
			", room='" + room + '\'' +
			", credit=" + credit +
			", cores=" + cores +
			", hoursOfDay=" + hoursOfDay +
			"}";
	}
}
