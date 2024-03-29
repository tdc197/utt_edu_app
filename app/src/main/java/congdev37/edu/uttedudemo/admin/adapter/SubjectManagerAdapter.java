package congdev37.edu.uttedudemo.admin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import congdev37.edu.uttedudemo.R;
import congdev37.edu.uttedudemo.model.Subject;

public class SubjectManagerAdapter extends RecyclerView.Adapter<SubjectManagerAdapter.ViewHolder> {

    Context mContext;
    List<Subject> mDatasets;
    List<Subject> list;
    OnClick mOnClick;
    Unbinder unbinder;

    public void setOnClick(OnClick mOnClick) {
        this.mOnClick = mOnClick;
    }

    public SubjectManagerAdapter(List<Subject> mDatasets) {
        list = new ArrayList<>();
        this.mDatasets = mDatasets;
        list.addAll(mDatasets);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_subject, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.initView(mDatasets.get(i));
    }

    @Override
    public int getItemCount() {
        return mDatasets == null ? 0 : mDatasets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvSubject)
        TextView tvSubject;
        @BindView(R.id.rlEdit)
        RelativeLayout rlEdit;
        @BindView(R.id.lnContent)
        LinearLayout lnContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

        public void initView(final Subject subject) {
            tvSubject.setText(subject.getSubjectName());
            rlEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnClick!=null){
                        mOnClick.onItemClick(subject);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mOnClick!=null){
                        mOnClick.longclick(subject);
                    }
                    return false;
                }
            });
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mDatasets.clear();
        if (charText.length() == 0) {
            mDatasets.addAll(list);
        } else {
            for (Subject model : list) {
                if (charText.length() != 0 && model.getSubjectCode() != null && model.getSubjectCode().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mDatasets.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    public interface OnClick {
        void onItemClick(Subject subject);

        void longclick(Subject subject);
    }
}