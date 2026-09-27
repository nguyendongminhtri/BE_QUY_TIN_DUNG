package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "question")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private String optionA;
    @Column(nullable = false)
    private String optionB;
    @Column(nullable = false)
    private String optionC;
    @Column(nullable = false)
    private String optionD;
    @Column(nullable = false)
    private String correctOption; // "A"|"B"|"C"|"D"
    private Integer level; // 1..15
    private String category;
    private Boolean isActive = true;
    private Long userActionId;
    @ManyToOne
    User user;
}
