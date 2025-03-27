package org.wallentines.cookieapi.impl;

import net.fabricmc.fabric.api.event.Event;
import org.wallentines.cookieapi.api.ConfigurationCookieEvents;

public interface ServerExtension {

    Event<ConfigurationCookieEvents.RequestCookies> getRequestCookiesEvent();

}
