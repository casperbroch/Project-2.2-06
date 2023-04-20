package com.mda.Engine;

import java.util.HashMap;
import java.util.Map;

public class Skills {
    Map<String, String> skills;
    Skills() {
        skills = new HashMap<String, String>();
        this.addFunction("C", "Calendar");
        this.addFunction("W", "Weather");

    }

    private void addFunction(String index, String name) {
        if(skills.containsKey(index))
            System.out.println("I already have this skill!");
        else {
            skills.put(index, name);
            System.out.println("Skill successfully added.");
        }
    }

    public static void main(String[] args) {
        Skills skills = new Skills();
    }

    public boolean containsKey(String index) {
        return skills.containsKey(index);
    }

    public String get(String index) {
        return skills.get(index);
    }
}
