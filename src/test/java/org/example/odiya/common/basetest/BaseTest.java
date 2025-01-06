package org.example.odiya.common.basetest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public abstract class BaseTest {

    @BeforeEach
    protected void initMocks() {
        MockitoAnnotations.openMocks(this);
    }
}
