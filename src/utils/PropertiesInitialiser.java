package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

public class PropertiesInitialiser {
	
	private static final Properties properties = new Properties();
	private static String mainFolder;
	private static String editTagsModifier;
	private static String editTagsKeyCode;
	private static String externalName;
	private static String externalPath;
	private static String importShortCutModifier;
	private static String importShortCutKeyCode;
	private static String renameShortCutModifier;
	private static String renameShortCutKeyCode;
	private static String photoInfoShortCutModifier;
	private static String photoInfoShortCutKeyCode;
	
	static{
		initProperties();
	}
	
	private static void initProperties(){
		
		try {
			properties.load(new FileInputStream("res/config.properties"));
			
			mainFolder = properties.getProperty("mainFolder");
			
			editTagsModifier = properties.getProperty("editTagsShortcutModifier", null);
			
			editTagsKeyCode = properties.getProperty("editTagsShortcut", null);
			
			externalName = properties.getProperty("imageEditorName", null);
			
			externalPath = properties.getProperty("imageEditorPath", null);
			
			importShortCutKeyCode = properties.getProperty("importShortCutKeyCode", null);
			
			importShortCutModifier = properties.getProperty("importShortCutModifier", null);
			
			renameShortCutModifier = properties.getProperty("renameShortCutModifier", null);
			
			renameShortCutKeyCode = properties.getProperty("renameShortCutKeyCode", null);
			
			photoInfoShortCutModifier = properties.getProperty("photoInfoShortCutModifier", null);
			
			photoInfoShortCutKeyCode = properties.getProperty("photoInfoShortCutKeyCode", null);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	

	public static String getMainFolder() {
		return mainFolder;
	}


	public static void setMainFolder(String mainFolder) {
		properties.setProperty("mainFolder", mainFolder);
	}


	public static void setEditTagsModifier(String editTagsModifier) {
		properties.setProperty("editTagsShortcutModifier", editTagsModifier);
	}

	public static void setEditTagsKeyCode(String editTagsKeyCode) {
		properties.setProperty("editTagsShortcut", editTagsKeyCode);
	}

	public static String getEditTagsModifier() {
		return editTagsModifier;
	}


	public static String getEditTagsKeyCode() {
		return editTagsKeyCode;
	}

	public static String getExternalName() {
		return externalName;
	}



	public static void setExternalName(String externalName) {
		properties.setProperty("imageEditorName",externalName);
	}



	public static String getExternalPath() {
		return externalPath;
	}



	public static void setExternalPath(String imageEditorPath) {
		properties.setProperty("imageEditorPath",imageEditorPath);
	}



	public static void storeConfigValues(){
		try {
			properties.store(new PrintWriter("res/config.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void setImportShortCutKeyCode(String importShortCutKeyCode) {
		properties.setProperty("importShortCutKeyCode", importShortCutKeyCode);
		
	}

	public static String getImportShortCutKeyCode() {
		return importShortCutKeyCode;
	}


	public static void setImportShortCutModifier(String importShortCutModifier) {
		properties.setProperty("importShortCutModifier", importShortCutModifier);
		
	}


	public static String getImportShortCutModifier() {
		return importShortCutModifier;
	}



	public static String getRenameShortCutModifier() {
		return renameShortCutModifier;
	}

	public static void setRenameShortCutModifier(String renameShortCutModifier) {
		properties.setProperty("renameShortCutModifier",renameShortCutModifier);
	}


	public static String getRenameShortCutKeyCode() {
		return renameShortCutKeyCode;
	}


	public static void setRenameShortCutKeyCode(String renameShortCutKeyCode) {
		properties.setProperty("renameShortCutKeyCode",renameShortCutKeyCode);
	}



	public static String getPhotoInfoShortCutModifier() {
		return photoInfoShortCutModifier;
	}



	public static void setPhotoInfoShortCutModifier(String photoInfoShortCutModifier) {
		properties.setProperty("photoInfoShortCutModifier", photoInfoShortCutModifier);

	}



	public static String getPhotoInfoShortCutKeyCode() {
		return photoInfoShortCutKeyCode;
	}



	public static void setPhotoInfoShortCutKeyCode(String photoInfoShortCutKeyCode) {
		properties.setProperty("photoInfoShortCutKeyCode", photoInfoShortCutKeyCode);
	}



	
}
