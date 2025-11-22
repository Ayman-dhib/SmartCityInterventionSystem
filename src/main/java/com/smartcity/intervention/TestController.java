package com.smartcity.intervention;

import java.util.*;
import com.smartcity.intervention.service.SmartScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {
    
    @Autowired
    private SmartScheduler smartScheduler;
    
    @GetMapping("/ml-smart-schedule")
    public String testMLSmartScheduling() {
        String[] testProblems = {
            "CAR ACCIDENT - FIRE - PEOPLE TRAPPED - EMERGENCY!!!",
            "Broken traffic light near school, children at risk",
            "Small crack in sidewalk needs repair",
            "Water pipe burst flooding street URGENT", 
            "Park bench needs painting next week"
        };
        
        StringBuilder result = new StringBuilder();
        result.append("<pre style='font-family: Arial; font-size: 14px; line-height: 1.5;'>");
        result.append("🤖 ML-POWERED SMART SCHEDULING\n\n");
        
        for (int i = 0; i < testProblems.length; i++) {
            String problem = testProblems[i];
            int urgency = smartScheduler.analyzeUrgency(problem);
            double confidence = smartScheduler.getUrgencyConfidence(problem);
            String bestTech = smartScheduler.findBestTechnician("ELECTRICAL", "Downtown", 
                Arrays.asList("ELECTRICAL", "HEIGHTS"));
            
            result.append(String.format(
                "=== REQUEST %d ===\n" +
                "Problem: %s\n" +
                "ML Urgency: %d/10\n" +
                "Confidence: %.1f%%\n" +
                "Best Tech: %s\n\n",
                i + 1,
                problem, // Full text - no cutting!
                urgency,
                confidence * 100,
                bestTech
            ));
        }
        
        result.append("</pre>");
        return result.toString();
    }
    
    @GetMapping("/test-different-skills")
    public String testDifferentSkills() {
        StringBuilder result = new StringBuilder();
        result.append("<pre style='font-family: Arial; font-size: 14px; line-height: 1.5;'>");
        result.append("🔧 TESTING DIFFERENT SKILL MATCHING\n\n");
        
        // Test different problem types
        String[][] testCases = {
            {"ELECTRICAL", "Downtown", "ELECTRICAL,HEIGHTS"},
            {"PLUMBING", "Suburbs", "PLUMBING,EMERGENCY"}, 
            {"CONSTRUCTION", "City Center", "CONSTRUCTION,ROAD_WORK"},
            {"GENERAL", "Downtown", "GENERAL"}
        };
        
        for (String[] testCase : testCases) {
            String problemType = testCase[0];
            String location = testCase[1];
            List<String> skills = Arrays.asList(testCase[2].split(","));
            
            String bestTech = smartScheduler.findBestTechnician(problemType, location, skills);
            
            result.append(String.format(
                "Problem: %s repair in %s\n" +
                "Required Skills: %s\n" +
                "Best Match: %s\n\n",
                problemType, location, skills, bestTech
            ));
        }
        
        result.append("</pre>");
        return result.toString();
    }
}