package com.example.gsbppe4vu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AccountantListExpenseFormLeftActivity extends AppCompatActivity {

    private Button btn_dcnx;
    private Button btn_frais_mois;
    private Button btn_profil;
    private TextView tv_ident;
    private ListView lv_expense_form;
    private List<com.example.gsbppe4vu.ExpenseForm> expenseFormList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountant_list_expense_form_left);

        btn_dcnx = findViewById(R.id.deconnexion);
        btn_frais_mois = findViewById(R.id.frais_mois);
        btn_profil = findViewById(R.id.profil_compt);
        tv_ident = findViewById(R.id.tv_ident);
        lv_expense_form = findViewById(R.id.lv_expense_form);

        //récupère les infos pour le nom prénom
        Intent i_recu = getIntent();
        com.example.gsbppe4vu.User currentUser = (com.example.gsbppe4vu.User) i_recu.getSerializableExtra("currentUser");
        tv_ident.setText(currentUser.getName() + " " + currentUser.getFirstName());


        // listage des fiches de frais non validées

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        expenseFormList = new ArrayList<>();

        db.collection("expense_forms")
                .get() // récupère tout les fiches de frais de la collection
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("FIREC", document.getId() + " => " + document.getData());

                                if (document.getString("state").equals("Saisie clôturée")){
                                    expenseFormList.add(new com.example.gsbppe4vu.ExpenseForm(document));
                                }

                            }
                            // ajoute la liste des fiches frais dans la listview
                            com.example.gsbppe4vu.ExpenseFormAdapter expenseAdapt = new com.example.gsbppe4vu.ExpenseFormAdapter(com.example.gsbppe4vu.AccountantListExpenseFormLeftActivity.this,expenseFormList);
                            lv_expense_form.setAdapter(expenseAdapt); // affiche la listview
                        } else {
                            Log.d("FIREC", "Error getting documents: ", task.getException());
                        }
                    }
                });

        lv_expense_form.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                com.example.gsbppe4vu.ExpenseForm current = (com.example.gsbppe4vu.ExpenseForm) parent.getAdapter().getItem(position);
                Intent expenseFormInfo = new Intent(com.example.gsbppe4vu.AccountantListExpenseFormLeftActivity.this, com.example.gsbppe4vu.AccountantExpenseFormValidation.class);
                expenseFormInfo.putExtra("currentUser", currentUser);
                expenseFormInfo.putExtra("expenseFormInfo", current);
                startActivity(expenseFormInfo);
            }
        });
        

        btn_dcnx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent connexion = new Intent(com.example.gsbppe4vu.AccountantListExpenseFormLeftActivity.this,  com.example.gsbppe4vu.MainActivity.class);
                connexion.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(connexion);
            }
        });

        btn_frais_mois.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent frais_mois = new Intent(com.example.gsbppe4vu.AccountantListExpenseFormLeftActivity.this, com.example.gsbppe4vu.AccountantBundleMontlyActivity.class);
                frais_mois.putExtra("currentUser", currentUser);
                startActivity(frais_mois);
            }
        });

        btn_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profil = new Intent(com.example.gsbppe4vu.AccountantListExpenseFormLeftActivity.this, com.example.gsbppe4vu.AccountantProfilActivity.class);
                profil.putExtra("currentUser", currentUser);
                startActivity(profil);
            }
        });

    }
}