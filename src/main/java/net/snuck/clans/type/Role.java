package net.snuck.clans.type;

import lombok.Getter;

public enum Role {

    LEADER("Leader", "L", 3),
    CAPTAIN("Captain", "C", 2),
    MEMBER("Member", "M", 1),
    RECRUIT("Recruit", "R", 0),
    NO_CLAN("No clan", "", -1);

    @Getter private final String name;
    @Getter private final String chatPrefix;
    @Getter private final int permissionIndex;

    Role(String name, String chatPrefix, int permissionIndex) {
        this.name = name;
        this.chatPrefix = chatPrefix;
        this.permissionIndex = permissionIndex;
    }
}
