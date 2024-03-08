package edu.ucsb.cs156.happiercows.controllers;

import edu.ucsb.cs156.happiercows.entities.Announcements;
import edu.ucsb.cs156.happiercows.entities.Commons;
import edu.ucsb.cs156.happiercows.repositories.AnnouncementsRepository;
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
public class AnnouncementsController extends ApiController {

    @Autowired
    AnnouncementsRepository announcementsRepository;

    @Autowired
    CommonsRepository commonsRepository;

    @Operation(summary = "Create a new announcement")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/post")
    public Announcements createAnnouncements(
            @Parameter(name="commonsId") @RequestParam Long commonsId,
            @Parameter(name="startTime",description="in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601") @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(name="endTime",description="in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601") @RequestParam(value = "endTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(name="announcement") @RequestParam String announcement
            ) 
            throws JsonProcessingException {

        var builder = Announcements.builder()
                .commonsId(commonsId)
                .startTime(startTime)
                .endTime(endTime)
                .announcement(announcement);
        
        Announcements announcements = builder.build();

        // Commons exists
        Commons commons = commonsRepository.findById(commonsId)
            .orElseThrow(() -> new EntityNotFoundException(Commons.class, commonsId));

        // End is after Start
        if (endTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End cannot be before start");
        }
        
        // Announcement is not empty
        if (announcement.isEmpty()) {
            throw new IllegalArgumentException("Announcement cannot be empty");
        }

        // Save announcement
        return announcementsRepository.save(announcements);
    }

    @Operation(summary = "Get announcement by id")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/id")
    public Announcements getAnnouncementById(@Parameter(name="id") @RequestParam Long id) throws JsonProcessingException {
        Announcements announcement = announcementsRepository.findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException(Announcements.class, id));
        return announcement;
    } 

    @Operation(summary = "Get announcement by commons")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/commons")
    public Iterable<Announcements> getAnnouncementsByCommons(@Parameter(name="commonsId") @RequestParam Long commonsId) throws JsonProcessingException {
        // Commons exists
        Commons commons = commonsRepository.findById(commonsId)
            .orElseThrow(() -> new EntityNotFoundException(Commons.class, commonsId));
        
        return announcementsRepository.findByCommonsId(commonsId);
    } 

    @Operation(summary = "Delete an announcement")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteAnnouncements(
        @Parameter(description = "The id of the announcement") @RequestParam Long id) {

        // Get the announcement
        Announcements announcement = announcementsRepository.findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException(Announcements.class, id));

        // Delete the announcement
        announcementsRepository.delete(announcement);
        String responseString = String.format("announcement with id %d deleted", id);
        return genericMessage(responseString);
    }


    @Operation(summary = "Update an announcement")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("")
    public Announcements updateAnnouncements(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid Announcements incoming
            ) 
            throws JsonProcessingException {
        
        Long commonsId = incoming.getCommonsId();
        LocalDateTime startTime = incoming.getStartTime();
        LocalDateTime endTime = incoming.getEndTime();
        String announcement = incoming.getAnnouncement();
        
        // Get the announcement
        Announcements announcements = announcementsRepository.findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException(Announcements.class, id));

        // Commons exists
        Commons commons = commonsRepository.findById(commonsId)
            .orElseThrow(() -> new EntityNotFoundException(Commons.class, commonsId));

        // End is after Start
        if (endTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End cannot be before start");
        }
        
        // Announcement is not empty
        if (announcement.isEmpty()) {
            throw new IllegalArgumentException("Announcement cannot be empty");
        }

        // Update
        announcements.setCommonsId(commonsId);
        announcements.setStartTime(startTime);
        announcements.setEndTime(endTime);
        announcements.setAnnouncement(announcement);

        // Save announcement
        announcementsRepository.save(announcements);
        return announcements;
    }
    


}