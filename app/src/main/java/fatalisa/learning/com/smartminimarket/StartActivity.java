package fatalisa.learning.com.smartminimarket;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class StartActivity extends AppCompatActivity
{
    String cart_list;
    Intent intent;
    static String ip_addr = "http://192.168.204.1";
    Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        try {
            extras = getIntent().getExtras();
            assert extras != null;
            cart_list = extras.getString("lists");
            Log.d("cart",cart_list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            cart_list = "0000";
        }
        /*if (extras != null)
            cart_list = extras.getString("lists");
        else
            cart_list = "0000";*/
    }

    public void order_button(View v)
    {
        intent = new Intent(this, BarcodeCaptureActivity.class);
        intent.putExtra("lists", cart_list);
        startActivity(intent);
        this.finish();
    }

    public void cart_button(View v)
    {
        intent = new Intent(this, CartActivity.class);
        intent.putExtra("lists", cart_list);
        startActivity(intent);
        this.finish();
    }

    public void exit_button(View v)
    {
        onBackPressed();
    }

    public void onBackPressed()
    {
        if (!cart_list.equals("0000"))
        {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Exit");
            alertDialog.setMessage("Ada barang di keranjang.\nApakah anda ingin menutup aplikasi?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    new finalizing().execute();
                }
            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    cart_button(null);
                }
            }).setCancelable(false).show();
        }
        else
        {
            finish();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class finalizing extends AsyncTask<Void, Void, Void>
    {
        private JSONClass JC;
        String data_link;
        ProgressDialog pg;
        @Override
        protected void onPreExecute()
        {
            data_link = ip_addr + "/smartmini/data/cancel_all.php?data=" + cart_list;
            pg = new ProgressDialog(StartActivity.this);
            pg.setMessage("Loading");
            pg.setCancelable(false);
            pg.show();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            JC = new JSONClass();
            JC.getJSONFromUrl(data_link);
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            pg.dismiss();
            finish();
        }
    }
}