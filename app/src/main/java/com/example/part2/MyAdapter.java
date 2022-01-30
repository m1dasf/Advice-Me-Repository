package com.example.part2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    Context context;
    private ArrayList<Cases> list;

    private RecycleViewClickListener clickListener;

    public MyAdapter(Context context, ArrayList<Cases> list, RecycleViewClickListener clickListener) {
        this.context = context;
        this.list = list;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.case_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cases oneCase = list.get(position);
        holder.itemTitle.setText(oneCase.getTitle());
        holder.itemDescription.setText(oneCase.getDescription());
        holder.itemCategory.setText(oneCase.getCategory());
        holder.itemTimeLeft.setText(findDifference(oneCase.dueDate));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView itemTitle;
        TextView itemDescription;
        TextView itemCategory;
        TextView itemTimeLeft;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemCaseTitle);
            itemDescription = itemView.findViewById(R.id.itemCaseDescription);
            itemCategory = itemView.findViewById(R.id.itemCaseCategory);
            itemTimeLeft = itemView.findViewById(R.id.itemCaseTimeLeft);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.click(view, getAdapterPosition());
        }
    }

    public interface RecycleViewClickListener{
        void click(View view, int position);
    }

    private static String findDifference(String dueDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        Date currentDate = new Date();
        Calendar dueDateCal = Calendar.getInstance();
        try {
            dueDateCal.setTime(simpleDateFormat.parse(dueDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date dueDateDate = dueDateCal.getTime();

        long difference = dueDateDate.getTime() - currentDate.getTime();
        long differenceInDays = TimeUnit.MILLISECONDS.toDays(difference);
        long differenceInHours = TimeUnit.MILLISECONDS.toHours(difference) % 24;
        long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(difference) % 60;

        String remainingTime = "";
        if (differenceInDays > 0) {
            remainingTime = differenceInDays + "d. ";
        }
        if (differenceInHours > 0) {
            remainingTime = remainingTime + differenceInHours + "h. ";
        }
        if (differenceInMinutes > 0) {
            remainingTime = remainingTime + differenceInMinutes + "m. ";
        } else {
            remainingTime = "Expired";
        }

        return remainingTime;
    }
}
