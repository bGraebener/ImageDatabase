package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class PropertiesInitialiser {

	private static Properties userProperties;
	private static Properties defaultProperties;
	private static ResourceBundle resources;
	
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
	private static String deletePhotoShortCutModifier;
	private static String deletePhotoShortCutKeyCode;
	private static String fullViewShortCutKeyCode;
	private static String fullViewShortCutModifier;
	private static String language;

	static {
		initProperties();
	}

	private static void initProperties() {
		

		

		if (!Files.exists(Paths.get("res/userConfig.properties"))) {
			try {
				Files.createFile(Paths.get("res/userConfig.properties"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		defaultProperties = new Properties();
		userProperties = new Properties(defaultProperties);

		try {
			defaultProperties.load(new FileInputStream("res/config.properties"));
			userProperties.load(new FileInputStream("res/userConfig.properties"));

			mainFolder = userProperties.getProperty("mainFolder");

			editTagsModifier = userProperties.getProperty("editTagsShortcutModifier");

			editTagsKeyCode = userProperties.getProperty("editTagsShortcut");

			externalName = userProperties.getProperty("imageEditorName");

			externalPath = userProperties.getProperty("imageEditorPath");

			importShortCutKeyCode = userProperties.getProperty("importShortCutKeyCode");

			importShortCutModifier = userProperties.getProperty("importShortCutModifier");

			renameShortCutModifier = userProperties.getProperty("renameShortCutModifier");

			renameShortCutKeyCode = userProperties.getProperty("renameShortCutKeyCode");

			photoInfoShortCutModifier = userProperties.getProperty("photoInfoShortCutModifier");

			photoInfoShortCutKeyCode = userProperties.getProperty("photoInfoShortCutKeyCode");

			deletePhotoShortCutKeyCode = userProperties.getProperty("deletePhotoShortCutKeyCode");

			deletePhotoShortCutModifier = userProperties.getProperty("deletePhotoShortCutModifier");

			fullViewShortCutKeyCode = userProperties.getProperty("fullViewShortCutKeyCode");
			
			fullViewShortCutModifier = userProperties.getProperty("fullViewShortCutModifier");
			
			language = userProperties.getProperty("language");
			
			resources=ResourceBundle.getBundle("res.lang", new Locale(getLanguage()));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static ResourceBundle getResources(){
		return resources;
	}

	public static void storeConfigValues() {
		try {
			userProperties.store(new PrintWriter("res/userConfig.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getMainFolder() {
		return mainFolder;
	}

	public static void setMainFolder(String mainFolder) {
		userProperties.setProperty("mainFolder", mainFolder);
	}

	public static void setEditTagsModifier(String editTagsModifier) {
		userProperties.setProperty("editTagsShortcutModifier", editTagsModifier);
	}

	public static void setEditTagsKeyCode(String editTagsKeyCode) {
		userProperties.setProperty("editTagsShortcut", editTagsKeyCode);
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
		userProperties.setProperty("imageEditorName", externalName);
	}

	public static String getExternalPath() {
		return externalPath;
	}

	public static void setExternalPath(String imageEditorPath) {
		userProperties.setProperty("imageEditorPath", imageEditorPath);
	}

	public static void setImportShortCutKeyCode(String importShortCutKeyCode) {
		userProperties.setProperty("importShortCutKeyCode", importShortCutKeyCode);

	}

	public static String getImportShortCutKeyCode() {
		return importShortCutKeyCode;
	}

	public static void setImportShortCutModifier(String importShortCutModifier) {
		userProperties.setProperty("importShortCutModifier", importShortCutModifier);

	}

	public static String getImportShortCutModifier() {
		return importShortCutModifier;
	}

	public static String getRenameShortCutModifier() {
		return renameShortCutModifier;
	}

	public static void setRenameShortCutModifier(String renameShortCutModifier) {
		userProperties.setProperty("renameShortCutModifier", renameShortCutModifier);
	}

	public static String getRenameShortCutKeyCode() {
		return renameShortCutKeyCode;
	}

	public static void setRenameShortCutKeyCode(String renameShortCutKeyCode) {
		userProperties.setProperty("renameShortCutKeyCode", renameShortCutKeyCode);
	}

	public static String getPhotoInfoShortCutModifier() {
		return photoInfoShortCutModifier;
	}

	public static void setPhotoInfoShortCutModifier(String photoInfoShortCutModifier) {
		userProperties.setProperty("photoInfoShortCutModifier", photoInfoShortCutModifier);

	}

	public static String getPhotoInfoShortCutKeyCode() {
		return photoInfoShortCutKeyCode;
	}

	public static void setPhotoInfoShortCutKeyCode(String photoInfoShortCutKeyCode) {
		userProperties.setProperty("photoInfoShortCutKeyCode", photoInfoShortCutKeyCode);
	}

	public static String getDeletePhotoShortCutModifier() {
		return deletePhotoShortCutModifier;
	}

	public static String getDeletePhotoShortCutKeyCode() {
		return deletePhotoShortCutKeyCode;
	}

	public static void setDeletePhotoShortCutModifier(String deletePhotoShortCutModifier) {
		userProperties.setProperty("deletePhotoShortCutModifier", deletePhotoShortCutModifier);
	}

	public static void setDeletePhotoShortCutKeyCode(String deletePhotoShortCutKeyCode) {
		userProperties.setProperty("deletePhotoShortCutKeyCode", deletePhotoShortCutKeyCode);
	}

	public static String getFullViewShortCutModifier() {
		return fullViewShortCutModifier;
	}

	public static void setFullViewShortCutModifier(String fullViewShortCutModifier) {
		userProperties.setProperty("fullViewShortCutModifier", fullViewShortCutModifier);
	}

	public static String getFullViewShortCutKeyCode() {
		return fullViewShortCutKeyCode;
	}

	public static void setFullViewShortCutKeyCode(String fullViewShortCutKeyCode) {
		userProperties.setProperty("fullViewShortCutKeyCode", fullViewShortCutKeyCode);
	}

	public static void setLanguage(String language) {
		userProperties.setProperty("language", language);		
	}

	public static String getLanguage(){
		return language;
	}

}
