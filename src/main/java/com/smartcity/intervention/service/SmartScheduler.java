package com.smartcity.intervention.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SmartScheduler {
    
    private SimpleMLPredictor mlPredictor = new SimpleMLPredictor();
    
    public String findBestTechnician(String problemType, String location, List<String> requiredSkills) {
        double bestScore = 0;
        String bestTech = "No technician available";
        
        for (String tech : getAvailableTechnicians()) {
            double score = calculateMatchScore(tech, problemType, location, requiredSkills);
            if (score > bestScore) {
                bestScore = score;
                bestTech = tech;
            }
        }
        
        return bestTech + " (Match Score: " + String.format("%.1f", bestScore) + ")";
    }
    
    /**
     * SIMPLE BUT EFFECTIVE ML URGENCY PREDICTION
     */
    public int analyzeUrgency(String description) {
        double mlScore = mlPredictor.predictUrgency(description);
        return (int) (mlScore * 9) + 1;
    }
    
    public double getUrgencyConfidence(String description) {
        return mlPredictor.getPredictionConfidence(description);
    }
    
    // ... [rest of your existing methods stay exactly the same] ...
    
    private double calculateMatchScore(String technician, String problemType, String location, List<String> requiredSkills) {
        double score = 0.0;
        double skillMatch = calculateSkillMatch(technician, requiredSkills);
        score += skillMatch * 0.4;
        double locationScore = calculateLocationScore(technician, location);
        score += locationScore * 0.3;
        double expertiseScore = calculateExpertiseScore(technician, problemType);
        score += expertiseScore * 0.2;
        score += 0.1;
        return score;
    }
    
    private double calculateSkillMatch(String tech, List<String> requiredSkills) {
        Set<String> techSkills = getTechnicianSkills(tech);
        long matches = requiredSkills.stream().filter(techSkills::contains).count();
        return (double) matches / requiredSkills.size();
    }
    
    private double calculateLocationScore(String tech, String location) {
        return tech.toLowerCase().contains(location.toLowerCase()) ? 1.0 : 0.3;
    }
    
    private double calculateExpertiseScore(String tech, String problemType) {
        if (problemType.contains("ELECTRIC") && tech.contains("John")) return 1.0;
        if (problemType.contains("WATER") && tech.contains("Mike")) return 1.0;
        if (problemType.contains("ROAD") && tech.contains("Sarah")) return 1.0;
        return 0.5;
    }
    
    private List<String> getAvailableTechnicians() {
        return Arrays.asList("John Electric - Downtown", "Mike Plumber - Suburbs", "Sarah Builder - City Center");
    }
    
    private Set<String> getTechnicianSkills(String tech) {
        if (tech.contains("John")) return Set.of("ELECTRICAL", "HEIGHTS", "NETWORK");
        if (tech.contains("Mike")) return Set.of("PLUMBING", "EMERGENCY", "GENERAL");
        if (tech.contains("Sarah")) return Set.of("CONSTRUCTION", "ROAD_WORK");
        return Set.of("GENERAL");
    }
}

/**
 * SIMPLE ML without Weka dependencies
 */
class SimpleMLPredictor {
    private Map<String, Double> wordWeights;
    private Map<String, Integer> wordFrequencies;
    
    public SimpleMLPredictor() {
        trainModel();
    }
    
    /**
     * Train simple ML model
     */
    private void trainModel() {
        wordWeights = new HashMap<>();
        wordFrequencies = new HashMap<>();
        
        // Train on urgency patterns - this is ML!
        String[] urgentExamples = {
            "accident fire emergency danger urgent!!! children school hospital",
            "broken traffic light risk critical asap immediately",
            "gas leak explosion collapse flood emergency",
            "not working danger risk urgent emergency"
        };
        
        String[] normalExamples = {
            "small crack maintenance repair check",
            "street light bulb replacement painting",
            "park bench needs painting next week",
            "regular inspection routine maintenance"
        };
        
        // Learn from urgent examples
        for (String example : urgentExamples) {
            learnFromText(example, 1.0);
        }
        
        // Learn from normal examples  
        for (String example : normalExamples) {
            learnFromText(example, 0.0);
        }
        
        System.out.println("🤖 ML Model trained successfully! Learned " + wordWeights.size() + " word patterns");
    }
    
    private void learnFromText(String text, double targetUrgency) {
        String[] words = text.toLowerCase().split(" ");
        for (String word : words) {
            if (word.length() > 2) { // Ignore short words
                wordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0) + 1);
                
                // Update weight using simple learning
                double currentWeight = wordWeights.getOrDefault(word, 0.5);
                double newWeight = currentWeight + (targetUrgency - currentWeight) * 0.3;
                wordWeights.put(word, newWeight);
            }
        }
    }
    
    /**
     * Predict using learned ML model
     */
    public double predictUrgency(String description) {
        if (description == null) return 0.3;
        
        String[] words = description.toLowerCase().split(" ");
        double totalScore = 0.0;
        int wordCount = 0;
        
        for (String word : words) {
            if (wordWeights.containsKey(word)) {
                totalScore += wordWeights.get(word);
                wordCount++;
            }
        }
        
        if (wordCount > 0) {
            double mlScore = totalScore / wordCount;
            
            // Boost score if contains strong indicators
            if (description.matches(".*!!!.*") || description.toLowerCase().contains("emergency")) {
                mlScore = Math.min(mlScore + 0.2, 1.0);
            }
            
            return mlScore;
        }
        
        // Fallback to keyword matching
        return fallbackPrediction(description);
    }
    
    private double fallbackPrediction(String description) {
        if (description == null) return 0.3;
        
        description = description.toLowerCase();
        double score = 0.0;
        
        if (description.matches(".*\\b(accident|fire|emergency|danger|risk|urgent)\\b.*")) {
            score += 0.6;
        }
        if (description.matches(".*\\b(broken|not working|critical|asap|immediately)\\b.*")) {
            score += 0.3;
        }
        if (description.matches(".*\\b(children|school|hospital|elderly)\\b.*")) {
            score += 0.1;
        }
        
        return Math.min(score, 1.0);
    }
    
    public double getPredictionConfidence(String description) {
        if (description == null) return 0.5;
        
        String[] words = description.toLowerCase().split(" ");
        int knownWords = 0;
        
        for (String word : words) {
            if (wordWeights.containsKey(word)) {
                knownWords++;
            }
        }
        
        return Math.min((double) knownWords / words.length, 1.0);
    }
}