// Em java/com/example/helpai/TicketAdapter.java
package com.example.helpai;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.io.Serializable; // <-- IMPORT EXPLÍCITO DE SERIALIZABLE
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private Context context;
    private List<Ticket> ticketList;
    private int userType;

    public TicketAdapter(Context context, List<Ticket> ticketList, int userType) {
        this.context = context;
        this.ticketList = ticketList;
        this.userType = userType;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_item, parent, false);
        return new TicketViewHolder(view, context, ticketList, userType);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);

        holder.userName.setText(ticket.getUserName());
        holder.avatarLetter.setText(String.valueOf(ticket.getUserName().charAt(0)));
        // Título + 3 pontos + descrição (para ficar parecido com o protótipo)
        holder.titleAndDesc.setText(ticket.getTitulo() + ". . . " + ticket.getDescricao());
        holder.status.setText(ticket.getStatus());

        int priorityColor;
        switch (ticket.getPrioridade()) {
            case "Alta":
                priorityColor = ContextCompat.getColor(context, R.color.priorityRed);
                break;
            case "Média":
                priorityColor = ContextCompat.getColor(context, R.color.priorityYellow);
                break;
            case "Baixa":
            default:
                priorityColor = ContextCompat.getColor(context, R.color.priorityGreen);
                break;
        }

        // Pega o drawable (priority_dot_red) e muda a cor dinamicamente
        Drawable priorityDot = holder.priorityDot.getBackground();
        if (priorityDot instanceof GradientDrawable) {
            ((GradientDrawable) priorityDot).setColor(priorityColor);
        }
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView userName, avatarLetter, titleAndDesc, status;
        View priorityDot;

        private Context context;
        private List<Ticket> ticketList;
        private int userType;

        public TicketViewHolder(@NonNull View itemView, Context context, List<Ticket> ticketList, int userType) {
            super(itemView);
            this.context = context;
            this.ticketList = ticketList;
            this.userType = userType;

            userName = itemView.findViewById(R.id.user_name);
            avatarLetter = itemView.findViewById(R.id.avatar_letter);
            titleAndDesc = itemView.findViewById(R.id.ticket_title_and_desc);
            status = itemView.findViewById(R.id.ticket_status);
            priorityDot = itemView.findViewById(R.id.priority_dot);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            Ticket clickedTicket = ticketList.get(position);

            Intent intent = new Intent(context, TicketDetailActivity.class);

            // !! CORREÇÃO DO putExtra !!
            // O Android espera um 'java.io.Serializable'
            intent.putExtra("TICKET_DATA", (Serializable) clickedTicket);
            intent.putExtra(MainActivity.USER_TYPE_KEY, userType);

            context.startActivity(intent);
        }
    }
}