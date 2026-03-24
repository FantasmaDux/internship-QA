
/*
 * Класс для единовременного запуска всех наборов тестов из IntelliJ IDEA
 */

import api.*;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        CreateAdEndpointTests.class,
        GetAdByIdEndpointTests.class,
        GetAdsBySellerIdEndpointTests.class,
        GetStatisticsByAdIdTests.class,
        E2ETests.class,
})
public class TestRunner {

}