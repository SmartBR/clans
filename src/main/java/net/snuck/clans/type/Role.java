package net.snuck.clans.type;

public enum Role {

    LEADER("Leader", "L"),
    CAPTAIN("Captain", "C"),
    MEMBER("Member", "M"),
    RECRUIT("Recruit", "R"),
    NO_CLAN("No clan", "");

    private final String name;
    private final String chatPrefix;

    Role(String name, String chatPrefix) {
        this.name = name;
        this.chatPrefix = chatPrefix;
    }

    public String getName() {
        return this.name;
    }

    public String getChatPrefix() {
        return chatPrefix;
    }
}
