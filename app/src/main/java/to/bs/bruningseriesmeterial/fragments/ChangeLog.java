package to.bs.bruningseriesmeterial.fragments;




import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.gmariotti.changelibs.library.view.ChangeLogRecyclerView;
import to.bs.bruningseriesmeterial.R;


public class ChangeLog extends DialogFragment {


    public ChangeLog() {
        // Required empty public constructor
    }


    public static ChangeLog newInstance() {
        ChangeLog fragment = new ChangeLog();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        ChangeLogRecyclerView chgList= (ChangeLogRecyclerView) layoutInflater.inflate(R.layout.fragment_change_log, null);

        return new AlertDialog.Builder(getActivity(),R.style.ThemeOverlay_AppCompat_Dialog)
                .setTitle(R.string.chnangelog)
                .setView(chgList)
                .setPositiveButton(R.string.about_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();
    }


}
