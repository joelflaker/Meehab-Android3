package com.citrusbits.meehab.webservices;

import java.util.Observable;

public abstract class BaseDataSource extends Observable  {
	protected void triggerObservers() {
        setChanged();
        notifyObservers();
    }
}
