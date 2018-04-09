package nl.aoros.boot2showcase.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Image entity class.
 *
 * @author adrian
 */
@Data
@NoArgsConstructor
public class Image {
    private String id;
    private String name;

    public Image(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
