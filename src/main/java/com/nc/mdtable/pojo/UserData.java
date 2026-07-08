package com.nc.mdtable.pojo;

import com.nc.mdtable.Globals;

public class UserData {
    
    private Globals.ALIGNMENT alignment;
    private final int id;

    public UserData(Globals.ALIGNMENT alignment, int id) {
        this.alignment = alignment;
        this.id = id;
    }

    public Globals.ALIGNMENT getAlignment() {
        return alignment;
    }

    public int getId() {
        return id;
    }

    public void setAlignment(Globals.ALIGNMENT alignment) {
        this.alignment = alignment;
    }
    
    
}
