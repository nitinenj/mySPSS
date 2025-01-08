package src;

import javax.swing.JOptionPane;
import java.util.ArrayList;

public class Shell {
	
	public static void main(String[] args) {
		JOptionPane.showMessageDialog(null, "Welcome to the Mock Submit Server!");
		String command;
		int numTests = Integer.parseInt(
				JOptionPane.showInputDialog("Enter number of tests in a submission:").trim()
				);
		
		// set up SPSS server
		SPSS server = new SPSS(numTests);
		
		while (true) {
			command = JOptionPane.showInputDialog("Enter a command:");
			if (command == null) {
				JOptionPane.showMessageDialog(null, "ok goodbye");
				return;
			}

			String[] params = command.split(" ");
			
			switch (params[0].toLowerCase()) {
				case "addsub": {
					String f = params[1];
					String l = params[2];
					ArrayList<Integer> submissions = new ArrayList<Integer>();
					for (int i = 0; i < numTests; i++) {
						submissions.add(Integer.parseInt(params[i + 3]));
					}
					
					boolean result = server.addSubmission(f, l, submissions);
					JOptionPane.showMessageDialog(null, 
							result ? "Submission added for " + f + " " + l :
								"Student not found: " + f + " " + l
							);
					}
					break;
				
				case "addstu": {
					String f = params[1];
					String l = params[2];
					boolean result = server.addStudent(f, l);
					JOptionPane.showMessageDialog(null, 
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
					boolean result = server.readSubmissionsConcurrently(files);
					JOptionPane.showMessageDialog(null, 
							result ? "Submissions added" : "Error" 
							);
					}
					break;
				
				case "numstu":
					JOptionPane.showMessageDialog(null, 
							"There are currently " + server.numStudents() + 
								" student(s) in the server. "
							);
					break;
				
				case "numsubs": {
					if (params.length == 1) {
						// list all 
						JOptionPane.showMessageDialog(null, 
								"There are a total of " + server.numSubmissions() + 
									" submission(s). ");
					} else {
						// list specific student
						JOptionPane.showMessageDialog(null, 
								"There are a total of " + 
										server.numSubmissions(params[1], params[2]) + 
										" submission(s) for " + params[1] + " " + params[2] + ". ");
					}
					
					}
					break;
				
				case "stuscore": {
					JOptionPane.showMessageDialog(null, 
							params[1] + " " + params[2] + " currently has a score of " + 
									server.score(params[1], params[2]) + ". ");
					}
					break;
				
				case "stusatis": {
					JOptionPane.showMessageDialog(null, 
							params[1] + " " + params[2] + " currently " + 
									(server.satisfactory(params[1], params[2]) ? "IS " : "is NOT ") + 
									"satisfactory. ");
					}
					break;
				
				case "struxcred": {
					JOptionPane.showMessageDialog(null, 
							params[1] + " " + params[2] + 
									(server.satisfactory(params[1], params[2]) ? " HAS " : " has NOT ") + 
									"gotten extra credit. ");
					}
					break;
				
				case "exit": {
					JOptionPane.showMessageDialog(null, "pce gang love u");
					}
					return;
				
				default:
					JOptionPane.showMessageDialog(null, "\"" + command + "\" is not a command!");
			}
		}
	}

}
