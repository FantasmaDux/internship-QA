package api;

/*
 * Класс содержит автоматизированные тест-кейсы получения
 * статистики объявления по уникальному идентификатору объявления
 */

import api.dto.StatisticResponseDto;
import api.util.CommonMethods;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static api.util.CommonMethods.getIdFromStatus;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

@Epic("API тестирование")
@Feature("Получение статистики объявления по идентификатору объявления")
public class GetStatisticsByAdIdTests {

    /*
     * Позитивные проверки
     */

    @Test
    @DisplayName("Получение статистики существующего объявления")
    @Description("Проверка, что для существующего объявления возвращается статистика со статусом 200")
    @Severity(SeverityLevel.BLOCKER)
    public void getExistingAdStatisticTest() {

        String adId = "9cdb83fd-5224-4aab-b4aa-c3e2f1e57d48";

        Response response = CommonMethods.sendGetStatisticsRequest(adId);

        CommonMethods.checkStatusCode(response, HttpStatus.SC_OK);

        List<StatisticResponseDto> statsList = response.jsonPath().getList(".", StatisticResponseDto.class);
        StatisticResponseDto statistics = statsList.getFirst();

        assertThat(statistics.getLikes(), greaterThanOrEqualTo(0L));
        assertThat(statistics.getViewCount(), greaterThanOrEqualTo(0L));
        assertThat(statistics.getContacts(), greaterThanOrEqualTo(0L));
    }

    /*
     * Негативные проверки
     */

    @Test
    @DisplayName("Получение статистики несуществующего объявления")
    @Description("Проверка, что для несуществующего объявления возвращается статус-код 404")
    @Severity(SeverityLevel.NORMAL)
    public void getNonExistingAdStatisticTest() {

        String adId = "7b32ebda-b357-4101-bc4c-869a0ed32201";

        Response response = CommonMethods.sendGetStatisticsRequest(adId);

        CommonMethods.checkStatusCode(response, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Получение статистики несуществующего объявления с невалидной формой ID")
    @Description("Проверка, что для невалидной формы ID будет возвращен статус-код 400")
    @Severity(SeverityLevel.NORMAL)
    public void getAdStatisticByInvalidAdIdTest() {

        String adId = "9cdb83fd";

        Response response = CommonMethods.sendGetStatisticsRequest(adId);

        CommonMethods.checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);
    }


    @Test
    @DisplayName("Получение статистики для объявления с измененным порядком полей")
    @Description("Проверка, что порядок полей статистики при создании объявления не повлияет" +
            " на ответ запроса получения статистики объявления")
    @Severity(SeverityLevel.NORMAL)
    public void getAdStatisticWithDifferentOrderTest() {
        Long likes = 1L;
        Long viewCount = 2L;
        Long contacts = 3L;

        String status = createAd();
        String adId = getIdFromStatus(status);

        Response response = CommonMethods.sendGetStatisticsRequest(adId);

        CommonMethods.checkStatusCode(response, HttpStatus.SC_OK);

        List<StatisticResponseDto> statsList = response.jsonPath().getList(".", StatisticResponseDto.class);
        StatisticResponseDto statistic = statsList.getFirst();

        assertThat(statistic.getLikes(), is(likes));
        assertThat(statistic.getViewCount(), is(viewCount));
        assertThat(statistic.getContacts(), is(contacts));
    }

    /*
     * Корнер-тесты
     */

    @Test
    @DisplayName("Проверка идемпотентности GET запроса")
    @Description("Проверка, что повторные GET-запросы возвращают " +
            "одинаковый результат и не изменяют состояние системы")
    @Severity(SeverityLevel.NORMAL)
    public void getRequestIdempotencyTest() {

        String status = createAd();
        String adId = getIdFromStatus(status);

        Response firstResponse = CommonMethods.sendGetStatisticsRequest(adId);

        CommonMethods.checkStatusCode(firstResponse, HttpStatus.SC_OK);

        List<StatisticResponseDto> firstStatisticList = firstResponse.jsonPath()
                .getList(".", StatisticResponseDto.class);
        StatisticResponseDto firstStatistic = firstStatisticList.getFirst();

        Response secondResponse = CommonMethods.sendGetStatisticsRequest(adId);

        CommonMethods.checkStatusCode(secondResponse, HttpStatus.SC_OK);

        List<StatisticResponseDto> secondStatisticList = secondResponse.jsonPath()
                .getList(".", StatisticResponseDto.class);
        StatisticResponseDto secondStatistic = secondStatisticList.getFirst();

        Response thirdResponse = CommonMethods.sendGetStatisticsRequest(adId);

        CommonMethods.checkStatusCode(thirdResponse, HttpStatus.SC_OK);

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
                .post(Constants.FULL_ITEM_URL)
                .then()
                .extract()
                .response();

        CommonMethods.checkStatusCode(createResponse, HttpStatus.SC_OK);
        return createResponse.jsonPath().getString("status");
    }


}
