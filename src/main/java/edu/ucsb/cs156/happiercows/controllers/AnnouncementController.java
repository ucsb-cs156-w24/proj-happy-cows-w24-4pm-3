package edu.ucsb.cs156.happiercows.controllers;

import edu.ucsb.cs156.happiercows.entities.Announcement;
import edu.ucsb.cs156.happiercows.entities.Commons;
import edu.ucsb.cs156.happiercows.repositories.AnnouncementRepository;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ucsb.cs156.happiercows.errors.EntityNotFoundException;

import javax.validation.Valid;

import java.time.LocalDateTime;

@Tag(name = "Announcements")
@RequestMapping("/api/announcements")
@RestController
@Slf4j
public class AnnouncementController extends ApiController {

    @Autowired
    AnnouncementRepository announcementRepository;

    @Autowired
    CommonsRepository commonsRepository;

    @Operation(summary = "Create a new announcement")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/post")
    public Announcement createAnnouncement(
            @Parameter(name="commonsId") @RequestParam Long commonsId,
            @Parameter(name="start",description="in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601") @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(name="end",description="in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601") @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @Parameter(name="announcement") @RequestParam String announcement
            ) 
            throws JsonProcessingException {

        var builder = Announcement.builder()
                .commonsId(commonsId)
                .start(start)
                .end(end)
                .announcement(announcement);
        
        Announcement announcements = builder.build();

        // Commons exists
        Commons commons = commonsRepository.findById(commonsId)
            .orElseThrow(() -> new EntityNotFoundException(Commons.class, commonsId));

        // End is after Start
        if (end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("End cannot be before start");
        }
        
        // Announcement is not empty
        if (announcement.isEmpty()) {
            throw new IllegalArgumentException("Announcement cannot be empty");
        }

        // Save announcement
        return announcementRepository.save(announcements);
    }

    @Operation(summary = "Get announcement by id")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/id")
    public Announcement getAnnouncementById(@Parameter(name="id") @RequestParam Long id) throws JsonProcessingException {
        Announcement announcement = announcementRepository.findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException(Announcement.class, id));
        return announcement;
    } 

    @Operation(summary = "Get announcement by commons")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/commons")
    public Iterable<Announcement> getAnnouncementByCommons(@Parameter(name="commonsId") @RequestParam Long commonsId) throws JsonProcessingException {
        // Commons exists
        Commons commons = commonsRepository.findById(commonsId)
            .orElseThrow(() -> new EntityNotFoundException(Commons.class, commonsId));
        
        return announcementRepository.findByCommonsId(commonsId);
    } 

    @Operation(summary = "Delete an announcement")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteAnnouncement(
        @Parameter(description = "The id of the announcement") @RequestParam Long id) {

        // Get the announcement
        Announcement announcement = announcementRepository.findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException(Announcement.class, id));

        // Delete the announcement
        announcementRepository.delete(announcement);
        String responseString = String.format("announcement with id %d deleted", id);
        return genericMessage(responseString);
    }


    @Operation(summary = "Update an announcement")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("")
    public Announcement updateAnnouncement(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid Announcement incoming
            ) 
            throws JsonProcessingException {
        
        Long commonsId = incoming.getCommonsId();
        LocalDateTime start = incoming.getStart();
        LocalDateTime end = incoming.getEnd();
        String announcement = incoming.getAnnouncement();
        
        // Get the announcement
        Announcement announcements = announcementRepository.findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException(Announcement.class, id));

        // Commons exists
        Commons commons = commonsRepository.findById(commonsId)
            .orElseThrow(() -> new EntityNotFoundException(Commons.class, commonsId));

        // End is after Start
        if (end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("End cannot be before start");
        }
        
        // Announcement is not empty
        if (announcement.isEmpty()) {
            throw new IllegalArgumentException("Announcement cannot be empty");
        }

        // Update
        announcements.setCommonsId(commonsId);
        announcements.setStart(start);
        announcements.setEnd(end);
        announcements.setAnnouncement(announcement);

        // Save announcement
        announcementRepository.save(announcements);
        return announcements;
    }
    


}