package org.acme.repository;

import org.acme.domain.NoteEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NoteRepository implements PanacheRepositoryBase<NoteEntity, Long> {

}
