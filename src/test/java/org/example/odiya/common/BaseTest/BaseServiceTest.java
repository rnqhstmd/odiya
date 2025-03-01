package org.example.odiya.common.BaseTest;

import org.example.odiya.common.Fixture.DtoGenerator;
import org.example.odiya.common.Fixture.FixtureGenerator;
import org.example.odiya.common.config.FixtureGeneratorConfig;
import org.example.odiya.common.config.TestAuthConfig;
import org.example.odiya.common.config.TestFcmConfig;
import org.example.odiya.common.config.TestRouteConfig;
import org.example.odiya.notification.service.fcm.FcmEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@Import({TestRouteConfig.class, FixtureGeneratorConfig.class, TestAuthConfig.class, TestFcmConfig.class})
@ActiveProfiles("test")
@RecordApplicationEvents
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseServiceTest {

    @Autowired
    protected FixtureGenerator fixtureGenerator;

    @MockBean
    protected FcmEventListener fcmEventListener;

    @Autowired
    protected ApplicationEvents applicationEvents;

    protected DtoGenerator dtoGenerator = new DtoGenerator();
}
