package fatalisa.learning.com.smartminimarket;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class OrderActivitiy extends AppCompatActivity
{
    int tempvalue, max_data, flagorder;
    String bar_type, cart_list;
    ImageView imageView;
    TextView textView,textView3;
    Spinner jumlahView;
    ProgressDialog progressDialog;
    Intent i;
    static String ip_addr = StartActivity.ip_addr;
    String[] id,nama,jumlah,link,price;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_activitiy);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        cart_list = extras.getString("lists");
        bar_type = extras.getString("type");
        flagorder = extras.getInt("flag");
        Log.d("cart",cart_list);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        textView3 = findViewById(R.id.textView3);
        jumlahView = findViewById(R.id.spinner3);
        if (flagorder == 1)
            i = new Intent(this, CartActivity.class);
        else
            i = new Intent(this, BarcodeCaptureActivity.class);
        max_data = 0;
        tempvalue = 0;

        id = new String[4];
        nama = new String[4];
        jumlah = new String[4];
        link = new String[4];
        price = new String[4];

        Button b6 = findViewById(R.id.button6);
        if (flagorder == 1) b6.setText("Perbarui");

        new get_item().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class get_item extends AsyncTask<Void, Void, Void>
    {
        private JSONClass JC;
        private JSONObject jURL;
        private JSONArray jArray;
        String data_link;
        ProgressDialog pg;
        boolean sukses;
        @Override
        protected void onPreExecute()
        {
            sukses = false;
            data_link = ip_addr + "/smartmini/data/read_barang.php";
            pg = new ProgressDialog(OrderActivitiy.this);
            pg.setMessage("Loading");
            pg.setCancelable(false);
            pg.show();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            JC = new JSONClass();
            jURL = JC.getJSONFromUrl(data_link);
            try
            {
                if (jURL.getJSONArray("products")!=null)
                {
                    jArray = jURL.getJSONArray("products");
                    sukses = true;

                if (jArray.length() > 0)
                {

                    for (int i = 0; i < jArray.length(); i++)
                    {
                        JSONObject job = jArray.getJSONObject(i);
                        id[i] = job.getString("id");
                        nama[i] = job.getString("nama");
                        jumlah[i] = job.getString("jumlah");
                        link[i] = job.getString("url");
                        price[i] = job.getString("harga");
                    }
                }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                exit_procedure();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            if (!sukses)
            {
                Toast.makeText(OrderActivitiy.this,"Gagal. Coba Lagi.",Toast.LENGTH_LONG).show();
                exit_procedure();
            }
            else
            {
                int check_count = 0;
                String[] jumx = null;

                while (true)
                {
                    if (id[check_count].equals(bar_type))
                    {
                        if (flagorder == 1)
                        {
                            int coode = Integer.valueOf(bar_type.concat(" ").substring(6,7))-1;
                            String jumlahtemp = String.valueOf(cart_list).concat(" ");
                            for (int asd = 0; asd < 4; asd++)
                            {
                                if (coode == asd)
                                {
                                    jumlah[check_count] = jumlahtemp.substring(asd,asd+1);
                                    //Log.d(jumlahtemp.substring(asd,asd+1),String.valueOf(coode));
                                    break;
                                }
                            }
                        }

                        jumx = new String[Integer.valueOf(jumlah[check_count])+1];

                        for (int count = 0; count <= Integer.valueOf(jumlah[check_count]); count++) {
                            jumx[count] = String.valueOf(count);
                        }
                        textView.setText(nama[check_count]);
                        String printharga = "Harga satuan : " + String.valueOf(Integer.valueOf(price[check_count]));
                        textView3.setText(printharga);
                        break;
                    } else if (check_count <= 3)
                        check_count++;
                    else break;
                }
                assert jumx != null;
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(OrderActivitiy.this, android.R.layout.simple_spinner_item, jumx);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                jumlahView.setAdapter(spinnerArrayAdapter);
                pg.dismiss();
                new DownloadImageTask(imageView).execute(ip_addr + link[check_count]);
            }
        }
    }

    int nilaikirim;
    public void order_button(View v)
    {
        int jumlah = Integer.valueOf(jumlahView.getSelectedItem().toString());
        Log.d("jumlah",String.valueOf(jumlah));
        int coode = Integer.valueOf(bar_type.substring(6,7));
        Log.d("code",String.valueOf(coode));
        //String template = "";
        cart_list.concat("'");
        Log.d("cart",cart_list);
        int arraybartemp[] = new int[4];
        for (int q = 0; q < 4; q++)
        {
            arraybartemp[q] = Integer.valueOf(cart_list.substring(q,q+1));
            Log.d("item",String.valueOf(arraybartemp[q]));
        }
        cart_list = "";
        for (int q = 0; q < 4; q++)
        {
            if (q == coode-1)
            {
                if (flagorder == 0)
                {
                    nilaikirim = jumlah;
                    arraybartemp[q] += jumlah;
                }
                else
                {
                    nilaikirim = arraybartemp[q] - jumlah;
                    arraybartemp[q] = jumlah;
                }
            }
            Log.d("arr",String.valueOf(arraybartemp[q]));
            //cart_list.concat(String.valueOf(arraybartemp[q]));
            cart_list += String.valueOf(arraybartemp[q]);
        }
        //cart_list.replace(":","");
        Log.d("cart",cart_list);
        new sending().execute();
        /*for (int q = 0;q < cart_list.length()-1; q++)
        {
            if (q == coode-1)
            {
                int nilaibaru;
                String vvv = cart_list.substring(q,q+1);
                if (cart_list.equals("0000"))
                {
                    template.concat(String.valueOf(jumlah));
                }
                else
                {                                                   //  nilai baru
                    if (jumlah == 0)                                //    v
                        nilaibaru = 0;                              //  0000
                    else if (flagorder == 0)                        //
                        nilaibaru = Integer.valueOf(vvv) + jumlah;  //
                    else                                            //
                        nilaibaru = jumlah;                         //

                    template += String.valueOf(nilaibaru);
                }

                if (flagorder == 1)
                {
                    if (jumlah == 0)
                        nilaikirim = Integer.valueOf(vvv);
                    else
                        nilaikirim = Integer.valueOf(vvv) - jumlah;
                }
                else
                    nilaikirim = jumlah;
            }
            else
                template += cart_list.substring(q,q+1);
            Log.d("template",template);
        }*/
    }

    public void cancel_button(View v)
    {
        exit_procedure();
    }

    void exit_procedure()
    {
        i.putExtra("lists", cart_list);
        this.startActivity(i);
        this.finish();
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
    {
        ProgressDialog progressDialog;
        ImageView bmImage;
        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(OrderActivitiy.this);
            progressDialog.setMessage("Loading");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected Bitmap doInBackground(String... urls)
        {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result)
        {
            bmImage.setImageBitmap(result);
            bmImage.invalidate();bmImage.refreshDrawableState();
            progressDialog.dismiss();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class sending extends AsyncTask<Void, Void, Void>
    {
        private JSONClass JC;
        String data_link;
        ProgressDialog pg;
        @Override
        protected Void doInBackground(Void... voids)
        {
            data_link = ip_addr + "/smartmini/data/update_barang.php?id=" + bar_type +"&jumlah=" + nilaikirim + "&flag=";
            if (flagorder == 1)
                data_link += "+";
            else
                data_link += "-";
            JC = new JSONClass();
            JC.getJSONFromUrl(data_link);
            return null;
        }

        protected void onPreExecute()
        {
            pg = new ProgressDialog(OrderActivitiy.this);
            pg.setMessage("Loading");
            pg.setCancelable(false);
            pg.show();
        }

        protected void onPostExecute(Void result)
        {
            pg.dismiss();
            exit_procedure();
        }
    }
}