package tomoaki.courseClasses;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.PriorityQueue;

public class Schedule {
	List<Course> courses;
	double totalCredit;
	
	public Schedule() {
		this.courses = new ArrayList<Course>();
		totalCredit = 0;
	}
	
	public List<Course> getCourses() {
		return courses;
	}
	
	public void addCourse(Course course) {
		courses.add(course);
		totalCredit += course.getCredit();
	}
	
	public double getTotalCredit() {
		return totalCredit;
	}
	
//	public List<Course> filterByCourseCategory(String category) {
//		List<Course> res = new ArrayList();
//		for(Course course : courses){
//			if(course.getCourseCategory().equals(category)){
//				res.add(course);
//			}
//		}
//		return res;
//	}
//
//	public List<Course> filterByTitle(String title) {
//		List<Course> res = new ArrayList();
//		for(Course course : courses){
//			if(course.getTitle().equals(title)){
//				res.add(course);
//			}
//		}
//		return res;
//	}
//
//	public List<Course> filterByFaculty(String faculty) {
//		List<Course> res = new ArrayList();
//		for(Course course : courses){
//			if(course.getFaculty().toLowerCase().contains(faculty.toLowerCase())){
//				res.add(course);
//			}
//		}
//		return res;
//	}
	
	/**
	 * Remove the course that matches the param course
	 *
	 * @param course
	 * @return True, if successfully found course to be removed
	 *         False, otherwise
	 */
	public boolean removeCourse(Course course) {
		totalCredit -= course.getCredit();
		return courses.remove(course);
	}
	
	/**
	 * Check if the courses have any overlapped hours
	 *
	 * @return True, if some course hours is overlapped
	 */
//	public boolean isHoursOverlap() {
//		if(courses.size() <= 1) return false;
//		// loop courses
//		// Store Hours in Map<DayOfWeek, List<Hours>>
//		EnumMap<DayOfWeek,List<Hours>> map = new EnumMap<>(DayOfWeek.class);
//		for(Course course : courses){
//			for(DayOfWeek day : course.getHoursOfDay().keySet()){
//				Hours hours = course.getHoursFromDay(day);
//				if(map.get(day) == null){
//					ArrayList<Hours> list = new ArrayList();
//					list.add(hours);
//					map.put(day, list);
//				}else{
//					map.get(day).add(hours);
//				}
//			}
//		}
//
//		// sort Hours by START
//		// check if prev course START exceed next course END
//		for(DayOfWeek day : map.keySet()){
//			List<Hours> list = map.get(day);
//			PriorityQueue<Hours> minHeap = new PriorityQueue<Hours>((a,b) -> {
//				if(a.getStartHour() == b.getStartHour()){
//					return a.getStartMinute() - b.getStartMinute();
//				}
//				return a.getStartHour() - b.getStartHour();
//			});
//
//			for(Hours hours : list){
//				minHeap.offer(hours);
//			}
//
//			if(minHeap.size() <= 1) continue;
//
//			Hours prev = minHeap.poll();
//			while(!minHeap.isEmpty()){
//				Hours current = minHeap.poll();
//				if(prev.getStartHour() == current.getStartHour()){
//					if(prev.getStartMinute() > current.getStartMinute()){
//						return true;
//					}
//				}else if(prev.getStartHour() > prev.getStartHour()){
//					return true;
//				}
//			}
//		}
//
//		return false;
//	}
}
