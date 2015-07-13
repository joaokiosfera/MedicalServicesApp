package br.com.medicalservices.app;

import java.util.ArrayList;


public class DataManager {

	public static String status = "";
	public static String message = "";
	public static int position = 0;
	public static boolean isindividualopen = false;
	public static String username = "";
	public static String senderid = "";
	public static String profileid = "";
	public static String groupid = "";
	public static String fullname = "";
	public static String url = "http://webservice.techrevolution.com.br/chatserver/";
	public static String PROJECT_NUMBER = "359744907192";  // GCM Project number
	public static ArrayList<UserPojo> alluserlist;

	public static int selectedposition =0;
	
	public static String groupname = "";
	public static String adminid = "";

	public static final String FILE_UPLOAD_URL = "http://webservice.techrevolution.com.br/chatserver/fileUpload.php";
    public static final String IMAGE_DIRECTORY_NAME = "MedicalServices";
    public static final String FILE_PATH = "http://webservice.techrevolution.com.br/chatserver/uploads/";
    public static String action = "";

}