package com.test.voicerecognition;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author swathi
 *
 */
public class VoiceRecognitionDemo extends Activity{

	private static final int REQUEST_CODE = 1234;
	private ListView predictions;
	private TextToSpeech mTextToSpeech;
	// just my checksum to pass to the tts
	private static final int MY_DATA_CHECK_CODE= 1235;

	/**
	 * Called with the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final Button speakButton = (Button) findViewById(R.id.speakButton);
		predictions = (ListView) findViewById(R.id.list);

		// Disable button if no recognition service is present
		PackageManager pm = getPackageManager();	
		List<ResolveInfo> activities = pm.queryIntentActivities(
				new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0)
		{
			speakButton.setEnabled(false);
			speakButton.setText("Recognizer not present");
		}


		// Fire off an intent to check if a TTS engine is installed
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

	}

	/**
	 * Handle the action of the button being clicked
	 */
	public void speakButtonClicked(View v)
	{
		startVoiceRecognitionActivity();
	}

	/**
	 * Fire an intent to start the voice recognition activity.
	 */
	private void startVoiceRecognitionActivity()
	{
		Intent voiceRecognitionintent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		voiceRecognitionintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		//intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		voiceRecognitionintent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
		startActivityForResult(voiceRecognitionintent, REQUEST_CODE);
	}

	/**
	 * Handle the results from the voice recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_CODE  && resultCode == RESULT_OK)
		{
			// Hmmmm.. My predictions will be here....
			ArrayList<String> listOfPredictions = data.getStringArrayListExtra(
					RecognizerIntent.EXTRA_RESULTS);
			predictions.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listOfPredictions));
			Toast.makeText(getApplicationContext(), "Click the on the word you actually meant!", Toast.LENGTH_LONG).show();
			predictions.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Object object = predictions.getItemAtPosition(position);
					Toast.makeText(getApplicationContext(), "Okay!! So You Meant: "+object.toString(), Toast.LENGTH_LONG).show();
					mTextToSpeech.speak("Okay!! So You Meant: "+object.toString(), TextToSpeech.QUEUE_FLUSH, null);
					if(object.toString().equals("call")) {
						mTextToSpeech.speak("Okay!! Call Whom?", TextToSpeech.QUEUE_FLUSH, null);
					}
					else if(object.toString().equals("message")||object.toString().equals("text")) {
						mTextToSpeech.speak("Okay!! Message or Text whom?", TextToSpeech.QUEUE_FLUSH, null);
					}
				}

			});
		}

		else if(requestCode==MY_DATA_CHECK_CODE) {
			if(resultCode==TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				mTextToSpeech = new TextToSpeech(getApplicationContext(), new OnInitListener() {

					@Override
					public void onInit(int status) {
						// TODO Auto-generated method stub
						mTextToSpeech.speak("Hello! This is neither Siri nor Iris!! Welcome to Swathi's Application!! A Kiddish Application!!"+"Now click on the button ans speak", TextToSpeech.QUEUE_FLUSH, null);
					}
				});
			}

			else
			{
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * shutdown my tts engine.... once everything is done... so that other applications can use it....(non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy()
	{
		// Don't forget to shutdown!
		if (mTextToSpeech != null)
		{
			mTextToSpeech.stop();
			mTextToSpeech.shutdown();
		}
		super.onDestroy();
	}

}
