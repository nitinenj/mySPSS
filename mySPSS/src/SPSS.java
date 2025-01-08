package src;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.io.FileReader;

/*
 * Author: Nitin Enjamuri
 * UID: 120094262
 * 
 * I pledge on my honor that I have not given or received any unauthorized
 * assistance on this assignment/examination
 */

/*
 * This is the SPSS Class that basically acts as the mock
 * "public test server"
 * that are we trying to create. We have 3 variables in this class,
 * numTests - describes the number of tests in a submission for
 * for this submit 
 * stuList - describes a list of Student objects in the submit server,
 * each with its own submission.
 */


public class SPSS {

  private int numTests;
  private ArrayList<Student> stuList; // using stuList as a synch lock as well


  /*
   * Initializes non-static fields. This constructor is set up makes it
   * allowed to have 0 or less maximum submissions. However, the lowest
   * numTests can be is 1. Any value less than 1 is replaced with 1.
   */
  public SPSS(int numTests) {
    this.numTests = (numTests < 1) ? 1 : numTests;
    stuList = new ArrayList<>();
  }

  /*
   * The addStudent() method first checks to see if there any other
   * students with the same firstName. If there aren't, it checks if the
   * parameter newStudentName and makes sure it is not null or blank.
   */
  public boolean addStudent(String f, String l) {
    synchronized (stuList) { // synchronized this method due to this method
                             // editing stuList
      boolean worked = false;

      // check if string is null or blank or if student exists
      if (f != null && l != null && indexOfStudent(f, l) == -1 
          && !f.equals("") && !l.equals("")) {
        stuList.add(new Student(f, l));
        worked = true;
      }

      return worked;
    }
  }

  /*
   * The numStudents() method returns the number of students in the SPSS
   * class's list of students.
   */
  public int numStudents() {
    return stuList.size();
  }

  /*
   * The addSubmission() method is meant to add a Submission to a
   * particular Student. To do this, we check if the firstName parameter is even
   * in the stuList, using the indexOfStudent() helper method (for more
   * info see indexOfStudent() comment). Once the parameters are verified
   * and correct, the Student in the list gets its own submission added and
   * returns true. It returns false, otherwise.
   */
  public boolean addSubmission(String f, String l, List<Integer> testResults) {
    boolean worked = false;
    boolean hasPosTestResults = true;

    // check for any negative values in test results
    for (int result : testResults) {
      if (result < 0)
        hasPosTestResults = false;
    }

    // check to see if student exists, has positive test results, then adds
    // submission.
    synchronized (stuList) {
      if (indexOfStudent(f, l) > -1 && hasPosTestResults) {
        worked = stuList.get(indexOfStudent(f, l))
            .addSubmission(testResults, numTests);
      }
    }

    return worked;
  }

  public boolean readSubmissionNorm(String fileName) {
	  String command;
      try { // processing data
    	  Scanner scan = new Scanner(new FileReader("inputs/" + fileName));
    	  //FileWriter fw = new FileWriter(.toString()); NOT MAKING LOGGER
    	  while (scan.hasNextLine()) {
    		  command = scan.nextLine();
    		  String[] params = command.split("\\s+");
    		  for(String a : params) {
    			  System.out.print(" " + a);
    		  }
  			
  			  switch (params[0].toLowerCase()) {
  				case "addsub": {
  					String f = params[1];
  					String l = params[2];
  					ArrayList<Integer> submissions = new ArrayList<Integer>();
  					for (int i = 0; i < numTests; i++) {
  						submissions.add(Integer.parseInt(params[i + 3]));
  					}
  					System.out.println(indexOfStudent(f, l));
  					boolean result = addSubmission(f, l, submissions);
  					System.out.println(
  							result ? "Submission added for " + f + " " + l :
  								"Student not found: " + f + " " + l
  							);
  					}
  					break;
  				
  				case "addstu": {
  					String f = params[1];
  					String l = params[2];
  					boolean result = addStudent(f, l);
  					System.out.println(
  							result ? "Student added: " + f + " " + l :
  								"Student already exists: " + f + " " + l
  							);
  					}
  					break;
  				
  				case "readfiles": {
  					ArrayList<String> files = new ArrayList<String>();
  					for (int i = 1; i < params.length; i++) {
  						files.add(params[i]);
  					}
  					boolean result = readSubmissionsConcurrently(files);
  					System.out.println(
  							result ? "Submissions added" : "Error" 
  							);
  					}
  					break;
  				
  				case "numstu":
  					System.out.println(
  							"There are currently " + numStudents() + 
  								" student(s) in the  "
  							);
  					break;
  				
  				case "numsubs": {
  					if (params.length == 1) {
  						// list all 
  						System.out.println(
  								"There are a total of " + numSubmissions() + 
  									" submission(s). ");
  					} else {
  						// list specific student
  						System.out.println(
  								"There are a total of " + 
  										numSubmissions(params[1], params[2]) + 
  										" submission(s) for " + params[1] + " " + params[2] + ". ");
  					}
  					
  					}
  					break;
  				
  				case "stuscore": {
  					System.out.println(
  							params[1] + " " + params[2] + " currently has a score of " + 
  									score(params[1], params[2]) + ". ");
  					}
  					break;
  				
  				case "stusatis": {
  					System.out.println(
  							params[1] + " " + params[2] + " currently " + 
  									(satisfactory(params[1], params[2]) ? "IS " : "is NOT ") + 
  									"satisfactory. ");
  					}
  					break;
  				
  				case "struxcred": {
  					System.out.println(
  							params[1] + " " + params[2] + 
  									(satisfactory(params[1], params[2]) ? " HAS " : " has NOT ") + 
  									"gotten extra credit. ");
  					}
  					break;
  				
  				case "exit": {
  					System.out.println("pce gang love u");
  					}
  					
  				
  				default:
  					System.out.println("\"" + command + "\" is not a command!");
  			  }
    	  }
      } catch (Exception e) {
    	  System.err.println(e.getMessage());
      }
      return true;
  }
  
  /*
   * This method reads multiple files at the same time given a list of
   * fileNames. Returns
   */
  public boolean readSubmissionsConcurrently(List<String> fileNames) {

    /*
     * MyReader inner class is an subclass of the Thread class. The purpose
     * of this class is to be instantiated and read through a given file,
     * adding data to SPSS database when applicable.
     */
    class MyReader extends Thread {

      // only has one private data, which is the file firstName as a String.
      private String fileName;

      // MyReader constructor
      MyReader(String fn) {
        fileName = fn;
      }

//      // Overriding the Thread run() method
//      @Override public void run() {
//        String firstName;
//        String lastName;
//        
//        try { // processing data
//          Scanner scan = new Scanner(new FileReader("inputs/" + fileName));
//
//          // per line, next() is called 1 + numTests times. first time is
//          // for the firstName, the second time is for the loop that reads the
//          // numTests# of ints
//          while (scan.hasNext()) {
//
//            // first next() call
//            firstName = scan.next();
//            lastName = scan.next();
//            ArrayList<Integer> results = new ArrayList<>();
//
//            for (int i = 0; i < numTests; i++)
//              results.add(scan.nextInt()); // calls next() numTests times
//
//            // do addSubmission with corresponding firstName and results. if it
//            // returns false, set threadFailedFlag to true, as a thread
//            // failed processing.
//            addSubmission(firstName, lastName, results);
//          }
//
//          scan.close(); // close scanner object
//
//        } catch (IOException e) {
//          System.err.println(e.getMessage());
//        }
//        
// Overriding the Thread run() method
      @Override public void run() {
    	  String command;
          try { // processing data
        	  Scanner scan = new Scanner(new FileReader("inputs/" + fileName));
        	  //FileWriter fw = new FileWriter(.toString());
        	  while (scan.hasNextLine()) {
        		  command = scan.nextLine();
        		  String[] params = command.split("\\s+");
        		  for(String a : params) {
        			  System.out.print(" " + a);
        		  }
      			
      			  switch (params[0].toLowerCase()) {
      				case "addsub": {
      					String f = params[1];
      					String l = params[2];
      					ArrayList<Integer> submissions = new ArrayList<Integer>();
      					for (int i = 0; i < numTests; i++) {
      						submissions.add(Integer.parseInt(params[i + 3]));
      					}
      					System.out.println(submissions);
      					boolean result = addSubmission(f, l, submissions);
      					System.out.println(
      							result ? "Submission added for " + f + " " + l :
      								"Student not found: " + f + " " + l
      							);
      					}
      					break;
      				
      				case "addstu": {
      					String f = params[1];
      					String l = params[2];
      					boolean result = addStudent(f, l);
      					System.out.println(
      							result ? "Student added: " + f + " " + l :
      								"Student already exists: " + f + " " + l
      							);
      					}
      					break;
      				
      				case "readfiles": {
      					ArrayList<String> files = new ArrayList<String>();
      					for (int i = 1; i < params.length; i++) {
      						files.add(params[i]);
      					}
      					boolean result = readSubmissionsConcurrently(files);
      					System.out.println(
      							result ? "Submissions added" : "Error" 
      							);
      					}
      					break;
      				
      				case "numstu":
      					System.out.println(
      							"There are currently " + numStudents() + 
      								" student(s) in the  "
      							);
      					break;
      				
      				case "numsubs": {
      					if (params.length == 1) {
      						// list all 
      						System.out.println(
      								"There are a total of " + numSubmissions() + 
      									" submission(s). ");
      					} else {
      						// list specific student
      						System.out.println(
      								"There are a total of " + 
      										numSubmissions(params[1], params[2]) + 
      										" submission(s) for " + params[1] + " " + params[2] + ". ");
      					}
      					
      					}
      					break;
      				
      				case "stuscore": {
      					System.out.println(
      							params[1] + " " + params[2] + " currently has a score of " + 
      									score(params[1], params[2]) + ". ");
      					}
      					break;
      				
      				case "stusatis": {
      					System.out.println(
      							params[1] + " " + params[2] + " currently " + 
      									(satisfactory(params[1], params[2]) ? "IS " : "is NOT ") + 
      									"satisfactory. ");
      					}
      					break;
      				
      				case "struxcred": {
      					System.out.println(
      							params[1] + " " + params[2] + 
      									(satisfactory(params[1], params[2]) ? " HAS " : " has NOT ") + 
      									"gotten extra credit. ");
      					}
      					break;
      				
      				case "exit": {
      					System.out.println("pce gang love u");
      					}
      					return;
      				
      				default:
      					System.out.println("\"" + command + "\" is not a command!");
      			  }
        	  }
          } catch (Exception e) {
        	  System.err.println(e.getMessage());
          }
      }
    }

    // making list of threads
    ArrayList<MyReader> threadList = new ArrayList<>();
    boolean worked = false;

    // checks to see fileNames list is not null and not empty
    if (fileNames != null && !fileNames.isEmpty()) {

      // for each file in fileNames, makes a thread
      for (String filefirstName : fileNames) {
        MyReader thread = new MyReader(filefirstName);
        threadList.add(thread);
        thread.start();
        System.out.println("started reading " + filefirstName);
      }

      // for each thread we made, call .join() to wait for all threads to
      // complete so we can return a value from this method once all files
      // are read
      for (MyReader thread : threadList) {
        try {
          thread.join();
        } catch (InterruptedException e) {
          System.err.print(e);
        }
      }

      worked = true;
    }

    // returns true if and only if worked is true.
    return worked;
  }

  /*
   * The score() method is meant to return the highest score of a
   * particular student. If the parameters are invalid, the method returns
   * -1.
   */
  public int score(String f, String l) {
    return (indexOfStudent(f, l) > -1)
        ? stuList.get(indexOfStudent(f, l)).getHighestScore()
        : -1;
  }

  /*
   * The numSubmissions() method that has its parameter as String firstName
   * is meant to return the number of submissions of a particular student.
   * If the parameters are invalid, the method returns -1.
   */
  public int numSubmissions(String f, String l) {
    return (indexOfStudent(f, l) > -1)
        ? stuList.get(indexOfStudent(f, l)).getNumSubmissions()
        : -1;
  }

  /*
   * The numSubmissions() method that has no parameters is meant to return
   * the highest score of all of the students combined.
   */
  public int numSubmissions() {
    int sum = 0;
    for (Student s : stuList)
      sum += s.getNumSubmissions();
    return sum;
  }

  /*
   * The satisfactory() method is meant to return if a particular student
   * is satisfactory. If the parameters are invalid, the method will return
   * false.
   */
  public boolean satisfactory(String f, String l) {
    return (indexOfStudent(f, l) > -1)
        ? stuList.get(indexOfStudent(f, l)).isSatisfactory()
        : false;
  }

  /*
   * The gotExtraCred() method is meant to return if a particular student
   * has extra credit. If the parameters are invalid, the method will
   * return false.
   */
  public boolean gotExtraCredit(String f, String l) {
    return (indexOfStudent(f, l) > -1)
        ? stuList.get(indexOfStudent(f, l)).gotExtraCred()
        : false;
  }

  /*
   * Throughout the code, we need to check if a firstName is found in stuList
   * and if so, where. So this private helper method does that
   * specifically. Given a string that resembles a Student object's firstName,
   * it returns its index in stuList if there is one. If a Student object
   * with the firstName is not found, the method returns a -1.
   */
  private int indexOfStudent(String f, String l) {
    int result = -1;
    for (int i = 0; i < stuList.size(); i++) {
      if (stuList.get(i).isName(f, l)) {
        result = i;
      }
    }

    return result;
  }

}

