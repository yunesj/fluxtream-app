package com.fluxtream.updaters.quartz;

import com.fluxtream.connectors.google_latitude.LocationFacet;
import com.fluxtream.services.ApiDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * User: candide
 * Date: 10/04/13
 * Time: 17:34
 */
@Component
@Scope("prototype")
public class LocationDatapointHandler implements Runnable {

    LocationFacet locationFacet;

    @Autowired
    ApiDataService apiDataService;

    @Override
    public void run() {
        apiDataService.processLocation(locationFacet);
    }

}