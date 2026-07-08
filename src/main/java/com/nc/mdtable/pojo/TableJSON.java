package com.nc.mdtable.pojo;

import java.util.List;

public class TableJSON {

    private List<String> columnNames;
    private List<String> columnAligns;
    private List<Class<?>> columnClasses;
    private List<List<String>> data;

    public TableJSON() {

    }

    public TableJSON(List<String> columnNames, List<String> columnAligns, List<List<String>> data) {
        this.columnNames = columnNames;
        this.columnAligns = columnAligns;
        this.data = data;
    }

    public TableJSON(List<String> columnNames, List<String> columnAligns, List<Class<?>> columnClasses, List<List<String>> data) {
        this.columnNames = columnNames;
        this.columnAligns = columnAligns;
        this.columnClasses = columnClasses;
        this.data = data;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<String> getColumnAligns() {
        return columnAligns;
    }

    public void setColumnAligns(List<String> columnAligns) {
        this.columnAligns = columnAligns;
    }

    public List<Class<?>> getColumnClasses() {
        return columnClasses;
    }

    public void setColumnClasses(List<Class<?>> columnClasses) {
        this.columnClasses = columnClasses;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }

}
