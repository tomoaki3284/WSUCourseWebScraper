package tomoaki.jsonreader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import tomoaki.courseClasses.Course;

public class ReadCurrentSemester {
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
		
		for (Course course : courses) {
			System.out.println(course);
		}
	}
}
