package src;

import java.util.ArrayList;
import java.util.List;

/*
 * Author: Nitin Enjamuri
 * UID: 120094262
 * 
 * I pledge on my honor that I have not given or received any unauthorized
 * assistance on this assignment/examination
 */

/*
 * This is the Student Class that is found in SPSS Class's stuList (list of
 * Student objects) A student has its own properties, methods, and
 * variables. We have 5 variables in this class,
 * name - the name of the Student object.
 * scores - a list of scores that the student has achieved.
 * bestAttempt - a variable that contains the best current attempt
 */

public class Student {

  private String firstName;
  private String lastName;
  private ArrayList<Integer> scores;
  private List<Integer> bestAttempt;

  /*
   * The Student() constructor is the main constructor for this class.
   * The parameter here is the name the student will now have. In here, we
   * do not validate the parameter as we already do so in the SPSS class,
   * when we call this method.
   */
  Student(String f, String l) {
    firstName = f;
    lastName = l;
    scores = new ArrayList<>();
    bestAttempt = new ArrayList<>();
  }

  /*
   * The addSubmission() method returns true if the submission has been
   * added successfully, and false otherwise. If the attempt is not null
   * and incorrect number of tests, then a score will be calculated. New
   * score will only be updated if it is higher than old score. If it is
   * the first attempt, this does not apply and any score will be added. It
   * increases numSubmissions and calculates isSatisfactory and
   * gotExtraCred. Method is synchronized to prevent data race in scores.
   */
  synchronized boolean addSubmission(List<Integer> attempt, int numTests) {
    boolean worked = false;
    if (attempt == null) // edge case: attempt is null
      return false;

    // if there are a correct amount of tests
    if (attempt.size() == numTests) {
      int attemptScore = calculateScore(attempt);
      if (scores.size() == 0) {
        scores.add(attemptScore);
        bestAttempt = attempt;
      } else { // if/else is needed bc we cannot do scores.get(-1) when its
               // empty
        if (attemptScore > scores.get(scores.size() - 1)) {
          scores.add(attemptScore);
          bestAttempt = attempt; // change bestAttempt, new one is the
                                 // highest
        } else
          scores.add(scores.get(scores.size() - 1));
        // do not change bestAttempt, as there is already a better attempt
      }

      worked = true;
    }

    return worked;
  }

  /*
   * The isName() method returns if the parameter n is equal to the name of
   * the student.
   */
  boolean isName(String f, String l) {
    return firstName.equals(f) && lastName.equals(l);
  }

  /*
   * The isSatisfactory() method returns whether or not the student is
   * satisfactory or not.
   */
  boolean isSatisfactory() {

    // makes it so that 12/2 = 6 and 11/2 = 6
    int benchmark = bestAttempt.size() / 2 + (bestAttempt.size() % 2);
    int satisCount = 0;
    for (int test : bestAttempt) {
      if (test > 0)
        satisCount++; // goes through tests in attempt and checks # of
                      // non-zeros
    }

    // returns false if no attempts were ever made by the student
    return bestAttempt.size() != 0 && satisCount >= benchmark;
  }

  /*
   * The getNumSubmissions() method returns the current number of
   * submissions the student has.
   */
  int getNumSubmissions() {
    return scores.size();
  }

  /*
   * The gotExtraCred() method returns whether or not the student has
   * extra credit or not.
   */
  boolean gotExtraCred() {
    int satisCount = 0;
    for (int result : bestAttempt) {
      if (result > 0)
        satisCount++; // goes through tests in attempt and checks # of
                      // non-zeros
    }

    // checks for extra credit (all tests > 0 and on first attempt)
    return satisCount == bestAttempt.size() && scores.size() == 1;

  }

  /*
   * The getHighestScore() method returns the highest score in the list of
   * scores. If scores is empty, method returns 0.
   */
  int getHighestScore() {
    return scores.size() == 0 ? 0 : scores.get(scores.size() - 1);
  }

  /*
   * The calculateScore() helper method calculates the score of a given
   * attempt. Assumes attempt is not null.
   */
  private int calculateScore(List<Integer> attempt) {
    int sum = 0;
    for (int i = 0; i < attempt.size(); i++)
      sum += attempt.get(i);
    return sum;
  }
}
