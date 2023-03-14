package com.rungway;

import com.rungway.entity.Note;
import com.rungway.entity.User;
import com.rungway.repository.NoteRepository;
import com.rungway.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class NoteApplication implements CommandLineRunner {
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(NoteApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Note note = new Note();
        note.setTitle("task1");
        note.setContent("do");

        User user = new User();
        user.setUsername("A");
        user.setPassword("A1@");
        user.getNoteList().add(note);

        noteRepository.save(note);
        userRepository.save(user);
    }

}