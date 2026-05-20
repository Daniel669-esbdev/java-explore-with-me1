package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.LocationArea;

import java.util.Optional;

@Repository
public interface LocationAreaRepository extends JpaRepository<LocationArea, Long> {
    Optional<LocationArea> findByName(String name);
}