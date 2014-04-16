package org.fluxtream.core.mvc.models;

import org.fluxtream.core.domain.oauth2.Application;

/**
 * User: candide
 * Date: 16/04/14
 * Time: 10:29
 */
public class ApplicationModel {

    public String name;
    public String description;

    public ApplicationModel() {}

    public ApplicationModel(Application app) {
        this.name = app.name;
        this.description = app.description;
    }

}
