package ch.jamiete.bunbun.permissions;

import java.util.HashMap;

public enum Flag {

    DEFAULT('D'), ADMIN('a'), SUPER_ADMIN('s');

    private char flag;
    private final static HashMap<Character, Flag> flags = new HashMap<Character, Flag>();

    private Flag(char flag) {
        this.flag = flag;
    }

    public char getChar() {
        return this.flag;
    }

    public static Flag byChar(final char Char) {
        return Flag.flags.get(Char);
    }

    public static Flag[] getFlags() {
        return Flag.flags.values().toArray(new Flag[Flag.flags.size()]);
    }

    static {
        for (final Flag f : Flag.values()) {
            Flag.flags.put(f.getChar(), f);
        }
    }

}
