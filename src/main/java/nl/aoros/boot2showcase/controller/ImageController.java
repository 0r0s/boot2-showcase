package nl.aoros.boot2showcase.controller;


import nl.aoros.boot2showcase.model.Image;
import nl.aoros.boot2showcase.service.ImageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * Controller for handling image CRUD operations.
 *
 * @author adrian
 */
@Controller
@Log4j2
public class ImageController {

    private static final String BASE_PATH = "/backend/images";
    static final String FILENAME = "{filename:.+}";

    private ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Index with all the images.
     *
     * @param model model which will contain the image list
     * @return next view
     */
    @GetMapping("/")
    public Mono<String> index(Model model) {
        model.addAttribute("images", imageService.findAllImages());
        return Mono.just("index");
    }

    /**
     * Lists all the images.
     *
     * @return flux of images
     */
    @GetMapping(BASE_PATH)
    Flux<Image> images() {
        return imageService.findAllImages();
    }

    /**
     * Gets a raw image based on its filename.
     *
     * @param filename the image file name
     * @return stream containing the found image
     */
    @GetMapping(value = BASE_PATH + "/" + FILENAME + "/raw", produces = MediaType.IMAGE_JPEG_VALUE)
    Mono<ResponseEntity<?>> rawImage(@PathVariable String filename) {
        return imageService.findOneImage(filename).map(resource -> {
            try {
                return ResponseEntity.ok()
                        .contentLength(resource.contentLength())
                        .body(new InputStreamResource(resource.getInputStream()));
            } catch (IOException e) {
                log.error("Could not read raw image " + filename, e);
                return ResponseEntity.badRequest().body("Could not find " + filename + " => " + e.getMessage());
            }
        });
    }

    /**
     * Handler for saving images.
     *
     * @param files filest to be removed
     * @return next view
     */
    @PostMapping(BASE_PATH)
    Mono<String> create(@RequestPart Flux<FilePart> files) {
        return imageService.createImage(files).then(Mono.just("redirect:/"));
    }

    /**
     * Hanlder for removing a single image.
     *
     * @param filename file to be removed
     * @return next view
     */
    @DeleteMapping(BASE_PATH + "/" + FILENAME)
    public Mono<String> deleteFile(@PathVariable String filename) {
        return imageService.deleteImage(filename).then(Mono.just("redirect:/"));
    }
}
