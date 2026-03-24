package api.util;

/*
 * Класс содержит общие для нескольких наборов тестов методы
 */

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import api.Constants;
import api.dto.AdCreationRequestDto;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Random;

public final class CommonMethods {

  private CommonMethods() {}

  public static String getIdFromStatus(String status) {
    return status.replace("Сохранили объявление - ", "");
  }

  @Step("Проверка статус-кода {expectedCode}")
  public static void checkStatusCode(Response response, int expectedCode) {
    assertThat(
        String.format("Статус-код должен быть %d", expectedCode),
        response.statusCode(),
        is(expectedCode));
  }

  public static Integer generateSellerId() {
    return new Random().nextInt(Constants.MIN_SELLER_ID, Constants.MAX_SELLER_ID + 1);
  }

  @Step("Отправка POST запроса на создание объявления")
  public static Response sendCreateRequest(AdCreationRequestDto data) {
    return given()
        .contentType("application/json")
        .body(data)
        .when()
        .post(Constants.FULL_ITEM_URL)
        .then()
        .extract()
        .response();
  }

  @Step("Отправка GET запроса для sellerId = {sellerId}")
  public static Response sendGetAdsRequest(int sellerId) {
    return given()
        .contentType("application/json")
        .when()
        .get(String.format(Constants.GET_ADS_BY_SELLER_URL, sellerId))
        .then()
        .extract()
        .response();
  }

  @Step("Отправка GET запроса на получение статистики для adId = {adId}")
  public static Response sendGetStatisticsRequest(String adId) {
    return given()
        .contentType("application/json")
        .when()
        .get(Constants.FULL_STATISTIC_URL + "/" + adId)
        .then()
        .extract()
        .response();
  }

  @Step("Отправка GET запроса на получение объявления по id = {adId}")
  public static Response sendGetAdByIdRequest(String adId) {
    return given()
        .contentType("application/json")
        .when()
        .get(Constants.FULL_ITEM_URL + "/" + adId)
        .then()
        .extract()
        .response();
  }
}
