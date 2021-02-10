package net.snuck.clans.type;

public enum Role {

    LEADER("Leader"),
    CAPTAIN("Captain"),
    MEMBER("Member"),
    RECRUIT("Recruit"),
    NO_CLAN("No clan");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
