package tests;

/*
 * Author: Nitin Enjamuri 
 */

/*
 * These are some JUnit tests that tests the functionality of the project.
 */

import org.junit.*;

import src.SPSS;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;

public class JUnitTests {

	// NOT SURE WHETHER OR NOT readSubmissionsConcurrently() IS SUPPOSED TO
	// RETURN FALSE WHEN ANY THREAD READS ANY LINE AND DOES NOT SUCCESSFULLY
	// PROCESS IT, MADE IT SO IT DOES NOT DO SO.

	// Tests that some methods are treating names that are not spelled
	// identically as different. Note that this test does not create or use
	// any threads.
	@Test
	public void test1() {
		SPSS server = new SPSS(4);

		assertTrue(server.addStudent("Alice", "L"));
		assertTrue(server.addStudent("Alicia", "L"));

		server.addSubmission("Alice", "L", Arrays.asList(9, 1, 1, 1));
		server.addSubmission("Alicia", "L", Arrays.asList(9, 0, 0, 0));
		server.addSubmission("Alicia", "L", Arrays.asList(0, 0, 9, 0));

		assertEquals(1, server.numSubmissions("Alice", "L"));
		assertEquals(2, server.numSubmissions("Alicia", "L"));
		assertTrue(server.gotExtraCredit("Alice", "L"));
		assertFalse(server.gotExtraCredit("alice", "L"));
		assertFalse(server.gotExtraCredit("Alicia", "L"));
		assertTrue(server.satisfactory("Alice", "L"));
		assertFalse(server.satisfactory("Alicia", "L"));
		assertEquals(12, server.score("Alice", "L"));
		assertEquals(9, server.score("Alicia", "L"));
	}

	// Tests calling satisfactory() for a student who hasn't made any
	// submissions at all. Note that this test does not create or use threads.
	@Test
	public void test2() {
		SPSS server = new SPSS(5);

		server.addStudent("Bob Marley", "L");
		assertFalse(server.satisfactory("Bob Marley", "L"));

		server.addSubmission("Bob Marley", "L", Arrays.asList(1, 0, 0, 0, 0));
		assertFalse(server.satisfactory("Bob Marley", "L"));

		server.addSubmission("Bob Marley", "L", Arrays.asList(1, 2, 0, 0, 0));
		assertFalse(server.satisfactory("Bob, Marley", "L"));

		server.addStudent("Chris Pine", "L");
		server.addSubmission("Chris Pine", "L", Arrays.asList(0, 12, 0, 1, 1));
		assertTrue(server.satisfactory("Chris Pine", "L"));
	}

	// Tests calling satisfactory() in boundary cases.
	@Test
	public void test3() {
		SPSS server = new SPSS(4);

		server.addStudent("Daniel", "Dastname");
		server.addSubmission("Daniel", "Dastname", Arrays.asList(0, 0, 0, 0));
		assertFalse(server.satisfactory("Daniel", "Dastname"));

		server.addStudent("Eve", "Eastname");
		server.addSubmission("Eve", "Eastname", Arrays.asList(1, 1, 1, 0));
		assertTrue(server.satisfactory("Eve", "Eastname"));

		server.addStudent("Fay", "Fastname");
		server.addSubmission("Fay", "Fastname", Arrays.asList(0, 2, 0, 0));
		assertFalse(server.satisfactory("Fay", "Fastname"));

		server.addStudent("Gina", "Gastname");
		server.addSubmission("Gina", "Gastname", Arrays.asList(0, 2, 3, 0));
		assertTrue(server.satisfactory("Gina", "Gastname"));

		server.addStudent("Harry", "Hastname");
		server.addSubmission("Harry", "Hastname", Arrays.asList(0, 2, 0, 0));
		assertFalse(server.satisfactory("Harry", "Hastname"));

		server.addStudent("Ivy", "Istname");
		server.addSubmission("Ivy", "Istname", Arrays.asList(0, 2, 3, 4));
		assertTrue(server.satisfactory("Ivy", "Istname"));

		server.addStudent("Jack", "Jastname");
		server.addSubmission("Jack", "Jastname", Arrays.asList(0, 2, 0, 4));
		assertTrue(server.satisfactory("Jack", "Jastname"));
	}

	// Tests calling readSubmissionsConcurrently() to create one thread to
	// read one list of submissions made by one student.
	@Test
	public void test4() {
		SPSS server = new SPSS(5);

		server.addStudent("Good", "Grief");
		server.readSubmissionsConcurrently(Arrays.asList("public4-input"));

		assertEquals(1, server.numStudents());
		assertEquals(7, server.numSubmissions());
		assertEquals(7, server.numSubmissions("Good", "Grief"));
		assertTrue(server.satisfactory("Good", "Grief"));
		assertEquals(75, server.score("Good", "Grief"));
	}

	// Tests calling readSubmissionsConcurrently() to create one thread to
	// read one list of submissions made by two students.
	@Test
	public void test5() {
		SPSS server = new SPSS(5);

		server.addStudent("Good", "Grief");
		server.addStudent("Worried", "Wallow");
		server.readSubmissionsConcurrently(Arrays.asList("public5-input"));

		assertEquals(2, server.numStudents());
		assertEquals(13, server.numSubmissions());

		assertEquals(7, server.numSubmissions("Good", "Grief"));
		assertTrue(server.satisfactory("Good", "Grief"));
		assertEquals(75, server.score("Good", "Grief"));

		assertEquals(6, server.numSubmissions("Worried", "Wallow"));
		assertTrue(server.satisfactory("Worried", "Wallow"));
		assertEquals(85, server.score("Worried", "Wallow"));
	}

	// Tests calling readSubmissionsConcurrently() with some invalid
	// filenames in its argument list.
	@Test
	public void test10() {
		SPSS server = new SPSS(5);

		server.addStudent("Good", "Grief");
		server.addStudent("Worried", "Wallow");
		server.readSubmissionsConcurrently(Arrays.asList("nonexistent-file", "public10-input", "also-nonexistent"));

		assertEquals(2, server.numStudents());
		assertEquals(13, server.numSubmissions());

		assertEquals(7, server.numSubmissions("Good", "Grief"));
		assertTrue(server.satisfactory("Good", "Grief"));
		assertEquals(75, server.score("Good", "Grief"));

		assertEquals(6, server.numSubmissions("Worried", "Wallow"));
		assertTrue(server.satisfactory("Worried", "Wallow"));
		assertEquals(85, server.score("Worried", "Wallow"));
	}

	// Tests input and output
	@Test
	public void IOTest() {
		SPSS s = new SPSS(5);
		s.readSubmissionsConcurrently(Arrays.asList("public10-input"));
		s.readSubmissionsConcurrently(Arrays.asList("publicDNE-input"));
	}

	// Tests an Empty List
	@Test
	public void testEmptyFileList() {
		SPSS s = new SPSS(5);

		s.addStudent("Good", "Grief");

		s.readSubmissionsConcurrently(Collections.emptyList());

		// Ensure no changes to SPSS object
		assertEquals(1, s.numStudents());
		assertEquals(0, s.numSubmissions());
	}

	@Test
	public void testMultipleCallsToMethod() {
		SPSS s = new SPSS(5);

		s.addStudent("Good", "Grief");
		s.readSubmissionsConcurrently(Arrays.asList("public7a-input"));

		s.addStudent("Worried", "Wallow");
		s.readSubmissionsConcurrently(Arrays.asList("public7b-input"));

		// Ensure that the end results are correct
		assertEquals(2, s.numStudents());
		assertEquals(13, s.numSubmissions());

		assertTrue(s.satisfactory("Good", "Grief"));
		assertEquals(75, s.score("Good", "Grief"));
		assertEquals(7, s.numSubmissions("Good", "Grief"));

		assertTrue(s.satisfactory("Worried", "Wallow"));
		assertEquals(85, s.score("Worried", "Wallow"));
		assertEquals(6, s.numSubmissions("Worried", "Wallow"));
	}

	// Test case for reading submissions concurrently with null fileNames
	@Test
	public void testReadSubmissionsConcurrently_NullFileNames() {
		SPSS spss = new SPSS(1);
		assertFalse(spss.readSubmissionsConcurrently(null));
		assertEquals(0, spss.numSubmissions());
	}

	// Test case for reading submissions concurrently with empty fileNames list
	@Test
	public void testReadSubmissionsConcurrently_EmptyFileNames() {
		SPSS spss = new SPSS(1);
		assertFalse(spss.readSubmissionsConcurrently(Arrays.asList()));
		assertEquals(0, spss.numSubmissions());
	}

}
