package net.snuck.clans.type;

public enum Role {

    LEADER("Leader", "L", 3),
    CAPTAIN("Captain", "C", 2),
    MEMBER("Member", "M", 1),
    RECRUIT("Recruit", "R", 0),
    NO_CLAN("No clan", "", -1);

    private final String name;
    private final String chatPrefix;
    private final int permissionIndex;

    Role(String name, String chatPrefix, int permissionIndex) {
        this.name = name;
        this.chatPrefix = chatPrefix;
        this.permissionIndex = permissionIndex;
    }

    public String getName() {
        return this.name;
    }

    public String getChatPrefix() {
        return chatPrefix;
    }

    public int getPermissionIndex() {
        return permissionIndex;
    }
}
