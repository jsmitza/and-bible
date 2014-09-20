package net.bible.android.view.activity;

import net.bible.android.BibleApplication;
import net.bible.android.SharedConstants;
import net.bible.android.activity.R;
import net.bible.android.control.ControlFactory;
import net.bible.android.control.document.DocumentControl;
import net.bible.android.view.activity.base.Callback;
import net.bible.android.view.activity.base.CustomTitlebarActivityBase;
import net.bible.android.view.activity.base.Dialogs;
import net.bible.android.view.activity.download.Download;
import net.bible.android.view.activity.page.MainBibleActivity;
import net.bible.service.common.CommonUtils;
import net.bible.service.sword.SwordDocumentFacade;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

/** Called first to show download screen if no documents exist
 * 
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author.
 */
public class StartupActivity extends CustomTitlebarActivityBase {

	private static final int CAN_DOWNLOAD_DLG = 10;
	
	private DocumentControl documentControl = ControlFactory.getInstance().getDocumentControl();
	
	private static final String TAG = "StartupActivity";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_view);

        // do not show an actionBar/title on the splash screen
        getSupportActionBar().hide();
        
        TextView versionTextView = (TextView)findViewById(R.id.versionText);
        String versionMsg = BibleApplication.getApplication().getString(R.string.version_text, CommonUtils.getApplicationVersionName());
        versionTextView.setText(versionMsg);
        
        //See if any errors occurred during app initialisation, especially upgrade tasks
        int abortErrorMsgId = BibleApplication.getApplication().getErrorDuringStartup();
        
        // check for SD card 
        // it would be great to check in the Application but how to show dialog from Application?
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
        	abortErrorMsgId = R.string.no_sdcard_error;
        }
        
        // show fatal startup msg and close app
        if (abortErrorMsgId!=0) {
        	Dialogs.getInstance().showErrorMsg(abortErrorMsgId, new Callback() {
				@Override
				public void okay() {
					// this causes the blue splashscreen activity to finish and since it is the top the app closes
					finish();					
				}
			});
        	// this aborts further initialisation but leaves blue splashscreen activity
        	return;
        }

    
        // allow call back and continuation in the ui thread after JSword has been initialised
        runOnUiThread(new Runnable() {
			@Override
			public void run() {
			    postBasicInitialisationControl();
			}
        });
    }
    
    private void postBasicInitialisationControl() {
        if (SwordDocumentFacade.getInstance().getBibles().size()==0) {
        	Log.i(TAG, "Invoking download activity because no bibles exist");
        	askIfGotoDownloadActivity();
        } else {
        	Log.i(TAG, "Going to main bible view");
        	gotoMainBibleActivity();
        }
    }

	private void askIfGotoDownloadActivity() {
    	showDialog(CAN_DOWNLOAD_DLG);
    }
    private void doGotoDownloadActivity() {
    	String errorMessage = "";
    	if (!CommonUtils.isInternetAvailable()) {
    		errorMessage = getString(R.string.no_internet_connection);
    	} else if (CommonUtils.getSDCardMegsFree() < SharedConstants.REQUIRED_MEGS_FOR_DOWNLOADS) {
    		errorMessage = getString(R.string.storage_space_warning);
    	}
    	
    	if (errorMessage.length()==0) {
	       	Intent handlerIntent = new Intent(StartupActivity.this, Download.class);
	    	startActivityForResult(handlerIntent, 1);

	    	finish();
		} else {
			Dialogs.getInstance().showErrorMsg(errorMessage, new Callback() {
				@Override
				public void okay() {
		    		finish();
				}
			});
		}
    }

	private void gotoMainBibleActivity() {
		Log.i(TAG, "Going to MainBibleActivity");
    	Intent handlerIntent = new Intent(this, MainBibleActivity.class);
    	startActivity(handlerIntent);
    	finish();
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog superDlg = super.onCreateDialog(id);
    	if (superDlg!=null) {
    		return superDlg;
    	}
    	
        switch (id) {
            case CAN_DOWNLOAD_DLG:
            	return new AlertDialog.Builder(StartupActivity.this)
            		   .setMessage(R.string.download_confirmation)
            	       .setCancelable(false)
            	       .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	        	   removeDialog(CAN_DOWNLOAD_DLG);
            	        	   doGotoDownloadActivity();
            	           }
            	       })
            	       .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	        	   removeDialog(CAN_DOWNLOAD_DLG);
            	        	   StartupActivity.this.finish();
            	        	   // ensure app exits to force Sword to reload or if a sdcard/jsword folder is created it may not be recognised 
            	        	   System.exit(2);
            	           }
            	       }).create();
        }
        return null;
    }

    /** on return from download we may go to bible
     *  on return from bible just exit
     */
    @Override 
    public void onActivityResult(int requestCode, int resultCode, Intent data) { 
    	Log.d(TAG, "Activity result:"+resultCode);
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if (requestCode == 1) {
    		Log.i(TAG, "Returned from Download");
    		if (SwordDocumentFacade.getInstance().getBibles().size()>0) {
        		Log.i(TAG, "Bibles now exist so go to main bible view");
    			gotoMainBibleActivity();
    		} else {
        		Log.i(TAG, "No Bibles exist so exit");
    			finish();
    		}
    	}
    }
}
