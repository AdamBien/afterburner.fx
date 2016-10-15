package com.airhacks.afterburner.views;

import com.airhacks.afterburner.topgun.TopgunView;
import com.airhacks.afterburner.views.binary.BinaryView;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests to check that css files are loaded as required.
 * @author Ben Oxley
 */
public class CSSLoadTest {

    @Test
    public void cssIsLoadedTest(){
        TopgunView view = new TopgunView();
        assertTrue(view.getView().getStylesheets().stream()
                .anyMatch(s->s.contains("topgun.css"))
        );
    }

    @Test
    public void bssIsLoadedTest(){
        BinaryView view = new BinaryView();
        System.out.println(view.getView().getStylesheets());
        assertTrue(view.getView().getStylesheets().stream()
                .anyMatch(s->s.contains("binary.bss"))
        );
        assertTrue(view.getView().getStylesheets().stream()
                .noneMatch(s->s.contains("binary.css"))
        );
    }
}
