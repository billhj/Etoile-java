/*
 * This file is part of VIB (Virtual Interactive Behaviour).
 */
package vib.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains informations about characters that can be used by the system.<br/>
 * A default character is set at the initialization. Other character can be added with the function {@code addCharacter(String)}
 * or can be set as the character to use currently with {@code setCharacter(String)}.<br/>
 * If a parameter of the current character is changed or if an other character is set, all {@code CharacterDependent} added
 * with {@code add(CharacterDependent)} will be informed that a change occurs.
 * @author Andre-Marie Pez
 * @see vib.core.util.CharacterDependent CharacterDependent
 */
public class CharacterManager {

    /**
     * Don't let anyone instantiate this class.
     */
    private CharacterManager() {
    }

    private static final String DEFAULT_CHARACTER_NAME = "GRETA";
    private static IniManager characterDefinitions;
    private static Map<String, String> characterMapFile;
    private static List<CharacterDependent> dependents;
    private static String currentCaracterName;

    static {
        dependents = new ArrayList<CharacterDependent>();
        characterMapFile = new HashMap<String, String>();
        currentCaracterName = "DEFAULT_CHARACTER";
        String filename = IniManager.getGlobals().getValueString("DEFAULT_CHARACTER");
        if (!(new File(filename)).exists()) {
            currentCaracterName = DEFAULT_CHARACTER_NAME;
            filename = characterMapFile.get(DEFAULT_CHARACTER_NAME);
        }
        characterMapFile.put(currentCaracterName, (new File(filename)).getAbsolutePath());
        characterDefinitions = new IniManager((new File(filename)).getAbsolutePath());
        setCharacter(IniManager.getGlobals().getValueString("CURRENT_CHARACTER"));
    }

    /**
     * Adds a {@code CharacterDependent}.<br/>
     * All {@code CharacterDependent} added will be informed when the character change.
     * @param dependent the {@code CharacterDependent} to add
     */
    public static void add(CharacterDependent dependent) {
        if( ! dependents.contains(dependent)) {
            dependents.add(dependent);
        }
    }

    /**
     * Removes a {@code CharacterDependent}.<br/>
     * This {@code CharacterDependent} will not be informed when the character change.
     * @param dependent the {@code CharacterDependent} to remove
     */
    public static void remove(CharacterDependent dependent){
        try{
            dependents.remove(dependent);
        }catch(Exception e){/* remove may throw an exception but we dont take care of it */}
    }

    /**
     * Removes all {@code CharacterDependent} added.
     */
    public static void clearListOfDependents(){
        dependents.clear();
    }

    /**
     * Send a notification to all {@code CharacterDependent} added.
     */
    public static void notifyChanges() {
        for (CharacterDependent dependent : dependents) {
            dependent.onCharacterChanged();
        }
    }

    /**
     * Set a charater as current charater and informs all {@code CharacterDependent} that the character has changed.<br/>
     * If the character is unknown, this fuction tries to add it.
     * @param name the name of the character to set
     * @see #addCharacter(java.lang.String)
     */
    public static void setCharacter(String name) {
        String filename = fileNameOfCharacter(name);

        if (filename != null) {
            currentCaracterName = name;
            characterDefinitions.setDefinition(filename);
            notifyChanges();
        }
    }

    /**
     * Adds a new charater.<br/>
     * The name of the corresponding ini file of this character must be the value of the parameter, in the global ini file, that have the same name.<br/>
     * i.e. if {@code name="prudence"}, in the global ini file you must have {@code prudence=prud.ini}.
     * @param name the name of the character to add
     */
    public static void addCharacter(String name) {
        if (characterMapFile.get(name) == null) { //else it is already added
            String filename = IniManager.getGlobals().getValueString(name);
            if (!filename.isEmpty()) {
                filename = (new File(filename)).getAbsolutePath();
                characterMapFile.put(name, filename);
                characterDefinitions.addDefinition(filename);
            }
        }
    }

    /**
     * Returns the value of a parameter from the default character.
     * @param name the name of the parameter
     * @return the value from the default character
     */
    public static String getDefaultValueString(String name) {
        return characterDefinitions.getDefault(name).getParamValue();
    }

    /**
     * Returns the list of all known values of one specific parameter.
     * @param name the name of the parameter
     * @return the list of all known values
     */
    public static List<String> getAllValuesString(String name) {
        List<IniParameter> params = characterDefinitions.getAllFromOne(name);
        ArrayList<String> values = new ArrayList<String>(params.size());
        for (IniParameter param : params) {
            values.add(param.getParamValue());
        }
        return values;
    }

    /**
     * Returns the value of a IniParameter as a boolean.<br/>
     * This method returns the value of the parameters if found,
     * otherwise returns {@code false}.
     * @param name the name of the parameter
     * @return the boolean value of the parameter
     */
    public boolean getValueBoolean(String name) {
        return characterDefinitions.getValueBoolean(name);
    }

    /**
     * Returns the value of a parameter as an integer.<br/>
     * This method returns the value of the parameters if found,
     * otherwise returns the value -999999999.
     * @param name the name of the parameter
     * @return the integer value of the parameter
     */
    public static int getValueInt(String name) {
        return characterDefinitions.getValueInt(name);
    }

    /**
     * Returns the value of a parameter as a double.<br/>
     * This method returns the value of the parameters if found,
     * otherwise returns the value -999999999.0f
     * @param name the name of the parameter
     * @return the double value of the parameter
     */
    public static double getValueDouble(String name) {
        return characterDefinitions.getValueDouble(name);
    }

    /**
     * Returns the value of a parameter as a string.<br/>
     * This method returns the value of the parameters if found,
     * otherwise returns the empty string "".
     * @param name the name of the parameter
     * @return the string value of the parameter
     */
    public static String getValueString(String name) {
        return characterDefinitions.getValueString(name);
    }

    /**
     * Returns the value of a IniParameter as a boolean.<br/>
     * This method returns the value of the parameters if found,
     * otherwise returns {@code false}.
     * @param name the name of the parameter
     * @param characterName the name of the character
     * @return the boolean value of the parameter
     */
    public boolean getValueBoolean(String name, String characterName) {
        return characterDefinitions.getValueBoolean(name, fileNameOfCharacter(characterName));
    }

    /**
     * Returns the value of a parameter as an integer.<br/>
     * This method returns the value of the parameters if found,
     * otherwise returns the value -999999999.
     * @param name the name of the parameter
     * @param characterName the name of the character
     * @return the integer value of the parameter
     */
    public static int getValueInt(String name, String characterName) {
        return characterDefinitions.getValueInt(name, fileNameOfCharacter(characterName));
    }

    /**
     * Returns the value of a parameter as a double.<br/>
     * This method returns the value of the parameters if found,
     * otherwise returns the value -999999999.0f
     * @param name the name of the parameter
     * @param characterName the name of the character
     * @return the double value of the parameter
     */
    public static double getValueDouble(String name, String characterName) {
        return characterDefinitions.getValueDouble(name, fileNameOfCharacter(characterName));
    }

    /**
     * Returns the value of a parameter as a string.<br/>
     * This method returns the value of the parameters if found,
     * otherwise returns the empty string "".
     * @param name the name of the parameter
     * @param characterName the name of the character
     * @return the string value of the parameter
     */
    public static String getValueString(String name, String characterName) {
        return characterDefinitions.getValueString(name, fileNameOfCharacter(characterName));
    }

    /**
     * Sets the value of a parameter.
     * @param name the name of the parameter
     * @param value the value of the parameter
     */
    public void setValueBoolean(String name, boolean value) {
        characterDefinitions.setValueBoolean(name, value);
        notifyChanges();
    }

    /**
     * Sets the value of a parameter.
     * @param name the name of the parameter
     * @param value the value of the parameter
     */
    public static void setValueInt(String name, int value) {
        characterDefinitions.setValueInt(name, value);
        notifyChanges();
    }

    /**
     * Sets the value of a parameter.
     * @param name the name of the parameter
     * @param value the value of the parameter
     */
    public static void setValueDouble(String name, double value) {
        characterDefinitions.setValueDouble(name, value);
        notifyChanges();
    }

    /**
     * Sets the value of a parameter.
     * @param name the name of the parameter
     * @param value the value of the parameter
     */
    public static void setValueString(String name, String value) {
        characterDefinitions.setValueString(name, value);
        notifyChanges();
    }

    /**
     * add or set a value in the current character.
     * @param name the name of the parameter
     * @param value the value of the parameter
     */
    public static void addValueString(String name, String value){
        characterDefinitions.addValueString(name, value);
        notifyChanges();
    }

    /**
     * Returns the name of the current character.
     * @return the name of the current character
     */
    public static String getCurrentCharacterName() {
        return currentCaracterName;
    }

    /**
     * Returns the name of the default character.
     * @return the name of the default character
     */
    public static String getDefaultCharacterName() {
        return DEFAULT_CHARACTER_NAME;
    }

    /**
     * Returns the file name of the current character.
     * @return the file name of the current character
     */
    public static String getCurrentCharacterFile() {
        return fileNameOfCharacter(currentCaracterName);
    }

    /**
     * Returns the file name of the default character.
     * @return the file name of the default character
     */
    public static String getDefaultCharacterFile() {
        return fileNameOfCharacter(DEFAULT_CHARACTER_NAME);
    }

    /**
     * Returns the {@code IniManager} used to manage Ini files of chararters.
     * @return the {@code IniManager} used
     */
    public static IniManager getIniManager() {
        return characterDefinitions;
    }

    private static String fileNameOfCharacter(String characterName) {
        String filename = characterMapFile.get(characterName);

        if (filename == null) {
            addCharacter(characterName);
            //retry to get the file name :
            filename = characterMapFile.get(characterName);
        }
        return filename;
    }
}
