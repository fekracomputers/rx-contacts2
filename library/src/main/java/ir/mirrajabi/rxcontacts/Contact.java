/*
 * Copyright (C) 2016 Ulrich Raab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ir.mirrajabi.rxcontacts;


import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Contact entity.
 * @author Ulrich Raab
 * @author MADNESS
 */

 public static class Contact implements Comparable<Contact> {


         @SerializedName("id")
         @Expose
         private final long id;
         private String notificationToken;
         private boolean isEnable = false;
         private boolean isSelected = false;
         @SerializedName("inVisibleGroup")
         @Expose
         private int mInVisibleGroup;
         @SerializedName("displayName")
         @Expose
         private String displayName;
         @SerializedName("starred")
         @Expose
         private boolean starred;
         @SerializedName("photo")
         @Expose
         private String photo;
         @SerializedName("thumbnail")
         @Expose
         private String thumbnail;
         @SerializedName("emails")
         @Expose
         private String[] emails ;
         @SerializedName("phoneNumber")
         @Expose
         private String phoneNumber ;

         Contact(long id) {
             this.id = id;
         }

         public boolean isSelected() {
             return isSelected;
         }

         public void setSelected(boolean selected) {
             isSelected = selected;
         }

         public boolean isEnable() {
             return isEnable;
         }

         public Contact setEnable(boolean enable) {
             isEnable = enable;
             return this;
         }

         public String getNotificationToken() {
             return notificationToken;
         }

         public void setNotificationToken(String notificationToken) {
             this.notificationToken = notificationToken;
         }

         public  Contact fromJson(String json) {
             Gson gson = new GsonBuilder()
                     .create();
             return gson.fromJson(json, Contact.class);
         }

         public long getId() {
             return id;
         }

         public int getInVisibleGroup() {
             return mInVisibleGroup;
         }

         public void setInVisibleGroup(int inVisibleGroup) {
             mInVisibleGroup = inVisibleGroup;
         }

         public String getDisplayName() {
             return displayName;
         }

         public void setDisplayName(String displayName) {
             this.displayName = displayName;
         }

         public boolean isStarred() {
             return starred;
         }

         public void setStarred(boolean starred) {
             this.starred = starred;
         }

         public Uri getPhoto() {
             if (photo == null) return null;
             else
                 return Uri.parse(photo);
         }

         public void setPhoto(Uri photo) {
             this.photo = photo.toString();
         }

         public Uri getThumbnail() {
             if (thumbnail == null) return null;
             else
                 return Uri.parse(thumbnail);
         }

         public void setThumbnail(Uri thumbnail) {
             this.thumbnail = thumbnail.toString();
         }

         public String[] getEmails() {
             return emails;
         }

         public void setEmails(String[] emails) {
             this.emails = emails;
         }

         public String getPhoneNumber() {
             return phoneNumber == null ? phoneNumber : phoneNumber.trim();
         }

         public void setPhoneNumber(String phoneNumber) {
             this.phoneNumber = phoneNumber;
         }

         @Override
         public int compareTo(@NonNull Contact other) {
             if (displayName != null && other.displayName != null)
                 return displayName.compareTo(other.displayName);
             else return -1;
         }

         @Override
         public int hashCode() {
             return (int) (id ^ (id >>> 32));
         }

         @Override
         public boolean equals(Object o) {
             if (this == o) {
                 return true;
             }
             if (o == null || getClass() != o.getClass()) {
                 return false;
             }
             Contact contact = (Contact) o;
             return id == contact.id;
         }

         public String getJson() {
             Gson gson = new GsonBuilder()
                     .create();
             return gson.toJson(this);
         }
     }
