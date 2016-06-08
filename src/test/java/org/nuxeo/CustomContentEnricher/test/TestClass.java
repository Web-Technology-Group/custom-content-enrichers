package org.nuxeo.CustomContentEnricher.test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
/**
 * A class to test ideas.
 * 
 * @author Abidur.Rahman
 *
 */
public class TestClass {
	
	enum TestEnum {
		ENUM_ONE,
		ENUM_TWO,
		ENUM_THREE
	}
	
	class TestThis {
		
	}
	private Date lastModified;

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	private List<String> _hospitalName; // Initialize all this stuff

	public List<String> getHospitalName() {
		return _hospitalName;
	}

	public void set_categories(List<String> hospitalName) {
		this._hospitalName = hospitalName;
	}

	public int getStringPos(String hospitalName) {
	  return _hospitalName.indexOf(hospitalName);
	}
	
	public static TestClass latestNews(List<TestClass> documentList) {
		Comparator<TestClass> byDate = new Comparator<TestClass>() {
			@Override
			public int compare(TestClass o1, TestClass o2) {
				if (o1.getLastModified() == null && o2.getLastModified() == null)
					return 0;
				int date1 = (int) o1.getLastModified().getTime();
				int date2 = (int) o2.getLastModified().getTime();
				return date1 > date2 ? -1 : date2 > date1 ? 1 : 0;

			}
		};
		Collections.sort(documentList, byDate);
		return documentList.get(0);
	}
	
	public String splitItems(String contents) {
		String outcome = "";
		String[] splitContents = contents.split("/");
		for(String output : splitContents) {
			outcome = output.substring(output.lastIndexOf("/") + 1, output.length());
		}
		return outcome;
	}
	
	public String returnLastIndex(String words) {
		String[] stringArray = words.split("/");
		return stringArray[stringArray.length - 1];
	}
	
	public Class<?> retrieveClass(String className) throws ClassNotFoundException {
		Class<?> classes = Class.forName(className);
		return classes;
	}
	
	public boolean returnTypeOf(Object object) {
		if(object instanceof Object) {
			return true;
		}
		return false;
	}
	
	public Enum<?> returnPropertyType(String className, String propertyName, String innerClassName) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Class<?> classes = Class.forName(className);
		List<Class<?>> listOfClasses = Arrays.asList(classes.getDeclaredClasses());
		for(Class classResult : listOfClasses) {
			if(classResult.getCanonicalName().equals(innerClassName)) {
				Enum<?> returnEnum = Enum.valueOf(classResult, propertyName);
				return returnEnum;
			}
		}
		return null;
	}
	
	public String modifyStringForLinks(String modifyString) {
		if (modifyString == null || modifyString.length() == 0 )return "";
		String[] modifiedString = modifyString.split("/");
		String firstIndex = modifiedString[0].replaceAll(" ", "-").toLowerCase() + "/";
		return firstIndex;
	}
	
	
}
