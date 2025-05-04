package ru.rpovetkin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rpovetkin.repository.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
