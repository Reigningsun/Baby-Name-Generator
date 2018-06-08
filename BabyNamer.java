package babyNameGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class BabyNamer {
	
    public static ArrayList<String> getData (String filename, int markovOrder) throws IOException{			// Reads the data file line by line. Creates a list of formatted data
    	ArrayList<String> dataSet = new ArrayList<String>();
    	BufferedReader reader;		
		reader = new BufferedReader(new FileReader(filename));

        String line;
        
		while ((line = reader.readLine()) != null) {
			line = formatIt(line, markovOrder);
			dataSet.add(line);
		}

		reader.close();
        return dataSet;
    }
    
    
    
    public static String formatIt (String line, int markovOrder){							// Formats each name so that the proper number of _ and # characters surround it
    	String startFormat = "";
    	String endFormat = "";
    	for (int i = 0; i < markovOrder; i++){
    		startFormat = startFormat + "_";
    		endFormat = endFormat + "#";
    	}
    	
    	line = startFormat + line + endFormat; 
    	return line;
    }
    
    
    
    public static String unFormatIt (String name, int markovOrder){							// Removes beginning and ending tags from names
    	String output = name.substring(markovOrder, name.length() - markovOrder + 1);
    	return output;
    }
    
    
    
    public static HashMap<String, LetterProb> learnPatterns (ArrayList<String> dataSet, int markovOrder){
    	HashMap<String, LetterProb> patterns = new HashMap<String, LetterProb>();
    	String endChar = "#";
    	String next = "";
    	
    	for (int i = 0; i < dataSet.size(); i++){									// Iterate through each name in the dataSet
    		String name = dataSet.get(i);										// Get the name
    		for (int j = 0; j < name.length() - markovOrder; j++){							// Look at each character in the name one at a time
    			if (Character.toString(name.charAt(j)) == endChar){						// If we see the end character then stop looking at this name
    				break;									
    			} else {											// Else collect characters between our current index and the markovOrder limit   					
    				String subStr = name.substring(j, j + markovOrder);
    				if (patterns.containsKey(subStr)){							// If we have seen this subStr before then add our current next character to its nextLetter ArrayList		
    					next = Character.toString(name.charAt(j + markovOrder));
    					patterns.get(subStr).nextLetter.add(next);					// Adds the next letter after our substring to the ArrayList of next letters
    				} else {										// Else add this new subStr to the patterns hash map
    					LetterProb current = new LetterProb(subStr);
    					ArrayList <String> nextLetter = new ArrayList<String>();
    					nextLetter.add(Character.toString(name.charAt(j + markovOrder)));
    					current.nextLetter = nextLetter;
    					patterns.put(subStr, current);							// Adds the next letter after our substring to the ArrayList of next letters
    				}
    			}
    		}
    	}
    	return patterns;
    }
    
    
    
    public static String genStart (int markovOrder){									// Creates our starting string for each name
    	String output = "";
    	for (int i = 0; i < markovOrder; i++){
    		output = output + "_";
    	}
    	return output;
    }

    
	
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner (System.in);
		
		System.out.println("Would you like to generate male or female names? (M/F) ");
		String selectedGender = scanner.next();
		
		System.out.println("What is the minimum length of name I should generate? (int) ");
		int minLen = scanner.nextInt();
		
		System.out.println("What is the maximum length of name I should generate? (int) ");
		int maxLen = scanner.nextInt();
		
		System.out.println("What is the Markov Order I should model? (int) ");
		int markovOrder = scanner.nextInt();
		
		System.out.println("How many names should I generate? (int) ");
		int num = scanner.nextInt();
		

		
		ArrayList<String> dataSet = new ArrayList<String>();
		if (selectedGender.toLowerCase().compareTo("M".toLowerCase()) == 0){
			dataSet = getData("src/babyNameGenerator/namesBoys.txt", markovOrder);
		} else {
			dataSet = getData("src/babyNameGenerator/namesGirls.txt", markovOrder);
		}
		
		System.out.println("Generating model... ");
		HashMap <String, LetterProb> patterns = learnPatterns(dataSet, markovOrder);
		
		
		
		System.out.println("Generating new names... ");
		int legalNames = 0;
		String endSymbol = "#";
		String [] goodNames = new String [num];
		String currName = genStart(markovOrder);
		
		while (legalNames < num){
			int i = markovOrder - 1;									// Reset to "__" starting condition
			String subStr = "__";										
			currName = genStart(markovOrder);								
			
			while ((Character.toString(currName.charAt(i)).compareTo(endSymbol) != 0)){			// Continue building a name until we encounter the endSymbol		
				LetterProb currProb = patterns.get(subStr);
				String nextL = currProb.predictedLetter(currProb.nextLetter);				// Pick the next letter based on the subStr's pattern
				currName = currName + nextL;								// Append the next letter to our new name
				subStr = currName.substring(i, i + 2);							// Isolate the next substring
				i++;
			}
			if (!(currName.length() < minLen || currName.length() > maxLen || dataSet.contains(currName))){	// Keeps any legal names generated
				currName = unFormatIt(currName, markovOrder);						// Remove beginning and ending markers
				goodNames[legalNames] = currName;
				legalNames++;
			}
		}
		
		
		for (int j = 0; j < goodNames.length; j++){
			System.out.println(goodNames[j]);
		}
	}

}
