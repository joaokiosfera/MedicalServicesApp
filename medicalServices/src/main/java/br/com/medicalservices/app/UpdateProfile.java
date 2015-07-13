package br.com.medicalservices.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import br.com.medicalservices.app.utils.AndroidMultiPartEntity;
import br.com.medicalservices.app.utils.AndroidMultiPartEntity.ProgressListener;
import chat.demo.app.R;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class UpdateProfile extends ActionBarActivity implements OnClickListener{
	
	private ProgressDialog progress;
	EditText etfname, etlname, etgender;
	Button btnprofilepic, btnupdate, btncancel, btnchangepassword;
	ProgressDialog pDialog;
	ConnectionDetector connection;
	String firstname, lastname, gender;
	Dialog builder;
	public static final int MEDIA_TYPE_IMAGE = 1;
	Uri fileuri;
	private static final int REQUEST_CAMERA = 1;
	private static final int SELECT_FILE = 2;
	String deviceid = "",selectedfile = "";
	public static final int progress_bar_type = 0;
	long totalSize = 0;
	double longitude = 0, latitude = 0;
	String PROJECT_NUMBER = DataManager.PROJECT_NUMBER;
	SessionManager session;
	GoogleCloudMessaging gcm;
	String myusername;
	ArrayList<UserPojo> userlist = new ArrayList<UserPojo>();
	boolean fileselected =  false;
	String newpass, oldpass;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_profile);
		session = new SessionManager(this);
		deviceid = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		
		deviceid = session.getdeviceid();
		myusername = session.getuserid();
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(
				getResources().getDrawable(R.drawable.back));
		
		connection = new ConnectionDetector(this);
		widgets();
		new getuserprofile().execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.individual, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {

			Intent i = new Intent(this, MainActivity.class);
			i.setAction("splash");
			finish();
			startActivity(i);
			overridePendingTransition(0, 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class getuserprofile extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			

		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getuserprofile(UpdateProfile.this, myusername, deviceid, myusername);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			if (DataManager.status.equalsIgnoreCase("1")) {
				 userlist = DataManager.alluserlist ;
				 displaydata();
			}else if(DataManager.status.equalsIgnoreCase("false"))
			{
				session.logoutUser();
			}
				
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}
	
	public void displaydata()
	{
		
		firstname = userlist.get(0).getFirstname();
		lastname =  userlist.get(0).getLastname();
		gender = userlist.get(0).getGender().toUpperCase();
		
		etfname.setText(""+firstname);
		etlname.setText(""+lastname);
		etgender.setText(""+gender);		
		
	}
	

	public void widgets() {
		
		etfname = (EditText) findViewById(R.id.etfname);
		etlname = (EditText) findViewById(R.id.etlname);
	
		etgender = (EditText) findViewById(R.id.etgender);
		btnprofilepic = (Button) findViewById(R.id.btnprofile);
		btnupdate = (Button) findViewById(R.id.btnupdate);
		btncancel = (Button) findViewById(R.id.btncancel);
		btnchangepassword = (Button)findViewById(R.id.btnchangepassword);
		etgender.setOnClickListener(this);
		btnprofilepic.setOnClickListener(this);
		btnupdate.setOnClickListener(this);
		btncancel.setOnClickListener(this);
		btnchangepassword.setOnClickListener(this);
	}
	
	private void openmenu() {

		builder = new Dialog(UpdateProfile.this,
				android.R.style.Theme_DeviceDefault_DialogWhenLarge_NoActionBar);
		builder.setContentView(R.layout.changepass_layout);
		builder.setCanceledOnTouchOutside(true);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		builder.show();
		
	
		final EditText etnewpass = (EditText) builder.findViewById(R.id.etnewpass);
		final EditText etconfirmpass = (EditText) builder.findViewById(R.id.etnewconfpass);
		
		TextView txtconfirm = (TextView) builder.findViewById(R.id.txtok);
		TextView txtcancel = (TextView) builder.findViewById(R.id.txtcancel);

		

		txtconfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				newpass = etnewpass.getText().toString();
				String confirmpass = etconfirmpass.getText().toString();
				
				if (newpass.length() < 6) {
					Toast.makeText(UpdateProfile.this, "Minimum 6 characters",
							Toast.LENGTH_LONG).show();
				} else if (confirmpass.length() < 6) {
					Toast.makeText(UpdateProfile.this, "Enter valid Confirm password",
							Toast.LENGTH_LONG).show();
				}else {

					if(newpass.equals(confirmpass))
					{
					new updatepassword().execute();
					}else
					{
						Toast.makeText(UpdateProfile.this, "Password does not match",
								Toast.LENGTH_LONG).show();
					}
				
				}
			}
		});

		txtcancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				builder.dismiss();

			}
		});

		
	}
	
	private class updatepassword extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(UpdateProfile.this, "Changing Password...",
					"Please wait....");
		}

		@Override
		protected String doInBackground(String... params) {

			APIManager.changepassword(myusername, deviceid, newpass);

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {

			progress.cancel();
			if(DataManager.status.equals("false"))
			{
				session.logoutUser();
			}else
			{
				if(DataManager.status.equals("1"))
				{
					builder.dismiss();
					Toast.makeText(getApplicationContext(), "Password Update Succesfully", Toast.LENGTH_LONG).show();
				}else
				{
					Toast.makeText(getApplicationContext(), "Connection error! Try Again", Toast.LENGTH_LONG).show();
				}
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btnprofilepic) {
			selectpic();
		} else if (v == btnupdate) {
			register();
		} else if (v == btncancel) {
			Intent i = new Intent(UpdateProfile.this, MainActivity.class);
			i.setAction("splash");
			finish();
			startActivity(i);
			overridePendingTransition(0, 0);
		} else if (v == etgender) {
			selectgender(etgender);
		}else if (v == btnchangepassword)
		{
			openmenu();
		}

	}

	
	public void selectgender(final EditText v) {

		final Dialog myDialog = new Dialog(this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		myDialog.setContentView(R.layout.custom_spinner_dialog);
		myDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		TextView txtheader = (TextView) myDialog.findViewById(R.id.txtheader);

		txtheader.setText("Select Gender");

		ArrayList<String> genderlist = new ArrayList<String>();
		genderlist.add("Male");
		genderlist.add("Female");

		final ListView listview = (ListView) myDialog
				.findViewById(R.id.spinnerlist);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.spinner_item, genderlist);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				myDialog.dismiss();
				TextView txtview = (TextView) view.findViewById(R.id.spntxt);
				gender = txtview.getText().toString();
				v.setText(gender);

			}
		});

		myDialog.show();

	}

	public void register() {
		
		firstname = etfname.getText().toString();
		lastname = etlname.getText().toString();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		 if (firstname.length() < 1) {
			etfname.setError("Please enter valid first name");
			etfname.setFocusable(true);
		}else if (lastname.length() < 1) {
			etlname.setError("Please enter valid last name");
			etlname.setFocusable(true);
		} else {
			
				if (connection.isConnectingToInternet()) {
					
							new UploadFileToServer().execute();
						
				} else {
					Toast.makeText(UpdateProfile.this,
							"Please Connect to Internet...", Toast.LENGTH_LONG)
							.show();
				}
			
		}

	}

	public void selectpic() {

		final Dialog myDialog = new Dialog(this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		myDialog.setContentView(R.layout.custom_dialog_rateapp);
		myDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		Button btncamera = (Button) myDialog.findViewById(R.id.btncamera);
		Button btngallery = (Button) myDialog.findViewById(R.id.btngallery);

		btncamera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

				Uri uriSavedImage = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
				fileuri = uriSavedImage;
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
				fileuri = uriSavedImage;

				startActivityForResult(cameraIntent, REQUEST_CAMERA);
				myDialog.cancel();
			}
		});

		btngallery.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(intent, SELECT_FILE);
				myDialog.cancel();
			}
		});

		myDialog.show();
	}

	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/**
	 * returning image / video
	 */
	public File getOutputMediaFile(int type) {

		// External sdcard location
		File imagesFolder = new File(Environment.getExternalStorageDirectory(),
				"MedicalServices");
		imagesFolder.mkdirs();

		File image = new File(imagesFolder, deviceid + ".png");

		return image;
	}

	private Bitmap getBitmap(String path) {
		int IMAGE_MAX_SIZE = 250000;
		File externalFile = new File(path);
		Uri uri = Uri.fromFile(externalFile);
		ContentResolver mconContentResolver = getContentResolver();
		InputStream in = null;
		try {

			in = mconContentResolver.openInputStream(uri);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}
			Log.d("TAG", "scale = " + scale + ", orig-width: " + o.outWidth
					+ ", orig-height: " + o.outHeight);

			Bitmap b = null;
			in = mconContentResolver.openInputStream(uri);
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);

				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();
				Log.d("TAG", "1th scale operation dimenions - width: " + width
						+ ",height: " + height);

				double y = Math.sqrt(IMAGE_MAX_SIZE
						/ (((double) width) / height));
				double x = (y / height) * width;

				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
						(int) y, true);
				b.recycle();
				b = scaledBitmap;

				System.gc();

			} else {
				b = BitmapFactory.decodeStream(in);
			}
			in.close();

			Log.d("TAG", "bitmap size - width: " + b.getWidth() + ", height: "
					+ b.getHeight());
			return b;
		} catch (IOException e) {
			Log.e("TAG", e.getMessage(), e);
			return null;
		}
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		android.database.Cursor cursor = managedQuery(contentUri, proj, // Which
																		// columns
																		// to
																		// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_FILE) {
				if (data != null) {

					Uri selectedImageUri = data.getData();
					fileselected = true;
					selectedfile = getRealPathFromURI(selectedImageUri);
					Bitmap bitmap = getBitmap(selectedfile);

					FileOutputStream fOut;
					try {
						fOut = new FileOutputStream(selectedfile);

						bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
						fOut.flush();
						fOut.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

			else if (requestCode == REQUEST_CAMERA) {

				File imagesFolder = new File(
						Environment.getExternalStorageDirectory(), "MySteps");
				File image = new File(imagesFolder, deviceid + ".png");
				imagesFolder.mkdirs();
				selectedfile = image.toString();
				fileselected = true;
				Bitmap bitmap = getBitmap(selectedfile);

				FileOutputStream fOut;
				try {
					fOut = new FileOutputStream(image);

					bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
					fOut.flush();
					fOut.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		AlertDialog dialogDetails = null;
		switch (id) {
		case progress_bar_type: // we set this to 0
			pDialog = new ProgressDialog(this);
			pDialog.setMessage("Sending file....");
			pDialog.setIndeterminate(false);
			pDialog.setMax(100);
			pDialog.setCanceledOnTouchOutside(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(false);
			pDialog.show();
			return pDialog;

		default:
			return null;
		}
	}

	private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(progress_bar_type);
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			// Making progress bar visible
			pDialog.setProgress(progress[0]);

		}

		@Override
		protected String doInBackground(Void... params) {
			return uploadFile();
		}

		@SuppressWarnings("deprecation")
		private String uploadFile() {
			String responseString = null;

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(DataManager.url
					+ "updateprofile.php");

			try {
				AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
						new ProgressListener() {

							@Override
							public void transferred(long num) {
								publishProgress((int) ((num / (float) totalSize) * 100));
							}
						});
				if(fileselected)
				{
				File sourceFile = new File(selectedfile);	

				// Adding file data to http body
				entity.addPart("image", new FileBody(sourceFile));
				}
				entity.addPart("userid", new StringBody(myusername));
				entity.addPart("firstname", new StringBody(firstname));
				entity.addPart("lastname", new StringBody(lastname));
				entity.addPart("gender", new StringBody(gender));


				totalSize = entity.getContentLength();
				httppost.setEntity(entity);

				// Making server call
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity r_entity = response.getEntity();

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					// Server response
					responseString = EntityUtils.toString(r_entity);
				} else {
					responseString = "Error occurred! Http Status Code: "
							+ statusCode;
				}

			} catch (ClientProtocolException e) {
				responseString = e.toString();
			} catch (IOException e) {
				responseString = e.toString();
			}

			return responseString;

		}

		@Override
		protected void onPostExecute(String result) {
			Log.e("Response", "Response from server: " + result);
			super.onPostExecute(result);
			pDialog.dismiss();
			String success = "";
			try {
				JSONObject jobj = new JSONObject(result);

				success = jobj.getString("success");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (success.equals("1")) {
				Toast.makeText(
						getApplicationContext(),
						"You have successfully Updated Profile!",
						Toast.LENGTH_LONG).show();
				Intent i = new Intent(UpdateProfile.this, MyMessageActivity.class);
				finish();
				startActivity(i);
				overridePendingTransition(0, 0);
			}
		}

	}

}
