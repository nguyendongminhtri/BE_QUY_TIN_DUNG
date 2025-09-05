package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "singers")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Singer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //    @NotBlank
    @NotNull
    private String name;
    //    @NotBlank
    @NotNull
    private String sex;
    @NotNull
    private String nation;
    private Date birthday;
    //    @NotBlank
//    @NotNull

    @Lob
//    @Size(max = 2000)
    private String description;
    //    @NotNull
    @Lob
    private String avatar;
    @ManyToOne
    private User user;

}
