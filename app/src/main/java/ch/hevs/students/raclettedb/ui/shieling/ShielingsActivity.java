package ch.hevs.students.raclettedb.ui.shieling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.adapter.RecyclerAdapter;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;
import ch.hevs.students.raclettedb.util.RecyclerViewItemClickListener;
import ch.hevs.students.raclettedb.viewmodel.shieling.ShielingListViewModel;

public class ShielingsActivity extends BaseActivity {

    private static final String TAG = "TAG-"+ BaseApp.APP_NAME+"-"+"ShielingsActivity";

    private List<ShielingEntity> shielings;
    private RecyclerAdapter<ShielingEntity> adapter;
    private ShielingListViewModel viewModel;

    private boolean isAdmin = false;

    static SharedPreferences settings;
    static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Récupération du stockage commun
        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        editor = settings.edit();
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_shielings, frameLayout);

        setTitle(getString(R.string.empty));
        navigationView.setCheckedItem(position);

        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        isAdmin = settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false);

        RecyclerView recyclerView = findViewById(R.id.shielingsRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        shielings = new ArrayList<>();
        adapter = new RecyclerAdapter<>(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d(TAG, "clicked position:" + position);
                Log.d(TAG, "clicked on: " + shielings.get(position).getName());


                Intent intent = new Intent(ch.hevs.students.raclettedb.ui.shieling.ShielingsActivity.this, ShielingDetailActivity.class);
                intent.setFlags(
                        Intent.FLAG_ACTIVITY_NO_ANIMATION |
                                Intent.FLAG_ACTIVITY_NO_HISTORY
                );
                intent.putExtra("shielingId", shielings.get(position).getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View v, int position) {
                Log.d(TAG, "longClicked position:" + position);
                Log.d(TAG, "longClicked on: " + shielings.get(position).getName());

                if(isAdmin) {
                    createDeleteDialog(position);
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        if(isAdmin) {
            fab.setOnClickListener(view -> {
                        Intent intent = new Intent(ch.hevs.students.raclettedb.ui.shieling.ShielingsActivity.this, EditShielingActivity.class);
                        startActivity(intent);
                    }
            );
        } else {
            fab.setVisibility(View.INVISIBLE);
        }

        TextView tvShielingsNothing = findViewById(R.id.tvShielingsNothing);
        ShielingListViewModel.Factory factory = new ShielingListViewModel.Factory(
                getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(ShielingListViewModel.class);
        viewModel.getShielings().observe(this, shielingEntities -> {
            if (shielingEntities != null) {
                if(shielingEntities.size() > 0) {
                    shielings = shielingEntities;
                    adapter.setData(shielings);
                    tvShielingsNothing.setVisibility(View.GONE);
                } else {
                    tvShielingsNothing.setVisibility(View.VISIBLE);
                }
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == BaseActivity.position) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }
        /*
        The activity has to be finished manually in order to guarantee the navigation hierarchy working.
        */
        finish();
        return super.onNavigationItemSelected(item);
    }

    private void createDeleteDialog(final int position) {
        final ShielingEntity shieling = shielings.get(position);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.row_delete_item, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.shielings_list_delete_title));
        alertDialog.setCancelable(false);

        final TextView deleteMessage = view.findViewById(R.id.tvDeleteItem);
        deleteMessage.setText(String.format(getString(R.string.shielings_list_delete_warning_text), shieling.getName()));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.execute), (dialog, which) -> {
            Toast toast = Toast.makeText(this, getString(R.string.shielings_list_deleted), Toast.LENGTH_LONG);
            viewModel.deleteShieling(shieling, new OnAsyncEventListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "deleteShieling: success");
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "deleteShieling: failure", e);
                }
            });
            toast.show();
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> alertDialog.dismiss());
        alertDialog.setView(view);
        alertDialog.show();
    }
}

