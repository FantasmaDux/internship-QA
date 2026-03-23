package api;

public class AdCreationDto {
    private final int sellerID;
    private final String name;
    private final int price;
    private final Statistics statistics;

    public static class Statistics{
        private final int contacts;
        private final int viewCount;
        private final int likes;

        public Statistics(int contacts, int viewCount, int likes) {
            this.contacts = contacts;
            this.viewCount = viewCount;
            this.likes = likes;
        }

        public int getContacts() {
            return contacts;
        }

        public int getViewCount() {
            return viewCount;
        }

        public int getLikes() {
            return likes;
        }
    }

    public AdCreationDto(Integer sellerID, String name, int price, Statistics statistics) {
        this.sellerID = sellerID;
        this.name = name;
        this.price = price;
        this.statistics = statistics;
    }

    public int getSellerID() {
        return sellerID;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public Statistics getStatistics() {
        return statistics;
    }
}

