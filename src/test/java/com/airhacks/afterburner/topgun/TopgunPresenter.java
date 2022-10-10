/*
 * Copyright 2013 Adam Bien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.airhacks.afterburner.topgun;

/*
 * #%L
 * afterburner.fx
 * %%
 * Copyright (C) 2013 Adam Bien
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import jakarta.inject.Inject;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author adam-bien.com
 */
public class TopgunPresenter implements Initializable {

    @Inject
    private String host;

    @Inject
    GunService gs;

    @Inject
    Date date;

    @Inject
    int damage;

    ResourceBundle rb;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Host: " + host);
        this.rb = rb;
    }

    public String getHost() {
        return host;
    }

    public String getMessageFromGun() {
        return gs.fireAndForget();
    }

    public ResourceBundle getResourceBundle() {
        return rb;
    }

    public GunService getGunService() {
        return gs;
    }

    public Date getDate() {
        return date;
    }

    public int getDamage() {
        return damage;
    }

}
