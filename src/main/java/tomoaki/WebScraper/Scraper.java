package tomoaki.WebScraper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;

import tomoaki.courseClasses.Course;
import tomoaki.courseClasses.DayOfWeek;
import tomoaki.courseClasses.Hours;

public class Scraper {
	
	HashSet<Character> validHoursSet = new HashSet();
	private List<Course> courses;
	
	public Scraper() {
		this("http://www.westfield.ma.edu/offices/registrar/master-schedule");
	}
	
	public List<Course> getCourses() {
		return courses;
	}
	
	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}
	
	public Scraper(String URL) {
		addValidHoursChar();
		
		courses = new ArrayList<Course>();
		
		WebClient client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);

		try{
			HtmlPage page = client.getPage(URL);
			// In each courseTable, there are list of courses
			List<HtmlElement> trs = page.getByXPath("//tr");
			for(HtmlElement tr : trs){
				List<HtmlElement> tds = tr.getByXPath("td");
				if(tds.size() < 8) continue;
				
				//extract information to instantiate Course object
				Course course = new Course();
				scrapeFirstCell(course, tds.get(0));
				scrapeSecondCell(course, tds.get(1));
				scrapeFourthCell(course, tds.get(3));
				scrapeFifthCell(course, tds.get(4));
				scrapeSixthCell(course, tds.get(5));
				scrapeSevenCell(course, tds.get(6));
				scrapeEighthCell(course, tds.get(7));
				courses.add(course);
			}
			
			//check
			for(Course course : courses){
				System.out.println(course);
				System.out.println("\n");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void addValidHoursChar() {
		for(char c='0'; c <= '9'; c++){
			validHoursSet.add(c);
		}
		validHoursSet.add('M');
		validHoursSet.add('T');
		validHoursSet.add('W');
		validHoursSet.add('R');
		validHoursSet.add('F');
		validHoursSet.add('S');
		validHoursSet.add(':');
		validHoursSet.add('A');
		validHoursSet.add('P');
	}
	
	private void scrapeEighthCell(Course course, HtmlElement htmlElement) {
		String[] cores = htmlElement.getTextContent().split("/");
		for(String core : cores){
			course.addCore(core);
		}
	}

	private void scrapeSevenCell(Course course, HtmlElement htmlElement) {
		String content = htmlElement.getTextContent();
		double credit;
		try{
			credit = Double.parseDouble(content);
		}catch(NumberFormatException e){
			credit = 0;
		}
		course.setCredit(credit);
	}

	private void scrapeSixthCell(Course course, HtmlElement htmlElement) {
		String content = htmlElement.getTextContent();
		course.setRoom(content);
	}
	
	/**
	 * TODO: Detect whether content hours is in complex form or simple form
	 */
	private void scrapeFifthCell(Course course, HtmlElement htmlElement) {
		String content = htmlElement.getTextContent();
		String[] timeCells = content.split(" ");
		if(content == null || content.length() == 0 || timeCells.length < 4){
			return;
		}
		EnumMap<DayOfWeek, Hours> hoursOfDay = new EnumMap<DayOfWeek, Hours>(DayOfWeek.class);
		
		// if third(0-base) timeCell length in timeCells is larger than 2, it is complex form
		if(timeCells[3].length() > 2){
			extractHoursComplex(hoursOfDay, timeCells);
			course.setHoursOfDay(hoursOfDay);
		}else{
			if(timeCells.length < 10)
				extractHoursSimple(hoursOfDay, timeCells);
			course.setHoursOfDay(hoursOfDay);
		}
	}
	
	/**
	 * TODO: Extract/separate time interval in a form of Simple Form
	 *
	 * @param hoursOfDay
	 * @param timeCells Input Complex Format: "R 09:30 AM-10:30 PMMWF 11:45 AM-12:45 PM"
	 *                                        "R 09:30 AM-10:30 PMHybrid (...)" -> hours detail
	 *                                        "R 09:30 AM-10:30 PMFIRST (...)"  -> hours detail
	 *                                        "R 09:30 AM-10:30 PMSECOND (...)" -> hours detail
	 *
	 *                  Input Simple Format:  "R 09:30 AM-10:30 PM"
	 */
	private void extractHoursComplex(EnumMap<DayOfWeek, Hours> hoursOfDay, String[] timeCells) {
		// if third(0-base) timeCell char don't contains validHoursSet char, then there are hours detail and one time interval
		// So, eliminate those hours details by substring, and extract as simple
		// this might cause conflict in future, if there are two hours + hours details like (hybrid, etc)
		String concatCell = timeCells[3];
		for(char c : concatCell.toCharArray()){
			if(!validHoursSet.contains(c)){
				timeCells[3] = concatCell.substring(0,2);
				String[] newTimeCells = new String[]{timeCells[0],timeCells[1],timeCells[2],timeCells[3]};
				extractHoursSimple(hoursOfDay, newTimeCells);
				return;
			}
		}
		
		// if there are no hours detail on concatCell(third time cell), then timeCells has two(or more) time interval.
		// put @ between and separate them
		// split by single/multiple spaces, and then extract as simple
		timeCells[3] = concatCell.substring(0,2) + "@" + concatCell.substring(2);
		StringBuilder sb = new StringBuilder();
		for(String timeCell : timeCells){
			sb.append(timeCell);
			sb.append(" ");
		}
		String newContent = sb.toString().trim();
		String[] timesCells = newContent.split("@");
		for(String tc : timesCells){
			extractHoursSimple(hoursOfDay, tc.split("\\s+"));
		}
	}
	
	private void extractHoursSimple(EnumMap<DayOfWeek, Hours> hoursOfDay, String[] timeBox) {
		//"TR" -> 'T' 'R'
		char[] days = timeBox[0].toCharArray();
		
		// "PM-02:00" -> "PM" "02:00"
		String[] timeTag_endTime = timeBox[2].split("-");
		
		//concat times // might change it to StringBuilder for time complexity
		String startTime = timeBox[1] + timeTag_endTime[0].toLowerCase();
		String endTime = timeTag_endTime[1] + timeBox[timeBox.length-1].toLowerCase();
		String interval = startTime + "-" + endTime;
		try{
			Hours hours = new Hours(interval);
			for(char day : days){
				switch(day){
					case 'M': hoursOfDay.put(DayOfWeek.MONDAY, hours); break;
					case 'T': hoursOfDay.put(DayOfWeek.TUESDAY, hours); break;
					case 'W': hoursOfDay.put(DayOfWeek.WEDNESDAY, hours); break;
					case 'R': hoursOfDay.put(DayOfWeek.THURSDAY, hours); break;
					case 'F': hoursOfDay.put(DayOfWeek.FRIDAY, hours); break;
					case 'S': hoursOfDay.put(DayOfWeek.SATURDAY,hours); break;
				}
			}
			
		}catch(Exception e){
			System.out.println(interval);
			e.printStackTrace();
		}
	}
	
	private void scrapeFourthCell(Course course, HtmlElement htmlElement) {
		String faculty = htmlElement.getTextContent().trim();
		course.setFaculty(faculty);
	}

	private void scrapeSecondCell(Course course, HtmlElement htmlElement) {
		String title = htmlElement.getTextContent();
		DomNode anchor = htmlElement.getFirstByXPath("a");
		if(anchor != null){
			title = anchor.getTextContent();
		}
		course.setTitle(title);
	}

	private void scrapeFirstCell(Course course, HtmlElement htmlElement) {
		String content = htmlElement.getTextContent();
		course.setCourseCRN(content);
	}
	
	public void writeToJSON(String jsonFileName) {
		// write to JSON
		File menuFile = new File(jsonFileName);
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writerFor(new TypeReference<List<Course>>() {
			}).withDefaultPrettyPrinter()
				.writeValue(menuFile, courses);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Scraper scraper = new Scraper();
		List<Course> courses = scraper.getCourses();
		scraper.writeToJSON("current-semester.json");
	}
}
