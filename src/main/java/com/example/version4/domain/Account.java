package com.example.version4.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Account")
public class Account implements Serializable {
    @Id
    @Column(length = 30)
    private String username;

    @Column(length = 60, nullable = false)
    private String password;
}
