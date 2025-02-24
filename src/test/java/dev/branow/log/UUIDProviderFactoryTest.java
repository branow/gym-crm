package dev.branow.log;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(UUIDProviderFactory.class)
@ExtendWith(MockitoExtension.class)
public class UUIDProviderFactoryTest {

    @Mock
    private HttpServletRequest request;

    @Autowired
    private UUIDProviderFactory factory;

    @BeforeEach
    void setUp() {
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @Test
    public void testWebUUID() {
        var uuid = UUID.randomUUID();
        when(request.getAttribute("uuid")).thenReturn(uuid);
        var provider = factory.createWebUUIDProvider();
        var actualUUID = provider.getUUID();
        assertEquals(uuid, actualUUID);
    }

    @Test
    public void testWebUUID_withoutAttribute_throwException() {
        var provider = factory.createWebUUIDProvider();
        assertThrows(IllegalStateException.class, provider::getUUID);
    }

}
