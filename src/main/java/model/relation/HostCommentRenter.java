package model.relation;

import lombok.Data;

@Data
public class HostCommentRenter {
    private int hostUid; // PK
    private int renterUid; // PK
    private int reviewId; // FK
    private int timestamp;
}
