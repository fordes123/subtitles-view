package org.fordes.subview.entity;

import java.util.HashMap;

public class settingSet {

    private HashMap<String,String> baseSettings;
    public HashMap<String,String> getBaseSettings(){return baseSettings;}
    public settingSet(){
        baseSettings=new HashMap<String, String>();
    }

    public void RecSetting(){
        baseSettings.put("ThemeMode","0");
        baseSettings.put("TimingState","0");
        baseSettings.put("Timing","0");
        baseSettings.put("Editor_Font","System");
        baseSettings.put("Editor_Size","18");
        baseSettings.put("RecForm","0");
        baseSettings.put("RecFile","0");
        baseSettings.put("Recording","0");
    }

    class AdvancedSettings{

    }

    class ExperimentalSettings{
        
    }
}
