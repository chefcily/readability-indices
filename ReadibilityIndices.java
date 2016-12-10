/* File: ReadabilityIndices.java
 * Name: Cecily Foote
 * Section Leader: Anna Geiduschek!
 * 
 * This program estimates the readability of a text based on both the
 * Flesch-Kincaid and the Dale-Chall readability formulas.
 */

import acm.program.*;
import java.util.*;
import java.io.*;

public class ReadabilityIndices extends ConsoleProgram {
	//constants for the Flesch-Kincaid formula
	private static final double c0 = -15.59;
	private static final double c1 = 0.39;
	private static final double c2 = 11.8;
	
	//constants for the Dale-Chall formula
	private static final double d0 = 0.1579;
	private static final double d1 = 100.0;
	private static final double d2 = 0.0496;
	private static final double d3 = 3.6365;
		
	public void run() {
		while (true) {
			String filename = readLine("Enter filename: ");
			ArrayList<String> fileLines = findContent(filename);

			while (fileLines == null) {
				println("  Could not open that file.");
				filename = readLine("Enter filename: ");
				fileLines = findContent(filename);
			}
			println("  Flesch-Kincaid Grade Level: " +
					fleschKincaidGradeLevelOf(fileLines));
			println("  Dale-Chall Readability Score: " +
					daleChallReadabilityScoreOf(fileLines));
		}
	}
	
	/**
	 * Given a name of a file, returns the contents of that file.
	 * 
	 * @param filename The name of the file or web page from which
	 * to retrieve content
	 * @return A string of the contents of that file or web page
	 */
	private ArrayList<String> findContent(String filename) {
		if (filename.indexOf("http://") == 0 ||
			filename.indexOf("https://") == 0) {
			return Scraper.pageContents(filename);
		} else return fileContents(filename);
	}
	
	/**
	 * Given a word, returns an estimate of the number of syllables in that word.
	 * 
	 * @param word The word in question.
	 * @return An estimate of the number of syllables in the word.
	 */
	private int syllablesInWord(String word) {
		int vowels = 0;
		for (int i = 0; i < word.length(); i++) {
			if (charIsVowel(word.toLowerCase(), i)) vowels++;
		}
		if (vowels == 0) return 1;
		else return vowels;
	}
	
	/**
	 * Given a word, returns whether or not a character is a vowel.
	 * 
	 * @param word The word containing the potential vowel.
	 * @param i    The index of the character in question
	 * @return True if the character is a vowel.
	 */
	private boolean charIsVowel(String word, int i) {
		char letter = word.charAt(i);
		if (letter == 'a' || letter == 'e' || letter == 'i' ||
			letter == 'o' || letter == 'u' || letter == 'y') {
			if (i != 0) {
				char before = word.charAt(i - 1);
				if (before == 'a' || before == 'e' || before == 'i' ||
					before == 'o' || before == 'u' || before == 'y') return false;
			}
			if (letter == 'e' && i == word.length() - 1) return false;
			return true;
		} else return false;
	}
	
	/**
	 * Given a string, returns an ArrayList of the words, spaces,
	 * and punctuation.
	 * 
	 * @param input A string with words, spaces, and punctuation
	 * @return An ArrayList with all the tokens in the string
	 */
	private ArrayList<String> tokenize(String input) {
		
		ArrayList<String> tokenized = new ArrayList<String>();
		String token = "";
		
		for(int i = 0; i < input.length(); i++) {
			char curr = input.charAt(i);
			token += curr;
			
			/* 
			 * stops adding characters to the token only if: the current
			 * character isn't a letter, it's the last character, or
			 * the following character isn't a letter
			 */
			if (!Character.isLetter(curr) || i == input.length() - 1 ||
				!Character.isLetter(input.charAt(i + 1))) {
				tokenized.add(token);
				token = "";
			}
		}
		return tokenized;
	}

	/**
	 * Given a tokenized string, returns the total number of syllables.
	 * 
	 * @param tokens The tokenized string.
	 * @return The total number of syllables in the string.
	 */
	private int syllablesInLine(ArrayList<String> tokens) {
		int syllables = 0;
		
		for (String token : tokens) {
			if (Character.isLetter(token.charAt(0))) {
				syllables += syllablesInWord(token);
			}
		}	
		return syllables;
	}
	
	/**
	 * Given a tokenized string, returns the total number of words.
	 * 
	 * @param tokens The tokenized string.
	 * @return The total number of words in the string.
	 */
	private int wordsInLine(ArrayList<String> tokens) {
		int words = 0;
		
		for (String token : tokens) {
			if (Character.isLetter(token.charAt(0))) {
				words++;
			}
		}		
		return words;
	}
	
	/**
	 * Given a tokenized string, returns the total number of sentences.
	 * 
	 * @param tokens The tokenized string.
	 * @return The total number of sentences in the string.
	 */
	private int sentencesInLine(ArrayList<String> tokens) {
		int sentences = 0;
		
		for (String token : tokens) {
			char first = token.charAt(0);
			if (first == '.' || first == '?' || first == '!') {
				sentences++;
			}
		}
		return sentences;
	}

	/**
	 * Given a text file, reads all available lines from the file and
	 * returns an array containing those lines.
	 * 
	 * @param filename The file to break up into lines
	 * @return A string array of all the lines in the file
	 */
	private ArrayList<String> fileContents(String filename) {
		ArrayList<String> lines = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while(true) {
				String line = br.readLine();
				if (line == null) break;
				lines.add(line);
			}
			br.close();
			return lines;
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Given a string of text lines, calculates the average grade
	 * level appropriate for reading that text.
	 * 
	 * @param lines A string of the lines of the text to be assessed.
	 * @return The appropriate education level for text comprehension.
	 */
	private double fleschKincaidGradeLevelOf(ArrayList<String> lines) {
		double words = 0;
		double sentences = 0;
		double syllables = 0;

		//converts each line to tokens and updates variables
		for (String token : lines) {
			ArrayList<String> line = tokenize(token);
			words += wordsInLine(line);
			sentences += sentencesInLine(line);
			syllables += syllablesInLine(line);
		}
		
		if (words == 0) words = 1;
		if (sentences == 0) sentences = 1;
		
		//calculates and returns Flesch-Kincaid readibility score
		double grade = c0 + c1 * (words / sentences) +
					   c2 * (syllables / words);
		return grade;
	}
	
	/**
	 * Given a string of text lines, calculates the readability score of
	 * that text, with scores corresponding to the appropriate grade level.
	 * 
	 * @param lines A string of the lines of the text to be assessed.
	 * @return A score corresponding to the appropriate education level.
	 */
	private double daleChallReadabilityScoreOf(ArrayList<String> lines) {
		double diffWords = 0;
		double words = 0;
		double sentences = 0;
		double bonus = 0;
		
		//converts each line to tokens and updates variables
		for (String token : lines) {
			ArrayList<String> line = tokenize(token);
			words += wordsInLine(line);
			diffWords += diffWordsInLine(line);
			sentences += sentencesInLine(line);
		}
		
		if (words == 0) words = 1;
		if (sentences == 0) sentences = 1;
		if (diffWords / words >= 0.05) bonus = 1;
		
		double difficulty = d0 * (diffWords / words * d1) +
							d2 * (words / sentences ) + d3 * bonus;
		return difficulty;
	}
	
	/**
	 * Given a tokenized line of text, returns the number of words
	 * with 3 or more syllables.
	 * 
	 * @param line The tokenized line in question.
	 * @return The number of difficult words in the line.
	 */
	private int diffWordsInLine(ArrayList<String> tokens) {
		int diffWords = 0;
		for (String token : tokens) {
			int syllables = syllablesInWord(token);
			if (syllables >= 3) diffWords++;
		}
		return diffWords;
	}
}
