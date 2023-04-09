package academy.devdojo.springboot2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.domain.DevDojoUser;

public interface DevDojoUserRepository extends JpaRepository<DevDojoUser,Long> {

    DevDojoUser findByUsername(String username);

}
