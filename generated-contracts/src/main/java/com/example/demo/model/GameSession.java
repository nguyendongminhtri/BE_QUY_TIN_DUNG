package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_sesion")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer currentLevel; // 1..15
    @ElementCollection private List<Long> questionIdsAsked = new ArrayList<>();
    @ElementCollection private List<String> usedLifelines = new ArrayList<>(); // ["50-50",...]
    @Enumerated(EnumType.STRING)
    private Status status; // RUNNING, WON, LOST, QUIT
    private Integer score;
    private Instant startedAt;
    private Instant endedAt;
    @ManyToOne
    User user;
}
