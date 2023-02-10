package com.mda.Engine;

public class slot {
    
    private String slot; 
    private String parent; 

    public slot(String slot, String parent){
        this.slot = slot; 
        this.parent = parent; 
    }

    public String getSlot(){return this.slot;}
    public String getParent(){return this.parent;}
}
