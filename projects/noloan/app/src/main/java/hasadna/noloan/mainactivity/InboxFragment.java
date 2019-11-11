package hasadna.noloan.mainactivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import hasadna.noloan.common.DbMessages;
import hasadna.noloan.InboxRecyclerAdapter;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;
import noloan.R;

public class InboxFragment extends Fragment {

  private OnFragmentInteractionListener fragmentInteractionListener;
  private RecyclerView recyclerView;
  private InboxRecyclerAdapter inboxRecyclerAdapter;

  private ArrayList<SmsMessage> inbox;

  public InboxFragment(ArrayList<SmsMessage> inbox) {
    this.inbox = inbox;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    inboxRecyclerAdapter = new InboxRecyclerAdapter(inbox);

    // If new spams added to the DB - Check if the spams exists in user's inbox, if so update the
    // counter of suggesters
    DbMessages.getInstance() // TODO move to the recycler
        .addMessagesListener(
            new DbMessages.MessagesListener() {
              @Override
              public void messageAdded(SmsMessage smsMessage) {
                // Check if user has this message in the inbox
                if (DbMessages.findSms(smsMessage,inbox) != -1) {
                  getActivity()
                      .runOnUiThread(
                          () ->
                              inboxRecyclerAdapter.notifyItemChanged(
                                  DbMessages.findSms(smsMessage,inbox)));
                }
              }

              @Override
              public void messageModified(int index) {
                if (DbMessages.findSms(DbMessages.getInstance().getMessages().get(index),inbox)
                    != -1) {
                  getActivity().runOnUiThread(() -> inboxRecyclerAdapter.notifyItemChanged(index));
                }
              }

              @Override
              public void messageRemoved(int index, SmsMessage smsMessage) {
                if (DbMessages.findSms(smsMessage,inbox) != -1) {
                  getActivity()
                      .runOnUiThread(
                          () ->
                              inboxRecyclerAdapter.notifyItemChanged(
                                  DbMessages.findSms(smsMessage,inbox)));
                }
              }
            });

    inboxRecyclerAdapter.registerAdapterDataObserver(
        new RecyclerView.AdapterDataObserver() {
          @Override
          public void onItemRangeChanged(int positionStart, int itemCount) {
          }
        });
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_inbox, container, false);

    recyclerView = rootView.findViewById(R.id.RecyclerView_inboxMessages);
    recyclerView.setRotationY(180);

    recyclerView.setAdapter(inboxRecyclerAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    return rootView;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      fragmentInteractionListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(
          context.toString() + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    fragmentInteractionListener = null;
  }

  public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);
  }
}

