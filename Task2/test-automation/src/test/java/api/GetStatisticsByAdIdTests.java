package api;

/*
 * Класс содержит автоматизированные тест-кейсы получения
 * статистики объявления по уникальному идентификатору объявления
 */

import api.dto.AdResponseDto;
import api.dto.StatisticResponseDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static api.util.CommonMethods.getIdFromStatus;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class GetStatisticsByAdIdTests {
    private static final String CREATE_URL = "https://qa-internship.avito.com/api/1/item";
    private static final String STATISTIC_URL = "https://qa-internship.avito.com/api/1/statistic/";

    @Test
    public void getExistingAdStatisticTest() {

        String adId = "9cdb83fd-5224-4aab-b4aa-c3e2f1e57d48";

        Response response = given()
                .contentType("application/json")
                .when()
                .get(STATISTIC_URL + adId)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(200));

        List<StatisticResponseDto> statsList = response.jsonPath().getList(".", StatisticResponseDto.class);
        StatisticResponseDto statistics = statsList.getFirst();

        assertThat(statistics.getLikes(), greaterThanOrEqualTo(0L));
        assertThat(statistics.getViewCount(), greaterThanOrEqualTo(0L));
        assertThat(statistics.getContacts(), greaterThanOrEqualTo(0L));
    }

    @Test
    public void getNonExistingAdStatisticTest() {

        String adId = "7b32ebda-b357-4101-bc4c-869a0ed32201";

        Response response = given()
                .contentType("application/json")
                .when()
                .get(STATISTIC_URL + adId)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(404));
    }

    @Test
    public void getAdStatisticByInvalidAdIdTest() {

        String adId = "9cdb83fd";

        Response response = given()
                .contentType("application/json")
                .when()
                .get(STATISTIC_URL + adId)
                .then()
                .extract()
                .response();

        assertThat(response.statusCode(), is(400));
    }


    @Test
    public void getAdStatisticWithDifferentOrderTest() {
        Long likes = 1L;
        Long viewCount = 2L;
        Long contacts = 3L;

        String status = createAd();
        String adId = getIdFromStatus(status);

        Response statisticResponse = given()
                .contentType("application/json")
                .when()
                .get(STATISTIC_URL + adId)
                .then()
                .extract()
                .response();

        assertThat(statisticResponse.statusCode(), is(200));

        List<StatisticResponseDto> statsList = statisticResponse.jsonPath().getList(".", StatisticResponseDto.class);
        StatisticResponseDto statistic = statsList.getFirst();

        assertThat(statistic.getLikes(), is(likes));
        assertThat(statistic.getViewCount(), is(viewCount));
        assertThat(statistic.getContacts(), is(contacts));
    }

    @Test
    public void getRequestIdempotencyTest() {

        String status = createAd();
        String adId = getIdFromStatus(status);

        Response firstResponse = given()
                .contentType("application/json")
                .when()
                .get(STATISTIC_URL + adId)
                .then()
                .extract()
                .response();

        assertThat(firstResponse.statusCode(), is(200));
        List<StatisticResponseDto> firstStatisticList = firstResponse.jsonPath()
                .getList(".", StatisticResponseDto.class);
        StatisticResponseDto firstStatistic = firstStatisticList.getFirst();

        Response secondResponse = given()
                .contentType("application/json")
                .when()
                .get(STATISTIC_URL + adId)
                .then()
                .extract()
                .response();

        assertThat(secondResponse.statusCode(), is(200));
        List<StatisticResponseDto> secondStatisticList = secondResponse.jsonPath()
                .getList(".", StatisticResponseDto.class);
        StatisticResponseDto secondStatistic = secondStatisticList.getFirst();

        Response thirdResponse = given()
                .contentType("application/json")
                .when()
                .get(STATISTIC_URL + adId)
                .then()
                .extract()
                .response();

        assertThat(thirdResponse.statusCode(), is(200));
        List<StatisticResponseDto> thirdStatisticList = thirdResponse.jsonPath()
                .getList(".", StatisticResponseDto.class);
        StatisticResponseDto thirdStatistic = thirdStatisticList.getFirst();

        assertThat(secondStatistic.getLikes(), is(firstStatistic.getLikes()));
        assertThat(secondStatistic.getViewCount(), is(firstStatistic.getViewCount()));
        assertThat(secondStatistic.getContacts(), is(firstStatistic.getContacts()));

        assertThat(thirdStatistic.getLikes(), is(secondStatistic.getLikes()));
        assertThat(thirdStatistic.getViewCount(), is(secondStatistic.getViewCount()));
        assertThat(thirdStatistic.getContacts(), is(secondStatistic.getContacts()));

    }



    private static String createAd() {
        int sellerId = 111114;
        String name = "auto";

        String requestBody = String.format("""
                {
                    "sellerID": %d,
                    "name": "%s",
                    "price": 200,
                    "statistics": {
                        "viewCount": 2,
                        "contacts": 3,
                        "likes": 1
                    }
                }
                """, sellerId, name);

        Response createResponse = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(CREATE_URL)
                .then()
                .extract()
                .response();

        assertThat(createResponse.statusCode(), is(200));
        return createResponse.jsonPath().getString("status");
    }


}
