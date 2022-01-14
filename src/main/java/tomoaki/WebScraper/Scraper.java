package tomoaki.WebScraper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.util.Map;
import org.json.simple.JSONObject;
import org.w3c.dom.Node;
import tomoaki.courseClasses.Course;
import tomoaki.courseClasses.DayOfWeek;
import tomoaki.courseClasses.Hours;

public class Scraper {

	private List<Course> courses;
	
	private String offeringTerm = "";
	
	public Scraper() {
		this("https://www.westfield.ma.edu/offices/registrar/master-schedule");
	}
	
	public List<Course> getCourses() {
		return courses;
	}
	
	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}
	
	public Scraper(String URL) {
		long start = System.currentTimeMillis();
		courses = new ArrayList<Course>();
		
		WebClient client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		
		System.out.println("Scrap Start");

		try{
			HtmlPage page = client.getPage(URL);
			System.out.println(page);
			
			offeringTerm = scrapeCourseOfferingTerm(page);
			System.out.println(offeringTerm);
			
			// In each courseTable, there are list of courses
			List<HtmlElement> trs = page.getByXPath("//tr");
			for(HtmlElement tr : trs){
				List<HtmlElement> tds = tr.getByXPath("td");
				if(tds.size() < 8) continue;
				
				//extract information to instantiate Course object
				Course course = new Course();
				// whats is first cell, second cell, etc??
				// take a look at the website, it is about the table cell
				scrapeFirstCell(course, tds.get(0));
				scrapeSecondCell(course, tds.get(1));
				scrapeFourthCell(course, tds.get(3));
				scrapeFifthCell(course, tds.get(4));
				scrapeSixthCell(course, tds.get(5));
				scrapeSevenCell(course, tds.get(6));
				scrapeEighthCell(course, tds.get(7));
				courses.add(course);
			}
			
			/*
			 * ---------- below is for cleaning up edge cases -----------
			 */
			
			// detect isCancelled
			for(Course course : courses){
				if(course.getRoom().length() == 0 && course.getFaculty().equals("STAFF") && course.getHoursOfDay().size() == 0){
					course.setIsCancelled(true);
				}
			}
			
			// set subject
			HashSet<String> subjects = new HashSet();
			for(Course course : courses){
				if(!course.getIsLabCourse()){
					String subject = course.getCourseCRN().split(" ")[0];
					course.setSubject(subject);
					subjects.add(subject);
				} else {
					String subject = "Lab";
					course.setSubject(subject);
				}
			}
			
			// set course description edge cases
			for (Course course : courses) {
				if (course.getCourseDescription() == null || course.getCourseDescription().length() == 0) {
					course.setCourseDescription("Not available");
				}
			}
			
			
			/*
			 * ------------- Edge cases clean up end -------------
			 */
			
			// store result
			JSONObject obj = new JSONObject();
			LocalDate date = LocalDate.now();
			obj.put("update-date", date);
			obj.put("courses", courses);
			try {
				FileWriter file = new FileWriter("output.json");
				file.write(obj.toJSONString());
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("Time: " + ((System.currentTimeMillis() - start) / 1000) + " second");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private String scrapeCourseOfferingTerm(HtmlPage page) {
		final String xPathExpr = "//div[@class='l-header-generic']/div[@class='l-wrapper blue']/div/div/h1";
		DomNode node = page.getFirstByXPath(xPathExpr);
		return node.getTextContent();
	}
	
	private void scrapeEighthCell(Course course, HtmlElement htmlElement) {
		String[] cores = htmlElement.getTextContent().split("/");
		if (cores != null && cores.length >= 1 && htmlElement.getFirstChild() != null) {
			Node s = htmlElement.getFirstChild().getAttributes().getNamedItem("title");
			cores = s.getNodeValue().split("/");
		}
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
	 * Detect whether content hours is in complex form or simple form
	 * Remove unneeded content: <strong>Hybrid(...)</>
	 *                          <strong>First/Second(...)</>
	 *                          if child node exist
	 */
	private void scrapeFifthCell(Course course, HtmlElement htmlElement) {
		String content = htmlElement.getTextContent();
		
		DomNode child = htmlElement.getLastElementChild();
		String childTagName = null;
		// if child/<strong> tag exist
		if(child != null){
			String timeContent = child.getTextContent().trim();
			course.setTimeContent(timeContent);
			childTagName = child.getNodeName();
			// TODO: 2020/10/21:
			// when child tag is <strong> this code works, because <strong> wrap "Hybrid(...)"
			// if child tag is <br> this code would not remove, since <br> tag is not wrapper tag
			htmlElement.removeChild(childTagName,0);
			content = htmlElement.getTextContent();
		}
		
		String[] timeCells = content.split("\\s+");
		if(content == null || content.length() == 0 || timeCells.length < 4){
			return;
		}
		EnumMap<DayOfWeek, List<Hours>> hoursOfDay = new EnumMap<>(DayOfWeek.class);
		
		// if third(0-base) timeCell length in timeCells is larger than 2, it is complex form
		if(timeCells[3].length() > 2){
			extractHoursComplex(course, hoursOfDay, content);
			course.setHoursOfDay(hoursOfDay);
		}else{
			if(timeCells.length < 10)
				extractHoursSimple(course, hoursOfDay, content);
			course.setHoursOfDay(hoursOfDay);
		}
	}
	
	/**
	 * Extract/separate time interval in a form of Simple Form
	 *
	 * @param hoursOfDay
	 * @param timeAsText Input Complex Format: "R 09:30 AM-10:30 PMMWF 11:45 AM-12:45 PM"
	 *
	 *                   Input Simple Format:  "R 09:30 AM-10:30 PM"
	 */
	private void extractHoursComplex(Course course, EnumMap<DayOfWeek, List<Hours>> hoursOfDay, String timeAsText) {
		String[] timeCells = timeAsText.split("\\s+");
		
		// timeCells has two(or more) time interval.
		// ex: T 03:45 PM-05:45 PMMW 09:20 AM-10:10 AMF03:45 PM-05:45PM
		// put @ between in every 3rd(0-base) cell to separate time interval properly
		for(int i=3; i<timeCells.length; i+=3){
			String endOfFirstTimeInterval = timeCells[i].substring(0,2);
			String startOfSecondTimeInterval = timeCells[i].substring(2);
			timeCells[i] = endOfFirstTimeInterval + "@" + startOfSecondTimeInterval;
		}
		
		// simply convert timeCells to textFormat
		//  FROM: [T][03:45][PM-05:45][PM@MW][09:20]....[PM-05:45][PM]
		//  TO  :  T 03:45 PM-05:45 PM@MW 09:20 AM-10:10 AM@F03:45 PM-05:45 PM
		StringBuilder intervalTimeAsTextSB = new StringBuilder();
		for(String timeCell : timeCells){
			intervalTimeAsTextSB.append(timeCell);
			intervalTimeAsTextSB.append(" ");
		}
		
		// T 03:45 PM-05:45 PM@MW 09:20 AM-10:10 AM@F03:45 PM-05:45 PM
		// split by single/multiple spaces, and then extract as simple
		String multipleTimeIntervalAsText = intervalTimeAsTextSB.toString().trim();
		String[] timeIntervals = multipleTimeIntervalAsText.split("@");
		for(String timeInterval : timeIntervals){
			extractHoursSimple(course, hoursOfDay, timeInterval);
		}
	}
	
	private void extractHoursSimple(Course course, EnumMap<DayOfWeek, List<Hours>> hoursOfDay, String timeInterval) {
		setTimeContentInCourse(course, timeInterval);
		
		String[] timeBox = timeInterval.split("\\s+");
		//"TR" -> 'T' 'R'
		char[] days = timeBox[0].toCharArray();
		
		if(timeBox.length < 2) return;
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
					case 'M':
						handleChangesOnHoursMap(DayOfWeek.MONDAY, hoursOfDay, hours);
						break;
						
					case 'T':
						handleChangesOnHoursMap(DayOfWeek.TUESDAY, hoursOfDay, hours);
						break;
						
					case 'W':
						handleChangesOnHoursMap(DayOfWeek.WEDNESDAY, hoursOfDay, hours);
						break;
						
					case 'R':
						handleChangesOnHoursMap(DayOfWeek.THURSDAY, hoursOfDay, hours);
						break;
						
					case 'F':
						handleChangesOnHoursMap(DayOfWeek.FRIDAY, hoursOfDay, hours);
						break;
						
					case 'S':
						handleChangesOnHoursMap(DayOfWeek.SATURDAY, hoursOfDay, hours);
						break;
				}
			}
		}catch(Exception e){
			System.out.println(interval);
			e.printStackTrace();
		}
	}
	
	public void handleChangesOnHoursMap(DayOfWeek day, EnumMap<DayOfWeek,List<Hours>> hoursOfDay, Hours hours) {
		if(hoursOfDay.get(day) == null){
			List<Hours> hoursList = new ArrayList();
			hoursList.add(hours);
			hoursOfDay.put(day, hoursList);
		}else{
			hoursOfDay.get(day).add(hours);
		}
	}
	
	private void setTimeContentInCourse(Course course, String timeInterval) {
		// simply set timeInterval in course attributes timeContent
		String newTimeInterval = (timeInterval.charAt(timeInterval.length()-1) == '\n') ? timeInterval.substring(0,timeInterval.length()-1) : timeInterval;
		if(course.getTimeContent() == null || course.getTimeContent().length() == 0 ||course.getTimeContent().charAt(course.getTimeContent().length()-1) == '\n'){
			course.setTimeContent(newTimeInterval);
		}else{
			course.setTimeContent(course.getTimeContent() + "\n" + newTimeInterval);
		}
	}
	
	private void scrapeFourthCell(Course course, HtmlElement htmlElement) {
		String faculty = htmlElement.getTextContent().trim();
		course.setFaculty(faculty);
	}

	private void scrapeSecondCell(Course course, HtmlElement htmlElement)
		throws UnsupportedEncodingException {
		String title = htmlElement.getTextContent();
		DomNode anchor = htmlElement.getFirstByXPath("a");
		if(anchor != null){
			title = anchor.getTextContent();
		}
		course.setTitle(title);
		
		// extract course description
		List<HtmlElement> div = htmlElement.getByXPath("div");
		if(div != null && div.size() != 0){
			DomNode divChild = div.get(0).getFirstByXPath("div[@class='h2']");
			String description = divChild.getTextContent().trim().replace('�', ' ');
			if(description != null && description.length() > 0){
				course.setCourseDescription(description);
			}else{
				course.setCourseDescription("***No Available Description***");
			}
		}
		
		// detect if it is Lab course
		String[] words = title.split(" ");
		if(words != null && words.length != 0){
			if(words[words.length-1].toLowerCase().equals("lab")){
				course.serIsLabCourse(true);
			}else{
				course.serIsLabCourse(false);
			}
		}else{
			course.serIsLabCourse(false);
		}
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
	
	private void writeToJsonRich(String jsonFileName) {
		Map<String,Object> map = new HashMap<>();
		map.put("offering-term", offeringTerm);
		map.put("courses", courses);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(Paths.get(jsonFileName).toFile(), map);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Scraper scraper = new Scraper();
		List<Course> courses = scraper.getCourses();
		scraper.writeToJsonRich("current-semester.json");
	}
}
