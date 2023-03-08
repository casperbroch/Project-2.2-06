package com.mda;

public class Slot {
    
    private String slot; 
    private String parent; 

    public Slot(String slot, String parent){
        this.slot = slot; 
        this.parent = parent; 
    }

    public String getSlot(){return this.slot;}
    public String getParent(){return this.parent;}
}

