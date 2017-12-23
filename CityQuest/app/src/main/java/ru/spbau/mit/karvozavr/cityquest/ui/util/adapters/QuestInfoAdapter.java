package ru.spbau.mit.karvozavr.cityquest.ui.util.adapters;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ru.spbau.mit.karvozavr.cityquest.R;
import ru.spbau.mit.karvozavr.cityquest.quest.QuestController;
import ru.spbau.mit.karvozavr.cityquest.quest.QuestInfo;
import ru.spbau.mit.karvozavr.cityquest.ui.QuestInfoActivity;
import ru.spbau.mit.karvozavr.cityquest.ui.QuestStepActivity;

public class QuestInfoAdapter extends RecyclerView.Adapter<QuestInfoAdapter.QuestInfoViewHolder> {

  private ArrayList<QuestInfo> quests = new ArrayList<>();
  public int nextToLoad = 0;
  private static final int batchSize = 15;
  public boolean loading = false;

  public QuestInfoAdapter() {
    // load initial steps
    loadNextBatch();
  }

  @Override
  public QuestInfoAdapter.QuestInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View questInfoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_quest, null);
    return new QuestInfoAdapter.QuestInfoViewHolder(questInfoView);
  }

  /**
   * Replace the contents of a view (invoked by the layout manager)
   */
  @Override
  public void onBindViewHolder(QuestInfoAdapter.QuestInfoViewHolder holder, int position) {

    QuestInfo questInfo = quests.get(position);

    if (questInfo != null) {
      TextView name = holder.questInfoView.findViewById(R.id.quest_title);
      TextView avgDistance = holder.questInfoView.findViewById(R.id.quest_avg_distance);
      TextView description = holder.questInfoView.findViewById(R.id.quest_short_description);
      TextView usersPassed = holder.questInfoView.findViewById(R.id.quest_passed);
      AppCompatRatingBar ratingBar = holder.questInfoView.findViewById(R.id.quest_rating_bar);

      ratingBar.setRating(questInfo.rating);
      name.setText(questInfo.name);
      avgDistance.setText(String.format("%s %s", Float.toString(questInfo.averageDistance), "km"));
      usersPassed.setText(String.format("%s %s", Integer.toString(questInfo.usersPassed), "passed"));
      description.setText(questInfo.shortDescription);

      Button questStartButton = holder.questInfoView.findViewById(R.id.quest_start_button);
      questStartButton.setOnClickListener((view) -> {
        Intent intent = new Intent(holder.questInfoView.getContext(), QuestStepActivity.class);
        intent.putExtra("quest_info", questInfo);
        holder.questInfoView.getContext().startActivity(intent);
      });

      Button questInfoButton = holder.questInfoView.findViewById(R.id.quest_info_button);
      questInfoButton.setOnClickListener((view) -> {
        Intent intent = new Intent(holder.questInfoView.getContext(), QuestInfoActivity.class);
        intent.putExtra("quest_info", questInfo);
        holder.questInfoView.getContext().startActivity(intent);
      });

      // TODO picture
    }
  }

  public void loadNextBatch() {
    loading = true;
    new QuestInfoAdapter.AsyncLoadNextBatch().execute();
  }

  private void onNextBatchLoaded(ArrayList<QuestInfo> infos) {
    if (!infos.isEmpty()) {
      quests.addAll(infos);
      nextToLoad += infos.size();
      notifyDataSetChanged();
    }

    loading = false;
  }

  private class AsyncLoadNextBatch extends AsyncTask<Void, Void, Void> {

    ArrayList<QuestInfo> infos = null;

    @Override
    protected Void doInBackground(Void... args) {
      infos = QuestController.getQuestInfoList(nextToLoad, batchSize);
      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      onNextBatchLoaded(infos);
    }
  }

  @Override
  public int getItemCount() {
    return quests.size();
  }

  public static class QuestInfoViewHolder extends RecyclerView.ViewHolder {

    View questInfoView;

    public QuestInfoViewHolder(View view) {
      super(view);
      questInfoView = view;
    }
  }
}
