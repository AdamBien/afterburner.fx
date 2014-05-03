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
import java.util.ResourceBundle;

import org.junit.Test;

import com.airhacks.afterburner.topgun.TopgunPresenter;
import com.airhacks.afterburner.topgun.TopgunView;
import com.airhacks.afterburner.wrong.NoViewPresenter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

    @Test(expected = IllegalArgumentException.class)
    public void testGetViewForPresenterClassNullArgument() {
        FXMLView.getViewForPresenterClass(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetViewForPresenterClassWrongNaming() {
        FXMLView.getViewForPresenterClass(String.class);
    }

    @Test
    public void testGetViewForPresenterClassNoView() {
        FXMLView noView = FXMLView
                .getViewForPresenterClass(NoViewPresenter.class);
        assertNull(noView);
    }

    @Test
    public void testGetViewForPresenter() {
        TopgunView topgunView = (TopgunView) FXMLView
                .getViewForPresenterClass(TopgunPresenter.class);
        assertNotNull(topgunView);
    }
}
