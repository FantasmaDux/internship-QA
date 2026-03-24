package api.dto;

public class AdCreationRequestDto {
    private Integer sellerID;
    private String name;
    private Long price;
    private StatisticResponseDto statistics;

    public AdCreationRequestDto(Integer sellerID, String name, Long price, StatisticResponseDto statistics) {
        this.sellerID = sellerID;
        this.name = name;
        this.price = price;
        this.statistics = statistics;
    }

    public AdCreationRequestDto(Integer sellerID, String name, int price, StatisticResponseDto statistics) {
        this.sellerID = sellerID;
        this.name = name;
        this.price = (long) price;
        this.statistics = statistics;
    }


    public AdCreationRequestDto() {
    }

    public Integer getSellerID() {
        return sellerID;
    }

    public String getName() {
        return name;
    }

    public Long getPrice() {
        return price;
    }

    public StatisticResponseDto getStatistics() {
        return statistics;
    }
}

