package edu.ucsb.cs156.happiercows.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.lang.Iterable;

import edu.ucsb.cs156.happiercows.entities.Announcements;

@Repository
public interface AnnouncementsRepository extends CrudRepository<Announcements, Long> {
    Iterable<Announcements> findByCommonsId(Long commonsId);
}