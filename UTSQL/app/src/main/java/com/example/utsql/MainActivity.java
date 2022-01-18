package com.example.utsql;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.utsql.Util.ServerAPI;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProdukAdapter.ItemClickListener {
    RecyclerView mRecyclerview;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mManager;
    ArrayList<Produk> mItems;
    Button btnInsert, btnDelete;
    ProgressDialog pd;
    private ProdukAdapter adapter;
    private ArrayList<Produk> produkArrayList;
    private ArrayList<String> listGambar;
    ViewFlipper v_flipper;
    public double tot=0;
    String dataProduk[] = null;
    String dS[] = null;
    ArrayList<String> kode_produk, nama_produk, deskripsi, harga;

    int images[] = {R.drawable.keripik_sapi, R.drawable.keripik_kulit};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        v_flipper = findViewById(R.id.v_flipper);
        for (int i=0; i<images.length; i++){
            fliverImages(images[i]);
        }
        for (int image: images)
            fliverImages(image);
        mRecyclerview = (RecyclerView)
                findViewById(R.id.recycler_view);
        pd = new ProgressDialog(MainActivity.this);
        mItems = new ArrayList<>();
// Membaca Semua Barang
        loadJson();
//mManager = newLinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false);
//mRecyclerview.setLayoutManager(mManager);
        kode_produk = new ArrayList<>();
        nama_produk = new ArrayList<>();
        deskripsi = new ArrayList<>();
        harga = new ArrayList<>();

        mAdapter = new ProdukAdapter(mItems,this);
        adapter= new ProdukAdapter(mItems,this);
//mRecyclerview.setAdapter(mAdapter);
//GRID 2 kolom
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
//STAGGER 4 KOLOM
// StaggeredGridLayoutManager llm=newStaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.setAdapter(adapter);
//mAdapter.setClickListener(this);
        adapter.setClickListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action barif it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// Handle action bar item clicks here. The action barwill
// automatically handle clicks on the Home/Up button, solong
// as you specify a parent activity inAndroidManifest.xml.
        int id = item.getItemId();
//noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
//Intent intent = newIntent(MainActivity.this,TambahActivity.class);
//startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    public void postTotals()
    {
        TextView txtTot=(TextView) findViewById(R.id.totalPrice);
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        txtTot.setText("Rp. "+decimalFormat.format(tot));
    }
    public void onClick(View view, int position) {
//final Produk mhs = produkArrayList.get(position);
        final Produk barang = mItems.get(position);
        switch (view.getId()) {
            case R.id.txt_nama_produk:
                Toast.makeText(this,"okkkkkk" + barang.getNama(),Toast.LENGTH_SHORT).show();
                return;
            case R.id.img_card:
                tot=tot+Double.parseDouble(barang.getHarga());
                Toast.makeText(this,"gambare....." + barang.getNama() ,Toast.LENGTH_SHORT).show();
                postTotals();
                return;
            default:
                Toast.makeText(this,"lainnya..... -> " + barang.getNama()+ " Rp. "+tot ,Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void loadJson()
    {
        pd.setMessage("Mengambil Data");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest reqData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_DATA,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                pd.cancel();
                Log.d("volley","response : " + response.toString());
                for(int i = 0 ; i < response.length();
                    i++)
                {
                    try {
                        JSONObject data = response.getJSONObject(i);
                        Produk md = new Produk();
                        md.setKode(data.getString("kode_produk"));
                        md.setNama(data.getString("nama_produk"));
                        md.setHarga(data.getString("harga"));
                        md.setImg(data.getString("gambar"));;
                        mItems.add(md);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//mAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.cancel();
                        Log.d("volley", "error : " + error.getMessage());
                    }
                });
        com.example.utsql.Util.AppController.getInstance().addToRequestQueue(reqData);
    }
    public void ambildata(){
        listGambar= new ArrayList<String>();
        listGambar.add("keripik_kulit");
        listGambar.add("keripik_sapi");
    }
    public void checkout(View view) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        Toast.makeText(this,"Total Rp. "+ decimalFormat.format(tot) ,Toast.LENGTH_SHORT).show();
    }
    public void fliverImages(int images){
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(images);
        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(4000);
        v_flipper.setAutoStart(true);
        v_flipper.setInAnimation(this,android.R.anim.slide_in_left);
        v_flipper.setOutAnimation(this,android.R.anim.slide_out_right);
    }
}