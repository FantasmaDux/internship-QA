package api;

import api.dto.AdCreationRequestDto;
import api.dto.StatisticResponseDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static api.util.CommonMethods.getIdFromStatus;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CreateAdEndpointTests {

    private static final int MIN_SELLER_ID_BOUND = 111111;
    private static final int MAX_SELLER_ID_BOUND = 999999;
    private static final String URL = "https://qa-internship.avito.com/api/1/item";

    @Test
    public void adCreationWithValidInputTest() {
        StatisticResponseDto stats = new StatisticResponseDto(2, 18, 1);

        AdCreationRequestDto data = new AdCreationRequestDto(generateSellerId(), "auto", 120000, stats);

        Response response = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(200));
    }

    @Test
    public void adCreationWithValidInputAndMinSellerIdBoundTest() {
        StatisticResponseDto stats = new StatisticResponseDto(2, 18, 1);

        AdCreationRequestDto data = new AdCreationRequestDto(MIN_SELLER_ID_BOUND, "auto", 120000, stats);

        Response response = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(200));
    }


    @Test
    public void adCreationWithValidInputAndMaxSellerIdBoundTest() {
        StatisticResponseDto stats = new StatisticResponseDto(2, 18, 1);

        AdCreationRequestDto data = new AdCreationRequestDto(MAX_SELLER_ID_BOUND, "auto", 120000, stats);

        Response response = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(200));
    }

    @Test
    public void adCreationWithZeroPriceTest() {
        StatisticResponseDto stats = new StatisticResponseDto(2, 18, 1);

        AdCreationRequestDto data = new AdCreationRequestDto(generateSellerId(), "auto", 0, stats);

        Response response = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(200));
    }

    @Test
    public void adCreationWithZeroViewCountTest() {
        StatisticResponseDto stats = new StatisticResponseDto(2, 0, 1);

        AdCreationRequestDto data = new AdCreationRequestDto(generateSellerId(), "auto", 1, stats);

        Response response = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(200));
    }

    @Test
    public void adCreationWithNullSellerIdTest() {
        StatisticResponseDto stats = new StatisticResponseDto(2, 18, 1);

        AdCreationRequestDto data = new AdCreationRequestDto(null, "auto", 1, stats);

        Response response = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(400));
    }

    @ParameterizedTest
    @CsvSource({"-1", "-100"})
    public void testCreateAdWithNegativePriceTest(Long invalidPrice) {
        StatisticResponseDto stats = new StatisticResponseDto(2, 0, 1);
        AdCreationRequestDto data = new AdCreationRequestDto(generateSellerId(), "auto", invalidPrice, stats);
        Response response = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(400));
    }

    @ParameterizedTest
    @CsvSource({
            "-2, -18, -1",
            "-5, -10, -3"
    })
    public void testCreateAdWithNegativeStatisticsTest(int invalidLikes, int invalidViewCount, int invalidContacts) {
        StatisticResponseDto stats = new StatisticResponseDto(
                invalidContacts, invalidViewCount, invalidLikes);

        AdCreationRequestDto data = new AdCreationRequestDto(generateSellerId(), "clock", 200, stats);
        Response response = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(400));
    }

    @ParameterizedTest
    @CsvSource({"-1", "-100"})
    public void testCreateAdWithNegativeSellerIdTest(int invalidSellerId) {
        StatisticResponseDto stats = new StatisticResponseDto(2, 18, 1);
        AdCreationRequestDto data = new AdCreationRequestDto(invalidSellerId, "auto", 200, stats);
        Response response = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(400));
    }

    @Test
    public void adCreationWithBlankNameTest() {
        StatisticResponseDto stats = new StatisticResponseDto(2, 18, 1);

        AdCreationRequestDto data = new AdCreationRequestDto(generateSellerId(), "", 1, stats);

        Response response = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(400));
    }

    @Test
    public void adCreationWithMissingContactsFieldTest() {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("likes", 2);
        statistics.put("viewCount", 18);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("sellerID", generateSellerId());
        requestBody.put("name", "clock");
        requestBody.put("price", 200);
        requestBody.put("statistics", statistics);

        Response response = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(400));
    }

    @Test
    public void adCreationWithWrongMethodTest() {
        StatisticResponseDto stats = new StatisticResponseDto(2, 18, 1);

        AdCreationRequestDto data = new AdCreationRequestDto(generateSellerId(), "clock", 200, stats);

        Response response = given()
                .contentType("application/json")
                .body(data)
                .when()
                .get(URL)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(405));
    }

    @Test
    public void idempotencyTest() {

        StatisticResponseDto stats = new StatisticResponseDto(2, 18, 1);
        AdCreationRequestDto data = new AdCreationRequestDto(generateSellerId(), "clock", 200, stats);

        Response firstResponse = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(firstResponse.statusCode(), is(200));
        String firstId = getIdFromStatus(firstResponse.jsonPath().getString("status"));

        Response secondResponse = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(secondResponse.statusCode(), is(200));
        String secondId = getIdFromStatus(secondResponse.jsonPath().getString("status"));

        Response thirdResponse = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(secondResponse.statusCode(), is(200));
        String thirdId = getIdFromStatus(thirdResponse.jsonPath().getString("status"));

        assertThat(secondId, not(equalTo(firstId)));
        assertThat(thirdId, not(equalTo(secondId)));
    }

    @Test
    public void adCreationWithStrangeParamsTest() {
        StatisticResponseDto stats = new StatisticResponseDto(1, 1, 9999);

        AdCreationRequestDto data = new AdCreationRequestDto(999999, "#$%_y!%^:", Integer.MAX_VALUE, stats);

        Response response = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(200));
    }

    @Test
    public void sqlInjectionTest() {
        String sqlInjection = "' or 1=1--";

        StatisticResponseDto stats = new StatisticResponseDto(2, 18, 1);
        AdCreationRequestDto data = new AdCreationRequestDto(generateSellerId(), sqlInjection, 200, stats);

        Response createResponse = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(createResponse.statusCode(), is(200));
    }

    @Test
    public void xssInjectionTest() {
        String xssInjection = "<script>alert('hi!')</script>";

        StatisticResponseDto stats = new StatisticResponseDto(2, 18, 1);
        AdCreationRequestDto data = new AdCreationRequestDto(generateSellerId(), xssInjection, 200, stats);

        Response createResponse = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post(URL)
                .then()
                .extract()
                .response();

        assertThat(createResponse.statusCode(), is(200));
        String xssProtection = createResponse.getHeader("X-XSS-Protection");
        assertThat(xssProtection, is("1; mode=block"));
    }

    private Integer generateSellerId() {
        return new Random().nextInt(MIN_SELLER_ID_BOUND, MAX_SELLER_ID_BOUND + 1);
    }
}
