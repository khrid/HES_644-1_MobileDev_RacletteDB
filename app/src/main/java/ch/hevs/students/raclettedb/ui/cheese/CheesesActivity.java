package ch.hevs.students.raclettedb.ui.cheese;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;
import ch.hevs.students.raclettedb.util.RecyclerViewItemClickListener;
import ch.hevs.students.raclettedb.viewmodel.cheese.CheeseListViewModel;

public class CheesesActivity extends BaseActivity {

    private static final String TAG = "TAG-"+ BaseApp.APP_NAME+"-CheesesActivity";

    private List<CheeseEntity> cheeses;
    private RecyclerAdapter<CheeseEntity> adapter;
    private CheeseListViewModel viewModel;

    private boolean isAdmin = false;

    static SharedPreferences settings;
    static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_cheeses, frameLayout);

        setTitle(getString(R.string.empty));
        navigationView.setCheckedItem(position);


        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        editor = settings.edit();
        isAdmin = settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false);

        RecyclerView recyclerView = findViewById(R.id.cheesesRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setNestedScrollingEnabled(false);

        cheeses = new ArrayList<>();
        adapter = new RecyclerAdapter<>(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d(TAG, "clicked position:" + position);
                Log.d(TAG, "clicked on: " + cheeses.get(position).getName());

                Intent intent = new Intent(ch.hevs.students.raclettedb.ui.cheese.CheesesActivity.this, CheeseDetailActivity.class);
                intent.setFlags(
                        Intent.FLAG_ACTIVITY_NO_ANIMATION |
                                Intent.FLAG_ACTIVITY_NO_HISTORY
                );
                intent.putExtra("cheeseId", cheeses.get(position).getId());
                startActivity(intent);

            }

            @Override
            public void onItemLongClick(View v, int position) {
                Log.d(TAG, "longClicked position:" + position);
                Log.d(TAG, "longClicked on: " + cheeses.get(position).getName());

                if(isAdmin) {
                    createDeleteDialog(position);
                }
            }
        });


        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        if(isAdmin) {
            fab.setOnClickListener(view -> {
                        Intent intent = new Intent(ch.hevs.students.raclettedb.ui.cheese.CheesesActivity.this, EditCheeseActivity.class);
                        startActivity(intent);
                    }
            );
        } else {
            fab.setVisibility(View.INVISIBLE);
        }

        TextView tvCheesesNothing = findViewById(R.id.tvCheesesNothing);
        CheeseListViewModel.Factory factory = new CheeseListViewModel.Factory(
                getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(CheeseListViewModel.class);
        viewModel.getCheeses().observe(this, cheeseEntities -> {
            if (cheeseEntities != null) {
                if(cheeseEntities.size() > 0) {
                    cheeses = cheeseEntities;
                    adapter.setData(cheeses);
                    tvCheesesNothing.setVisibility(View.GONE);
                } else {
                    tvCheesesNothing.setVisibility(View.VISIBLE);
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
        final CheeseEntity cheese = cheeses.get(position);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.row_delete_item, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.cheeses_list_delete_title));
        alertDialog.setCancelable(false);

        final TextView deleteMessage = view.findViewById(R.id.tvDeleteItem);
        deleteMessage.setText(String.format(getString(R.string.cheeses_list_delete_text), cheese.getName()));

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.execute), (dialog, which) -> {
            Toast toast = Toast.makeText(this, getString(R.string.cheeses_list_deleted), Toast.LENGTH_LONG);
            viewModel.deleteCheese(cheese, new OnAsyncEventListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "deleteCheese: success");
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "deleteCheese: failure", e);
                }
            });
            toast.show();
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> alertDialog.dismiss());
        alertDialog.setView(view);
        alertDialog.show();
    }
}

