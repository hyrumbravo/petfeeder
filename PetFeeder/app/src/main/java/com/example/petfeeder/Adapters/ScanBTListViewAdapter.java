package com.example.petfeeder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petfeeder.Database.DatabaseHelper;
import com.example.petfeeder.R;
import com.example.petfeeder.Pages.ScanBluetooth.DeviceObject;

import java.util.List;

public class ScanBTListViewAdapter extends RecyclerView.Adapter<ScanBTListViewAdapter.ScanBTListViewHolder> {

    private Context context;
    private List<DeviceObject> DeviceScanList;
    private OnItemClickListener clickListener;
    private DatabaseHelper databaseHelper;

    public ScanBTListViewAdapter(Context context, List<DeviceObject> DeviceScanList) {
        this.context = context;
        this.DeviceScanList = DeviceScanList;
    }

    public interface OnItemClickListener {
        void onItemClick(DeviceObject deviceObject);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ScanBTListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bluetooth_listview_item, parent, false);
        return new ScanBTListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanBTListViewHolder holder, int position) {
        boolean isPaired = DeviceScanList.get(position).getPaired();
        if (DeviceScanList.get(position).getName() != null) {
            holder.BluetoothName.setText(DeviceScanList.get(position).getName());
        } else {
            holder.BluetoothName.setText("Bluetooth Device" + (isPaired? "":" (Unpaired)"));
        }
        holder.MACAddress.setText(DeviceScanList.get(position).getMacAddress());
        holder.Indicator.setBackgroundColor(context.getResources().getColor(R.color.lightGray));
    }

    @Override
    public int getItemCount() { return DeviceScanList.size(); }

    public class ScanBTListViewHolder extends RecyclerView.ViewHolder {
        TextView BluetoothName, MACAddress;
        ImageView Indicator;

        public ScanBTListViewHolder(View itemView) {
            super(itemView);

            databaseHelper = new DatabaseHelper(context);

            BluetoothName = itemView.findViewById(R.id.BluetoothName);
            MACAddress = itemView.findViewById(R.id.MAC_Address);
            Indicator = itemView.findViewById(R.id.indicator);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (clickListener != null) {
                        clickListener.onItemClick(DeviceScanList.get(position));
                    }
                }
            });
        }
    }
}