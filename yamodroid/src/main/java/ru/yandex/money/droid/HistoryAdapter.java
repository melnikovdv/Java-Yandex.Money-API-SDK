package ru.yandex.money.droid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ru.yandex.money.api.enums.MoneyDirection;
import ru.yandex.money.api.response.util.Operation;

import java.text.DateFormat;
import java.util.List;

/**
 * @author dvmelnikov
 */

class HistoryAdapter extends ArrayAdapter {

    private final LayoutInflater inflater;
    ViewHolder holder;
    private final List<Operation> history;
    private LoadHistoryTask loadHistoryTask;

    private final Activity context;
    private String accessToken;
    private String clientId;

    public HistoryAdapter(Activity context, int textViewResourceId,
            List<Operation> history, String clientId, String accessToken) {
        super(context, textViewResourceId, history);

        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        this.history = history;
        this.accessToken = accessToken;
        this.clientId = clientId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView,
            ViewGroup parent) {
        if (position == getCount() - 1) {
            loadHistoryTask = new LoadHistoryTask(context, clientId, accessToken, this);
            if ((loadHistoryTask.getStatus() != AsyncTask.Status.RUNNING))
                loadHistoryTask.execute(getCount());
        }

        if (convertView == null) {
            convertView =
                    inflater.inflate(R.layout.ymd_history_item,
                            parent, false);

            holder = new ViewHolder();
            holder.header = (TextView) convertView.findViewById(R.id.header);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.sum = (TextView) convertView.findViewById(R.id.sum);
            holder.image = (ImageView) convertView.findViewById(R.id.img_direction);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Operation op = history.get(position);

        holder.header.setText(op.getTitle());
        String df = DateFormat.getDateInstance().format(op.getDatetime());
        holder.date.setText(df);
        String sum = op.getAmount().toString();
        Bitmap source = null;
        if (op.getDirection() == MoneyDirection.in)
////            sum = "+" + sum;
            source = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_plus);
        else
////            sum = "-" + sum;
            source = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_minus_g);
            
        holder.image.setImageBitmap(source);
        holder.sum.setText(sum);

        return convertView;
    }

    class ViewHolder {
        ImageView image;
        TextView header;
        TextView date;
        TextView sum;
    }

}
