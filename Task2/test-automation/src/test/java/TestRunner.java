import api.CreateAdEndpointTests;
import api.GetAdByIdEndpointTests;
import api.GetAdsBySellerIdEndpointTests;
import api.GetStatisticsByAdIdTests;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        CreateAdEndpointTests.class,
        GetAdByIdEndpointTests.class,
        GetAdsBySellerIdEndpointTests.class,
        GetStatisticsByAdIdTests.class
})
public class TestRunner{

}