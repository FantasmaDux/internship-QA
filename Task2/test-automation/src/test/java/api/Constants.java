package api;

/*
 * Класс содержит константы для API
 */

public final class Constants {
  private Constants() {}

  public static final String BASE_URL = "https://qa-internship.avito.com/api/1";
  public static final String AD_URL_PART = "/item";
  public static final String STATISTIC_URL_PART = "/statistic";

  public static final String FULL_ITEM_URL = BASE_URL + AD_URL_PART;
  public static final String FULL_STATISTIC_URL = BASE_URL + STATISTIC_URL_PART;
  public static final String GET_ADS_BY_SELLER_URL = BASE_URL + "/%d" + AD_URL_PART;

  public static final int MIN_SELLER_ID = 111111;
  public static final int MAX_SELLER_ID = 999999;

  public static final Long MIN_VALID_PRICE = 1L;
  public static final Long MAX_VALID_PRICE = (long) Integer.MAX_VALUE;

  public static final Long MIN_VALID_STATISTICS_FIELD = 1L;
  public static final Long MAX_VALID_STATISTICS_FIELD = (long) Integer.MAX_VALUE;
}
