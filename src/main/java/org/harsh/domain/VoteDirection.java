package org.harsh.domain;

import java.util.HashMap;
import java.util.Map;

public enum VoteDirection {
    UP(1, "Up"),
    DOWN(-1, "Down");

    int val;
    String desc;

    VoteDirection(int value, String desc) {
        this.val = value;
        this.desc = desc;
    }

    private static final Map<Integer, VoteDirection> mapByVal = new HashMap<>();
    private static final Map<String, VoteDirection> mapByDesc = new HashMap<>();

    static {
        for (VoteDirection voteDirection : VoteDirection.values()) {
            mapByVal.put(voteDirection.getVal(), voteDirection);
            mapByDesc.put(voteDirection.getDesc(), voteDirection);
        }
    }

    public static VoteDirection getByVal(int val) {
        return mapByVal.get(val);
    }

    public VoteDirection getByDesc(String desc) {
        return mapByDesc.get(desc);
    }

    public int getVal() {
        return val;
    }

    public void setVal(int value) {
        this.val = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
