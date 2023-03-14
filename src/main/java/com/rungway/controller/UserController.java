package com.rungway.controller;

import com.rungway.dto.NoteDto;
import com.rungway.dto.UserDto;
import com.rungway.entity.Note;
import com.rungway.entity.User;
import com.rungway.repository.NoteRepository;
import com.rungway.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/users")
public class UserController  {
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    UserRepository userRepository;

    @Operation(summary = "Get a user By Id")
    @GetMapping(path = "/{userId}")
    public User getUserById(@PathVariable Long userId) {
        return verifyUser(userId);
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User is created", content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = User.class))})
    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody @Valid UserDto userDto){
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        User usr = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(usr);
    }

    @Operation(summary = "Save a new note for an existing user")
    @ApiResponse(responseCode = "201", description = "Note is created", content = {@Content})
    @PostMapping("/{userId}/notes")
    public ResponseEntity<String> addNote(@PathVariable Long userId, @RequestBody @Valid NoteDto noteDto) {
        User user = verifyUser(userId) ;
        List<Note> existingNote = user.getNoteList().stream()
                .filter(td -> td.getContent().equals(noteDto.getContent()) && td.getTitle().equals(noteDto.getTitle()))
                .collect(Collectors.toList());
        if (existingNote.isEmpty()){
            Note note = new Note();
            note.setTitle(noteDto.getTitle());
            note.setContent(noteDto.getContent());
            user.getNoteList().add(note);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("Note added successfully");
        } else {
            return ResponseEntity.badRequest().body("Note with the same title and content already exists");
        }
    }

    @Operation(summary = "Toggle to archive or unarchive a note")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Note was un/archived", content = {@Content}),
            @ApiResponse(responseCode = "404", description = "Note not found", content = @Content)})
    @PutMapping("/todos/{noteId}")
    public void toggleArchiveNote(@PathVariable Long noteId){
        Note note = verifyNote(noteId);
        note.setArchived(!note.getArchived());
        noteRepository.save(note);
    }

    @Operation(summary = "Update a existing note by the userId")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Note was updated", content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "Note not found", content = @Content)})
    @PutMapping(path = "/{userId}/notes")
    public ResponseEntity<User> updateNote(@PathVariable Long userId, @RequestBody Note newNote){
        try {
            User user = verifyUser(userId);
            Note note = verifyNote(newNote.getId()) ;

            Optional<Note> existingNote = user.getNoteList().stream()
                    .filter(td -> td.getId().equals(newNote.getId()))
                    .findFirst();

            int indexToUpdate =  user.getNoteList().indexOf(existingNote.get());

            user.getNoteList().get(indexToUpdate).setTitle(newNote.getTitle());
            user.getNoteList().get(indexToUpdate).setContent(newNote.getContent());
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping(path = "{userId}")
    public void deleteUser(@PathVariable Long userId) {
        User user = verifyUser(userId);
        userRepository.delete(user);
    }

    @DeleteMapping(path = "{userId}/notes/{noteId}")
    public ResponseEntity deleteNote(@PathVariable Long userId, @PathVariable Long noteId) {
        try {
            User user = verifyUser(userId);
            Note note = verifyNote(noteId);
            user.getNoteList().remove(note);
            noteRepository.delete(note);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The user or todo does not exist!");
        }
    }

    @Operation(summary = "List saved notes that aren't archived")
    @ApiResponse(responseCode = "200", description = "found the unarchived notes", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Note.class)))
    @GetMapping(path = "/todos/unarchives")
    public ResponseEntity<List<Note>>  getUnarchivedTodos() {
        List<Note> notes = noteRepository.findAll().stream()
                .filter(td -> td.getArchived().equals(false))
                .collect(Collectors.toList());
        return ResponseEntity.ok(notes);
    }

    @Operation(summary = "List notes that are archived")
    @ApiResponse(responseCode = "200", description = "found the archived notes", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Note.class)))
    @GetMapping(path = "/todos/archives")
    public ResponseEntity<List<Note>>  getArchivedTodos() {
        List<Note> notes = noteRepository.findAll().stream()
                .filter(td -> td.getArchived().equals(true))
                .collect(Collectors.toList());
        return ResponseEntity.ok(notes);
    }

    public User verifyUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User does not exist: " + userId));
    }
    public Note verifyNote(Long noteId){
        return noteRepository.findById(noteId)
                .orElseThrow(() -> new NoSuchElementException("Note does not exist: " + noteId));
    }

}
