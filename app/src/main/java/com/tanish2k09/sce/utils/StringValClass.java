package com.tanish2k09.sce.utils;

import java.util.ArrayList;

public class StringValClass {
    private String name;
    private ArrayList<String> options;
    private String activeVal = "<no value set>";
    private String description;
    private String title;

    StringValClass(String name) {
        this.name = name;
        options = new ArrayList<>();
        description = "";
        title = "";
    }

    void addVal(String val) {
        for (int idx = 0; idx < options.size(); ++idx)
            if (options.get(idx).equals(val))
                return;
        options.add(val);
    }

    public void setActiveVal(String val) {
        activeVal = val;
    }

    public String getActiveVal() {
        return activeVal;
    }

    public String getName() {
        return name;
    }

    public int getNumOptions() {
        return options.size();
    }

    public String getOption(int index) {
        if (index < options.size())
            return options.get(index);
        return null;
    }

    void clearOptions() {
        options.clear();
    }

    void setTitle(String titleArg) {
        title = titleArg;
    }

    public String getTitle() { return title; }

    void setDescription(String desc) {
        description = desc;
    }

    public String getDescriptionString() {
        return description;
    }
}
