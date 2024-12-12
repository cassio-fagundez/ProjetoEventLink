package com.example.projetoeventlink.Telas;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetoeventlink.Logica.QuestionAdapter;
import com.example.projetoeventlink.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventQuestionsActivity extends AppCompatActivity {

    private String eventID;
    private String eventTitle;
    private DatabaseReference questionsRef;
    private List<Map<String, String>> questionsList;
    private QuestionAdapter questionsAdapter;
    private Button askQuestionButton;
    private boolean OWNER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_questions);

        askQuestionButton = findViewById(R.id.btnNewQuestion);


        // Obtener los datos del Intent
        eventID = getIntent().getStringExtra("eventId");
        eventTitle = getIntent().getStringExtra("titulo");
        OWNER = getIntent().getBooleanExtra("Owner", false);

        if (TextUtils.isEmpty(eventID) || TextUtils.isEmpty(eventTitle)) {
            Toast.makeText(this, getString(R.string.event_data_error), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (OWNER) { askQuestionButton.setVisibility(View.GONE); }

        // Configurar título del evento
        TextView titleTextView = findViewById(R.id.tvEventTitle);
        titleTextView.setText(eventTitle);

        // Referencia a la base de datos para preguntas
        questionsRef = FirebaseDatabase.getInstance().getReference("Events").child(eventID).child("Questions");

        // Configurar lista de preguntas
        RecyclerView questionsRecyclerView = findViewById(R.id.rvQuestionsAnswers);
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionsList = new ArrayList<Map<String, String>>();
        questionsAdapter = new QuestionAdapter(this, questionsList, OWNER);
        questionsRecyclerView.setAdapter(questionsAdapter);

        // Botón para hacer nueva pregunta
        askQuestionButton.setOnClickListener(v -> showAskQuestionDialog());

        // Cargar preguntas de la base de datos
        loadQuestions();
    }

    private void showAskQuestionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.question_button_visibility));

        // Crear un EditText para ingresar la pregunta
        final EditText input = new EditText(this);
        input.setHint(getString(R.string.ask_question_hint));
        builder.setView(input);

        // Botón para confirmar
        builder.setPositiveButton(getString(R.string.enviar), (dialog, which) -> {
            String question = input.getText().toString().trim();

            if (TextUtils.isEmpty(question)) {
                Toast.makeText(this, getString(R.string.ask_question_invalid), Toast.LENGTH_SHORT).show();
                return;
            }

            saveQuestionToDatabase(question);
        });

        // Botón para cancelar
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void saveQuestionToDatabase(String question) {
        // Generar un ID único para la pregunta
        String questionID = questionsRef.push().getKey();

        if (questionID != null) {
            // Crear un mapa con la estructura deseada (IDPregunta y QA)
            Map<String, String> questionMap = new HashMap<>();
            questionMap.put("IDPregunta", questionID);  // Almacenar el ID único
            questionMap.put("QA", question + "|");  // Guardar la pregunta con la respuesta vacía

            // Guardar la pregunta en la base de datos bajo la nueva estructura
            questionsRef.child(questionID).setValue(questionMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, getString(R.string.question_sent_success), Toast.LENGTH_SHORT).show();
                        // Agregar el mapa a la lista de preguntas
                        questionsList.add(questionMap);
                        questionsAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.question_sent_error), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, getString(R.string.question_id_error), Toast.LENGTH_SHORT).show();
        }
    }



    private void loadQuestions() {
        questionsRef.get().addOnSuccessListener(snapshot -> {
            questionsList.clear();
            for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                String questionData = questionSnapshot.child("QA").getValue(String.class);
                if (questionData != null) {
                    // Crear un mapa con IDPregunta y QA
                    Map<String, String> questionMap = new HashMap<>();
                    questionMap.put("IDPregunta", questionSnapshot.getKey());  // Almacenar el ID de la pregunta
                    questionMap.put("QA", questionData);  // Almacenar la Pregunta|Respuesta
                    questionsList.add(questionMap);  // Agregar el mapa a la lista
                }
            }
            questionsAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Toast.makeText(this, getString(R.string.load_questions_error), Toast.LENGTH_SHORT).show());
    }



    // Método para mostrar el diálogo de respuesta
    public void showAnswerDialog(int position, String question, String currentAnswer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.answer_dialog_title));

        // Crear el layout del diálogo
        final EditText input = new EditText(this);
        input.setHint(getString(R.string.answer_hint));
        input.setText(currentAnswer); // Mostrar la respuesta actual si existe
        builder.setView(input);

        // Botón para confirmar la respuesta
        builder.setPositiveButton(getString(R.string.contestar), (dialog, which) -> {
            String newAnswer = input.getText().toString().trim();
            if (TextUtils.isEmpty(newAnswer)) {
                Toast.makeText(this, getString(R.string.answer_empty_error), Toast.LENGTH_SHORT).show();
                return;
            }
            updateAnswerInDatabase(position, newAnswer);
        });

        // Botón para eliminar la pregunta
        builder.setNeutralButton(getString(R.string.deletequestion), (dialog, which) -> {
            deleteQuestionFromDatabase(position);
        });

        // Botón para cancelar
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void updateAnswerInDatabase(int position, String newAnswer) {
        String questionID = questionsList.get(position).get("IDPregunta"); // Obtener el ID de la pregunta actual
        if (questionID != null) {
            // Obtener la pregunta original y actualizar con la nueva respuesta
            String questionData = questionsList.get(position).get("QA");
            String question = questionData.split("\\|")[0]; // Obtener solo la pregunta

            // Crear un nuevo mapa con la respuesta actualizada
            Map<String, String> updatedQuestionMap = new HashMap<>();
            updatedQuestionMap.put("IDPregunta", questionID);  // ID de la pregunta
            updatedQuestionMap.put("QA", question + "|" + newAnswer);  // Pregunta y nueva respuesta

            // Actualizar en la base de datos
            questionsRef.child(questionID).setValue(updatedQuestionMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, getString(R.string.answer_updated_success), Toast.LENGTH_SHORT).show();
                        // Actualizar la lista de preguntas con el nuevo mapa
                        questionsList.set(position, updatedQuestionMap);
                        questionsAdapter.notifyItemChanged(position);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.answer_updated_error), Toast.LENGTH_SHORT).show());
        }
    }


    private void deleteQuestionFromDatabase(int position) {
        // Obtener el ID de la pregunta a eliminar desde la lista
        String questionID = questionsList.get(position).get("IDPregunta"); // Obtener el ID de la pregunta actual
        if (questionID != null) {
            questionsRef.child(questionID).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, getString(R.string.delete_question_success), Toast.LENGTH_SHORT).show();
                        questionsList.remove(position);
                        questionsAdapter.notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.delete_question_error), Toast.LENGTH_SHORT).show());
        }
    }


}