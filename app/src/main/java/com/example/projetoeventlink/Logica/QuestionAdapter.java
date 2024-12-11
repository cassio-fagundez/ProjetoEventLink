package com.example.projetoeventlink.Logica;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetoeventlink.R;
import com.example.projetoeventlink.Telas.EventQuestionsActivity;

import java.util.List;
import java.util.Map;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    private List<Map<String, String>> questionsAndAnswers; // Lista que contiene los mapas con ID y QA
    private Context context;
    private boolean isOwner;

    // Constructor
    public QuestionAdapter(Context context, List<Map<String, String>> questionsAndAnswers, boolean isOwner) {
        this.context = context;
        this.questionsAndAnswers = questionsAndAnswers;
        this.isOwner = isOwner;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_answer, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Map<String, String> questionData = questionsAndAnswers.get(position);
        String questionID = questionData.get("IDPregunta");
        String questionAnswer = questionData.get("QA");

        String[] parts = questionAnswer.split("\\|", 2); // Separar en Pregunta y Respuesta
        String question = parts[0]; // La pregunta
        String answer = parts.length > 1 ? parts[1] : ""; // Respuesta (puede estar vacía)

        holder.tvQuestion.setText("Pregunta: " + question);

        if (!answer.isEmpty()) {
            holder.tvAnswer.setText("Respuesta: " + answer);
            holder.tvAnswer.setVisibility(View.VISIBLE);
        } else {
            holder.tvAnswer.setVisibility(View.GONE);
        }

        if (isOwner) {
            holder.itemView.setOnClickListener(v -> {
                if (context instanceof EventQuestionsActivity) {
                    ((EventQuestionsActivity) context).showAnswerDialog(position, question, answer);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return questionsAndAnswers.size();
    }

    // ViewHolder interno
    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvAnswer;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
        }
    }
}

