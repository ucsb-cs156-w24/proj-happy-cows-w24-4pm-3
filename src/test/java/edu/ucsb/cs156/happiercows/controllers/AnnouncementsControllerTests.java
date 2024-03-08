package edu.ucsb.cs156.happiercows.controllers;

import edu.ucsb.cs156.happiercows.ControllerTestCase;
import edu.ucsb.cs156.happiercows.entities.Announcements;
import edu.ucsb.cs156.happiercows.entities.Commons;
import edu.ucsb.cs156.happiercows.repositories.AnnouncementsRepository;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@WebMvcTest(controllers = AnnouncementsController.class)
@AutoConfigureDataJpa
public class AnnouncementsControllerTests extends ControllerTestCase {

    @MockBean
    AnnouncementsRepository announcementsRepository;

    @MockBean
    CommonsRepository commonsRepository;

    Commons commons = Commons
            .builder()
            .name("test commons")
            .cowPrice(10)
            .milkPrice(2)
            .startingBalance(300)
            .startingDate(LocalDateTime.now())
            .build();

    long id = 1;
    long commonsId = commons.getId();
    LocalDateTime start = LocalDateTime.parse("2022-01-03T00:00:00");
    LocalDateTime end = LocalDateTime.parse("2023-01-03T00:00:00");
    String message = "test";

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/announcements/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void admin_can_post() throws Exception {
        when(commonsRepository.findById(commonsId)).thenReturn(Optional.of(commons));

        Announcements announcement = Announcements.builder().commonsId(commonsId).start(start).end(end).announcement(message).build();

        when(announcementsRepository.save(any(Announcements.class))).thenReturn(announcement);

        //act 
        MvcResult response = mockMvc.perform(post("/api/announcements/post?commonsId={commonsId}&start={start}&end={end}&announcement={message}", commonsId, start, end, message).with(csrf()))
            .andExpect(status().isOk()).andReturn();

        // assert
        verify(announcementsRepository, times(1)).save(any(Announcements.class));
        String responseString = response.getResponse().getContentAsString();
        String expectedJson = mapper.writeValueAsString(announcement);
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void admin_can_post_without_end() throws Exception {
        when(commonsRepository.findById(commonsId)).thenReturn(Optional.of(commons));

        Announcements announcement = Announcements.builder().commonsId(commonsId).start(start).announcement(message).build();

        when(announcementsRepository.save(any(Announcements.class))).thenReturn(announcement);

        //act 
        MvcResult response = mockMvc.perform(post("/api/announcements/post?commonsId={commonsId}&start={start}&announcement={message}", commonsId, start, message).with(csrf()))
            .andExpect(status().isOk()).andReturn();

        // assert
        verify(announcementsRepository, times(1)).save(any(Announcements.class));
        String responseString = response.getResponse().getContentAsString();
        String expectedJson = mapper.writeValueAsString(announcement);
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void end_cannot_be_before_start() throws Exception {
        when(commonsRepository.findById(commonsId)).thenReturn(Optional.of(commons));

        Announcements announcement = Announcements.builder().commonsId(commonsId).start(end).end(start).announcement(message).build();

        when(announcementsRepository.save(any(Announcements.class))).thenReturn(announcement);

        //act 
        MvcResult response = mockMvc.perform(post("/api/announcements/post?commonsId={commonsId}&start={end}&end={start}&announcement={message}", commonsId, end, start, message).with(csrf()))
            .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(announcementsRepository, times(0)).save(any(Announcements.class));
        assertInstanceOf(IllegalArgumentException.class, response.getResolvedException());
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void announcement_cannot_be_empty() throws Exception {
        when(commonsRepository.findById(commonsId)).thenReturn(Optional.of(commons));

        Announcements announcement = Announcements.builder().commonsId(commonsId).start(end).end(start).announcement(message).build();

        when(announcementsRepository.save(any(Announcements.class))).thenReturn(announcement);

        //act 
        MvcResult response = mockMvc.perform(post("/api/announcements/post?commonsId={commonsId}&start={start}&end={end}&announcement={message}", commonsId, start, end, "").with(csrf()))
            .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(announcementsRepository, times(0)).save(any(Announcements.class));
        assertInstanceOf(IllegalArgumentException.class, response.getResolvedException());
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void commons_not_found_in_post() throws Exception {

        // act
        MvcResult response = mockMvc.perform(post("/api/announcements/post?commonsId={commonsId}&start={start}&end={end}&announcement={message}", 2, start, end, message)
                        .with(csrf()))
                .andExpect(status().is(404)).andReturn();

        // assert

        String expectedString = "{\"type\":\"EntityNotFoundException\",\"message\":\"Commons with id 2 not found\"}";
        Map<String, Object> expectedJson = mapper.readValue(expectedString, Map.class);
        Map<String, Object> jsonResponse = responseToJson(response);
        assertEquals(expectedJson, jsonResponse);
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void commons_not_found_in_get_by_commons_id() throws Exception {

        // act
        MvcResult response = mockMvc.perform(get("/api/announcements/commons?commonsId={commonsId}", 2)
                        .with(csrf()))
                .andExpect(status().is(404)).andReturn();

        // assert

        String expectedString = "{\"type\":\"EntityNotFoundException\",\"message\":\"Commons with id 2 not found\"}";
        Map<String, Object> expectedJson = mapper.readValue(expectedString, Map.class);
        Map<String, Object> jsonResponse = responseToJson(response);
        assertEquals(expectedJson, jsonResponse);
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void successful_get_by_commons_id() throws Exception {
        when(commonsRepository.findById(commonsId)).thenReturn(Optional.of(commons));

        List<Announcements> expected = new ArrayList<Announcements>();
        Announcements announcement = Announcements.builder().commonsId(commonsId).start(end).end(start).announcement(message).build();
        expected.add(announcement);
        expected.add(announcement);
        when(announcementsRepository.findByCommonsId(commonsId)).thenReturn(expected);

        //act 
        MvcResult response = mockMvc.perform(get("/api/announcements/commons?commonsId={commonsId}", commonsId).with(csrf()))
            .andExpect(status().isOk()).andReturn();
        
        // assert
        verify(commonsRepository, times(1)).findById(commonsId);
        verify(announcementsRepository, times(1)).findByCommonsId(commonsId);
        String responseString = response.getResponse().getContentAsString();
        List<Announcements> actualAnnouncements = mapper.readValue(responseString, new TypeReference<List<Announcements>>() {
        });
        assertEquals(actualAnnouncements, expected);
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void announcement_not_found_in_get_by_announcement_id() throws Exception {
        // act
        MvcResult response = mockMvc.perform(get("/api/announcements/id?id={id}", 2)
                        .with(csrf()))
                .andExpect(status().is(404)).andReturn();

        // assert

        String expectedString = "{\"type\":\"EntityNotFoundException\",\"message\":\"Announcements with id 2 not found\"}";
        Map<String, Object> expectedJson = mapper.readValue(expectedString, Map.class);
        Map<String, Object> jsonResponse = responseToJson(response);
        assertEquals(expectedJson, jsonResponse);
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void successful_get_by_announcement_id() throws Exception {
        Announcements announcement = Announcements.builder().commonsId(commonsId).start(end).end(start).announcement(message).build();
        when(announcementsRepository.findById(id)).thenReturn(Optional.of(announcement));

        //act 
        MvcResult response = mockMvc.perform(get("/api/announcements/id?id={id}", id).with(csrf()))
            .andExpect(status().isOk()).andReturn();
        
        // assert
        verify(announcementsRepository, times(1)).findById(id);
        String responseString = response.getResponse().getContentAsString();
        String expectedJson = mapper.writeValueAsString(announcement);
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void admin_can_put() throws Exception {
        long commonsId2 = 2;
        LocalDateTime start2 = LocalDateTime.parse("2024-01-03T00:00:00");
        LocalDateTime end2 = LocalDateTime.parse("2025-01-03T00:00:00");
        String message2 = "testtest";

        Announcements announcement = Announcements.builder().id(id).commonsId(commonsId).start(start).end(end).announcement(message).build();
        Announcements announcement2 = Announcements.builder().id(id).commonsId(commonsId2).start(start2).end(end2).announcement(message2).build();

        when(commonsRepository.findById(commonsId2)).thenReturn(Optional.of(commons));
        when(announcementsRepository.findById(id)).thenReturn(Optional.of(announcement));

        String requestBody = mapper.writeValueAsString(announcement2);

        // act 
        MvcResult response = mockMvc.perform(
            put("/api/announcements?id={id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(requestBody)
                .with(csrf()))
            .andExpect(status().isOk()).andReturn();

        // assert
        verify(announcementsRepository, times(1)).findById(id);
        verify(commonsRepository, times(1)).findById(commonsId2);
        verify(announcementsRepository, times(1)).save(announcement2);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(requestBody, responseString);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void admin_can_put_without_end() throws Exception {
        long commonsId2 = 2;
        LocalDateTime start2 = LocalDateTime.parse("2024-01-03T00:00:00");
        String message2 = "testtest";

        Announcements announcement = Announcements.builder().id(id).commonsId(commonsId).start(start).end(end).announcement(message).build();
        Announcements announcement2 = Announcements.builder().id(id).commonsId(commonsId2).start(start2).announcement(message2).build();

        when(commonsRepository.findById(commonsId2)).thenReturn(Optional.of(commons));
        when(announcementsRepository.findById(id)).thenReturn(Optional.of(announcement));

        String requestBody = mapper.writeValueAsString(announcement2);

        // act 
        MvcResult response = mockMvc.perform(
            put("/api/announcements?id={id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(requestBody)
                .with(csrf()))
            .andExpect(status().isOk()).andReturn();

        // assert
        verify(announcementsRepository, times(1)).findById(id);
        verify(commonsRepository, times(1)).findById(commonsId2);
        verify(announcementsRepository, times(1)).save(announcement2);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(requestBody, responseString);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void end_cannot_be_before_start_in_put() throws Exception {
        long commonsId2 = 2;
        LocalDateTime start2 = LocalDateTime.parse("2025-01-03T00:00:00");
        LocalDateTime end2 = LocalDateTime.parse("2024-01-03T00:00:00");
        String message2 = "testtest";

        Announcements announcement = Announcements.builder().id(id).commonsId(commonsId).start(start).end(end).announcement(message).build();
        Announcements announcement2 = Announcements.builder().id(id).commonsId(commonsId2).start(start2).end(end2).announcement(message2).build();

        when(commonsRepository.findById(commonsId2)).thenReturn(Optional.of(commons));
        when(announcementsRepository.findById(id)).thenReturn(Optional.of(announcement));

        String requestBody = mapper.writeValueAsString(announcement2);

        // act 
        MvcResult response = mockMvc.perform(
            put("/api/announcements?id={id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(requestBody)
                .with(csrf()))
            .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(announcementsRepository, times(0)).save(any(Announcements.class));
        assertInstanceOf(IllegalArgumentException.class, response.getResolvedException());
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void announcement_cannot_be_empty_in_put() throws Exception {
        long commonsId2 = 2;
        LocalDateTime start2 = LocalDateTime.parse("2024-01-03T00:00:00");
        LocalDateTime end2 = LocalDateTime.parse("2025-01-03T00:00:00");
        String message2 = "";

        Announcements announcement = Announcements.builder().id(id).commonsId(commonsId).start(start).end(end).announcement(message).build();
        Announcements announcement2 = Announcements.builder().id(id).commonsId(commonsId2).start(start2).end(end2).announcement(message2).build();

        when(commonsRepository.findById(commonsId2)).thenReturn(Optional.of(commons));
        when(announcementsRepository.findById(id)).thenReturn(Optional.of(announcement));

        String requestBody = mapper.writeValueAsString(announcement2);

        // act 
        MvcResult response = mockMvc.perform(
            put("/api/announcements?id={id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(requestBody)
                .with(csrf()))
            .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(announcementsRepository, times(0)).save(any(Announcements.class));
        assertInstanceOf(IllegalArgumentException.class, response.getResolvedException());
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void commons_not_found_in_put() throws Exception {
        long commonsId2 = 2;
        LocalDateTime start2 = LocalDateTime.parse("2024-01-03T00:00:00");
        LocalDateTime end2 = LocalDateTime.parse("2025-01-03T00:00:00");
        String message2 = "";

        Announcements announcement = Announcements.builder().id(id).commonsId(commonsId).start(start).end(end).announcement(message).build();
        Announcements announcement2 = Announcements.builder().id(id).commonsId(commonsId2).start(start2).end(end2).announcement(message2).build();

        //when(commonsRepository.findById(commonsId2)).thenReturn(Optional.of(commons));
        when(announcementsRepository.findById(id)).thenReturn(Optional.of(announcement));

        String requestBody = mapper.writeValueAsString(announcement2);

        // act 
        MvcResult response = mockMvc.perform(
            put("/api/announcements?id={id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(requestBody)
                .with(csrf()))
            .andExpect(status().is(404)).andReturn();

        // assert
        verify(announcementsRepository, times(0)).save(any(Announcements.class));
        String expectedString = String.format("{\"type\":\"EntityNotFoundException\",\"message\":\"Commons with id %d not found\"}", commonsId2);
        Map<String, Object> expectedJson = mapper.readValue(expectedString, Map.class);
        Map<String, Object> jsonResponse = responseToJson(response);
        assertEquals(expectedJson, jsonResponse);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void announcement_not_found_in_put() throws Exception {
        long commonsId2 = 2;
        LocalDateTime start2 = LocalDateTime.parse("2024-01-03T00:00:00");
        LocalDateTime end2 = LocalDateTime.parse("2025-01-03T00:00:00");
        String message2 = "";

        Announcements announcement = Announcements.builder().id(id).commonsId(commonsId).start(start).end(end).announcement(message).build();
        Announcements announcement2 = Announcements.builder().id(id).commonsId(commonsId2).start(start2).end(end2).announcement(message2).build();

        when(commonsRepository.findById(commonsId2)).thenReturn(Optional.of(commons));

        String requestBody = mapper.writeValueAsString(announcement2);

        // act 
        MvcResult response = mockMvc.perform(
            put("/api/announcements?id={id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(requestBody)
                .with(csrf()))
            .andExpect(status().is(404)).andReturn();

        // assert
        verify(announcementsRepository, times(0)).save(any(Announcements.class));
        String expectedString = String.format("{\"type\":\"EntityNotFoundException\",\"message\":\"Announcements with id %d not found\"}", id);
        Map<String, Object> expectedJson = mapper.readValue(expectedString, Map.class);
        Map<String, Object> jsonResponse = responseToJson(response);
        assertEquals(expectedJson, jsonResponse);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void successful_delete() throws Exception {
        Announcements announcement = Announcements.builder().id(id).commonsId(commonsId).start(start).end(end).announcement(message).build();
        when(announcementsRepository.findById(id)).thenReturn(Optional.of(announcement));

        // act 
        MvcResult response = mockMvc.perform(delete("/api/announcements?id={id}", id).with(csrf()))
            .andExpect(status().isOk()).andReturn();

        // assert
        verify(announcementsRepository, times(1)).delete(any(Announcements.class));
        String responseString = response.getResponse().getContentAsString();
        String expectedResponseString = String.format("{\"message\":\"announcement with id %d deleted\"}", id);
        assertEquals(expectedResponseString, responseString);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void announcement_with_id_not_found_in_delete() throws Exception {
        Announcements announcement = Announcements.builder().id(id).commonsId(commonsId).start(start).end(end).announcement(message).build();

        // act 
        MvcResult response = mockMvc.perform(delete("/api/announcements?id={id}", id).with(csrf()))
            .andExpect(status().is(404)).andReturn();

        // assert
        String expectedString = String.format("{\"type\":\"EntityNotFoundException\",\"message\":\"Announcements with id %d not found\"}", id);
        Map<String, Object> expectedJson = mapper.readValue(expectedString, Map.class);
        Map<String, Object> jsonResponse = responseToJson(response);
        assertEquals(expectedJson, jsonResponse);
    }

}