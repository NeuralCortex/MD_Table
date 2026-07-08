package com.nc.mdtable.pojo;

import com.nc.mdtable.Globals;
import java.util.List;

public class Content {

    private final List<List<String>> content;
    private List<Integer> colWidths;
    private final List<Globals.ALIGNMENT> aligns;

    public Content(List<List<String>> content, List<Integer> colWidths, List<Globals.ALIGNMENT> aligns) {
        this.content = content;
        this.colWidths = colWidths;
        this.aligns = aligns;
    }

    public List<List<String>> getContent() {
        return content;
    }

    public List<Integer> getColWidths() {
        return colWidths;
    }

    public List<Globals.ALIGNMENT> getAligns() {
        return aligns;
    }

    public void setColWidths(List<Integer> colWidths) {
        this.colWidths = colWidths;
    }
    
    
}
