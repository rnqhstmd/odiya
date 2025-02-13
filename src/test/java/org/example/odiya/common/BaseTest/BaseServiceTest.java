package org.example.odiya.common.BaseTest;

import com.google.firebase.messaging.FirebaseMessaging;
import org.example.odiya.common.Fixture.DtoGenerator;
import org.example.odiya.common.Fixture.FixtureGenerator;
import org.example.odiya.common.config.FixtureGeneratorConfig;
import org.example.odiya.common.config.TestAuthConfig;
import org.example.odiya.common.config.TestRouteConfig;
import org.example.odiya.notification.config.FcmConfig;
import org.example.odiya.notification.service.fcm.FcmEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@Import({TestRouteConfig.class, FixtureGeneratorConfig.class, TestAuthConfig.class})
@ActiveProfiles("test")
@RecordApplicationEvents
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseServiceTest {

    @Autowired
    protected FixtureGenerator fixtureGenerator;

    @MockBean
    protected FcmEventListener fcmEventListener;

    @MockBean
    private FcmConfig fcmConfig;

    @MockBean
    protected FirebaseMessaging firebaseMessaging;

    @Autowired
    protected ApplicationEvents applicationEvents;

    protected DtoGenerator dtoGenerator = new DtoGenerator();
}
