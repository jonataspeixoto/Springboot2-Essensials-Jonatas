package academy.devdojo.springboot2.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import academy.devdojo.springboot2.domain.Anime;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SpringClient {
    public static void main(String[] args) {
        ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/animes/{id}", Anime.class, 2);
        log.info(entity);

        Anime object = new RestTemplate().getForObject("http://localhost:8080/animes/{id}", Anime.class, 2);

        log.info(object);

        Anime[] animes = new RestTemplate().getForObject("http://localhost:8080/animes/all", Anime[].class);

        log.info(Arrays.toString(animes));

        ResponseEntity<List<Anime>> exchange = new RestTemplate().exchange("http://localhost:8080/animes/all",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {});

        log.info(exchange.getBody());

//        Anime kingdom = Anime.builder().name("Kingdom").build();
//        Anime kingdomSaved = new RestTemplate().postForObject("http://localhost:8080/animes", kingdom, Anime.class);
//
//        log.info("saved anime {}", kingdomSaved);

        Anime samuraiX = Anime.builder().name("Samurai X").build();
        ResponseEntity<Anime> samuraiXSaved = new RestTemplate().exchange("http://localhost:8080/animes",
            HttpMethod.POST,
            new HttpEntity<>(samuraiX, createJsonHeader()),
            Anime.class);

        log.info("saved anime {}", samuraiXSaved);

        Anime animeToBeUpdated = samuraiXSaved.getBody();
        animeToBeUpdated.setName("Samurai X 2");

        ResponseEntity<Void> samuraiXUpdated = new RestTemplate().exchange("http://localhost:8080/animes",
            HttpMethod.PUT,
            new HttpEntity<>(animeToBeUpdated, createJsonHeader()),
            Void.class);

        log.info(samuraiXUpdated);

        ResponseEntity<Void> samuraiXDeleted = new RestTemplate().exchange("http://localhost:8080/animes/{id}",
            HttpMethod.DELETE,
            new HttpEntity<>(animeToBeUpdated, createJsonHeader()),
            Void.class,
            animeToBeUpdated.getId());

        log.info(samuraiXDeleted);
    }

    private static HttpHeaders createJsonHeader(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
