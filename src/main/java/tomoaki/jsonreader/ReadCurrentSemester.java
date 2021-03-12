package tomoaki.jsonreader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
		
		// do whatever
		
		
		HashSet<String> subjects = new HashSet<>();
		for (Course course : courses) {
			subjects.add(course.getSubject());
		}
		
		String[] subs = new String[subjects.toArray().length];
		int i = 0;
		for (Object sub : subjects.toArray()) {
			subs[i++] = sub.toString();
		}
		
		Arrays.sort(subs);
		
		for (String sub : subs) {
			if (sub == null || sub.toString().toLowerCase().equals("lab")) continue;
			System.out.println("\'" + sub.toString() + "\',");
		}
	}
}
