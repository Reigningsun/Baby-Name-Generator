package babyNameGenerator;

import java.util.ArrayList;
import java.util.Random;

public class LetterProb {
	String characters;								// Stores the characters we are looking at
	ArrayList<String> nextLetter;							// Stores each instance of a next letter seen in the training set to be used in randomly selecting a letter later

	LetterProb (String characters){
		this.characters = characters;
	}
	
	
	
	public String predictedLetter (ArrayList<String> nextLetter){			// Returns a random element of the nextLetter array list
		Random random = new Random();
		String guess = nextLetter.get(random.nextInt(nextLetter.size()));
		if (guess != null){
			return guess;
		} else {
			predictedLetter(nextLetter);
		}
		return guess;
	}
}
