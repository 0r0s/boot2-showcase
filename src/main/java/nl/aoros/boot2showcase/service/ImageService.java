package nl.aoros.boot2showcase.service;

import nl.aoros.boot2showcase.model.Image;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Service for managing images.
 *
 * @author adrian
 */
@Service
@Log4j2
public class ImageService {
    public static final String UPLOAD_ROOT = "uploads";
    private final ResourceLoader resourceLoader;

    public ImageService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Mono<Resource> findOneImage(String filename) {
        return Mono.fromSupplier(() -> resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + filename));

    }

    public Flux<Image> findAllImages() {
        try {
            return Flux.fromIterable(Files.newDirectoryStream(Paths.get(UPLOAD_ROOT)))
                    .map(path -> new Image(String.valueOf(path.hashCode()), path.getFileName().toString()));
        } catch (IOException e) {
            log.error("Could not list images", e);
            return Flux.empty();
        }
    }

    public Mono<Void> createImage(Flux<FilePart> files) {
        return files.flatMap(filePart -> filePart.transferTo(Paths.get(UPLOAD_ROOT, filePart.filename()).toFile())).then();
    }

    public Mono<Void> deleteImage(String filename) {
        return Mono.fromRunnable(() -> {
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
            } catch (IOException e) {
                log.error("Could not remove file " + filename, e);
                throw new RuntimeException(e);
            }
        });
    }
}
