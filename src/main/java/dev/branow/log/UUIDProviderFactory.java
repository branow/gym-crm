package dev.branow.log;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

@Component
public class UUIDProviderFactory {

    public UUIDProvider createWebUUIDProvider() {
        return () -> {
            var request = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                    .map(attributes -> ((ServletRequestAttributes) attributes).getRequest())
                    .orElseThrow(() -> new IllegalStateException("No request attributes found"));

            return Optional.ofNullable(request.getAttribute("uuid"))
                    .map(object -> (UUID) object)
                    .orElseThrow(() -> new IllegalStateException("No uuid attribute found"));
        };
    }

}
