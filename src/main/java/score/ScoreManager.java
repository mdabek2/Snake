/**
 * ScoreManager class implementation.
 */
package snake.score;

import java.io.*;
import java.util.*;

public class ScoreManager 
{
    private static final int MAX_SCORES = 10;

    private final File scoresFile;
    private final List<ScoreEntry> scores;

    public ScoreManager() 
    {
        String userHome = System.getProperty("user.home");
        File scoresDirectory = new File(userHome, "SnakeGame");

        if (!scoresDirectory.exists())
            scoresDirectory.mkdirs();

        scoresFile = new File("snake_scores.txt");
        scores = new ArrayList<>();
        loadScores();
    }

    // Adds score to the list
    public void addScore(String name, int score) 
    {
        if (name == null || name.trim().isEmpty()) { name = "Player";}
        name = name.trim();
       
        scores.add(new ScoreEntry(name, score));
        scores.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());

        if (scores.size() > MAX_SCORES) 
            scores.subList(MAX_SCORES, scores.size()).clear();

        saveScores();
    }

    
    public List<ScoreEntry> getScores() {
        return new ArrayList<>(scores);
    }

    public int getBestScore() 
    {
        if (scores.isEmpty()) 
            return 0;

        return scores.get(0).getScore();
    }

    // Saves scores to a .txt file
    private void saveScores() 
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(scoresFile))) 
        {
            for (ScoreEntry entry : scores) 
            {
                writer.write(entry.getName() + ";" + entry.getScore());
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error while saving scores:");
            e.printStackTrace();
        }
    }

    // Loads scores from .txt file
    private void loadScores() 
    {
        if (!scoresFile.exists())
            return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(scoresFile))) 
        {
            String line;

            while ((line = reader.readLine()) != null) 
            {
                line = line.trim();

                if (line.isEmpty())
                    continue;
    
                String[] parts = line.split(";");

                if (parts.length != 2)
                    continue;

                String name = parts[0].trim();

                try {
                    int score = Integer.parseInt(parts[1].trim());
                    scores.add(new ScoreEntry(name, score));

                } catch (NumberFormatException e) {
                    System.err.println("Incorrect score: " + line);
                }
            }

            scores.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());

            if (scores.size() > MAX_SCORES)
                scores.subList(MAX_SCORES, scores.size()).clear();

        } catch (IOException e) {

            System.err.println("Error while reading scores:");
            e.printStackTrace();
        }
    }
}