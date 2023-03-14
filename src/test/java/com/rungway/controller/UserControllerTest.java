package com.rungway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rungway.dto.NoteDto;
import com.rungway.dto.UserDto;
import com.rungway.entity.Note;
import com.rungway.entity.User;
import com.rungway.repository.NoteRepository;
import com.rungway.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserRepository userRepository;
    @MockBean
    NoteRepository noteRepository;
    Note note1 = new Note(1L, "task1", "Do", false);
    Note note2 = new Note(2L, "task1", "Do", false);
    User user1 = new User(1L, "A", "A1@", Arrays.asList(note1));
    User user2 = new User(2L, "B", "B2@", Arrays.asList(note1, note2));


    @Test
    void getAllUsers_success() throws Exception {
        List<User> users = new ArrayList<>(Arrays.asList(user1, user2));

        when(userRepository.findAll()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].username", is("B")));
    }

//    @Test
//    public void addUser_success() throws Exception {
//        Note note = Note.builder()
//                .id(1L).title("task1").content("do").archived(false)
//                .build();
//        User user = User.builder()
//                .id(1L).username("A").password("A1@").noteList(Arrays.asList())
//                .build();
//
//        UserDto userDto = UserDto.builder()
//                        .username("task1").password("A1@")
//                        .build();
//
//        when(userRepository.save(user)).thenReturn(user);
//
//        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(this.mapper.writeValueAsString(userDto));
//
//        mockMvc.perform(mockRequest)
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$", notNullValue()))
//                .andExpect(jsonPath("$.username", is("A")));
//    }


//    @Test
//    public void addUser_ShouldCreateUser() throws Exception {
//        UserDto userDto = new UserDto();
//        userDto.setUsername("testUsername");
//        userDto.setPassword("testPassword");
//
//        when(userRepository.save(user1)).thenReturn(user1);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .post("/users")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(userDto));
//
//        mockMvc.perform(requestBuilder)
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.username", is("testUsername")))
//                .andExpect(jsonPath("$.password", is("testPassword")));
//    }

    @Test
    public void addNote_ShouldCreateNote() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        NoteDto noteDto = new NoteDto();
        noteDto.setTitle("Test Title");
        noteDto.setContent("Test Content");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/1/notes")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(noteDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(content().string("Note added successfully"));
    }

    @Test
    public void addNote_ShouldNotCreateNoteIfItAlreadyExists() throws Exception {
        User user = new User();
        user.setId(1L);
        Note note = new Note();
        note.setTitle("Test Title");
        note.setContent("Test Content");
        user.getNoteList().add(note);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        NoteDto noteDto = new NoteDto();
        noteDto.setTitle("Test Title");
        noteDto.setContent("Test Content");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/1/notes")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(noteDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Note with the same title and content already exists"));
    }


//    @Test
//    void toggleArchiveNote() throws Exception {
//        Note note = Note.builder()
//                .id(1L).title("task1").content("do").archived(false)
//                .build();
//
//        when(noteRepository.findById(note1.getId())).thenReturn(Optional.of(note1));
//        when(noteRepository.save(note)).thenReturn(note);
//
//        MockHttpServletRequestBuilder mockRequest = put("/users/todos/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(this.mapper.writeValueAsString(note));
//
//        mockMvc.perform(mockRequest)
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(1L)));
//    }

    @Test
    public void toggleArchiveNote_ShouldArchiveNote() throws Exception {
        Note note = new Note();
        note.setId(1L);
        note.setArchived(false);
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        mockMvc.perform(put("/users/todos/1"))
                .andExpect(status().isOk());

        verify(noteRepository, times(1)).save(note);
        Assertions.assertTrue(note.getArchived());
    }

//    @Test
//    public void toggleArchiveNote_NoteNotFound() throws Exception {
//        when(noteRepository.findById(1L)).thenReturn(Optional.empty());
//
//        mockMvc.perform(put("/users/todos/1"))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    public void deleteNote_ShouldDeleteNote() throws Exception {
        User user = new User();
        user.setId(1L);
        Note note = new Note();
        note.setId(1L);
        user.getNoteList().add(note);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        mockMvc.perform(delete("/users/1/notes/1"))
                .andExpect(status().isOk());

        verify(noteRepository, times(1)).delete(note);
        Assertions.assertEquals(0, user.getNoteList().size());
    }

    @Test
    public void deleteNote_UserOrNoteNotFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/users/1/notes/1"))
                .andExpect(status().isNotFound());

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(noteRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/users/1/notes/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getArchivedTodos_returnsArchivedNotes() throws Exception {
        Note note1 = new Note(1L, "note 1","", true);
        Note note2 = new Note(2L, "note 2", "",  false);
        Note note3 = new Note(3l, "note 3","", true);
        List<Note> notes = Arrays.asList(note1, note2, note3);

        when(noteRepository.findAll()).thenReturn(notes);

        ResultActions resultActions = mockMvc.perform(get("/users/todos/archives")
                .accept(APPLICATION_JSON_VALUE));

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(2)));
    }

}

