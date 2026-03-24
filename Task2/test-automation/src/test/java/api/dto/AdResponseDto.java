package api.dto;

import java.util.UUID;

public class AdResponseDto {
    private String createdAt;
    private UUID id;
    private String name;
    private Long price;
    private Integer sellerId;
    private StatisticResponseDto statistics;

    public AdResponseDto(String createdAt, UUID id, String name, Long price, int sellerID, StatisticResponseDto statistics) {
        this.createdAt = createdAt;
        this.id = id;
        this.name = name;
        this.price = price;
        this.sellerId = sellerID;
        this.statistics = statistics;
    }

    public AdResponseDto() {
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getPrice() {
        return price;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public StatisticResponseDto getStatistics() {
        return statistics;
    }
}
