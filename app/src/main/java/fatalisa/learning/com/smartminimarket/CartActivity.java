package fatalisa.learning.com.smartminimarket;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity
{
    String cart_list;
    ListView listView;
    ArrayList<String> cart_item;
    ArrayAdapter<String> adapter;
    String[] nama,link,code;
    static String ip_addr = StartActivity.ip_addr;
    Button bayar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        cart_list = extras.getString("lists");
        Log.d("cart",cart_list);

        listView = findViewById(R.id.listView);
        bayar = findViewById(R.id.button7);
        nama = new String[4];
        link = new String[4];
        code = new String[4];
        cart_item = new ArrayList<String>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, cart_item);

        if (!cart_list.equals("0000"))
            new get_item().execute();
        else
        {
            cart_item.add("Keranjang Kosong");
            listView.setAdapter(adapter);
            bayar.setEnabled(false);
            bayar.setClickable(false);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String valueselected = cart_item.get(position).substring(0,7);
                Intent i = new Intent(CartActivity.this, OrderActivitiy.class);
                i.putExtra("lists", cart_list);
                i.putExtra("type", valueselected);
                i.putExtra("flag", 1);
                CartActivity.this.startActivity(i);
                CartActivity.this.finish();
            }
        });
    }

    void create_lists()
    {
        int index_num = 0;
        cart_list += " ";
        int arraybartemp[] = new int[4];
        for (int q = 0; q < 4; q++)
        {
            arraybartemp[q] = Integer.valueOf(cart_list.substring(q,q+1));
        }
        cart_list = cart_list.substring(0,4);

        for (int q = 0;q < 4; q++)
        {
            String textshow;
            if (arraybartemp[q] != 0)
            {
                textshow = code[q]+ " : " + nama[q] + "\nJumlah : " + arraybartemp[q];
                cart_item.add(index_num,textshow);
                index_num++;
            }
        }
        /*long cart_temp = Integer.valueOf(cart_list);
        while(cart_temp!=0)
        {
            if (cart_temp>=1000)
            {
                textshow = code[0]+ " : " + nama[0] + "\nJumlah : " + (int)cart_temp/1000;
                cart_item.add(index_num,textshow);
                cart_temp = cart_temp - ((int)(cart_temp/1000) * 1000);
            }
            else if (cart_temp>=100)
            {
                textshow = code[1]+ " : " + nama[1] + "\nJumlah : " + (int)cart_temp/100;
                cart_item.add(index_num,textshow);
                cart_temp = cart_temp - ((int)(cart_temp/100) * 100);
            }
            else if (cart_temp>=10)
            {
                textshow = code[2]+ " : " + nama[2] + "\nJumlah : " + (int)cart_temp/10;
                cart_item.add(index_num,textshow);
                cart_temp = cart_temp - ((int)(cart_temp/10) * 10);
            }
            else if (cart_temp>=1)
            {
                textshow = code[3]+ " : " + nama[3] + "\nJumlah : " + (int)cart_temp;
                cart_item.add(index_num,textshow);
                cart_temp = 0L;
            }
            index_num++;
        }*/
        listView.setAdapter(adapter);
    }

    public void back_button(View v)
    {
        onBackPressed();
    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(this, StartActivity.class);
        i.putExtra("lists", cart_list);
        this.startActivity(i);
        this.finish();
    }

    @SuppressLint("StaticFieldLeak")
    private class get_item extends AsyncTask<Void, Void, Void>
    {
        private JSONClass JC;
        private JSONObject jURL;
        private JSONArray jArray;
        String data_link;
        ProgressDialog pg;
        @Override
        protected void onPreExecute()
        {
            data_link = ip_addr + "/smartmini/data/read_barang.php";
            pg = new ProgressDialog(CartActivity.this);
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
                jArray = jURL.getJSONArray("products");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject job = jArray.getJSONObject(i);
                    code[i] = job.getString("id");
                    nama[i] = job.getString("nama");
                    link[i] = job.getString("url");
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            pg.dismiss();
            create_lists();
        }
    }

    public void kirimkan(View v)
    {
        if(!cart_list.equals("0000")) new sending().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class sending extends AsyncTask<Void, Void, Void>
    {
        private JSONClass JC;
        private JSONObject jURL;
        private JSONArray jArray;
        String data_link;
        ProgressDialog pg;
        boolean sukses;
        int queue[];
        @Override
        protected void onPreExecute()
        {
            sukses = false;
            data_link = ip_addr + "/smartmini/data/pelanggan_insert.php?data=" + cart_list;
            pg = new ProgressDialog(CartActivity.this);
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
                        queue = new int[jArray.length()];
                        for (int i = 0; i < jArray.length(); i++)
                        {
                            JSONObject job = jArray.getJSONObject(i);
                            queue[i] = Integer.valueOf(job.getString("queue"));
                            Log.d("queue",String.valueOf(queue[i]));
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result)
        {
            pg.dismiss();
            cart_list = "0000";
            StringBuilder displaying = new StringBuilder("No. Keranjang anda adalah : ");
            for (int aQueue : queue) {
                displaying.append(aQueue);
                displaying.append(" ");
            }
            displaying.append("\nSilakan berikan nomor ini pada kasir");
            final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(CartActivity.this);
            alertDialog.setTitle("Exit");
            alertDialog.setMessage(displaying.toString()).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    onBackPressed();
                }
            }).setCancelable(false).show();
            //alertDialog.setCancelable(false);
        }
    }
}