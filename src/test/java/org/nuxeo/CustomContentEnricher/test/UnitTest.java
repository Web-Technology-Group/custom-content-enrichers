package org.nuxeo.CustomContentEnricher.test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.nuxeo.CustomContentEnricher.test.TestClass.TestEnum;

public class UnitTest {

	private TestClass tc;

	@Before
	public void setUp() {
		tc = new TestClass();
	}

	@Test
	public void testHospitalTitles() {
		String test = "County Durham and Darlington/Bishop Auckland/Auckland Park Hospital";
		String expected = test.substring(test.lastIndexOf("/") + 1, test.length());
		String[] testSplit = test.split("/");
		String s = "";
		for (String t : testSplit) {
			s = t;
		}
		assertEquals(s, "Auckland Park Hospital");
	}

	@Test
	public void testLastModifiedDate() throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		Date d1 = df.parse("01-01-2001");
		Date d2 = df.parse("02-01-2001");

		tc.setLastModified(d1);

		TestClass tc2 = new TestClass();
		tc2.setLastModified(d2);

		List<TestClass> listTest = new ArrayList<>();
		listTest.add(tc);
		listTest.add(tc2);

		TestClass outcome = TestClass.latestNews(listTest);
		assertEquals(tc2, outcome);
	}

	@Test
	public void testIndexOfList() {
		List<String> list = new ArrayList<>();
		list.add("County Durham and Darlington/Bishop Auckland/Auckland Park Hospital");
		String test = "County Durham and Darlington/Bishop Auckland/Auckland Park Hospital";

		tc.set_categories(list);
		assertEquals(tc.getStringPos(test), 0);

		String listValue = list.get(tc.getStringPos(test));
		assertEquals(listValue, test);

		String testSplit = tc.splitItems(test);

		assertEquals("Auckland Park Hospital", testSplit);
	}

	@Test
	public void testStringArray() {
		String test = "County Durham and Darlington/Bishop Auckland/Auckland Park Hospital";
		String outcome = tc.returnLastIndex(test);
		String expected = "Auckland Park Hospital";

		assertEquals(outcome, expected);
	}

	@Test
	public void testClassTest() throws ClassNotFoundException {
		String className = "java.lang.Object";
		java.lang.Object test = new java.lang.Object().getClass();
		Class<?> returnedClass = tc.retrieveClass(className);

		assertEquals(test, returnedClass);
	}

	@Test
	public void testObjectType() {
		assertEquals(tc.returnTypeOf(new java.lang.Object()), true);
		TestEnum i = TestClass.TestEnum.ENUM_ONE;
	}

	@Test
	public void testEnum() throws ClassNotFoundException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		String innerClassName = "org.nuxeo.CustomContentEnricher.test.TestClass.TestEnum";
		String propertyName = "ENUM_ONE";
		String className = "org.nuxeo.CustomContentEnricher.test.TestClass";
		Enum actual = TestClass.TestEnum.ENUM_ONE;
		Enum expected = tc.returnPropertyType(className, propertyName, innerClassName);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testStringLinks() {
		String testString = "about care treatment/adult-mental-disorder";
		String actual = "about-care-treatment/";
		String expected = tc.modifyStringForLinks(testString);
		assertEquals(expected, actual);
	}
}
