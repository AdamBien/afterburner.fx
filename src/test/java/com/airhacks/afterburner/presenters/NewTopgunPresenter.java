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
package com.airhacks.afterburner.presenters;

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
import com.airhacks.afterburner.topgun.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * FXML Controller class
 */
public class NewTopgunPresenter implements Initializable {

    @FXML
    private Parent root;

    @Inject
    private String host;

    // TopgunHackPresenter is annotated with @Presenter
    @Inject
    private TopgunHackPresenter topgunHackPresenter;

    @Inject
    GunService gs;

    ResourceBundle rb;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Host: " + host + "  - don't work, because it's injected after work of the FXMLLoader");
        this.rb = rb;
    }

    @PostConstruct
    private void postContruct() {
        System.out.println("New Topgun - Host: " + host);
        if (root != null) {
            System.out.println("New Topgun - root field was filled");
        }

        if (topgunHackPresenter != null && topgunHackPresenter.getRoot() != null) {
            System.out.println("New Topgun - TopgunHackPresenter is annotated with @Presenter and load fxml file too");
        }
    }

    public String getHost() {
        return host;
    }

    public String getMessageFromGun() {
        return gs.fireAndForget();
    }

    public GunService getGunService() {
        return gs;
    }

    public ResourceBundle getResourceBundle() {
        return rb;
    }

    public Parent getRoot() {
        return root;
    }

    public TopgunHackPresenter getTopgunHackPresenter() {
        return topgunHackPresenter;
    }

}
