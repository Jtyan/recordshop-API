package com.northcoders.recordshopAPI.dal.albumcover;


import java.util.List;

public class AlbumCoverModel {
    private List<AlbumResult> results;

    public List<AlbumResult> getResults() {
        return results;
    }

    public static class AlbumResult {
//        @("collectionName")
        private String albumName;

//        @SerializedName("artworkUrl100")
        private String artworkUrl;

        public String getAlbumName() {
            return albumName;
        }

        public String getArtworkUrl() {
            return artworkUrl;
        }
    }
}
