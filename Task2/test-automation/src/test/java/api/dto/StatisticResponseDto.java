package api.dto;

/*
 * Класс содержит DTO для работы с полями статистики. Используется для сериализации
 */

public class StatisticResponseDto {
  private Long contacts;
  private Long viewCount;
  private Long likes;

  public StatisticResponseDto() {}

  public StatisticResponseDto(Long contacts, Long viewCount, Long likes) {
    this.contacts = contacts;
    this.viewCount = viewCount;
    this.likes = likes;
  }

  public StatisticResponseDto(int contacts, int viewCount, int likes) {
    this.contacts = (long) contacts;
    this.viewCount = (long) viewCount;
    this.likes = (long) likes;
  }

  public Long getContacts() {
    return contacts;
  }

  public Long getViewCount() {
    return viewCount;
  }

  public Long getLikes() {
    return likes;
  }
}
