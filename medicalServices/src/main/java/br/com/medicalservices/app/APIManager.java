package br.com.medicalservices.app;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class APIManager {

	static String url = DataManager.url;

	public static boolean getgroupinfo(Context context, String myuserid,
			String deviceid, String groupid) {

		DbUsers dbuser = new DbUsers(context);
		boolean result = false;
		String url = DataManager.url + "getGroupInfo.php?userid=" + myuserid
				+ "&deviceid=" + deviceid + "&groupid=" + groupid;
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				try {

					JSONObject obj = new JSONObject(returnString);

					JSONObject groupobj = obj.getJSONObject("group");

					String groupname = groupobj.getString("groupname");
					String adminid = groupobj.getString("adminid");

					String success = obj.getString("success");

					if (success.equals("1")) {

						JSONArray array = obj.getJSONArray("users");
						System.out.println("array ---" + array.length());
						ArrayList<UserPojo> userlist = new ArrayList<UserPojo>();
						UserPojo data;

						for (int i = 0; i < array.length(); i++)

						{

							data = new UserPojo();

							String userid = new String(
									array.getJSONObject(i).getString("userid")
											.getBytes("UTF-8"), "UTF-8");

							String firstname = new String(array
									.getJSONObject(i).getString("firstname")
									.getBytes("UTF-8"), "UTF-8");

							String lastname = new String(array.getJSONObject(i)
									.getString("lastname")
									.getBytes("UTF-8"), "UTF-8");

							String profilepic = new String(array
									.getJSONObject(i).getString("profilepic")
									.getBytes("UTF-8"), "UTF-8");
								
//							profilepic = URLEncoder.encode(profilepic, "utf-8");

							String logintype = new String(array
									.getJSONObject(i).getString("logintype")
									.getBytes("UTF-8"), "UTF-8");

							String gender = new String(
									array.getJSONObject(i).getString("gender")
											.getBytes("UTF-8"), "UTF-8");

							String city = new String(array.getJSONObject(i)
									.getString("city").getBytes("UTF-8"),
									"UTF-8");

							String country = new String(array.getJSONObject(i)
									.getString("country")
									.getBytes("UTF-8"), "UTF-8");
							String status = new String(
									array.getJSONObject(i).getString("status")
											.getBytes("UTF-8"), "UTF-8");

							data.setGender(gender);
							data.setCity(city);
							data.setCountry(country);
							data.setUserid(userid);
							data.setFirstname(firstname);
							data.setLastname(lastname);
							data.setLogintype(logintype);
							data.setProfilepic(profilepic);
							data.setStatus(status);

							userlist.add(data);

							if (dbuser.checkuser(userid) < 1) {
								dbuser.addContact(new UserPojo(userid,
										firstname, lastname, profilepic,
										logintype));
							}

						}

						DataManager.status = success;
						DataManager.alluserlist = userlist;
						DataManager.adminid = adminid;
						DataManager.groupname = groupname;
					} else if (success.equals("2")) {

						DataManager.status = success;
					
					} else {
						DataManager.status = success;
						String message = obj.getString("message");
						DataManager.message = message;
					}
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				}

			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean searchfriend(Context context, String myuserid,
			String search) {

		DbUsers dbuser = new DbUsers(context);
		boolean result = false;
		String url = DataManager.url + "searchuser.php?userid=" + myuserid
				+ "&search=" + search;
		url = url.replaceAll(" ", "%20");
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				try {

					JSONObject obj = new JSONObject(returnString);

					String success = obj.getString("success");
					if (success.equals("1")) {

						JSONArray array = obj.getJSONArray("users");
						System.out.println("array ---" + array.length());
						ArrayList<UserPojo> userlist = new ArrayList<UserPojo>();
						UserPojo data;

						for (int i = 0; i < array.length(); i++)

						{

							data = new UserPojo();

							String userid = new String(
									array.getJSONObject(i).getString("userid")
											.getBytes("UTF-8"), "UTF-8");

							String firstname = new String(array
									.getJSONObject(i).getString("firstname")
									.getBytes("UTF-8"), "UTF-8");

							String lastname = new String(array.getJSONObject(i)
									.getString("lastname")
									.getBytes("UTF-8"), "UTF-8");

							String profilepic = new String(array
									.getJSONObject(i).getString("profilepic")
									.getBytes("UTF-8"), "UTF-8");
				
							String logintype = new String(array
									.getJSONObject(i).getString("logintype")
									.getBytes("UTF-8"), "UTF-8");

							String gender = new String(
									array.getJSONObject(i).getString("gender")
											.getBytes("UTF-8"), "UTF-8");

							String city = new String(array.getJSONObject(i)
									.getString("city").getBytes("UTF-8"),
									"UTF-8");

							String country = new String(array.getJSONObject(i)
									.getString("country")
									.getBytes("UTF-8"), "UTF-8");
							String status = new String(
									array.getJSONObject(i).getString("status")
											.getBytes("UTF-8"), "UTF-8");

							data.setGender(gender);
							data.setCity(city);
							data.setCountry(country);
							data.setUserid(userid);
							data.setFirstname(firstname);
							data.setLastname(lastname);
							data.setLogintype(logintype);
							data.setProfilepic(profilepic);
							data.setStatus(status);

							userlist.add(data);

							if (dbuser.checkuser(userid) < 1) {
								dbuser.addContact(new UserPojo(userid,
										firstname, lastname, profilepic,
										logintype));
							}

						}

						DataManager.status = success;
						DataManager.alluserlist = userlist;
					} else if (success.equals("2")) {

						DataManager.status = success;
						
					} else {
						DataManager.status = success;
						String message = obj.getString("message");
						DataManager.message = message;
					}
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				}

			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean getmyfriends(Context context, String myuserid,
			String deviceid) {

		DbUsers dbuser = new DbUsers(context);
		boolean result = false;
		String url = DataManager.url + "getMyFriends.php?userid=" + myuserid
				+ "&deviceid=" + deviceid;
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				try {

					JSONObject obj = new JSONObject(returnString);

					String success = obj.getString("success");
					if (success.equals("1")) {

						JSONArray array = obj.getJSONArray("users");
						System.out.println("array ---" + array.length());
						ArrayList<UserPojo> userlist = new ArrayList<UserPojo>();
						UserPojo data;

						for (int i = 0; i < array.length(); i++)

						{

							data = new UserPojo();

							String userid = new String(
									array.getJSONObject(i).getString("userid")
											.getBytes("UTF-8"), "UTF-8");

							String firstname = new String(array
									.getJSONObject(i).getString("firstname")
									.getBytes("UTF-8"), "UTF-8");

							String lastname = new String(array.getJSONObject(i)
									.getString("lastname")
									.getBytes("UTF-8"), "UTF-8");

							String profilepic = new String(array
									.getJSONObject(i).getString("profilepic")
									.getBytes("UTF-8"), "UTF-8");
//							profilepic = URLEncoder.encode(profilepic, "utf-8");
							String logintype = new String(array
									.getJSONObject(i).getString("logintype")
									.getBytes("UTF-8"), "UTF-8");

							String gender = new String(
									array.getJSONObject(i).getString("gender")
											.getBytes("UTF-8"), "UTF-8");

							String city = new String(array.getJSONObject(i)
									.getString("city").getBytes("UTF-8"),
									"UTF-8");

							String country = new String(array.getJSONObject(i)
									.getString("country")
									.getBytes("UTF-8"), "UTF-8");
							String status = new String(
									array.getJSONObject(i).getString("status")
											.getBytes("UTF-8"), "UTF-8");

							data.setGender(gender);
							data.setCity(city);
							data.setCountry(country);
							data.setUserid(userid);
							data.setFirstname(firstname);
							data.setLastname(lastname);
							data.setLogintype(logintype);
							data.setProfilepic(profilepic);
							data.setStatus(status);

							userlist.add(data);

							if (dbuser.checkuser(userid) < 1) {
								dbuser.addContact(new UserPojo(userid,
										firstname, lastname, profilepic,
										logintype));
							}

						}

						DataManager.status = success;
						DataManager.alluserlist = userlist;
					} else if (success.equals("2")) {

						DataManager.status = success;
						String message = obj.getString("message");
					} else {
						DataManager.status = success;
						String message = obj.getString("message");
						DataManager.message = message;
					}
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				}

			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean getblockedfriends(Context context, String myuserid,
			String deviceid) {

		DbUsers dbuser = new DbUsers(context);
		boolean result = false;
		String url = DataManager.url + "getBlockedUser.php?userid=" + myuserid
				+ "&deviceid=" + deviceid;
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				try {

					JSONObject obj = new JSONObject(returnString);

					String success = obj.getString("success");
					if (success.equals("1")) {

						JSONArray array = obj.getJSONArray("users");
						System.out.println("array ---" + array.length());
						ArrayList<UserPojo> userlist = new ArrayList<UserPojo>();
						UserPojo data;

						for (int i = 0; i < array.length(); i++)

						{

							data = new UserPojo();

							String userid = new String(
									array.getJSONObject(i).getString("userid")
											.getBytes("UTF-8"), "UTF-8");

							String firstname = new String(array
									.getJSONObject(i).getString("firstname")
									.getBytes("UTF-8"), "UTF-8");

							String lastname = new String(array.getJSONObject(i)
									.getString("lastname")
									.getBytes("UTF-8"), "UTF-8");

							String profilepic = new String(array
									.getJSONObject(i).getString("profilepic")
									.getBytes("UTF-8"), "UTF-8");
//							profilepic = URLEncoder.encode(profilepic, "utf-8");
							String logintype = new String(array
									.getJSONObject(i).getString("logintype")
									.getBytes("UTF-8"), "UTF-8");

							String gender = new String(
									array.getJSONObject(i).getString("gender")
											.getBytes("UTF-8"), "UTF-8");

							String city = new String(array.getJSONObject(i)
									.getString("city").getBytes("UTF-8"),
									"UTF-8");

							String country = new String(array.getJSONObject(i)
									.getString("country")
									.getBytes("UTF-8"), "UTF-8");
							String status = new String(
									array.getJSONObject(i).getString("status")
											.getBytes("UTF-8"), "UTF-8");

							data.setGender(gender);
							data.setCity(city);
							data.setCountry(country);
							data.setUserid(userid);
							data.setFirstname(firstname);
							data.setLastname(lastname);
							data.setLogintype(logintype);
							data.setProfilepic(profilepic);
							data.setStatus(status);

							userlist.add(data);

							if (dbuser.checkuser(userid) < 1) {
								dbuser.addContact(new UserPojo(userid,
										firstname, lastname, profilepic,
										logintype));
							}

						}

						DataManager.status = success;
						DataManager.alluserlist = userlist;
					} else if (success.equals("2")) {

						DataManager.status = success;
						String message = obj.getString("message");
					} else {
						DataManager.status = success;
						String message = obj.getString("message");
						DataManager.message = message;
					}
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				}

			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean getuserprofile(Context context, String myuserid,
			String deviceid, String profileid) {

		DbUsers dbuser = new DbUsers(context);
		boolean result = false;
		String url = DataManager.url + "getuserprofile.php?userid=" + myuserid
				+ "&deviceid=" + deviceid + "&profileid=" + profileid;
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				try {

					JSONObject obj = new JSONObject(returnString);

					String success = obj.getString("success");
					if (success.equals("1")) {

						JSONArray array = obj.getJSONArray("users");

						System.out.println("array ---" + array.length());

						ArrayList<UserPojo> userlist = new ArrayList<UserPojo>();
						UserPojo data;

						for (int i = 0; i < array.length(); i++)

						{

							data = new UserPojo();

							String userid = new String(
									array.getJSONObject(i).getString("userid")
											.getBytes("UTF-8"), "UTF-8");

							String firstname = new String(array
									.getJSONObject(i).getString("firstname")
									.getBytes("UTF-8"), "UTF-8");

							String lastname = new String(array.getJSONObject(i)
									.getString("lastname")
									.getBytes("UTF-8"), "UTF-8");

							String profilepic = new String(array
									.getJSONObject(i).getString("profilepic")
									.getBytes("UTF-8"), "UTF-8");
//							profilepic = URLEncoder.encode(profilepic, "utf-8");
							String logintype = new String(array
									.getJSONObject(i).getString("logintype")
									.getBytes("UTF-8"), "UTF-8");

							String gender = new String(
									array.getJSONObject(i).getString("gender")
											.getBytes("UTF-8"), "UTF-8");

							String city = new String(array.getJSONObject(i)
									.getString("city").getBytes("UTF-8"),
									"UTF-8");

							String country = new String(array.getJSONObject(i)
									.getString("country")
									.getBytes("UTF-8"), "UTF-8");

							String status = new String(
									array.getJSONObject(i).getString("status")
											.getBytes("UTF-8"), "UTF-8");

							data.setGender(gender);
							data.setCity(city);
							data.setCountry(country);
							data.setUserid(userid);
							data.setFirstname(firstname);
							data.setLastname(lastname);
							data.setLogintype(logintype);
							data.setProfilepic(profilepic);
							data.setStatus(status);

							userlist.add(data);

						}

						DataManager.status = success;
						DataManager.alluserlist = userlist;
					} else if (success.equals("2")) {

						DataManager.status = success;
						String message = obj.getString("message");
					} else {
						DataManager.status = success;
						String message = obj.getString("message");
						DataManager.message = message;
					}
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				}

			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean getnearbyfriends(Context context, String myuserid,
			String deviceid, String longitude, String latitude, String distance) {

		boolean result = false;
		String url = DataManager.url + "getnearbyfriends.php?userid="
				+ myuserid + "&deviceid=" + deviceid + "&longitude="
				+ longitude + "&latitude=" + latitude + "&distance=" + distance;
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				try {

					JSONObject obj = new JSONObject(returnString);

					String success = obj.getString("success");
					if (success.equals("1")) {

						JSONArray array = obj.getJSONArray("users");

						ArrayList<UserPojo> userlist = new ArrayList<UserPojo>();
						System.out.println("array ---" + array.length());
						UserPojo data;

						for (int i = 0; i < array.length(); i++)

						{

							data = new UserPojo();

							String userid = new String(
									array.getJSONObject(i).getString("userid")
											.getBytes("UTF-8"), "UTF-8");

							String firstname = new String(array
									.getJSONObject(i).getString("firstname")
									.getBytes("UTF-8"), "UTF-8");

							String lastname = new String(array.getJSONObject(i)
									.getString("lastname")
									.getBytes("UTF-8"), "UTF-8");

							String profilepic = new String(array
									.getJSONObject(i).getString("profilepic")
									.getBytes("UTF-8"), "UTF-8");
//							profilepic = URLEncoder.encode(profilepic, "utf-8");
							String logintype = new String(array
									.getJSONObject(i).getString("logintype")
									.getBytes("UTF-8"), "UTF-8");

							String gender = new String(
									array.getJSONObject(i).getString("gender")
											.getBytes("UTF-8"), "UTF-8");

							String city = new String(array.getJSONObject(i)
									.getString("city").getBytes("UTF-8"),
									"UTF-8");

							String country = new String(array.getJSONObject(i)
									.getString("country")
									.getBytes("UTF-8"), "UTF-8");
							data.setGender(gender);
							data.setCity(city);
							data.setCountry(country);

							data.setUserid(userid);
							data.setFirstname(firstname);
							data.setLastname(lastname);
							data.setLogintype(logintype);
							data.setProfilepic(profilepic);

							userlist.add(data);

						}

						DataManager.status = success;
						DataManager.alluserlist = userlist;
					} else if (success.equals("2")) {

						DataManager.status = success;
						String message = obj.getString("message");
					} else {
						DataManager.status = success;
						String message = obj.getString("message");
						DataManager.message = message;
					}
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				}

			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean sendindividualmessage(String from, String to,
			String message, String deviceid, String messagetype) {
		boolean result = false;

		String url = DataManager.url + "sendIndividualMessage.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);
		System.out.println("deviceid---" + deviceid);
		System.out.println("fromid---" + from);
		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("fromid", from));
		paramas.add(new BasicNameValuePair("toid", to));
		paramas.add(new BasicNameValuePair("message", message));
		paramas.add(new BasicNameValuePair("deviceid", deviceid));
		paramas.add(new BasicNameValuePair("messagetype", messagetype));
		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean addasfriend(String from, String to, String message,
			String deviceid, String messagetype) {
		boolean result = false;

		String url = DataManager.url + "addFriend.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);
		System.out.println("deviceid---" + deviceid);
		System.out.println("fromid---" + from);
		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("fromid", from));
		paramas.add(new BasicNameValuePair("toid", to));
		paramas.add(new BasicNameValuePair("message", message));
		paramas.add(new BasicNameValuePair("deviceid", deviceid));
		paramas.add(new BasicNameValuePair("messagetype", messagetype));
		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean sendgroupmessage(String from, String groupid,
			String message, String deviceid, String messagetype) {
		boolean result = false;

		String url = DataManager.url + "sendGroupMessage.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("fromid", from));
		paramas.add(new BasicNameValuePair("groupid", groupid));
		paramas.add(new BasicNameValuePair("message", message));
		paramas.add(new BasicNameValuePair("deviceid", deviceid));
		paramas.add(new BasicNameValuePair("messagetype", messagetype));
		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean addnewgroup(String groupname, String adminid,
			String memberid, String membername, String deviceid, String userid) {
		boolean result = false;

		String url = DataManager.url + "createnewgroup.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("groupname", groupname));
		paramas.add(new BasicNameValuePair("adminid", adminid));
		paramas.add(new BasicNameValuePair("memberid", memberid));
		paramas.add(new BasicNameValuePair("membername", membername));
		paramas.add(new BasicNameValuePair("deviceid", deviceid));
		paramas.add(new BasicNameValuePair("userid", userid));
		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean updategroup(String groupid, String groupname,
			String adminid, String memberid, String membername,
			String deviceid, String userid) {
		boolean result = false;

		String url = DataManager.url + "updategroup.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);
		System.out.println("memberid--" + memberid);
		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("groupid", groupid));
		paramas.add(new BasicNameValuePair("groupname", groupname));
		paramas.add(new BasicNameValuePair("adminid", adminid));
		paramas.add(new BasicNameValuePair("memberid", memberid));
		paramas.add(new BasicNameValuePair("membername", membername));
		paramas.add(new BasicNameValuePair("deviceid", deviceid));
		paramas.add(new BasicNameValuePair("userid", userid));
		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean updatelocation(String userid, String longitude,
			String latitude, String deviceid) {
		boolean result = false;

		String url = DataManager.url + "updatelocation.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("userid", userid));
		paramas.add(new BasicNameValuePair("longitude", longitude));
		paramas.add(new BasicNameValuePair("latitude", latitude));
		paramas.add(new BasicNameValuePair("deviceid", deviceid));

		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean updatestatus(String userid, String status,
			String deviceid) {
		boolean result = false;

		String url = DataManager.url + "updatestatus.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("userid", userid));
		paramas.add(new BasicNameValuePair("status", status));
		paramas.add(new BasicNameValuePair("deviceid", deviceid));

		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean logout(String userid, String deviceid) {
		boolean result = false;

		String url = DataManager.url + "logout.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("userid", userid));
		paramas.add(new BasicNameValuePair("deviceid", deviceid));

		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean blockuser(String userid, String friendid,
			String deviceid) {
		boolean result = false;

		String url = DataManager.url + "blockuser.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("userid", userid));
		paramas.add(new BasicNameValuePair("friendid", friendid));
		paramas.add(new BasicNameValuePair("deviceid", deviceid));

		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean unblockuser(String userid, String friendid,
			String deviceid) {
		boolean result = false;

		String url = DataManager.url + "UnBlockUser.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("userid", userid));
		paramas.add(new BasicNameValuePair("friendid", friendid));
		paramas.add(new BasicNameValuePair("deviceid", deviceid));

		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean deletrgroup(String userid, String groupid,
			String deviceid) {
		boolean result = false;

		String url = DataManager.url + "deleteGroup.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("userid", userid));
		paramas.add(new BasicNameValuePair("groupid", groupid));
		paramas.add(new BasicNameValuePair("deviceid", deviceid));

		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean changepassword(String userid, String deviceid,
			String newpassword) {
		boolean result = false;

		String url = DataManager.url + "updatepassword.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("userid", userid));
		paramas.add(new BasicNameValuePair("password", newpassword));
		paramas.add(new BasicNameValuePair("deviceid", deviceid));

		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean resetpassword(String email, String userid) {
		boolean result = false;

		String url = DataManager.url + "resetPassword.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("email", email));
	

		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
}
