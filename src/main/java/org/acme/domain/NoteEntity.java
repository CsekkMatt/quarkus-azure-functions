package org.acme.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "Note")
@Getter
@Setter
public class NoteEntity extends PanacheEntity {
    // TODO implement user - connection
    private String title;
    private String content;
    private LocalDateTime creationDate;

}
