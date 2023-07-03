package model.entity;

import lombok.Data;

@Data
public class Review {
    private int reviewId; // PK
    private String comment;
    private int rating; // 1-5
}
