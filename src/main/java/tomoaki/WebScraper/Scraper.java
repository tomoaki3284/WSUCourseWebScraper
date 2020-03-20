package tomoaki.WebScraper;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import tomoaki.courseClasses.Course;
import tomoaki.courseClasses.DayOfWeek;
import tomoaki.courseClasses.Hours;

public class Scraper {
	
	private List<Course> courses;
	
	public Scraper() {
		this("http://www.westfield.ma.edu/offices/registrar/master-schedule");
	}
	
	public Scraper(String URL) {
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
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void scrapeEighthCell(Course course, HtmlElement htmlElement) {
		String[] cores = htmlElement.getTextContent().split("/");
		for(String core : cores){
			course.addCore(core);
		}
	}

	private void scrapeSevenCell(Course course, HtmlElement htmlElement) {
		double credit = Double.parseDouble(htmlElement.getTextContent());
		course.setCredit(credit);
	}

	private void scrapeSixthCell(Course course, HtmlElement htmlElement) {
		String content = htmlElement.getTextContent();
		course.setRoom(content);
	}

	private void scrapeFifthCell(Course course, HtmlElement htmlElement) {
		EnumMap<DayOfWeek, Hours> hoursOfDay = new EnumMap<DayOfWeek, Hours>(DayOfWeek.class);
		int countBRTag = htmlElement.getByXPath("br").size();
		boolean isHybrid = (htmlElement.getByXPath("strong").size() > 0);
		
		// isHybrid or amount of <br> tag >= 1, then simple Time structures
		if(isHybrid || countBRTag >= 1){
			extractHoursSimple(hoursOfDay, htmlElement.getTextContent(), isHybrid);
		}else{
			while(countBRTag >= 0){
				extractHoursComplex(hoursOfDay, htmlElement.getTextContent());
				countBRTag--;
			}
		}
		// if isHybrid, then simple Time structure
		while(countBRTag != 0){
			
			countBRTag--;
		}
	}
	
	private void extractHoursComplex(EnumMap<DayOfWeek, Hours> hoursOfDay, String textContent) {
		// maximum difference in time is two
		// format example: "R 09:30 AM-10:30 PMMWF 11:45 AM-12:45 PM"
		
		// "R 09:30 AM-10:30 PMMWF 11:45 AM-12:45 PM" -> "R 09:30 AM-10:30 PM" "MWF 11:45 AM-12:45 PM"
		// "R 09:30 AM-10:30 PM@MWF 11:45 AM-12:45 PM" and then separate by "@"
		boolean isComplex = false;
		String[] words = textContent.split(" ");
		for(int i=0; i<words.length; i++){
			String word = words[i];
			if(!word.contains("0") && !word.contains("1")){
				// search where "PM..." || "AM..."
				if ((word.contains("AM") || word.contains("PM")) && word.length() >= 3){
					System.out.println(word);
					words[i] = word.substring(0,2) + "@" + word.substring(2);
					isComplex = true;
				}
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<words.length; i++){
			String word = words[i];
			sb.append(word);
			if(i != words.length-1){
				sb.append(" ");
			}
		}
		
		if(!isComplex){
			extractHoursSimple(hoursOfDay,textContent,false);
			return;
		}
		
		// split by " " or "@"
		words = sb.toString().split(" |@");
		System.out.println(textContent);
		System.out.println(Arrays.toString(words));
		for(int i=0; i<words.length; i++){
			sb.append(words[i]);
			if(i % 3 == 0){
				extractHoursSimple(hoursOfDay, sb.toString(), false);
				sb.setLength(0);
			}else{
				sb.append(" ");
			}
		}
	}
	
	private void extractHoursSimple(EnumMap<DayOfWeek, Hours> hoursOfDay, String textContent, boolean isHybrid) {
		// textContent format example: "TR 12:45 PM-02:00 PM"
		//                             "TR 12:45 PM-02:00 PMHybrid (...)"
		
		// "TR 12:45 PM-02:00 PMHybrid (...)" -> "TR 12:45 PM-02:00 PM"
		if(isHybrid){
			String[] hybridWords = new String[]{"Hybrid", "FIRST", "SECOND"};
			int index = -1;
			int i = 0;
			while(index == -1 && i < hybridWords.length){
				index = textContent.indexOf(hybridWords[i]);
				i++;
			}
			if(index != -1){
				textContent = textContent.substring(0,index);
			}
		}
		
		String[] timeBox = textContent.split(" ");
		if(timeBox.length < 4) return;
		
		//"TR" -> 'T' 'R'
		char[] days = timeBox[0].toCharArray();
		
		// "PM-02:00" -> "PM" "02:00"
		String[] timeTag_endTime = timeBox[2].split("-");
		
		//concat times
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
		course.setTitle(title);
	}

	private void scrapeFirstCell(Course course, HtmlElement htmlElement) {
		String content = htmlElement.getTextContent();
		
		String[] contents = content.split(" ");
		
		if(contents.length != 3) return;
		
		String category = contents[0];
		String[] level_section = contents[1].split("-");
		
		if(level_section.length <= 1){
			System.out.println(htmlElement.getTextContent());
			return;
		}
		
		int level = Integer.parseInt(level_section[0]);
		String section = level_section[1];
		String crn = contents[2].substring(1,contents[2].length()-1);

		course.setCategory(category);
		course.setLevel(level);
		course.setSection(section);
		course.setCrn(crn);
	}
}
