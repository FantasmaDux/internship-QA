package api;

/*
 * Класс содержит автоматизированные тест-кейсы создания
 * объявления
 */

import static api.util.CommonMethods.getIdFromStatus;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import api.dto.AdCreationRequestDto;
import api.dto.StatisticResponseDto;
import api.util.CommonMethods;
import io.qameta.allure.*;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Epic("API тестирование")
@Feature("Создание объявлений")
public class CreateAdEndpointTests {

  /*
   * Позитивные проверки
   */

  @Test
  @DisplayName("Создание объявления с валидными параметрами")
  @Description("Проверка, что объявление успешно создаётся с корректными параметрами")
  @Severity(SeverityLevel.BLOCKER)
  public void adCreationWithValidInputTest() {
    StatisticResponseDto statistics = new StatisticResponseDto(2, 18, 1);

    AdCreationRequestDto data =
        new AdCreationRequestDto(CommonMethods.generateSellerId(), "auto", 120000, statistics);

    Response response = CommonMethods.sendCreateRequest(data);

    CommonMethods.checkStatusCode(response, HttpStatus.SC_OK);
  }

  @Test
  @DisplayName(
      "Создание объявления с валидными параметрами и минимальным граничным значением sellerID")
  @Description(
      "Проверка, что объявление успешно создаётся с корректными параметрами "
          + "и минимальным граничным значением sellerID")
  @Severity(SeverityLevel.CRITICAL)
  public void adCreationWithValidInputAndMinSellerIdBoundTest() {
    StatisticResponseDto statistics = new StatisticResponseDto(2, 18, 1);

    AdCreationRequestDto data =
        new AdCreationRequestDto(Constants.MIN_SELLER_ID, "auto", 120000, statistics);

    Response response = CommonMethods.sendCreateRequest(data);

    CommonMethods.checkStatusCode(response, HttpStatus.SC_OK);
  }

  @Test
  @DisplayName(
      "Создание объявления с валидными параметрами и максимальным граничным значением sellerID")
  @Description(
      "Проверка, что объявление успешно создаётся"
          + " с корректными параметрами и максимальным граничным значением sellerID")
  @Severity(SeverityLevel.CRITICAL)
  public void adCreationWithValidInputAndMaxSellerIdBoundTest() {
    StatisticResponseDto statistics = new StatisticResponseDto(2, 18, 1);

    AdCreationRequestDto data =
        new AdCreationRequestDto(Constants.MAX_SELLER_ID, "auto", 120000, statistics);

    Response response = CommonMethods.sendCreateRequest(data);

    CommonMethods.checkStatusCode(response, HttpStatus.SC_OK);
  }

  @Test
  @DisplayName("Создание объявления c нулевым значением цены")
  @Description("Проверка, что объявление успешно создаётся при нулевом значении цены")
  @Severity(SeverityLevel.NORMAL)
  public void adCreationWithZeroPriceTest() {
    StatisticResponseDto statistics = new StatisticResponseDto(2, 18, 1);

    AdCreationRequestDto data =
        new AdCreationRequestDto(CommonMethods.generateSellerId(), "auto", 0, statistics);

    Response response = CommonMethods.sendCreateRequest(data);

    CommonMethods.checkStatusCode(response, HttpStatus.SC_OK);
  }

  @Test
  @DisplayName("Создание объявления c нулевым значением просмотров")
  @Description("Проверка, что объявление успешно создаётся при нулевом значении просмотров")
  @Severity(SeverityLevel.NORMAL)
  public void adCreationWithZeroViewCountTest() {
    StatisticResponseDto statistics = new StatisticResponseDto(2, 0, 1);

    AdCreationRequestDto data =
        new AdCreationRequestDto(CommonMethods.generateSellerId(), "auto", 1, statistics);

    Response response = CommonMethods.sendCreateRequest(data);

    CommonMethods.checkStatusCode(response, HttpStatus.SC_OK);
  }

  /*
   * Негативные проверки
   */

  @Test
  @DisplayName("Создание объявления c null значением sellerId")
  @Description("Проверка, что при попытке отправить null значение будет возвращен статус-код 400")
  @Severity(SeverityLevel.NORMAL)
  public void adCreationWithNullSellerIdTest() {
    StatisticResponseDto statistics = new StatisticResponseDto(2, 18, 1);

    AdCreationRequestDto data = new AdCreationRequestDto(null, "auto", 1, statistics);

    Response response = CommonMethods.sendCreateRequest(data);

    CommonMethods.checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);
  }

  @ParameterizedTest
  @CsvSource({"-1", "-100"})
  @DisplayName("Создание объявления с отрицательной ценой")
  @Description(
      "Проверка, что при попытке создать объявление "
          + "с отрицательной ценой будет возвращен статус-код 400")
  @Severity(SeverityLevel.NORMAL)
  public void createAdWithNegativePriceTest(Long invalidPrice) {
    StatisticResponseDto statistics = new StatisticResponseDto(2, 1, 1);
    AdCreationRequestDto data =
        new AdCreationRequestDto(
            CommonMethods.generateSellerId(), "auto", invalidPrice, statistics);
    Response response = CommonMethods.sendCreateRequest(data);

    CommonMethods.checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);
  }

  @ParameterizedTest
  @CsvSource({"-2, -18, -1", "-5, -10, -3"})
  @DisplayName("Создание объявления с отрицательными значениями полей статистики")
  @Description(
      "Проверка, что при попытке создать объявление с отрицательными полями "
          + "Likes, ViewCount, Contacts будет возвращен статус-код 400")
  @Severity(SeverityLevel.NORMAL)
  public void createAdWithNegativeStatisticsTest(
      int invalidLikes, int invalidViewCount, int invalidContacts) {
    StatisticResponseDto statistics =
        new StatisticResponseDto(invalidContacts, invalidViewCount, invalidLikes);

    AdCreationRequestDto data =
        new AdCreationRequestDto(CommonMethods.generateSellerId(), "clock", 200, statistics);
    Response response = CommonMethods.sendCreateRequest(data);

    CommonMethods.checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);
  }

  @ParameterizedTest
  @CsvSource({"-1", "-100"})
  @DisplayName("Создание объявления с отрицательными значением sellerId")
  @Description(
      "Проверка, что при попытке создать объявление с отрицательными sellerId "
          + "будет возвращен статус-код 400")
  @Severity(SeverityLevel.NORMAL)
  public void createAdWithNegativeSellerIdTest(int invalidSellerId) {
    StatisticResponseDto statistics = new StatisticResponseDto(2, 18, 1);
    AdCreationRequestDto data = new AdCreationRequestDto(invalidSellerId, "auto", 200, statistics);
    Response response = CommonMethods.sendCreateRequest(data);

    CommonMethods.checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  @DisplayName("Создание объявления с пустым названием")
  @Description(
      "Проверка, что при попытке создать объявление с пустым названием "
          + "будет возвращен статус-код 400")
  @Severity(SeverityLevel.NORMAL)
  public void adCreationWithBlankNameTest() {
    StatisticResponseDto statistics = new StatisticResponseDto(2, 18, 1);

    AdCreationRequestDto data =
        new AdCreationRequestDto(CommonMethods.generateSellerId(), "", 1, statistics);

    Response response = CommonMethods.sendCreateRequest(data);

    CommonMethods.checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  @DisplayName("Создание объявления c отсутствующим полем")
  @Description(
      "Проверка, что при попытке создать объявление c отсутствующим полем "
          + "будет возвращен статус-код 400")
  @Severity(SeverityLevel.NORMAL)
  public void adCreationWithMissingContactsFieldTest() {
    Map<String, Object> statistics = new HashMap<>();
    statistics.put("likes", 2);
    statistics.put("viewCount", 18);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("sellerID", CommonMethods.generateSellerId());
    requestBody.put("name", "clock");
    requestBody.put("price", 200);
    requestBody.put("statistics", statistics);

    Response response =
        given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post(Constants.FULL_ITEM_URL)
            .then()
            .extract()
            .response();

    CommonMethods.checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  @DisplayName("Создание объявления c неправильным методом")
  @Description(
      "Проверка, что при попытке создать объявление c методом GET "
          + "будет возвращен статус-код 405")
  @Severity(SeverityLevel.MINOR)
  public void adCreationWithWrongMethodTest() {
    StatisticResponseDto statistics = new StatisticResponseDto(2, 18, 1);

    AdCreationRequestDto data =
        new AdCreationRequestDto(CommonMethods.generateSellerId(), "clock", 200, statistics);

    Response response =
        given()
            .contentType("application/json")
            .when()
            .get(Constants.FULL_ITEM_URL)
            .then()
            .extract()
            .response();

    CommonMethods.checkStatusCode(response, HttpStatus.SC_METHOD_NOT_ALLOWED);
  }

  /*
   * Корнер-тесты
   */

  @Test
  @DisplayName("Идемпотентность запроса на добавление объявления")
  @Description(
      "Проверка, что при повторном создании объявления с одинаковыми данными "
          + "генерируется новый ID (объявление не дублируется, а создаётся новое)")
  @Severity(SeverityLevel.NORMAL)
  public void idempotencyTest() {

    StatisticResponseDto statistics = new StatisticResponseDto(2, 18, 1);
    AdCreationRequestDto data =
        new AdCreationRequestDto(CommonMethods.generateSellerId(), "clock", 200, statistics);

    Response firstResponse = CommonMethods.sendCreateRequest(data);

    CommonMethods.checkStatusCode(firstResponse, HttpStatus.SC_OK);
    String firstId = getIdFromStatus(firstResponse.jsonPath().getString("status"));

    Response secondResponse = CommonMethods.sendCreateRequest(data);

    CommonMethods.checkStatusCode(secondResponse, HttpStatus.SC_OK);
    String secondId = getIdFromStatus(secondResponse.jsonPath().getString("status"));

    Response thirdResponse = CommonMethods.sendCreateRequest(data);

    CommonMethods.checkStatusCode(thirdResponse, HttpStatus.SC_OK);
    String thirdId = getIdFromStatus(thirdResponse.jsonPath().getString("status"));

    assertThat(secondId, not(equalTo(firstId)));
    assertThat(thirdId, not(equalTo(secondId)));
  }

  @Test
  @DisplayName("Создание объявления с нестандартным сочетанием параметров")
  @Description("Проверка, что возможно создать объявление с нестандартными параметрами")
  @Severity(SeverityLevel.NORMAL)
  public void adCreationWithStrangeParamsTest() {
    StatisticResponseDto statistics = new StatisticResponseDto(1, 1, 9999);

    AdCreationRequestDto data =
        new AdCreationRequestDto(999999, "#$%_y!%^:", Integer.MAX_VALUE, statistics);

    Response response = CommonMethods.sendCreateRequest(data);

    CommonMethods.checkStatusCode(response, HttpStatus.SC_OK);
  }

  /*
   * Проверки безопасности
   */

  @Test
  @DisplayName("Создание объявления с вредоносным запросом")
  @Description(
      "Проверка, что внедрение вредоносной строки (sql injection) в поле name не "
          + "вызовет необычного поведения")
  @Severity(SeverityLevel.NORMAL)
  public void sqlInjectionTest() {
    String sqlInjection = "' or 1=1--";

    StatisticResponseDto statistics = new StatisticResponseDto(2, 18, 1);
    AdCreationRequestDto data =
        new AdCreationRequestDto(CommonMethods.generateSellerId(), sqlInjection, 200, statistics);

    Response response = CommonMethods.sendCreateRequest(data);

    CommonMethods.checkStatusCode(response, HttpStatus.SC_OK);
  }

  @Test
  @DisplayName("Создание объявления с XSS-атакой")
  @Description("Проверка, что внедрение XSS-атаки в поле name не " + "вызовет необычного поведения")
  @Severity(SeverityLevel.NORMAL)
  public void xssInjectionTest() {
    String xssInjection = "<script>alert('hi!')</script>";

    StatisticResponseDto statistics = new StatisticResponseDto(2, 18, 1);
    AdCreationRequestDto data =
        new AdCreationRequestDto(CommonMethods.generateSellerId(), xssInjection, 200, statistics);

    Response response = CommonMethods.sendCreateRequest(data);
    CommonMethods.checkStatusCode(response, HttpStatus.SC_OK);
    String xssProtection = response.getHeader("X-XSS-Protection");
    assertThat(xssProtection, is("1; mode=block"));
  }
}
