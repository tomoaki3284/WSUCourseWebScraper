package tomoaki.WebScraper;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.util.ArrayList;
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
				
				String[] contents = tds.get(4).getTextContent().split(" ");
				for(String content : contents){
					if(content.length() != 0)
						System.out.print(content + " ");
				}
				System.out.println();
				//extract information to instantiate Course object
//				Course course = new Course();
//				scrapeFirstCell(course, tds.get(0));
//				scrapeSecondCell(course, tds.get(1));
//				scrapeFourthCell(course, tds.get(3));
//				scrapeFifthCell(course, tds.get(4));
//				scrapeSixthCell(course, tds.get(5));
//				scrapeSevenCell(course, tds.get(6));
//				scrapeEigthCell(course, tds.get(7));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void scrapeEigthCell(Course course, HtmlElement htmlElement) {
	}

	private void scrapeSevenCell(Course course, HtmlElement htmlElement) {
	}

	private void scrapeSixthCell(Course course, HtmlElement htmlElement) {
	}

	private void scrapeFifthCell(Course course, HtmlElement htmlElement) {
		EnumMap<DayOfWeek, Hours> hoursOfDay = new EnumMap<DayOfWeek, Hours>(DayOfWeek.class);
		int countBRTag = htmlElement.getByXPath("br").size();
		boolean isHybrid = (htmlElement.getByXPath("strong").size() >= 1);
		
		// idHybrid and amount of <br> tag is 1, then simple Time structures
		if(isHybrid || countBRTag == 1){
			extractHoursSimple(hoursOfDay, htmlElement.getTextContent(), isHybrid);
		}else{
			extractHoursComplex(hoursOfDay, htmlElement.getTextContent());
		}
		// if isHybrid, then simple Time structure
		while(countBRTag != 0){
			
			countBRTag--;
		}
	}
	
	private void extractHoursComplex(EnumMap<DayOfWeek, Hours> hoursOfDay, String textContent) {
	
	}
	
	private void extractHoursSimple(EnumMap<DayOfWeek, Hours> hoursOfDay, String textContent, boolean isHybrid) {
		// textContent format example: "TR 12:45 PM-02:00 PM"
		//                             "TR 12:45 PM-02:00 PMHybrid (...)"
		
		// "TR 12:45 PM-02:00 PMHybrid (...)" -> "TR 12:45 PM-02:00 PM"
		if(isHybrid){
			int index = textContent.indexOf("Hybrid");
			textContent = textContent.substring(0,index);
		}
		//"TR" -> 'T' 'R'
		String[] timeBox = textContent.split(" ");
		char[] days = timeBox[0].toCharArray();
		
		// "PM-02:00" -> "PM" "02:00"
		String[] timeTag_endTime = timeBox[2].split("-");
		
		//concat times
		String startTime = timeBox[1] + timeTag_endTime[0];
		String endTime = timeTag_endTime[0] + timeBox[timeBox.length-1];
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
			System.out.println("Hours class throws Exception @Tomoaki Mitsuhashi");
			e.printStackTrace();
		}
	}
	
	private void scrapeFourthCell(Course course, HtmlElement htmlElement) {
		String faculty = htmlElement.getTextContent().trim();
		course.setFaculty(faculty);
	}

	private void scrapeSecondCell(Course course, HtmlElement htmlElement) {
		HtmlElement anchor = htmlElement.getFirstByXPath("a[@href='#']");
		String title = anchor.getTextContent();
		course.setTitle(title);
	}

	private void scrapeFirstCell(Course course, HtmlElement htmlElement) {
		String[] contents = htmlElement.getTextContent().split(" ");
		String category = contents[0];
		String[] level_section = contents[1].split("-");
		int level = Integer.parseInt(level_section[0]);
		String section = level_section[1];
		String crn = contents[2].substring(1,contents[2].length()-1);

		course.setCategory(category);
		course.setLevel(level);
		course.setSection(section);
		course.setCrn(crn);
	}
}
