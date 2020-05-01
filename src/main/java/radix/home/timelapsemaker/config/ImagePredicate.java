package radix.home.timelapsemaker.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import radix.home.timelapsemaker.enums.EnumImageType;

import java.io.File;
import java.util.function.Predicate;

@Slf4j
@Component
public class ImagePredicate implements Predicate<File> {

    @Override
    public boolean test(File file) {
        return EnumImageType.isAllowed(file.toPath());
    }
}
