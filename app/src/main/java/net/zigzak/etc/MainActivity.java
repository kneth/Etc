package net.zigzak.etc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


public class MainActivity extends AppCompatActivity {

    private Realm realm;
    private TextService msService;
    static private String baseUrl = "http://etc.etxt.dk/";
    private String currentId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.deleteRealm(configuration);
        Realm.setDefaultConfiguration(configuration);
        realm = Realm.getDefaultInstance();

        realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm element) {
                RealmResults<Strip> strips = realm.where(Strip.class).equalTo("seen", false).findAll();
                Log.d("ETC", "New strip unseen: " + strips.size());
                if (!strips.isEmpty()) {
                    Strip strip = strips.first();
                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    Picasso.with(getApplicationContext()).load(baseUrl + "/etc" + strip.getId() + ".jpg").into(imageView);
                    realm.beginTransaction();
                    strip.setSeen(true);
                    realm.commitTransaction();

                    currentId = strip.getId();

                    Button prev = (Button) findViewById(R.id.buttonPrev);
                    prev.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            String id = currentId;
                            if (id.equals("for")) {
                                id = "for";
                            } else if (id.equals("bag")) {
                                id = "115";
                            } else {
                                Long n = Long.valueOf(id);
                                id = String.format("%03d", n - 1);
                            }
                            downloadStrip(id);
                        }
                    });

                    Button next = (Button) findViewById(R.id.buttonNext);
                    next.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            String id = currentId;
                            if (id.equals("bag")) {
                                id = "bag";
                            } else if (id.equals("for")) {
                                id = "001";
                            } else if (id.equals("115")) {
                                id = "bag";
                            } else {
                                Long n = Long.valueOf(id);
                                id = String.format("%03d", n + 1);
                            }
                            downloadStrip(id);
                        }
                    });
                }
            }
        });

        msService = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
                .create(TextService.class);

        if (realm.where(Strip.class).findAll().isEmpty()) {
            // nothing downloaded - first time used and load first stripe
            downloadStrip("for");
        }
    }

    @Override
    protected void onDestroy() {
        if (realm != null) {
            realm.close();
        }
        super.onDestroy();
    }

    private void downloadStrip(final String id) {
        msService.getStrip(id).enqueue(new Callback<Text>() {
            @Override
            public void onResponse(Call<Text> call, Response<Text> response) {
                final Text text = response.body();
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Strip strip = new Strip();
                        strip.setId(id);       // set the primary key
                        strip.setSeen(false);  // strip hasn't been seen yet
                        //strip.setNext(text.getNext());
                        //strip.setPrev(text.getPrev());
                        Log.d("ETC", realm.copyToRealm(strip).toString());
                    }
                });
                realm.close();
            }

            @Override
            public void onFailure(Call<Text> call, Throwable t) {
                Log.e("ETC", "Error in loading XML: " + t.getCause() + "; " + t.getMessage());
            }
        });
    }
}
