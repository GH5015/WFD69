package io.github.some_example_name.model;

public class SeasonHistory {
    private int year;
    private String ligaResult;
    private String copaResult;

    public SeasonHistory(int year, String ligaResult, String copaResult) {
        this.year = year;
        this.ligaResult = ligaResult;
        this.copaResult = copaResult;
    }

    public int getYear() { return year; }
    public String getLigaResult() { return ligaResult; }
    public String getCopaResult() { return copaResult; }
}
