package academy.devdojo.springboot2.integration;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.domain.DevDojoUser;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.repository.DevDojoUserRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.wapper.PageableResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnimeControllerIT {
    @Autowired
    @Qualifier("testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateUser;
    @Autowired
    @Qualifier("testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateAdmin;
    @Autowired
    private AnimeRepository animeRepository;
    @Autowired
    private DevDojoUserRepository devDojoUserRepository;
    private static final DevDojoUser USER = DevDojoUser.builder()
        .name("DevDojo Academy")
        .password("{bcrypt}$2a$10$G/tw7QNu/qL09t7fJuK80.ucz9YXlo7sA7XI1GeXGJpwsmU0moEJG")
        .username("devdojo")
        .authorities("ROLE_USER")
        .build();

    private static final DevDojoUser ADMIN = DevDojoUser.builder()
        .name("Jônatas Peixoto")
        .password("{bcrypt}$2a$10$G/tw7QNu/qL09t7fJuK80.ucz9YXlo7sA7XI1GeXGJpwsmU0moEJG")
        .username("jonatas")
        .authorities("ROLE_ADMIN")
        .build();
    @TestConfiguration
    @Lazy
    static class Config{
        @Bean("testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port){
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                .rootUri("http://localhost:" + port)
                .basicAuthentication("devdojo", "academy");

            return new TestRestTemplate(restTemplateBuilder);
        }
        @Bean("testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port){
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                .rootUri("http://localhost:" + port)
                .basicAuthentication("jonatas", "academy");

            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("list returns list of anime inside page object when successful")
    void list_ReturnsListOfAnimeInsidePageObject_WhenSuccessful(){
        devDojoUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        String expectedName = savedAnime.getName();

        PageableResponse<Anime> animePage = testRestTemplateUser.exchange("/animes", HttpMethod.GET, null,
            new ParameterizedTypeReference<PageableResponse<Anime>>() {
        }).getBody();

        Assertions.assertThat(animePage).isNotNull();
        Assertions.assertThat(animePage.toList())
            .isNotEmpty()
            .hasSize(1);
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }


    @Test
    @DisplayName("list returns list of anime when successful")
    void listAll_ReturnsListOfAnime_WhenSuccessful(){
        devDojoUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        String expectedName = savedAnime.getName();

        List<Anime> animes = testRestTemplateUser.exchange("/animes/all", HttpMethod.GET, null,
            new ParameterizedTypeReference<List<Anime>>() {
        }).getBody();

        Assertions.assertThat(animes)
            .isNotNull()
            .isNotEmpty()
            .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns anime when successful")
    void findById_ReturnsAnime_WhenSuccessful(){
        devDojoUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        Long expectedId = savedAnime.getId();

        Anime anime = testRestTemplateUser.getForObject("/animes/{id}", Anime.class, expectedId);

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId())
            .isNotNull()
            .isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findById returns list of anime when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful(){
        devDojoUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        String expectedName = savedAnime.getName();

        String url = String.format("/animes/find?name=%s", expectedName);

        List<Anime> animes = testRestTemplateUser.exchange(url, HttpMethod.GET, null,
            new ParameterizedTypeReference<List<Anime>>() {
            }).getBody();

        Assertions.assertThat(animes)
            .isNotNull()
            .isNotEmpty()
            .hasSize(1);

        Assertions.assertThat(animes.get(0)).isNotNull();

        Assertions.assertThat(animes.get(0).getName())
            .isNotNull()
            .isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns empty list of anime when anime is not found")
    void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound(){
        devDojoUserRepository.save(USER);

        List<Anime> animes = testRestTemplateUser.exchange("/animes/find?name=null", HttpMethod.GET, null,
            new ParameterizedTypeReference<List<Anime>>() {
            }).getBody();

        Assertions.assertThat(animes)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("save returns anime when successful")
    void save_ReturnsAnime_WhenSuccessful(){
        devDojoUserRepository.save(USER);

        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createPostRequestBody();

        ResponseEntity<Anime> animeResponseEntity = testRestTemplateUser.postForEntity("/animes", animePostRequestBody, Anime.class);

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED).isNotNull();
        Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();
        Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();
        Assertions.assertThat(animeResponseEntity.getBody().getName()).isNotNull().isEqualTo(animePostRequestBody.getName());
    }

    @Test
    @DisplayName("replace updade anime when successful")
    void replace_UpdateAnime_WhenSuccessful(){
        devDojoUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        savedAnime.setName("New Name");

        ResponseEntity<Void> animeResponseEntity = testRestTemplateUser.exchange("/animes",
            HttpMethod.PUT, new HttpEntity<>(savedAnime), Void.class);

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT).isNotNull();

    }

    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemovesAnime_WhenSuccessful(){
        devDojoUserRepository.save(ADMIN);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        ResponseEntity<Void> animeResponseEntity = testRestTemplateAdmin.exchange("/animes/admin/{id}",
            HttpMethod.DELETE, null, Void.class, savedAnime.getId());

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT).isNotNull();
    }

    @Test
    @DisplayName("delete returns 403 when user is not admin")
    void delete_Returns403_WhenUserisNotAdmin(){
        devDojoUserRepository.save(USER);

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        ResponseEntity<Void> animeResponseEntity = testRestTemplateUser.exchange("/animes/admin/{id}",
            HttpMethod.DELETE, null, Void.class, savedAnime.getId());

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN).isNotNull();
    }
}
