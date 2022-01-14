package tomoaki.jsonreader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import tomoaki.courseClasses.Course;

public class ReadCurrentSemester {
	
	List<Course> courses;
	
	public ReadCurrentSemester() {
		courses = getCourses("current-semester.json");
	}
	
	public List<Course> getCourses() {
		return courses;
	}
	
	public static List<Course> getCourses(String fileName) {
		File courseFile = new File(fileName);
		
		List<Course> courses = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			courses = mapper.readerFor(new TypeReference<List<Course>>() {
			})
				.readValue(courseFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return courses;
	}
	
	public static void main(String[] args) {
		List<Course> courses = getCourses("current-semester.json");
		
		// do whatever
		
		printCores(courses);
	}
	
	public static void printCores(List<Course> courses) {
		Set<String> cores = new TreeSet<>();
		courses.forEach(course -> cores.addAll(course.getCores()));
		cores.forEach(core -> System.out.print("\'"+core+"\', "));
	}
	
	public static void printCourseThatHasTwoClassInADay(List<Course> courses) {
		courses.forEach(course -> {
				course.getHoursOfDay().values().forEach(hours -> {
					if (hours.size() >= 2) {
						System.out.println(course.getTitle());
					}
				});
			});
	}
}
