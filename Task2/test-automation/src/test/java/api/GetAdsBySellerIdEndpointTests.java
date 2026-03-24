package api;

/*
 * Класс содержит автоматизированные тест-кейсы получения
 * объявлений по идентификатору продавца (sellerId)
 */

import api.dto.AdResponseDto;
import api.util.CommonMethods;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("API тестирование")
@Feature("Получение объявлений по идентификатору продавца")
public class GetAdsBySellerIdEndpointTests {

    /*
     * Позитивные проверки
     */

    @Test
    @DisplayName("Получение существующих объявлений по существующему ID продавца")
    @Description("Проверка, что по запросу возвращается список объявлений со статус-кодом 200")
    @Severity(SeverityLevel.BLOCKER)
    public void getExistingAdsBySellerIdTest() {

        int sellerId = Constants.MAX_SELLER_ID;

        Response response = CommonMethods.sendGetAdsRequest(sellerId);

        CommonMethods.checkStatusCode(response, HttpStatus.SC_OK);

        List<AdResponseDto> responses = response.jsonPath().getList(".", AdResponseDto.class);

        responses.forEach(ad -> assertEquals(sellerId, ad.getSellerId()));
    }

    @Test
    @DisplayName("Получение пустого списка объявлений по существующему ID продавца")
    @Description("Проверка, что по запросу возвращается пустой список объявлений со статус-кодом 200")
    @Severity(SeverityLevel.NORMAL)
    public void getEmptyAdListBySellerIdTest() {

        int sellerId = 917699;

        Response response = CommonMethods.sendGetAdsRequest(sellerId);

        CommonMethods.checkStatusCode(response, HttpStatus.SC_OK);

        List<AdResponseDto> responses = response.jsonPath().getList(".", AdResponseDto.class);

        assertTrue(responses.isEmpty());

    }

    /*
     * Негативные проверки
     */

    @Test
    @DisplayName("Получение списка объявлений по невалидному отрицательному ID продавца")
    @Description("Проверка, что по запросу с отрицательным ID возвращается статус-код 400")
    @Severity(SeverityLevel.NORMAL)
    public void getAdsByNegativeInvalidSellerIdTest() {

        int sellerId = -999;

        Response response = CommonMethods.sendGetAdsRequest(sellerId);

        CommonMethods.checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);

    }

    @Test
    @DisplayName("Получение списка объявлений по невалидному ID продавца")
    @Description("Проверка, что по запросу с невалидным ID возвращается статус-код 400")
    @Severity(SeverityLevel.NORMAL)
    public void getAdsByInvalidSellerIdTest() {

        String sellerId = "%09";

        Response response = given()
                .contentType("application/json")
                .when()
                .get(Constants.BASE_URL + "/" + sellerId + Constants.AD_URL_PART)
                .then()
                .extract()
                .response();

        CommonMethods.checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);

    }

    /*
     * Корнер-тесты
     */

    @Test
    @DisplayName("Идемпотентность запроса на получение объявлений по sellerId")
    @Description("Проверка, что повторные GET-запросы возвращают " +
            "одинаковый результат и не изменяют состояние системы")
    @Severity(SeverityLevel.NORMAL)
    public void getRequestIdempotencyTest() {
        int sellerId = Constants.MIN_SELLER_ID;

        Response firstResponse = CommonMethods.sendGetAdsRequest(sellerId);

        CommonMethods.checkStatusCode(firstResponse, HttpStatus.SC_OK);
        List<AdResponseDto> firstResponses = firstResponse.jsonPath().getList(".", AdResponseDto.class);
        AdResponseDto firstAd = firstResponses.getFirst();

        Response secondResponse = CommonMethods.sendGetAdsRequest(sellerId);

        CommonMethods.checkStatusCode(secondResponse, HttpStatus.SC_OK);
        List<AdResponseDto> secondResponses = secondResponse.jsonPath().getList(".", AdResponseDto.class);
        AdResponseDto secondAd = secondResponses.getFirst();

        Response thirdResponse = CommonMethods.sendGetAdsRequest(sellerId);

        CommonMethods.checkStatusCode(thirdResponse, HttpStatus.SC_OK);
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

    /*
     * Проверки безопасности
     */

    @Test
    @DisplayName("Получения объявления с XSS-атакой в поле sellerId")
    @Description("Проверка, что внедрение XSS-атаки в поле sellerId не " +
            "вызовет необычного поведения")
    @Severity(SeverityLevel.NORMAL)
    public void xssInjectionTest() {
        String xssInjection = "<script>alert(1);</script>";

        Response response = given()
                .contentType("application/json")
                .when()
                .get(Constants.BASE_URL + "/" + xssInjection + Constants.AD_URL_PART)
                .then()
                .extract()
                .response();

        CommonMethods.checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);
    }
}
