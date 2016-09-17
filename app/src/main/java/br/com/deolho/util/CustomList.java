package br.com.deolho.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;
import java.sql.SQLOutput;

import br.com.deolho.deolho.R;

public class CustomList extends ArrayAdapter<String> {

    private Activity context = null;
    private String[] web = null;
    private String[] web2 = null;
    private int[] imageId;
    public CustomList(Activity context,
                      String[] web, String[] web2, int[] imageId) {
        super(context, R.layout.list_single, web);

        this.context = context;
        this.web = web;
        this.web2 = web2;
        this.imageId = null;
        this.imageId = imageId;
    }
    
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.descricaoDespesa);
        TextView txtValor = (TextView) rowView.findViewById(R.id.valorDespesa);
//        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        txtTitle.setText(web[position]);
        txtValor.setText(web2[position]);

        return rowView;
    }
}