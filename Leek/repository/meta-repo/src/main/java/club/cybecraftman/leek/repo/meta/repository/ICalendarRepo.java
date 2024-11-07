package club.cybecraftman.leek.repo.meta.repository;

import club.cybecraftman.leek.repo.meta.model.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICalendarRepo extends JpaRepository<Calendar, String> {
}
