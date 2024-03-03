package org.acme.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Note {
    private Long id;
    private String title;
    private String content;
    private String creationDate;
}
