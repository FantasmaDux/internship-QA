package api;

import api.dto.AdResponseDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class GetAdsBySellerIdEndpointTests {
    private static final int MIN_SELLER_ID_BOUND = 111111;
    private static final int MAX_SELLER_ID_BOUND = 999999;
    private static final String URL_FIRST_PART = "https://qa-internship.avito.com/api/1/";
    private static final String URL_SECOND_PART = "/item";

    @Test
    public void getExistingAdsBySellerIdTest() {

        int sellerId = MIN_SELLER_ID_BOUND;

        Response response = given()
                .contentType("application/json")
                .when()
                .get(URL_FIRST_PART + sellerId + URL_SECOND_PART)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(200));

        List<AdResponseDto> responses = response.jsonPath().getList(".", AdResponseDto.class);

        responses.forEach(ad -> assertEquals(sellerId, ad.getSellerId()));
    }

    @Test
    public void getEmptyAdListBySellerIdTest() {

        int sellerId = 917699;

        Response response = given()
                .contentType("application/json")
                .when()
                .get(URL_FIRST_PART + sellerId + URL_SECOND_PART)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(200));

        List<AdResponseDto> responses = response.jsonPath().getList(".", AdResponseDto.class);

        assertTrue(responses.isEmpty());

    }

    @Test
    public void getAdsByNegativeInvalidSellerIdTest() {

        int sellerId = -999;

        Response response = given()
                .contentType("application/json")
                .when()
                .get(URL_FIRST_PART + sellerId + URL_SECOND_PART)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(400));

    }

    @Test
    public void getAdsByInvalidSellerIdTest() {

        String sellerId = "%09";

        Response response = given()
                .contentType("application/json")
                .when()
                .get(URL_FIRST_PART + sellerId + URL_SECOND_PART)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(400));

    }

    @Test
    public void getRequestIdempotencyTest() {
        int sellerId = MIN_SELLER_ID_BOUND;

        Response firstResponse = given()
                .contentType("application/json")
                .when()
                .get(URL_FIRST_PART + sellerId + URL_SECOND_PART)
                .then()
                .extract()
                .response();

        assertThat(firstResponse.statusCode(), is(200));
        List<AdResponseDto> firstResponses = firstResponse.jsonPath().getList(".", AdResponseDto.class);
        AdResponseDto firstAd = firstResponses.getFirst();

        Response secondResponse = given()
                .contentType("application/json")
                .when()
                .get(URL_FIRST_PART + sellerId + URL_SECOND_PART)
                .then()
                .extract()
                .response();

        assertThat(secondResponse.statusCode(), is(200));
        List<AdResponseDto> secondResponses = secondResponse.jsonPath().getList(".", AdResponseDto.class);
        AdResponseDto secondAd = secondResponses.getFirst();

        Response thirdResponse = given()
                .contentType("application/json")
                .when()
                .get(URL_FIRST_PART + sellerId + URL_SECOND_PART)
                .then()
                .extract()
                .response();

        assertThat(thirdResponse.statusCode(), is(200));
        List<AdResponseDto> thirdResponses = thirdResponse.jsonPath().getList(".", AdResponseDto.class);
        AdResponseDto thirdAd = thirdResponses.getFirst();

        assertThat(secondAd.getId(), is(firstAd.getId()));
        assertThat(secondAd.getName(), is(firstAd.getName()));
        assertThat(secondAd.getPrice(), is(firstAd.getPrice()));
        assertThat(secondAd.getSellerId(), is(firstAd.getSellerId()));
        assertThat(secondAd.getCreatedAt(), is(firstAd.getCreatedAt()));

        assertThat(thirdAd.getId(), is(secondAd.getId()));
        assertThat(thirdAd.getName(), is(secondAd.getName()));
        assertThat(thirdAd.getPrice(), is(secondAd.getPrice()));
        assertThat(thirdAd.getSellerId(), is(secondAd.getSellerId()));
        assertThat(thirdAd.getCreatedAt(), is(secondAd.getCreatedAt()));

        assertThat(secondAd.getStatistics().getLikes(), is(firstAd.getStatistics().getLikes()));
        assertThat(secondAd.getStatistics().getViewCount(), is(firstAd.getStatistics().getViewCount()));
        assertThat(secondAd.getStatistics().getContacts(), is(firstAd.getStatistics().getContacts()));

    }


    @Test
    public void xssInjectionTest() {
        String xssInjection = "<script>alert(1);</script>";

        Response response = given()
                .contentType("application/json")
                .when()
                .get(URL_FIRST_PART + xssInjection + URL_SECOND_PART)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(400));
    }
}
