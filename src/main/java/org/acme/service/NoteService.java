package org.acme.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.acme.domain.NoteEntity;
import org.acme.model.Note;
import org.acme.repository.NoteRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class NoteService {

    @Inject
    private NoteRepository noteRepository;

    private Logger logger;

    public List<Note> getAllNotes() {
        List<NoteEntity> noteEntities = noteRepository.listAll();
        return noteEntities.stream()
                .map(this::mapNoteEntityToNote)
                .collect(Collectors.toList());
    }

    public Note findNoteById(Long id) {
        NoteEntity noteEntity = noteRepository.findById(id);
        return mapNoteEntityToNote(noteEntity);
    }

    @Transactional
    public void saveNote(Note note) {
        NoteEntity noteEntity = new NoteEntity();
        noteEntity.setTitle(note.getTitle());
        noteEntity.setContent(note.getContent());
        noteEntity.setCreationDate(LocalDateTime.now());
        noteRepository.persist(noteEntity);
    }

    @Transactional
    public void updateNote(Long id, Note note) {
        NoteEntity noteEntity = noteRepository.findById(id);
        if (noteEntity != null) {
            noteEntity.setTitle(note.getTitle());
            noteEntity.setContent(note.getContent());
        } else {
            throw new IllegalArgumentException("Note not found");
        }
    }

    @Transactional
    public void deleteNote(Long id) {
        NoteEntity noteEntity = noteRepository.findById(id);
        if (noteEntity != null) {
            noteRepository.delete(noteEntity);
        } else {
            throw new IllegalArgumentException("Note not found");
        }
    }

    public void deleteAllNotes() {
        noteRepository.deleteAll();
    }

    private Note mapNoteEntityToNote(NoteEntity noteEntity) {
        Note note = new Note();
        note.setId(noteEntity.id);
        note.setTitle(noteEntity.getTitle());
        note.setContent(noteEntity.getContent());
        Optional.ofNullable(noteEntity.getCreationDate())
                .ifPresent(creationDate -> note.setCreationDate(creationDate.toString()));
        return note;
    }

    public boolean isNoteValid(Note note) {
        return note != null && note.getTitle() != null
                && !note.getTitle().isEmpty() && note.getContent() != null;
    }

}
