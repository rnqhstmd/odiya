package org.example.odiya.map.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MapSearchResponse {

    private List<Document> documents;
    private Meta meta;

    @Getter
    @Setter
    public static class Meta {
        @JsonProperty("is_end")
        private boolean isEnd;

        @JsonProperty("pageable_count")
        private int pageableCount;

        @JsonProperty("same_name")
        private SameName sameName;

        @JsonProperty("total_count")
        private int totalCount;
    }

    @Getter
    @Setter
    public static class SameName {
        private List<String> region;
        private String keyword;

        @JsonProperty("selected_region")
        private String selectedRegion;
    }

    @Getter
    @Setter
    public static class Document {
        private String addressName;
        private String categoryGroupCode;
        private String categoryGroupName;
        private String categoryName;
        private String distance;
        private String id;
        private String phone;
        private String placeName;
        private String placeUrl;
        private String roadAddressName;
        private String x;  // 경도
        private String y;  // 위도

        @JsonProperty("address_name")
        public void setAddressName(String addressName) {
            this.addressName = addressName;
        }

        @JsonProperty("category_group_code")
        public void setCategoryGroupCode(String categoryGroupCode) {
            this.categoryGroupCode = categoryGroupCode;
        }

        @JsonProperty("category_group_name")
        public void setCategoryGroupName(String categoryGroupName) {
            this.categoryGroupName = categoryGroupName;
        }

        @JsonProperty("category_name")
        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        @JsonProperty("place_name")
        public void setPlaceName(String placeName) {
            this.placeName = placeName;
        }

        @JsonProperty("place_url")
        public void setPlaceUrl(String placeUrl) {
            this.placeUrl = placeUrl;
        }

        @JsonProperty("road_address_name")
        public void setRoadAddressName(String roadAddressName) {
            this.roadAddressName = roadAddressName;
        }
    }
}
