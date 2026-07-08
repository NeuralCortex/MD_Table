package com.nc.mdtable.pojo;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

public class Row {

    private List<SimpleStringProperty> row=new ArrayList<>();

    public Row(){
        
    }
    
    public Row(int numCols) {
        for (int j = 0; j < numCols; j++) {
            String value = "New";
            row.add(new SimpleStringProperty(value));
        }
    }
    
     public Row(List<String> items) {
        for (int j = 0; j < items.size(); j++) {
            row.add(new SimpleStringProperty(items.get(j)));
        }
    }

    public SimpleStringProperty propertyAt(int index) {
        return row.get(index);
    }
    
    public void removeAt(int index){
        row.remove(index);
    }

    public void extend(int index, String text) {
        row.add(index, new SimpleStringProperty(text));
    }

    public int getColumnCount() {
        return row.size();
    }

    public List<SimpleStringProperty> getRow() {
        return row;
    }

    @Override
    public String toString() {
        return "DataRow{" + "row=" + row + '}';
    }
    
    
}
