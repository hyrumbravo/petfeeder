package com.example.petfeeder.Adapters;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petfeeder.Application.PetFeeder;
import com.example.petfeeder.DataSharing.PetProviderConstants;
import com.example.petfeeder.Database.Constants;
import com.example.petfeeder.Database.DatabaseHelper;
import com.example.petfeeder.Pages.DisplayPetDetails;
import com.example.petfeeder.R;
import com.example.petfeeder.Models.RecordModel;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordHolder>{

    private Context context;
    private ArrayList<RecordModel> recordsList;
    private Boolean isListed;
    AlertDialog dialog;
    DatabaseHelper databaseHelper;

    public RecordAdapter(Context context, ArrayList<RecordModel> recordsList, Boolean isListed) {
        this.context = context;
        this.recordsList = recordsList;
        this.isListed = isListed;
        databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public RecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false);
        return new RecordHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull RecordHolder holder, int position) {
        RecordModel model = recordsList.get(position);
        String id = model.getId();
        String petName = model.getName();
        String breed = model.getBreed();
        String sex = model.getSex();
        String image = model.getImage();

        holder.petName.setText(petName);
        holder.petBreed.setText(breed);

        if (sex.equalsIgnoreCase("female")) {
            holder.petSex.setText("F");
        } else if (sex.equalsIgnoreCase("male")) {
            holder.petSex.setText("M");
        } else {
            holder.petSex.setText(sex);
        }

        if (image.equals("null")) {
            holder.petPic.setImageResource(R.drawable.profile);
        } else {
            holder.petPic.setImageURI(Uri.parse(image));
        }

        View.OnClickListener listedClick = view -> {
            Intent intent = new Intent(context, DisplayPetDetails.class);
            intent.putExtra("RECORD_ID", id);
            context.startActivity(intent);
        };
        View.OnClickListener unlistedClick = view -> {
            askForConfirmation(model);
        };
        holder.itemView.setOnClickListener(isListed?listedClick:unlistedClick);
    }

    @Override
    public int getItemCount() {
        return recordsList.size();
    }


    private Long storeData(RecordModel model){
        String timestamp = ""+System.currentTimeMillis();
        long id = databaseHelper.storeData(
                ""+ model.getName(),
                ""+model.getBreed(),
                ""+model.getSex(),
                ""+model.getBirthdate(),
                ""+model.getAge(),
                ""+model.getWeight(),
                ""+model.getImage(),
                ""+model.getAddedtime(),
                ""+timestamp,
                model.getPetFinderID());
        Toast.makeText(context, "Record Added against Id: "+id, Toast.LENGTH_SHORT).show();

        ContentValues values = new ContentValues();

        values.put(Constants.COLUMN_ID, id);
        values.put(Constants.COLUMN_PETNAME, model.getName());
        values.put(Constants.COLUMN_BREED, model.getBreed());
        values.put(Constants.COLUMN_SEX, model.getSex());
        values.put(Constants.COLUMN_BIRTHDATE, model.getBirthdate());
        values.put(Constants.COLUMN_AGE, model.getAge());
        values.put(Constants.COLUMN_WEIGHT, model.getWeight());
        values.put(Constants.COLUMN_IMAGE, model.getImage());
        values.put(Constants.COLUMN_ADDED_TIMESTAMP, model.getAddedtime());
        values.put(Constants.COLUMN_UPDATED_TIMESTAMP, timestamp);
        values.put(Constants.COLUMN_PET_FINDER_ID, model.getPetFinderID());

        PetFeeder.getInstance().getContentResolver().update(PetProviderConstants.CONTENT_URI_PETS, values, null, null);
        return id;
    }

    private void askForConfirmation(RecordModel model){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add this pet?")
                .setIcon(R.drawable.tffi_logo)
                .setMessage("By clicking confirm, you are adding "+model.getName()+" to your Pet Feeder database.")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PositiveButtonAction(model);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NegativeButtonAction();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    private void PositiveButtonAction(RecordModel model) {
        Long Id = storeData(model);
        Intent intent = new Intent(context, DisplayPetDetails.class);
        intent.putExtra("RECORD_ID", String.valueOf(Id));
        context.startActivity(intent);
    }
    private void NegativeButtonAction() {
        dialog.cancel();
    }
    class RecordHolder extends RecyclerView.ViewHolder{

        ImageView petPic;
        TextView petName, petBreed, petSex;

        public RecordHolder(@NonNull View itemView) {
            super(itemView);

            petPic = itemView.findViewById(R.id.petProfile);
            petName = itemView.findViewById(R.id.namePet);
            petBreed = itemView.findViewById(R.id.petbreed);
            petSex = itemView.findViewById(R.id.sexPet);
        }
    }
}
