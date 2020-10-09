package com.example.dts_proyek1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_STORAGE=100;

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle("Aplikasi Catatan");
        toolbar.inflateMenu(R.menu.menuitem);
        lv = findViewById(R.id.lv);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.addCatatan){
                    Intent intent = new Intent(MainActivity.this, KelolaActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, KelolaActivity.class);
                Map<String, Object> data = (Map<String, Object>)adapterView.getAdapter().getItem(position);

                intent.putExtra("filename", data.get("name").toString());
                Toast.makeText(MainActivity.this, "Anda Memilih " + data.get("name"), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Map<String, Object> data = (Map<String, Object>) adapterView.getAdapter().getItem(position);
                tampilkanDialogKonfirmasiHapusCatatan(data.get("name").toString());
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
            if (Build.VERSION.SDK_INT >= 23) {
                if (periksaIzinPenyimpanan()) {
                    mengambilListFilePadaFolder();
    }        } else {
                mengambilListFilePadaFolder();
            }
    }

    public boolean periksaIzinPenyimpanan() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mengambilListFilePadaFolder();
                }
                break;
        }
    }

    void mengambilListFilePadaFolder() {
        String path = Environment.getExternalStorageDirectory().toString() + "/kominfoProyek1";
        File directory = new File(path);
            if (directory.exists()) {
                File[] files = directory.listFiles();
                String[] filenames = new String[files.length];
                String[] dateCreated = new String[files.length];
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM YYYY HH:mm:ss");
                ArrayList<Map<String, Object>> itemDataList = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < files.length; i++) {
                    filenames[i] = files[i].getName();
                    Date lastModDate = new Date(files[i].lastModified());
                    dateCreated[i] = simpleDateFormat.format(lastModDate);
                    Map<String, Object> listItemMap = new HashMap<>();
                    listItemMap.put("name", filenames[i]);
                    listItemMap.put("date", dateCreated[i]);
                    itemDataList.add(listItemMap);
                }
                SimpleAdapter simpleAdapter = new SimpleAdapter(this, itemDataList, android.R.layout.simple_list_item_1, new String[]{"name", "date"}, new int[]{android.R.id.text1, android.R.id.text2});
                    lv.setAdapter(simpleAdapter);
                    simpleAdapter.notifyDataSetChanged();
            }
    }

    void tampilkanDialogKonfirmasiHapusCatatan(final String filename) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus Catatan "+filename+"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        hapusFile(filename);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    void hapusFile(String filename) {
        String path = Environment.getExternalStorageDirectory().toString() + "/kominfoProyek1";
        File file = new File(path, filename);
        if (file.exists()) {
            file.delete();
        }
        mengambilListFilePadaFolder();
    }
}
