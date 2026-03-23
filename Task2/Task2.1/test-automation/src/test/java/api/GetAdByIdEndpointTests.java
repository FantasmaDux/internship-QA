package api;

import api.dto.AdResponseDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetAdByIdEndpointTests {
    private static final String URL = "https://qa-internship.avito.com/api/1/item/";

    @Test
    public void getExistingAdByIdTest() {

        String id = "556df215-62ce-4616-ba4f-53aeac9d6673";

        Response response = given()
                .contentType("application/json")
                .when()
                .get(URL + id)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(200));

        List<AdResponseDto> ads = response.jsonPath().getList(".", AdResponseDto.class);
        AdResponseDto ad = ads.getFirst();
        assertEquals(id.toLowerCase(), ad.getId().toString());

    }


    @Test
    public void getExistingAdByUpperCaseIdTest() {

        String id = "556DF215-62CE-4616-BA4F-53AEAC9D6673";

        Response response = given()
                .contentType("application/json")
                .when()
                .get(URL + id)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(200));

        List<AdResponseDto> ads = response.jsonPath().getList(".", AdResponseDto.class);
        AdResponseDto ad = ads.getFirst();
        assertThat(ad.getId(), is(id.toLowerCase()));
    }

    @Test
    public void getNonExistingAdByIdTest() {

        String id = "556df215-62ce-4616-ba4f-53aeac9d6613";

        Response response = given()
                .contentType("application/json")
                .when()
                .get(URL + id)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(404));

    }

    @Test
    public void getAdByInvalidIdTest() {

        String id = "f";

        Response response = given()
                .contentType("application/json")
                .when()
                .get(URL + id)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(400));

    }

    @Test
    public void getAdWithWrongMethodTest() {
        String id = "556DF215-62CE-4616-BA4F-53AEAC9D6673";

        Response response = given()
                .contentType("application/json")
                .when()
                .post(URL + id)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(405));
    }


    @Test
    public void getRequestIdempotencyTest() {
        String id = "a93d5c57-2fe2-4d4f-8094-b5812ffc52d0";

        Response firstResponse = given()
                .contentType("application/json")
                .when()
                .get(URL + id)
                .then()
                .extract()
                .response();

        assertThat(firstResponse.statusCode(), is(200));
        String firstBody = firstResponse.asString();

        Response secondResponse = given()
                .contentType("application/json")
                .when()
                .get(URL + id)
                .then()
                .extract()
                .response();

        assertThat(secondResponse.statusCode(), is(200));
        String secondBody = secondResponse.asString();

        Response thirdResponse = given()
                .contentType("application/json")
                .when()
                .get(URL + id)
                .then()
                .extract()
                .response();

        assertThat(thirdResponse.statusCode(), is(200));
        String thirdBody = thirdResponse.asString();

        assertThat(secondBody, equalTo(firstBody));
        assertThat(thirdBody, equalTo(secondBody));

    }
}
