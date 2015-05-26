package com.github.lg198.snackbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SBMenuItem {

    public String name;
    public boolean category;
    public double cost;

    public SBMenuItem(String n) {
        name = n;
        category = true;
    }

    public SBMenuItem(String n, double c) {
        name = n;
        cost = c;
        category = false;
    }

    @Override
    public String toString() {
        if (category) {
            return name;
        }
        return name + " (" + NumberFormat.getCurrencyInstance().format(cost) + ")";
    }

    public Map toMap() {
        Map m = new HashMap();
        m.put("name", name);
        m.put("category", new Boolean(category));
        if (category) {
            m.put("children", new ArrayList());
        } else {
            m.put("cost", new Double(cost));
        }
        return m;
    }

    public static List<SBMenuItem> toMenuItems(List l) {
        List<SBMenuItem> items = new ArrayList<>();
        for (Object value : l) {
            if (!(value instanceof Map)) {
                continue;
            }
            Map item = (Map) value;
            items.add(fromMap(item));
        }
        return items;
    }

    public static SBMenuItem fromMap(Map item) {
        if ((Boolean) item.get("category")) {
            return new SBMenuItem((String) item.get("name"));
        } else {
            return new SBMenuItem((String) item.get("name"), (Double) item.get("cost"));
        }
    }

}
