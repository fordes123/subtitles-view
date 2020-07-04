package org.fordes.subview.controller;

import org.fordes.subview.main.Launcher;

public class RootController {

    public RootController(){
        Launcher.controllers.put(this.getClass().getSimpleName(), this);
    }
}
