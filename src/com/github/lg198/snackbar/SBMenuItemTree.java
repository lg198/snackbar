package com.github.lg198.snackbar;

import java.util.*;

public class SBMenuItemTree {

    private Map levels;

    public SBMenuItemTree() {
        levels = new HashMap() {
            {
                put("root", new ArrayList());
            }
        };
    }

    public SBMenuItemTree(Map l) {
        levels = l;
    }

    public List getFromLevel(String s) {
        if (!levels.containsKey(s)) {
            levels.put(s, new ArrayList());
        }
        return (List) levels.get(s);
    }

    public boolean contains(String path, SBMenuItem item) {
        if (!levels.containsKey(path)) {
            return false;
        }
        List l = (List) levels.get(path);
        for (Object o : l) {
            Map m = (Map) o;
            if (m.get("name").equals(item.name)) {
                return true;
            }
        }
        return false;
    }

    public void add(String path, SBMenuItem item) {
        if (!levels.containsKey(path)) {
            levels.put(path, new ArrayList());
        }
        List l = (List) levels.get(path);
        l.add(item.toMap());
    }

    public void remove(String path, SBMenuItem item) {
        if (!levels.containsKey(path)) {
            return;
        }
        List l = (List) levels.get(path);
        ListIterator li = l.listIterator();
        while (li.hasNext()) {
            SBMenuItem i = SBMenuItem.fromMap((Map) li.next());
            if (item.name.equals(i.name)) {
                li.remove();
                break;
            }
        }

        if (levels.containsKey(path + "." + item.name)) {
            levels.remove(path + "." + item.name);
        }
    }

    public int getCount(String path) {
        if (!levels.containsKey(path)) {
            levels.put(path, new ArrayList());
        }
        return ((List) levels.get(path)).size();
    }

    public Map levels() {
        return levels;
    }

}
