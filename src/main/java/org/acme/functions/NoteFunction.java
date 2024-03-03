package org.acme.functions;

import java.util.Optional;

import org.acme.model.Note;
import org.acme.service.NoteService;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import jakarta.inject.Inject;

public class NoteFunction {

    @Inject
    NoteService noteService;

    @FunctionName("GetAllNotes")
    public HttpResponseMessage getAllNotes(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.GET}, route = "notes", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Getting all notes.");
        return request.createResponseBuilder(HttpStatus.OK).body(noteService.getAllNotes()).build();
    }

    @FunctionName("GetNoteById")
    public HttpResponseMessage getNoteById(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.GET}, route = "notes/{id}", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {
        context.getLogger().info("Getting note by id.");
        Note note = noteService.findNoteById(Long.parseLong(id));
        if (note == null) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Note with id " + id + " not found")
                    .build();
        }
        return request.createResponseBuilder(HttpStatus.OK).body(note).build();
    }

    @FunctionName("CreateNote")
    public HttpResponseMessage createNote(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.POST}, route = "notes", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<Note>> request,
            final ExecutionContext context) {
        context.getLogger().info("Creating a note.");
        Note note = request.getBody().orElse(null);
        if (!noteService.isNoteValid(note)) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass a valid note in the request body")
                    .build();
        } else {
            noteService.saveNote(note);
            return request.createResponseBuilder(HttpStatus.CREATED).build();
        }
    }

    @FunctionName("UpdateNote")
    public HttpResponseMessage updateNote(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.PUT}, route = "notes/{id}", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<Note>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {
        context.getLogger().info("Updating a note.");
        try {
            Note note = request.getBody().orElse(null);
            if (note == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a note in the request body")
                        .build();
            } else {
                noteService.updateNote(Long.parseLong(id), note);
                return request.createResponseBuilder(HttpStatus.OK).build();
            }
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Note with id " + id + " not found")
                    .build();

        }
    }

    @FunctionName("DeleteNote")
    public HttpResponseMessage deleteNote(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.DELETE}, route = "notes/{id}", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {
        context.getLogger().info("Deleting a note.");
        try {

            noteService.deleteNote(Long.parseLong(id));
            return request.createResponseBuilder(HttpStatus.OK).build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Note with id " + id + " not found")
                    .build();
        }
    }

    @FunctionName("DeleteAllNotes")
    public HttpResponseMessage deleteAllNotes(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.DELETE}, route = "notes", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Deleting all notes.");
        noteService.deleteAllNotes();
        return request.createResponseBuilder(HttpStatus.OK).build();
    }

}
