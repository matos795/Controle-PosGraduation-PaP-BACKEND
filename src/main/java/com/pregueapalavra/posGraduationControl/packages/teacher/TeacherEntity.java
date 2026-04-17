package com.pregueapalavra.posGraduationControl.packages.teacher;

import com.pregueapalavra.posGraduationControl.packages.classSession.ClassSessionEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tb_teacher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 150, unique = true)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    @OneToMany(mappedBy = "teacher")
    private List<ClassSessionEntity> classSessions;
}
