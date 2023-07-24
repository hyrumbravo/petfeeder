package com.example.petfeeder.DataSharing;

import android.content.Intent;
import android.net.Uri;

import com.example.petfeeder.Constants;

import java.util.ArrayList;

public interface PetProviderConstants {
    Uri CONTENT_URI_PETS = Uri.parse("content://com.example.petfinder/"+ Constants.TABLE_NAME);
    Uri CONTENT_URI_STEP = Uri.parse("content://com.example.petfinder/"+ Constants.TABLE_NAME2);
}
