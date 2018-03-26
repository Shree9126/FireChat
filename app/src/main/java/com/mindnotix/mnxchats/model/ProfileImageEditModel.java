package com.mindnotix.mnxchats.model;

/**
 * Created by Sridharan on 12/12/2017.
 */

public class ProfileImageEditModel {
    private int titleId;
        private int imageId;

        public ProfileImageEditModel(int titleId, int imageId) {
            this.titleId = titleId;
            this.imageId = imageId;
        }

        public int getTitleId() {
            return titleId;
        }

        public void setTitleId(int titleId) {
            this.titleId = titleId;
        }

        public int getImageId() {
            return imageId;
        }

        public void setImageId(int imageId) {
            this.imageId = imageId;
        }
}

