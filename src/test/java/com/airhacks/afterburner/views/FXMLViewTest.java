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
package com.airhacks.afterburner.views;

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
import com.airhacks.afterburner.topgun.TopgunView;
import java.net.URL;
import java.util.ResourceBundle;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author adam-bien.com
 */
public class FXMLViewTest {

    @Test
    public void loadNonExistingBundle() {
        ResourceBundle loaded = FXMLView.getResourceBundle("non-existing");
        assertNull(loaded);
    }

    @Test
    public void fetchingNonExistingResource() {
        URL shouldNotExist = FXMLView.class.getResource("non-existing");
        assertNull(shouldNotExist);
    }

    @Test
    public void fetchingExistingResource() {
        URL shouldNotExist = FXMLView.class.getResource("existing.xml");
        assertNotNull(shouldNotExist);
    }

    @Test
    public void loadViewWithCamelCaseFXML() {
        CamelCaseFXMLView view = new CamelCaseFXMLView();
        String existingName = view.getFXMLName();
        assertThat(existingName.toLowerCase(), is("CamelCaseFXML.fxml".toLowerCase()));
    }

    @Test
    public void loadViewWithLowerCaseFXML() {
        LowerCaseFXMLView view = new LowerCaseFXMLView();
        String existingName = view.getFXMLName();
        assertThat(existingName, is("lowercasefxml.fxml"));
    }

    @Test(expected = IllegalStateException.class)
    public void loadViewWithoutFXML() {
        AViewWithoutFXML view = new AViewWithoutFXML();
        String existingName = view.getFXMLName();
        assertNull(existingName);
    }

    @Test
    public void conventionalLowerCaseName() {
        FXMLView view = new TopgunView();
        String conventionalName = view.getConventionalName(true);
        assertThat(conventionalName, is("topgun"));
    }

    @Test
    public void conventionalUnchangedName() {
        FXMLView view = new TopgunView();
        String conventionalName = view.getConventionalName(false);
        assertThat(conventionalName, is("Topgun"));
    }

}
