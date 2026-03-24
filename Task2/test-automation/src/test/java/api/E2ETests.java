package api;

/*
 * Класс содержит автоматизированные E2E тест-кейсы работы
 * с эндпоинтами микросервиса объявлений
 */

import api.dto.AdCreationRequestDto;
import api.dto.AdResponseDto;
import api.dto.StatisticResponseDto;
import api.util.CommonMethods;
import api.util.DataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static api.util.CommonMethods.getIdFromStatus;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Epic("API тестирование")
@Feature("E2E тестирование")
public class E2ETests {


    /*
     * Позитивные проверки
     */

    @Test
    @DisplayName("Создание объявления и получение по ID")
    @Description("Проверка стандартной последовательности действий при работе с корректно созданным объявлением")
    @Severity(SeverityLevel.BLOCKER)
    public void createAndGetAdByAdIdTest() {
        StatisticResponseDto statistics = new StatisticResponseDto(
                DataGenerator.generateValidStatisticsField(),
                DataGenerator.generateValidStatisticsField(),
                DataGenerator.generateValidStatisticsField()
        );

        AdCreationRequestDto data = new AdCreationRequestDto(
                DataGenerator.generateValidSellerId(),
                DataGenerator.generateValidName(),
                DataGenerator.generateValidPrice(),
                statistics
        );

        Response createResponse = CommonMethods.sendCreateRequest(data);
        CommonMethods.checkStatusCode(createResponse, HttpStatus.SC_OK);

        String status = createResponse.jsonPath().getString("status");
        String adId = getIdFromStatus(status);

        Response getResponse = CommonMethods.sendGetAdByIdRequest(adId);
        CommonMethods.checkStatusCode(getResponse, HttpStatus.SC_OK);

    }

    @Test
    @DisplayName("Создание объявления и получение в списке продавца")
    @Description("Проверка, что при создании объявления с валидными параметрами оно" +
            " будет добавлено в список объявлений указанного продавца")
    @Severity(SeverityLevel.CRITICAL)
    public void createAndGetAdBySellerIdTest() {
        Integer sellerId = DataGenerator.generateValidSellerId();

        StatisticResponseDto statistics = new StatisticResponseDto(
                DataGenerator.generateValidStatisticsField(),
                DataGenerator.generateValidStatisticsField(),
                DataGenerator.generateValidStatisticsField()
        );

        AdCreationRequestDto data = new AdCreationRequestDto(
                sellerId,
                DataGenerator.generateValidName(),
                DataGenerator.generateValidPrice(),
                statistics
        );

        Response createResponse = CommonMethods.sendCreateRequest(data);
        CommonMethods.checkStatusCode(createResponse, HttpStatus.SC_OK);

        Response getResponse = CommonMethods.sendGetAdsRequest(sellerId);
        CommonMethods.checkStatusCode(getResponse, HttpStatus.SC_OK);

        String status = createResponse.jsonPath().getString("status");
        String adId = getIdFromStatus(status);

        List<AdResponseDto> ads = getResponse.jsonPath().getList(".", AdResponseDto.class);

        boolean found = false;

        for (AdResponseDto ad : ads) {
            if (ad.getId().toString().equals(adId)) {
                found = true;
            }
        }
        assertThat("Созданное объявление найдено", found, is(true));
    }

    @Test
    @DisplayName("Создание нескольких объявлений и получение в списке продавца")
    @Description("Проверка, что при создании нескольких объявлений с валидными параметрами все " +
            "они будут добавлены в список объявлений указанного продавца")
    @Severity(SeverityLevel.NORMAL)
    public void createAndGetAdsBySellerIdTest() {
        Integer sellerId = DataGenerator.generateValidSellerId();

        int adsCount = 3;

        String[] adIds = new String[adsCount];

        for (int i = 0; i < adsCount; i++) {
            StatisticResponseDto statistics = new StatisticResponseDto(
                    DataGenerator.generateValidStatisticsField(),
                    DataGenerator.generateValidStatisticsField(),
                    DataGenerator.generateValidStatisticsField()
            );

            AdCreationRequestDto data = new AdCreationRequestDto(
                    sellerId,
                    DataGenerator.generateValidName(),
                    DataGenerator.generateValidPrice(),
                    statistics
            );

            Response createResponse = CommonMethods.sendCreateRequest(data);
            CommonMethods.checkStatusCode(createResponse, HttpStatus.SC_OK);

            String status = createResponse.jsonPath().getString("status");
            adIds[i] = getIdFromStatus(status);
        }

        Response getResponse = CommonMethods.sendGetAdsRequest(sellerId);
        CommonMethods.checkStatusCode(getResponse, HttpStatus.SC_OK);

        List<AdResponseDto> ads = getResponse.jsonPath().getList(".", AdResponseDto.class);

        boolean found = false;

        for (String expectedId : adIds) {
            found = ads.stream()
                    .anyMatch(ad -> ad.getId().toString().equals(expectedId));
            if (!found) {
                break;
            }
        }

        assertThat("Созданные объявления найдены", found, is(true));
    }

    /*
     * Негативные проверки
     */

    @Test
    @DisplayName("Попытка получения статистики несуществующего объявления")
    @Description("Проверка, что при замене одного символа в UUID успешно созданного объявления и" +
            " попытке получения статистики с таким ID приходит 404 Not Found")
    @Severity(SeverityLevel.NORMAL)
    public void createAdAndGetStatisticByNonExistingAdIdTest() {

        StatisticResponseDto statistics = new StatisticResponseDto(
                DataGenerator.generateValidStatisticsField(),
                DataGenerator.generateValidStatisticsField(),
                DataGenerator.generateValidStatisticsField()
        );

        AdCreationRequestDto data = new AdCreationRequestDto(
                DataGenerator.generateValidSellerId(),
                DataGenerator.generateValidName(),
                DataGenerator.generateValidPrice(),
                statistics
        );

        Response createResponse = CommonMethods.sendCreateRequest(data);
        CommonMethods.checkStatusCode(createResponse, HttpStatus.SC_OK);

        String status = createResponse.jsonPath().getString("status");
        String adId = getIdFromStatus(status);

        String nonExistingAdId = adId.substring(0, adId.length() - 1) + "f";

        Response getStatisticResponse = CommonMethods.sendGetStatisticsRequest(nonExistingAdId);
        CommonMethods.checkStatusCode(getStatisticResponse, HttpStatus.SC_NOT_FOUND);

    }
}
