package api;

/*
 * Класс содержит автоматизированные тест-кейсы получения
 * объявления по уникальному идентификатору объявления
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("API тестирование")
@Feature("Получение объявления по идентификатору объявления")
public class GetAdByIdEndpointTests {

    /*
     * Позитивные проверки
     */

    @Test
    @DisplayName("Получение существующего объявления")
    @Description("Проверка, что для по запросу возвращает существующее объявление со статус-кодом 200")
    @Severity(SeverityLevel.BLOCKER)
    public void getExistingAdByIdTest() {

        String id = "556df215-62ce-4616-ba4f-53aeac9d6673";

        Response response = CommonMethods.sendGetAdByIdRequest(id);

        CommonMethods.checkStatusCode(response, HttpStatus.SC_OK);

        List<AdResponseDto> ads = response.jsonPath().getList(".", AdResponseDto.class);
        AdResponseDto ad = ads.getFirst();
        assertEquals(id.toLowerCase(), ad.getId().toString());

    }


    @Test
    @DisplayName("Получение существующего объявления с ID в верхнем регистре")
    @Description("Проверка, что для на получение существующего объявления не влияет регистр ID")
    @Severity(SeverityLevel.MINOR)
    public void getExistingAdByUpperCaseIdTest() {

        String id = "556DF215-62CE-4616-BA4F-53AEAC9D6673";

        Response response = CommonMethods.sendGetAdByIdRequest(id);

        CommonMethods.checkStatusCode(response, HttpStatus.SC_OK);

        List<AdResponseDto> ads = response.jsonPath().getList(".", AdResponseDto.class);
        AdResponseDto ad = ads.getFirst();
        assertThat(ad.getId().toString().equalsIgnoreCase(id), is(true));
    }

    /*
     * Негативные проверки
     */

    @Test
    @DisplayName("Получение несуществующего объявления с валидной формой ID")
    @Description("Проверка, что при попытке получить несуществующее объявление возвращается статус-код 404")
    @Severity(SeverityLevel.NORMAL)
    public void getNonExistingAdByIdTest() {

        String id = "556df215-62ce-4616-ba4f-53aeac9d6613";

        Response response = CommonMethods.sendGetAdByIdRequest(id);

        CommonMethods.checkStatusCode(response, HttpStatus.SC_NOT_FOUND);

    }

    @Test
    @DisplayName("Получение объявления с невалидной формой ID")
    @Description("Проверка, что при попытке получить объявление с невалидной формой ID" +
            " возвращается статус-код 400")
    @Severity(SeverityLevel.NORMAL)
    public void getAdByInvalidIdTest() {

        String id = "f";

        Response response = CommonMethods.sendGetAdByIdRequest(id);

        CommonMethods.checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);

    }


    @Test
    @DisplayName("Получение объявления c неправильным методом")
    @Description("Проверка, что при попытке получить объявление c методом POST " +
            "будет возвращен статус-код 405")
    @Severity(SeverityLevel.MINOR)
    public void getAdWithWrongMethodTest() {
        String id = "556DF215-62CE-4616-BA4F-53AEAC9D6673";

        Response response = given()
                .contentType("application/json")
                .when()
                .post(Constants.FULL_ITEM_URL + "/" + id)
                .then()
                .extract()
                .response();

        CommonMethods.checkStatusCode(response, HttpStatus.SC_METHOD_NOT_ALLOWED);
    }

    /*
     * Корнер-тесты
     */

    @Test
    @DisplayName("Идемпотентность запроса на получение объявления")
    @Description("Проверка, что повторные GET-запросы возвращают " +
            "одинаковый результат и не изменяют состояние системы")
    @Severity(SeverityLevel.NORMAL)
    public void getRequestIdempotencyTest() {
        String id = "a93d5c57-2fe2-4d4f-8094-b5812ffc52d0";

        Response firstResponse = CommonMethods.sendGetAdByIdRequest(id);

        CommonMethods.checkStatusCode(firstResponse, HttpStatus.SC_OK);
        String firstBody = firstResponse.asString();

        Response secondResponse = CommonMethods.sendGetAdByIdRequest(id);

        CommonMethods.checkStatusCode(secondResponse, HttpStatus.SC_OK);
        String secondBody = secondResponse.asString();

        Response thirdResponse = CommonMethods.sendGetAdByIdRequest(id);

        CommonMethods.checkStatusCode(thirdResponse, HttpStatus.SC_OK);
        String thirdBody = thirdResponse.asString();

        assertThat(secondBody, equalTo(firstBody));
        assertThat(thirdBody, equalTo(secondBody));

    }
}
