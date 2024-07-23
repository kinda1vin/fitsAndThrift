package com.sp.fitsandthrift;

import android.net.Uri;

public class Item {
    private String itemdesc;
    private Uri imageUri;

    public Item(String itemdesc, Uri imageUri) {
        this.itemdesc = itemdesc;
        this.imageUri = imageUri;
    }

    public String getItemdesc() {
        return itemdesc;
    }

    public void setItemdesc(String itemdesc){
        this.itemdesc = itemdesc;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri){
        this.imageUri = imageUri;
    }
}