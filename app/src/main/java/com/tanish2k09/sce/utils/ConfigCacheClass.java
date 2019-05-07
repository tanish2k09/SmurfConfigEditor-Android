package com.tanish2k09.sce.utils;

import android.util.Log;

import java.util.ArrayList;

public class ConfigCacheClass {

    private static ArrayList<StringValClass> configList = new ArrayList<>();

    public ConfigCacheClass() {
    }

    public static int addConfig(String name,
                                String val,
                                boolean isValActive,
                                String title,
                                String description) {

        for (int idx = 0; idx < configList.size(); ++idx) {
            StringValClass tmp = configList.get(idx);
            if (tmp.getName().equals(name)) {
                tmp.addVal(val);

                if (isValActive)
                    tmp.setActiveVal(val);

                return idx;
            }
        }

        StringValClass newStringVal = new StringValClass(name);
        newStringVal.addVal(val);

        if (isValActive)
            newStringVal.setActiveVal(val);

        if (title != null)
            newStringVal.setTitle(title);

        if (description != null)
            newStringVal.setDescription(description);

        configList.add(newStringVal);
        return configList.size() - 1;
    }

    public static int getConfiglistSize() {
        return configList.size();
    }

    public static StringValClass getStringVal(int index) {
        if (index <= configList.size())
            return configList.get(index);
        return null;
    }

    public static void clearAll() {
        for (int idx = 0; idx < getConfiglistSize(); ++idx)
            configList.get(idx).clearOptions();
    }
}
