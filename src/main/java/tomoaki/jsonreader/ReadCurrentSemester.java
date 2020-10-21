package tomoaki.jsonreader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
		
		HashSet<String> rooms = new HashSet<>();
		for(Course course : courses){
			rooms.add(course.getRoom());
		}
		
		for (String room : rooms) {
			System.out.println(room);
		}
	}
}
