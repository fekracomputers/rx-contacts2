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


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.LongSparseArray;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static ir.mirrajabi.rxcontacts.ColumnMapper.mapDisplayName;
import static ir.mirrajabi.rxcontacts.ColumnMapper.mapEmail;
import static ir.mirrajabi.rxcontacts.ColumnMapper.mapInVisibleGroup;
import static ir.mirrajabi.rxcontacts.ColumnMapper.mapPhoneNumber;
import static ir.mirrajabi.rxcontacts.ColumnMapper.mapPhoto;
import static ir.mirrajabi.rxcontacts.ColumnMapper.mapStarred;
import static ir.mirrajabi.rxcontacts.ColumnMapper.mapThumbnail;


/**
 * Android contacts as rx observable.
 *
 * @author Ulrich Raab
 * @author MADNESS
 */
 public static class RxContacts {
     private static final String[] PROJECTION = {
             ContactsContract.Data.CONTACT_ID,
             ContactsContract.Data.DISPLAY_NAME_PRIMARY,
             ContactsContract.Data.STARRED,
             ContactsContract.Contacts.HAS_PHONE_NUMBER,
             ContactsContract.Data.PHOTO_URI,
             ContactsContract.Data.PHOTO_THUMBNAIL_URI,
             ContactsContract.Data.DATA1,
             ContactsContract.Data.MIMETYPE,
             ContactsContract.Data.IN_VISIBLE_GROUP
     };

     private ContentResolver mResolver;
     private Context context;
     private RxContacts(@NonNull Context context) {
         mResolver = context.getContentResolver();
         this.context = context;
     }

     public static Observable<Contact> fetch(@NonNull final Context context) {
         return Observable.create(new ObservableOnSubscribe<Contact>() {
             @Override
             public void subscribe(ObservableEmitter<Contact> e) throws Exception {
                 new RxContacts(context).fetch(e);
             }
         });
     }




     private void fetch(ObservableEmitter<Contact> emitter) {
         String countryCode = PreferenceUtility.open(context).getCountryCode();

         LongSparseArray<Contact> contacts = new LongSparseArray<>();
         Cursor cur = mResolver.query(ContactsContract.Contacts.CONTENT_URI,
                 null, null, null, null);
         if (cur != null && cur.getCount() > 0) {
             while (cur.moveToNext()) {
                 String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                 String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                 String photo = cur.getString(cur.getColumnIndex(ContactsContract.Data.PHOTO_URI));
                 if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                     Cursor pCur = mResolver.query(
                             ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                             null,
                             ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                             new String[]{id}, null);
                     String [] emails = null;
                     int index = 0;
                     while (pCur != null && pCur.getCount() > 0 && pCur.moveToNext()) {
                         if (emails == null) emails = new String[pCur.getCount()];
                         String email = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                         emails[index] = email;
                         index ++ ;
                     }


                     if (pCur != null) pCur.moveToFirst();
                     boolean isFirstTime = true;
                     while (pCur != null && pCur.getCount() > 0 && (isFirstTime || pCur.moveToNext())) {
                         isFirstTime = false;
                         String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                         phoneNo = Utility.cleanPhone(countryCode, phoneNo);

                         Contact contact = new Contact(Long.valueOf(id));
                         contact.setEmails(emails);
                         contact.setPhoneNumber(phoneNo);
                         contact.setDisplayName(name);
                         if (photo != null && !photo.isEmpty())
                             contact.setPhoto(Uri.parse(photo));
                         contacts.put(Long.valueOf(id), contact);
                         emitter.onNext(contact);

                     }
                     if (pCur != null) pCur.close();
                 }
             }
             cur.close();
             emitter.onComplete();
         } else if (cur != null)
             cur.close();
     }




     private Cursor createCursor() {
         return mResolver.query(
                 ContactsContract.Data.CONTENT_URI,
                 PROJECTION,
                 null,
                 null,
                 ContactsContract.Data.DISPLAY_NAME
         );
     }
 }
