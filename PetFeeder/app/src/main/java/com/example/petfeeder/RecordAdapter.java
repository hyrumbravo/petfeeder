package com.example.petfeeder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordHolder>{

    private Context context;
    private ArrayList<RecordModel> recordsList;
    private Boolean isListed;
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
            //TODO: WHAT HAPPENS TO THIS NEW PET.
            // SUGGESTION: Since you don't need any explicit ID for your database,
            // you can just put an option to import the pet where this unlisted pet
            // is saved to your database.
        };
        holder.itemView.setOnClickListener(isListed?listedClick:unlistedClick);
    }

    @Override
    public int getItemCount() {
        return recordsList.size();
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
