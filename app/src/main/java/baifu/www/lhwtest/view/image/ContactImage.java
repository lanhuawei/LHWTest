package baifu.www.lhwtest.view.image;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.InputStream;

/**
 * Created by Ivan.L on 2017/7/28.
 * 在SmartImageView中运用,连接图片
 */

public class ContactImage implements SmartImage {
    private long contactId;

    public ContactImage(long contactId) {
        this.contactId = contactId;
    }

    @Override
    public Bitmap getBitmap(Context context) {
        Bitmap bitmap = null;
        ContentResolver contentResolver = context.getContentResolver();

        try {
            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri);
            if(input != null) {
                bitmap = BitmapFactory.decodeStream(input);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
