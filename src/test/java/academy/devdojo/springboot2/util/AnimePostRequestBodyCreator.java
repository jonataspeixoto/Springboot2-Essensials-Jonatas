package academy.devdojo.springboot2.util;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;

public class AnimePostRequestBodyCreator {
    public static AnimePostRequestBody createPostRequestBody(){
        return AnimePostRequestBody.builder()
            .name(AnimeCreator.createAnimeToBeSaved().getName())
            .build();
    }
}
